// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.BufferInventory;
import com.schmaloogium.engine.buffers.BufferInventoryEntry;
import com.schmaloogium.engine.buffers.BufferPlan;
import com.schmaloogium.engine.buffers.BufferPlanRequest;
import com.schmaloogium.engine.buffers.BufferPlanResult;
import com.schmaloogium.engine.buffers.BufferResourceProjection;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.BufferRuntimeInputs;
import com.schmaloogium.engine.buffers.BufferSizing;
import com.schmaloogium.engine.buffers.CapabilityGate;
import com.schmaloogium.engine.buffers.CapabilityLimit;
import com.schmaloogium.engine.buffers.CapabilityShortfall;
import com.schmaloogium.engine.buffers.ColorBufferResource;
import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.buffers.InstanceResource;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.buffers.ResourceEvidenceStage;
import com.schmaloogium.engine.buffers.ResourceProjectionUnavailableReason;
import com.schmaloogium.engine.buffers.ResolvedBufferFormat;
import com.schmaloogium.engine.buffers.ShadowResourceProjection;
import com.schmaloogium.engine.buffers.ShadowTextureResource;
import com.schmaloogium.engine.buffers.VertexAttributeResource;
import com.schmaloogium.engine.config.BufferMinima;
import com.schmaloogium.engine.config.CenterDepthRequirements;
import com.schmaloogium.engine.config.ColorAttachmentFormat;
import com.schmaloogium.engine.config.ColorAttachmentKey;
import com.schmaloogium.engine.config.ColorAttachmentRequirement;
import com.schmaloogium.engine.config.NoiseRequirement;
import com.schmaloogium.engine.config.ResourceRequirements;
import com.schmaloogium.engine.config.ShadowDepthKey;
import com.schmaloogium.engine.config.ShadowRequirements;
import com.schmaloogium.engine.config.ShadowTextureKey;
import com.schmaloogium.engine.config.Vec4f;
import com.schmaloogium.engine.config.VertexAttribute;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.BufferRef;
import com.schmaloogium.engine.registry.DrawRouting;
import com.schmaloogium.engine.registry.DrawRoutingSlot;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramResolutionProjection;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
import com.schmaloogium.engine.registry.StageStep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * The pure planner (PHASE_5_DOC §4.1): consumes only immutable values, performs the ten
 * planning steps in order and produces either the canonical PLANNED evidence with its
 * artifacts or a typed failure with whatever snapshot remains derivable — never a GL call.
 * Stable for equal inputs.
 */
public final class BufferPlanner {

    /** v0.1 population gate: colortex indices above 7 stay unsupported until G8/S1. */
    static final int V01_MAX_COLORTEX_INDEX = 7;
    /** The fixed sampler contract addresses unit 15 regardless of pack declarations. */
    static final int REQUIRED_TEXTURE_IMAGE_UNITS = 16;

    private BufferPlanner() {
    }

    static BufferPlanResult plan(BufferPlanRequest request) {
        Objects.requireNonNull(request, "request");
        BufferRuntimeInputs runtime = request.runtime();
        BufferResourceSnapshot unavailableInput = unavailable(
            ResourceProjectionUnavailableReason.INPUT_INVALID);

        // Step 1: Phase 3 schema/fingerprint, Phase 4 registry fingerprint and its
        // fixed-policy fingerprint (schema 23 only per D-P5-44).
        if (request.configuration().schemaVersion() != PackFrontEnd.CURRENT_SCHEMA_VERSION) {
            return invalid(failure(BufferFailureCode.INVALID_INPUT,
                "schmaloogium.buffers.error.plan.schema"), unavailableInput);
        }
        if (!request.registryFingerprint().equals(request.registry().fingerprint())) {
            return invalid(failure(BufferFailureCode.INVALID_INPUT,
                "schmaloogium.buffers.error.plan.registry-fingerprint"), unavailableInput);
        }
        if (!FixedSamplerPolicies.appB3Fingerprint()
            .equals(request.registry().samplerPolicyFingerprint())) {
            return invalid(failure(BufferFailureCode.INVALID_INPUT,
                "schmaloogium.buffers.error.plan.sampler-policy"), unavailableInput);
        }

        // §4.11 runtime preconditions: positive display extent; finite positive multipliers.
        Extent2i display = runtime.displayExtent();
        if (display == null || display.width() < 1 || display.height() < 1
                || !isFinitePositive(runtime.renderQuality())
                || !isFinitePositive(runtime.shadowQuality())) {
            return invalid(failure(BufferFailureCode.INVALID_INPUT,
                "schmaloogium.buffers.error.plan.runtime-inputs"), unavailableInput);
        }

        ResourceRequirements resources = request.configuration().resources();
        BufferMinima minima = resources.minima();
        if (minima == null || minima.colorBuffers() < 1
                || minima.mainDepthTextures() < 1 || minima.mainDepthTextures() > 3
                || minima.shadowDepthBuffers() < 0 || minima.shadowColorBuffers() < 0) {
            return invalid(failure(BufferFailureCode.INVALID_INPUT,
                "schmaloogium.buffers.error.plan.minima"), unavailableInput);
        }

        // Step 3: depth count 1-3 and shadow counts 0-2.
        int depthCount = minima.mainDepthTextures();
        int shadowDepthCount = Math.min(2, minima.shadowDepthBuffers());
        int shadowColorCount = Math.min(2, minima.shadowColorBuffers());

        // Step 2: contiguous v0.1 colortex inventory 0 .. max(3, highestRequiredColorIndex).
        int highestRequired = scanHighestRequiredColorIndex(request, minima);
        if (highestRequired > V01_MAX_COLORTEX_INDEX) {
            return invalid(failure(BufferFailureCode.UNSUPPORTED_POST_V05_BUFFER_INDEX,
                "schmaloogium.buffers.error.plan.post-v05-index"), unavailableInput);
        }
        int colorCount = Math.max(3, highestRequired) + 1;
        List<BufferInventoryEntry> entries = new ArrayList<>();
        for (int index = 0; index < colorCount; index++) {
            entries.add(entry(BufferDomain.COLORTEX, index));
        }
        for (int index = 0; index < depthCount; index++) {
            entries.add(entry(BufferDomain.DEPTH, index));
        }
        for (int index = 0; index < shadowDepthCount; index++) {
            entries.add(entry(BufferDomain.SHADOWTEX, index));
        }
        for (int index = 0; index < shadowColorCount; index++) {
            entries.add(entry(BufferDomain.SHADOWCOLOR, index));
        }
        BufferInventory inventory = new BufferInventory(entries);
        BufferResourceSnapshot inventorySnapshot = new BufferResourceSnapshot.Available(
            projection(request, resources, inventory, colorCount, depthCount, shadowDepthCount,
                shadowColorCount, CapabilityGate.OK, List.of()));

        // Steps 4-5: resolve every route, retain explicit order, reject bad values.
        Map<ProgramSlotId, PlanningArtifacts.PlannedRoute> routes = new LinkedHashMap<>();
        int packedAttachmentCount = 0;
        for (StageStep step : request.registry().stages().schedule()) {
            for (PassDescriptor descriptor : request.registry().stages().passes(step)) {
                PlanningArtifacts.PlannedRoute route = resolveRoute(request.registry(),
                    descriptor, inventory);
                PlanningFailure failure = validateRoute(route, descriptor, inventory);
                if (failure != null) {
                    return invalid(failure.failure(),
                        unavailable(ResourceProjectionUnavailableReason.DERIVATION_INCOMPLETE));
                }
                routes.put(route.slot(), route);
                packedAttachmentCount = Math.max(packedAttachmentCount, attachmentCount(route));
            }
        }

        // Steps 6-7: exact capability shortfalls on the three wire limits.
        GLCapabilityProfile capabilities = request.capabilities();
        List<CapabilityShortfall> shortfalls = new ArrayList<>();
        int maxDrawBuffers = capabilities.maxDrawBuffers();
        int maxColorAttachments = capabilities.maxColorAttachments();
        int maxTextureImageUnits = capabilities.maxTextureImageUnits();
        if (maxTextureImageUnits < REQUIRED_TEXTURE_IMAGE_UNITS) {
            shortfalls.add(new CapabilityShortfall(CapabilityLimit.MAX_TEXTURE_IMAGE_UNITS,
                REQUIRED_TEXTURE_IMAGE_UNITS, maxTextureImageUnits));
        }
        for (PlanningArtifacts.PlannedRoute route : routes.values()) {
            int length = route.positional().size();
            int attachments = attachmentCount(route);
            if (length > maxDrawBuffers) {
                shortfalls.add(new CapabilityShortfall(CapabilityLimit.MAX_DRAW_BUFFERS, length,
                    maxDrawBuffers));
            }
            if (attachments > maxColorAttachments) {
                shortfalls.add(new CapabilityShortfall(
                    CapabilityLimit.MAX_COLOR_ATTACHMENTS, attachments, maxColorAttachments));
            }
        }
        if (!shortfalls.isEmpty()) {
            BufferResourceSnapshot.Available shortfalled = new BufferResourceSnapshot.Available(
                projection(request, resources, inventory, colorCount, depthCount,
                    shadowDepthCount, shadowColorCount, CapabilityGate.SHORTFALL, shortfalls));
            return invalid(failure(BufferFailureCode.CAPABILITY_LIMIT,
                "schmaloogium.buffers.error.plan.capability"), shortfalled);
        }

        // Step 8: extents with checked arithmetic and maxTextureSize gates (§4.11).
        ExtentSizing extents = extents(runtime, resources.shadow(), shadowDepthCount,
            shadowColorCount, capabilities.maxTextureSize());
        if (extents == null) {
            return invalid(failure(BufferFailureCode.CAPABILITY_LIMIT,
                "schmaloogium.buffers.error.plan.extent"), unavailable(
                    ResourceProjectionUnavailableReason.DERIVATION_INCOMPLETE));
        }

        // Step 9: resolve every format/parameter/clear rule into closed engine values.
        List<PlanningArtifacts.ColorPlan> colors =
            colorPlans(inventory, resources.colorAttachments());

        // PLANNED projection with the exact per-row declarative values (§4.1.1).
        BufferResourceProjection planned = projection(request, resources, inventory, colorCount,
            depthCount, shadowDepthCount, shadowColorCount, CapabilityGate.OK, List.of());

        // Step 10: deterministic artifacts — unit rows from the App B.3 map and reverse
        // creation order for teardown.
        List<List<String>> fullscreenRows = AppB3Policy.columnRows(AppB3Policy.Domain.FULLSCREEN);
        List<List<String>> gbuffersRows = AppB3Policy.columnRows(AppB3Policy.Domain.GBUFFERS);
        List<LogicalBuffer> teardownOrder = new ArrayList<>();
        for (int index = shadowColorCount - 1; index >= 0; index--) {
            teardownOrder.add(buffer(BufferDomain.SHADOWCOLOR, index));
        }
        for (int index = shadowDepthCount - 1; index >= 0; index--) {
            teardownOrder.add(buffer(BufferDomain.SHADOWTEX, index));
        }
        for (int index = depthCount - 1; index >= 1; index--) {
            teardownOrder.add(buffer(BufferDomain.DEPTH, index));
        }
        for (int index = colorCount - 1; index >= 0; index--) {
            teardownOrder.add(buffer(BufferDomain.COLORTEX, index));
        }

        PlanningArtifacts artifacts = new PlanningArtifacts(
            new BufferSizing(extents.mainExtent(),
                extents.shadowExtent()),
            inventory, depthCount, colors, routes, fullscreenRows, gbuffersRows, teardownOrder,
            planned);
        return new BufferPlanResult.Valid(new BufferPlan(artifacts),
            (BufferResourceSnapshot.Available) inventorySnapshot);
    }

    private static BufferInventoryEntry entry(BufferDomain domain, int index) {
        return new BufferInventoryEntry(buffer(domain, index), resolvedFormat(domain));
    }

    private static ResolvedBufferFormat resolvedFormat(BufferDomain domain) {
        return switch (domain) {
            case COLORTEX -> new ResolvedBufferFormat.Color(ColorInternalFormat.RGBA_COMPAT);
            case DEPTH -> new ResolvedBufferFormat.Depth(
                com.schmaloogium.engine.gl.DepthAttachmentFormat.DEPTH_COMPONENT);
            default -> new ResolvedBufferFormat.Color(ColorInternalFormat.RGBA_COMPAT);
        };
    }

    private static LogicalBuffer buffer(BufferDomain domain, int index) {
        return new LogicalBuffer(domain, new BufferIndex(index));
    }

    /**
     * Highest required COLORTEX index: the P3 minimum count, every explicit attachment
     * reference and every readable/writable/mipmapped/flip/composite reference in the
     * registry (§4.1 step 2; scan-driven growth through the highest required index).
     */
    private static int scanHighestRequiredColorIndex(BufferPlanRequest request,
            BufferMinima minima) {
        int highest = minima.colorBuffers() - 1;
        for (StageStep step : request.registry().stages().schedule()) {
            for (PassDescriptor descriptor : request.registry().stages().passes(step)) {
                highest = Math.max(highest, refIndex(descriptor.resources().readable(), highest));
                highest = Math.max(highest, refIndex(descriptor.resources().writes(), highest));
                highest = Math.max(highest,
                    refIndex(descriptor.resources().mipmappedBeforeRead(), highest));
                highest = Math.max(highest,
                    refIndex(descriptor.resources().explicitFlips().keySet(), highest));
                Optional<ResolvedProgramDescriptor> resolved =
                    request.registry().resolve(descriptor.slot());
                if (resolved.isPresent()) {
                    highest = Math.max(highest,
                        refIndex(resolved.get().state().compositeMipmaps(), highest));
                }
            }
        }
        return highest;
    }

    private static int refIndex(Set<BufferRef> refs, int current) {
        int highest = current;
        for (BufferRef ref : refs) {
            if (ref.domain() == BufferDomain.COLORTEX) {
                highest = Math.max(highest, ref.index());
            }
        }
        return highest;
    }

    /** Resolves one descriptor's routing symbolically; explicit order is retained exactly. */
    private static PlanningArtifacts.PlannedRoute resolveRoute(
            com.schmaloogium.engine.registry.ProgramRegistryView view, PassDescriptor descriptor,
            BufferInventory inventory) {
        Optional<ResolvedProgramDescriptor> resolved =
            view.resolve(descriptor.slot());
        DrawRouting routing = resolved
            .map(value -> value.state().drawRouting())
            .orElseGet(() -> new DrawRouting.AllUsedBuffers(BufferDomain.COLORTEX));
        List<DrawRoutingSlot> positional = new ArrayList<>();
        List<LogicalBuffer> writeBuffers = new ArrayList<>();
        switch (routing) {
            case DrawRouting.AllUsedBuffers allUsed -> {
                if (allUsed.domain() != BufferDomain.COLORTEX) {
                    throw new IllegalArgumentException(
                        "AllUsedBuffers routing is COLORTEX-only: " + allUsed.domain());
                }
                for (BufferInventoryEntry entry : inventory.entries()) {
                    if (entry.buffer().domain() == BufferDomain.COLORTEX) {
                        positional.add(new DrawRoutingSlot.Attachment(
                            new BufferRef(BufferDomain.COLORTEX,
                                entry.buffer().index().value())));
                        writeBuffers.add(entry.buffer());
                    }
                }
            }
            case DrawRouting.Explicit explicit -> {
                for (DrawRoutingSlot slot : explicit.slots()) {
                    positional.add(slot);
                    if (slot instanceof DrawRoutingSlot.Attachment attachment) {
                        BufferRef ref = attachment.buffer();
                        writeBuffers.add(buffer(ref.domain(), ref.index()));
                    }
                }
            }
        }
        return new PlanningArtifacts.PlannedRoute(descriptor.slot(), positional, writeBuffers);
    }

    /**
     * Route value validation (§4.1 step 5): duplicates, negatives and out-of-inventory
     * references fail closed before capability checks.
     */
    private static PlanningFailure validateRoute(PlanningArtifacts.PlannedRoute route,
            PassDescriptor descriptor, BufferInventory inventory) {
        Map<LogicalBuffer, Boolean> known = new HashMap<>();
        for (BufferInventoryEntry entry : inventory.entries()) {
            known.put(entry.buffer(), Boolean.TRUE);
        }
        java.util.HashSet<LogicalBuffer> seenMutable = new java.util.HashSet<>();
        for (LogicalBuffer logical : route.writeBuffers()) {
            if (!known.containsKey(logical)) {
                return new PlanningFailure(failure(BufferFailureCode.INVALID_INPUT,
                    "schmaloogium.buffers.error.plan.route-inventory"));
            }
            if (!seenMutable.add(logical)) {
                return new PlanningFailure(failure(BufferFailureCode.INVALID_INPUT,
                    "schmaloogium.buffers.error.plan.route-duplicate"));
            }
        }
        for (DrawRoutingSlot slot : route.positional()) {
            if (slot instanceof DrawRoutingSlot.Attachment attachment
                    && attachment.buffer().index() < 0) {
                return new PlanningFailure(failure(BufferFailureCode.INVALID_INPUT,
                    "schmaloogium.buffers.error.plan.route-negative"));
            }
        }
        return null;
    }

    private static int attachmentCount(PlanningArtifacts.PlannedRoute route) {
        int count = 0;
        for (DrawRoutingSlot slot : route.positional()) {
            if (slot instanceof DrawRoutingSlot.Attachment) {
                count++;
            }
        }
        return count;
    }

    /**
     * §4.11 sizing formulas with checked {@code double} arithmetic:
     * {@code mainWidth = max(1, round(displayWidth * renderQuality))} and
     * {@code shadowExtent = max(1, round(shadowMapResolution * shadowQuality))} when either
     * shadow minimum is positive. Any non-finite/overflow or above-{@code maxTextureSize}
     * result fails before allocation.
     */
    private static ExtentSizing extents(BufferRuntimeInputs runtime, ShadowRequirements shadow,
            int shadowDepthCount, int shadowColorCount, int maxTextureSize) {
        double mainWidth = checkExtent(runtime.displayExtent().width() * runtime.renderQuality(),
            maxTextureSize);
        double mainHeight = checkExtent(runtime.displayExtent().height() * runtime.renderQuality(),
            maxTextureSize);
        if (mainWidth < 0 || mainHeight < 0) {
            return null;
        }
        Extent2i mainExtent = new Extent2i(Math.max(1, (int) mainWidth),
            Math.max(1, (int) mainHeight));
        boolean shadowPlanned = shadowDepthCount > 0 || shadowColorCount > 0;
        Optional<Extent2i> shadowExtent = Optional.empty();
        if (shadowPlanned) {
            double shadowWidth = checkExtent(
                shadowExtentResolution(shadow) * runtime.shadowQuality(), maxTextureSize);
            if (shadowWidth < 0) {
                return null;
            }
            shadowExtent = Optional.of(new Extent2i(Math.max(1, (int) shadowWidth),
                Math.max(1, (int) shadowWidth)));
        }
        return new ExtentSizing(mainExtent, shadowExtent);
    }

    private static int shadowExtentResolution(ShadowRequirements shadow) {
        // §4.10 sizes all shadow estate sides from §4.11's shadow extent; the resolution
        // scalar is the declared pack-facing requirement value.
        return shadow == null ? 1024 : shadow.resolution();
    }

    /** Returns -1 on any checked-arithmetic or maxTextureSize failure. */
    private static double checkExtent(double value, int maxTextureSize) {
        if (!Double.isFinite(value) || value > Integer.MAX_VALUE) {
            return -1;
        }
        long rounded = Math.round(value);
        if (rounded < 1 || rounded > maxTextureSize) {
            return -1;
        }
        return rounded;
    }

    /**
     * Dense declarative color plans (§4.1.1): requested format preserves DefaultRgba versus
     * Explicit; the clear policy is the pack's explicit finite constant or the per-index
     * default (fog RGB alpha 1 for colortex0, white for colortex1, black for others).
     */
    private static List<PlanningArtifacts.ColorPlan> colorPlans(BufferInventory inventory,
            Map<ColorAttachmentKey, ColorAttachmentRequirement> attachments) {
        List<PlanningArtifacts.ColorPlan> plans = new ArrayList<>();
        for (BufferInventoryEntry entry : inventory.entries()) {
            if (entry.buffer().domain() != BufferDomain.COLORTEX) {
                continue;
            }
            int index = entry.buffer().index().value();
            ColorAttachmentKey key = new ColorAttachmentKey(index);
            ColorAttachmentRequirement requirement = attachments.get(key);
            ColorAttachmentFormat requested = requirement == null
                ? new ColorAttachmentFormat.DefaultRgba()
                : requirement.format();
            boolean clear = requirement == null || requirement.clear();
            ResourceClearPolicy policy = clearPolicy(index, requirement);
            plans.add(new PlanningArtifacts.ColorPlan(entry.buffer(), requested, clear, policy));
        }
        return List.copyOf(plans);
    }

    private static ResourceClearPolicy clearPolicy(int index,
            ColorAttachmentRequirement requirement) {
        Optional<Vec4f> override = requirement == null
            ? Optional.empty()
            : requirement.clearColorOverride();
        if (override.isPresent()) {
            Vec4f color = override.get();
            return new ResourceClearPolicy.Constant(color.red(), color.green(), color.blue(),
                color.alpha());
        }
        if (index == 0) {
            return new ResourceClearPolicy.FogRgbAlphaOne();
        }
        if (index == 1) {
            return new ResourceClearPolicy.Constant(1.0, 1.0, 1.0, 1.0);
        }
        return new ResourceClearPolicy.Constant(0.0, 0.0, 0.0, 0.0);
    }

    /**
     * The canonical PLANNED evidence (§4.1.1): dense requested values only, sorted unique
     * attributes/instances, exact shortfall rows, no handles/sides/generations.
     */
    private static BufferResourceProjection projection(BufferPlanRequest request,
            ResourceRequirements resources, BufferInventory inventory, int colorCount,
            int depthCount, int shadowDepthCount, int shadowColorCount, CapabilityGate gate,
            List<CapabilityShortfall> shortfalls) {
        List<ColorBufferResource> colors = new ArrayList<>();
        for (BufferInventoryEntry entry : inventory.entries()) {
            if (entry.buffer().domain() != BufferDomain.COLORTEX) {
                continue;
            }
            int index = entry.buffer().index().value();
            ColorAttachmentKey key = new ColorAttachmentKey(index);
            ColorAttachmentRequirement requirement = resources.colorAttachments().get(key);
            ColorAttachmentFormat requested = requirement == null
                ? new ColorAttachmentFormat.DefaultRgba()
                : requirement.format();
            boolean clear = requirement == null || requirement.clear();
            colors.add(new ColorBufferResource(requested, Optional.empty(), clear,
                clearPolicy(index, requirement)));
        }

        ShadowRequirements shadow = resources.shadow();
        int resolution = shadow == null ? 0 : shadow.resolution();
        List<ShadowTextureResource> shadowDepth = new ArrayList<>();
        List<ShadowTextureResource> shadowColor = new ArrayList<>();
        for (int index = 0; index < shadowDepthCount; index++) {
            ShadowDepthKey depthKey = index == 0 ? ShadowDepthKey.DEPTH_0 : ShadowDepthKey.DEPTH_1;
            ShadowTextureKey textureKey = index == 0
                ? ShadowTextureKey.DEPTH_0
                : ShadowTextureKey.DEPTH_1;
            shadowDepth.add(new ShadowTextureResource(
                shadow != null && shadow.hardwarePcf().contains(depthKey),
                shadow != null && shadow.mipmapped().contains(textureKey),
                shadow != null && shadow.nearest().contains(textureKey)));
        }
        for (int index = 0; index < shadowColorCount; index++) {
            ShadowTextureKey textureKey = index == 0
                ? ShadowTextureKey.COLOR_0
                : ShadowTextureKey.COLOR_1;
            shadowColor.add(new ShadowTextureResource(false,
                shadow != null && shadow.mipmapped().contains(textureKey),
                shadow != null && shadow.nearest().contains(textureKey)));
        }

        CenterDepthRequirements centerDepth = resources.centerDepth();
        NoiseRequirement noise = resources.noise();
        Set<VertexAttributeResource> attributes = new TreeSet<>(
            Comparator.comparing(VertexAttributeResource::program)
                .thenComparing(VertexAttributeResource::name));
        Map<String, Integer> instanceCounts = new HashMap<>();
        resources.programs().forEach((key, requirement) -> {
            String program = key.programName();
            requirement.vertices().attributes().forEach(attribute ->
                attributes.add(new VertexAttributeResource(program, attribute.name())));
            instanceCounts.merge(program, requirement.instanceCount(), Math::max);
        });
        List<InstanceResource> instances = instanceCounts.entrySet().stream()
            .map(entry -> new InstanceResource(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(InstanceResource::program))
            .toList();

        return new BufferResourceProjection(ResourceEvidenceStage.PLANNED, List.copyOf(colors),
            depthCount,
            new ShadowResourceProjection(shadowDepthCount, shadowColorCount, resolution,
                List.copyOf(shadowDepth), List.copyOf(shadowColor)),
            centerDepth != null && centerDepth.required(),
            noise != null && noise.enabled() ? noise.resolution() : 0,
            List.copyOf(attributes), instances, gate, List.copyOf(shortfalls));
    }

    private static BufferResourceSnapshot unavailable(ResourceProjectionUnavailableReason reason) {
        return new BufferResourceSnapshot.Unavailable(reason);
    }

    private static BufferPlanResult invalid(BufferFailure failure,
            BufferResourceSnapshot resources) {
        return new BufferPlanResult.Invalid(failure, resources);
    }

    private static BufferFailure failure(BufferFailureCode code, String messageKey) {
        return new BufferFailure(code, messageKey, messageKey, List.of(), Optional.empty(),
            Optional.empty());
    }

    private static boolean isFinitePositive(double value) {
        return Double.isFinite(value) && value > 0.0;
    }

    /** Internal pair for extent derivation results. */
    private record ExtentSizing(Extent2i mainExtent, Optional<Extent2i> shadowExtent) {
    }

    /** Internal typed failure carrier for route validation. */
    private record PlanningFailure(BufferFailure failure) {
    }
}
