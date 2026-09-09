// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.LegacyGeometryInputPrimitive;
import com.schmaloogium.engine.gl.LegacyGeometryOutputPrimitive;
import com.schmaloogium.engine.gl.SamplerUnitAssignment;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.BufferRef;
import com.schmaloogium.engine.registry.DrawRouting;
import com.schmaloogium.engine.registry.DrawRoutingSlot;
import com.schmaloogium.engine.registry.ExtendedAttribute;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.FixedSamplerLayoutPolicy;
import com.schmaloogium.engine.registry.GeometryInputRequirement;
import com.schmaloogium.engine.registry.ProgramBuildFailure;
import com.schmaloogium.engine.registry.ProgramBuildStage;
import com.schmaloogium.engine.registry.ProgramOwnBuildDisposition;
import com.schmaloogium.engine.registry.ProgramSamplerDeclaration;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint;
import com.schmaloogium.engine.registry.ProgramSlotDescriptor;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramSlotKind;
import com.schmaloogium.engine.registry.ProgramStateBundle;
import com.schmaloogium.engine.registry.ProgramUniformLayout;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.config.ColorAttachmentKey;
import com.schmaloogium.engine.config.EvaluatedProgramState;
import com.schmaloogium.engine.preprocess.LegacyGeometryConfig;
import com.schmaloogium.engine.config.EvaluatedProgramStates;
import com.schmaloogium.engine.config.FlipOverride;
import com.schmaloogium.engine.config.ProgramKey;
import com.schmaloogium.engine.config.ProgramRequirements;
import com.schmaloogium.engine.config.VertexAttribute;
import com.schmaloogium.engine.pack.DimensionConfiguration;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.preprocess.DeclaredUniformCatalog;
import com.schmaloogium.engine.preprocess.GeometryInputPrimitive;
import com.schmaloogium.engine.preprocess.GeometrySourceForm;
import com.schmaloogium.engine.preprocess.GeometrySourceRequest;
import com.schmaloogium.engine.preprocess.MaterializationResult;
import com.schmaloogium.engine.preprocess.MaterializedSource;
import com.schmaloogium.engine.preprocess.ShaderSourceStage;
import com.schmaloogium.engine.preprocess.SourceKey;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Pure per-slot planning and own-build classification (PHASE_4_DOC §4.6/§4.7), executed
 * before any GL allocation: source selection without base/override merging, the §4.6
 * classification precedence, materialization, uniform merge, all-band sampler policy
 * validation, capability and route validation, and the lossless state adaptation.
 */
final class Planner {

    /** One planned slot with its classification and (when enabled) its complete build plan. */
    static final class PlannedSlot {
        final ProgramSlotDescriptor descriptor;
        ProgramOwnBuildDisposition disposition;
        boolean sourcePresent;
        ProgramBuildFailure failure;
        List<MaterializedStage> materialized = List.of();
        ProgramUniformLayout uniformLayout = ProgramUniformLayout.empty();
        List<ProgramSamplerDeclaration> samplers = List.of();
        ProgramSamplerLayout samplerLayout;
        List<SamplerUnitAssignment> assignments = List.of();
        ProgramStateBundle stateBundle;
        GeometryInputRequirement expectedGeometryInput = GeometryInputRequirement.NONE;
        LegacyGeometryNative nativeConfigure;

        boolean poisoned;

        PlannedSlot(ProgramSlotDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        boolean succeeded() {
            return disposition == ProgramOwnBuildDisposition.SUCCEEDED;
        }

        ProgramSamplerLayout.Shader samplerLayoutShader() {
            return (ProgramSamplerLayout.Shader) samplerLayout;
        }
    }

    record MaterializedStage(ShaderSourceStage stage, MaterializedSource source) {
    }

    /** The admitted native-geometry pre-link configuration, exactly once before link. */
    record LegacyGeometryNative(
            LegacyGeometryInputPrimitive input,
            LegacyGeometryOutputPrimitive output,
            int maxVerticesOut) {
    }

    private Planner() {
    }

    /** Classify and plan one catalog slot against the selected dimension. */
    static PlannedSlot planSlot(
            ProgramSlotDescriptor descriptor,
            DimensionConfiguration dimension,
            EvaluatedProgramStates states,
            com.schmaloogium.engine.pack.PackConfiguration configuration,
            com.schmaloogium.engine.preprocess.MacroContribution contribution,
            FixedSamplerLayoutPolicy policy,
            GLCapabilityProfile capabilities,
            List<ProgramBuildFailure> policyFailuresOut) {
        PlannedSlot planned = new PlannedSlot(descriptor);
        if (descriptor.kind() != ProgramSlotKind.RASTER) {
            planned.disposition = ProgramOwnBuildDisposition.NOT_APPLICABLE;
            planned.sourcePresent = false;
            return planned;
        }
        String stem = descriptor.sourceStem().orElseThrow();
        List<SourceKey> roots = dimension.sourceRoots().stream()
            .filter(key -> key.programName().equals(stem))
            .toList();
        if (roots.isEmpty()) {
            // No selected source stages: NO_SOURCE even when enablement is false (§4.6);
            // missing overrides are never filled by merging base sources.
            planned.disposition = ProgramOwnBuildDisposition.NO_SOURCE;
            planned.sourcePresent = false;
            return planned;
        }
        planned.sourcePresent = true;
        ProgramKey key = new ProgramKey(dimension.key(), stem);
        Optional<EvaluatedProgramState> evaluated = states.programs().stream()
            .filter(state -> state.key().equals(key))
            .findFirst();
        if (evaluated.isPresent() && !evaluated.get().finalEnabled()) {
            planned.disposition = ProgramOwnBuildDisposition.DISABLED;
            return planned;
        }
        planned.disposition = ProgramOwnBuildDisposition.FAILED; // until complete success
        planBuild(planned, roots, key, states, configuration, contribution, policy,
            capabilities, policyFailuresOut);
        return planned;
    }

    private static void planBuild(
            PlannedSlot planned,
            List<SourceKey> roots,
            ProgramKey key,
            EvaluatedProgramStates states,
            com.schmaloogium.engine.pack.PackConfiguration configuration,
            com.schmaloogium.engine.preprocess.MacroContribution contribution,
            FixedSamplerLayoutPolicy policy,
            GLCapabilityProfile capabilities,
            List<ProgramBuildFailure> policyFailuresOut) {
        ProgramSlotId slot = planned.descriptor.id();
        Optional<ProgramRequirements> reqs = Optional.ofNullable(
            configuration.resources().programs()
                .get(new com.schmaloogium.engine.config.ProgramRequirementKey(
                    key.dimension(), key.programName())));
        Optional<LegacyGeometryConfig> legacy = reqs.flatMap(ProgramRequirements::legacyGeometry);
        boolean geometryRoot = roots.stream().anyMatch(r -> r.stage() == ShaderSourceStage.GEOMETRY);
        GeometrySourceRequest request = legacy.isPresent() && geometryRoot
            ? new GeometrySourceRequest.PreserveNative(legacy.get())
            : new GeometrySourceRequest.None();

        // Materialize once per available source stage, deterministic V, G, F order.
        List<SourceKey> ordered = new ArrayList<>(roots);
        ordered.sort(Comparator.comparingInt(r -> r.stage().ordinal()));
        List<MaterializedStage> materialized = new ArrayList<>();
        for (SourceKey root : ordered) {
            GeometrySourceRequest stageRequest = root.stage() == ShaderSourceStage.GEOMETRY
                ? request
                : new GeometrySourceRequest.None();
            MaterializationResult result =
                configuration.sources().materializer().materialize(root, contribution, stageRequest);
            if (result instanceof MaterializationResult.Available available) {
                if (!available.source().declaredUniforms().materialization()
                    .equals(available.source().fingerprint())) {
                    planned.failure = failure(slot, ProgramBuildStage.MATERIALIZE,
                        Optional.of(root.stage()), "", List.of(), "MATERIALIZE_CATALOG_MISMATCH");
                    return;
                }
                materialized.add(new MaterializedStage(root.stage(), available.source()));
            } else if (result instanceof MaterializationResult.Unavailable unavailable) {
                String detail = unavailable.diagnostics().isEmpty()
                    ? ""
                    : unavailable.diagnostics().get(0).detail();
                List<String> ids = unavailable.diagnostics().stream()
                    .map(d -> d.messageKey()).toList();
                planned.failure = failure(slot, ProgramBuildStage.MATERIALIZE,
                    Optional.of(root.stage()), detail, ids, "MATERIALIZE_UNAVAILABLE");
                return;
            }
        }
        planned.materialized = List.copyOf(materialized);
        if (materialized.isEmpty()) {
            planned.failure = failure(slot, ProgramBuildStage.MATERIALIZE, Optional.empty(),
                "", List.of(), "MATERIALIZE_NO_STAGE");
            return;
        }

        // Capability gates for the geometry route before any GL work (§4.8).
        Optional<MaterializedStage> geometryStage = materialized.stream()
            .filter(stage -> stage.stage() == ShaderSourceStage.GEOMETRY)
            .findFirst();
        if (geometryStage.isPresent()) {
            GeometrySourceForm form = geometryStage.get().source().geometry();
            switch (form) {
                case GeometrySourceForm.CoreLayout core -> {
                    if (!capabilities.atLeast(3, 2)) {
                        planned.failure = failure(slot, ProgramBuildStage.CAPABILITY,
                            Optional.of(ShaderSourceStage.GEOMETRY), "", List.of(),
                            "GEOMETRY_CORE_GL32");
                        return;
                    }
                    planned.expectedGeometryInput = mapInput(core.effective().input());
                }
                case GeometrySourceForm.NativeLegacy legacyLayout -> {
                    if (!capabilities.hasExtension("GL_ARB_geometry_shader4")) {
                        planned.failure = failure(slot, ProgramBuildStage.CAPABILITY,
                            Optional.of(ShaderSourceStage.GEOMETRY), "", List.of(),
                            "GEOMETRY_NATIVE_EXTENSION");
                        return;
                    }
                    if (!legacyLayout.declarations().isEmpty() && !capabilities.atLeast(3, 2)) {
                        planned.failure = failure(slot, ProgramBuildStage.CAPABILITY,
                            Optional.of(ShaderSourceStage.GEOMETRY), "", List.of(),
                            "GEOMETRY_SOURCE_GL32");
                        return;
                    }
                    planned.expectedGeometryInput = mapInput(legacyLayout.effective().input());
                    planned.nativeConfigure = new LegacyGeometryNative(
                        LegacyGeometryInputPrimitive.TRIANGLES,
                        LegacyGeometryOutputPrimitive.TRIANGLE_STRIP,
                        legacyLayout.config().maxVertices());
                }
                case GeometrySourceForm.None none -> {
                    planned.expectedGeometryInput = GeometryInputRequirement.NONE;
                }
            }
        } else {
            planned.expectedGeometryInput = GeometryInputRequirement.NONE;
        }

        // Uniform merge precedes every GL call (§4.7).
        List<DeclaredUniformCatalog> stageCatalogs = materialized.stream()
            .map(stage -> stage.source().declaredUniforms())
            .toList();
        UniformLayouts.Merged merged = UniformLayouts.merge(stageCatalogs);
        if (merged.hasTypeConflict()) {
            planned.failure = failure(slot, ProgramBuildStage.COMPILE, Optional.empty(),
                "", List.of(), "UNIFORM_TYPE_CONFLICT:" + merged.typeConflictName());
            return;
        }
        planned.uniformLayout = merged.layout();
        planned.samplers = merged.samplers();

        // All-band sampler policy validation before GL (D-P4-36).
        SamplerLayoutValidation validation;
        try {
            validation = UniformLayouts.validateAcrossBands(policy, planned.descriptor.stage(),
                new ArrayList<>(planned.descriptor.permittedBands()), planned.samplers);
        } catch (RuntimeException e) {
            policyFailuresOut.add(registryPolicyFailure(e));
            return;
        }
        Set<StageBand> attemptedBands = new java.util.TreeSet<>(
            java.util.Comparator.comparingInt(StageBand::ordinal));
        attemptedBands.addAll(planned.descriptor.permittedBands());
        ProgramSamplerLayoutFingerprint layoutFingerprint = new ProgramSamplerLayoutFingerprint(
            "pending");
        if (validation instanceof SamplerLayoutValidation.ConflictingTypes conflicting) {
            ProgramSamplerLayout.Shader failed = new ProgramSamplerLayout.Shader(
                new ProgramSamplerLayoutFingerprint("failed"),
                policy.fingerprint(),
                planned.descriptor.stage(),
                attemptedBands,
                planned.samplers,
                conflicting);
            planned.samplerLayout = failed;
            planned.failure = samplerFailure(slot, failed, "SAMPLER_UNIT_TYPE_CONFLICT");
            return;
        }
        if (validation instanceof SamplerLayoutValidation.Unsupported unsupported) {
            ProgramSamplerLayout.Shader failed = new ProgramSamplerLayout.Shader(
                new ProgramSamplerLayoutFingerprint("failed"),
                policy.fingerprint(),
                planned.descriptor.stage(),
                attemptedBands,
                planned.samplers,
                unsupported);
            planned.samplerLayout = failed;
            planned.failure = samplerFailure(slot, failed, "SAMPLER_LAYOUT_UNSUPPORTED");
            return;
        }
        List<SamplerUnitAssignment> assignments;
        try {
            assignments = policy.initializationAssignments(new ProgramSamplerLayout.Shader(
                layoutFingerprint, policy.fingerprint(), planned.descriptor.stage(),
                attemptedBands, planned.samplers, validation));
            validateAssignments(assignments, planned.samplers, policy);
        } catch (RuntimeException e) {
            policyFailuresOut.add(registryPolicyFailure(e));
            return;
        }
        planned.assignments = List.copyOf(assignments);

        // Capability gates: routing and declared attributes (§4.9).
        ProgramStateBundle bundle = adaptState(key, states, reqs);
        String routingRejection = routingFailure(bundle.drawRouting(), capabilities);
        if (routingRejection != null) {
            planned.failure = failure(slot, ProgramBuildStage.CAPABILITY, Optional.empty(),
                "", List.of(), routingRejection);
            return;
        }
        planned.stateBundle = bundle;
        for (ExtendedAttribute attribute : planned.stateBundle.attributes()) {
            if (capabilities.maxVertexAttribs() < attribute.requiredMaxVertexAttribs()) {
                planned.failure = failure(slot, ProgramBuildStage.CAPABILITY, Optional.empty(),
                    "", List.of(), "ATTRIBUTE_CAPABILITY:" + attribute.name());
                return;
            }
        }
        planned.disposition = ProgramOwnBuildDisposition.SUCCEEDED;
        planned.samplerLayout = new ProgramSamplerLayout.Shader(
            new ProgramSamplerLayoutFingerprint("pending"),
            policy.fingerprint(),
            planned.descriptor.stage(),
            attemptedBands,
            planned.samplers,
            validation);
    }

    static ProgramStateBundle adaptState(
            ProgramKey key,
            EvaluatedProgramStates states,
            Optional<ProgramRequirements> reqs) {
        Optional<EvaluatedProgramState> evaluated = states.programs().stream()
            .filter(state -> state.key().equals(key))
            .findFirst();
        DrawRouting routing = reqs.map(ProgramRequirements::routing)
            .map(Planner::adaptRouting)
            .orElse(new DrawRouting.AllUsedBuffers(BufferDomain.COLORTEX));
        Set<ColorAttachmentKey> mipmaps = reqs.map(ProgramRequirements::mipmappedAfterPass)
            .orElse(Set.of());
        Set<BufferRef> compositeMipmaps = new LinkedHashSet<>();
        for (ColorAttachmentKey attachment : mipmaps) {
            compositeMipmaps.add(new BufferRef(BufferDomain.COLORTEX, attachment.colortexIndex()));
        }
        int instanceCount = reqs.map(ProgramRequirements::instanceCount).orElse(1);
        if (instanceCount < 1) {
            instanceCount = 1;
        }
        Set<ExtendedAttribute> attributes = new LinkedHashSet<>();
        reqs.ifPresent(r -> {
            for (VertexAttribute attribute : r.vertices().attributes()) {
                attributes.add(ExtendedAttribute.of(attribute));
            }
        });
        Map<BufferRef, Boolean> flips = new LinkedHashMap<>();
        // Most programs declare no flip blocks, so the explicit-flip map has no entry
        // for them; absence is the default, never a failure (§4.7 "never throws").
        states.explicitFlips().getOrDefault(key, Map.of()).forEach((bufferKey, override) ->
            flips.put(
                new BufferRef(BufferDomain.COLORTEX, bufferKey.attachment().colortexIndex()),
                override == FlipOverride.TRUE));
        return new ProgramStateBundle(
            routing,
            compositeMipmaps,
            instanceCount,
            attributes,
            evaluated.map(EvaluatedProgramState::alphaTest).orElse(Optional.empty()),
            evaluated.map(EvaluatedProgramState::blend).orElse(Optional.empty()),
            evaluated.map(EvaluatedProgramState::scale).orElse(Optional.empty()),
            flips,
            reqs.flatMap(ProgramRequirements::legacyGeometry),
            GeometryInputRequirement.NONE);
    }

    /** Lossless P3 routing adaptation with the §4.9 deterministic validation rules. */
    static DrawRouting adaptRouting(com.schmaloogium.engine.config.DrawRouting routing) {
        if (routing instanceof com.schmaloogium.engine.config.DrawRouting.AllUsed) {
            return new DrawRouting.AllUsedBuffers(BufferDomain.COLORTEX);
        }
        List<DrawRoutingSlot> slots = new ArrayList<>();
        for (com.schmaloogium.engine.config.DrawSlot slot
                : ((com.schmaloogium.engine.config.DrawRouting.Explicit) routing).slots()) {
            if (slot instanceof com.schmaloogium.engine.config.DrawSlot.Attachment attachment) {
                slots.add(new DrawRoutingSlot.Attachment(new BufferRef(
                    BufferDomain.COLORTEX, attachment.target().colortexIndex())));
            } else {
                slots.add(new DrawRoutingSlot.None());
            }
        }
        return new DrawRouting.Explicit(slots);
    }

    /** §4.9 routing validation; returns the failure diagnostic id or null when valid. */
    static String routingFailure(DrawRouting routing, GLCapabilityProfile capabilities) {
        if (!(routing instanceof DrawRouting.Explicit explicit)) {
            return null;
        }
        Set<BufferRef> seen = new LinkedHashSet<>();
        int attachments = 0;
        for (DrawRoutingSlot slot : explicit.slots()) {
            if (slot instanceof DrawRoutingSlot.Attachment attachment) {
                attachments++;
                if (!seen.add(attachment.buffer())) {
                    return "ROUTE_DUPLICATE_ATTACHMENT";
                }
            }
        }
        if (explicit.slots().size() > capabilities.maxDrawBuffers()) {
            return "ROUTE_OVERFLOW_DRAW_BUFFERS";
        }
        if (attachments > capabilities.maxColorAttachments()) {
            return "ROUTE_OVERFLOW_COLOR_ATTACHMENTS";
        }
        return null;
    }

    static GeometryInputRequirement mapInput(GeometryInputPrimitive input) {
        return switch (input) {
            case POINTS -> GeometryInputRequirement.POINTS;
            case LINES -> GeometryInputRequirement.LINES;
            case LINES_ADJACENCY -> GeometryInputRequirement.LINES_ADJACENCY;
            case TRIANGLES -> GeometryInputRequirement.TRIANGLES;
            case TRIANGLES_ADJACENCY -> GeometryInputRequirement.TRIANGLES_ADJACENCY;
        };
    }

    private static void validateAssignments(
            List<SamplerUnitAssignment> assignments,
            List<ProgramSamplerDeclaration> declared,
            FixedSamplerLayoutPolicy policy) {
        Set<String> expected = new LinkedHashSet<>();
        for (ProgramSamplerDeclaration sampler : declared) {
            expected.add(sampler.exactName());
        }
        Set<String> seen = new LinkedHashSet<>();
        String previous = null;
        for (SamplerUnitAssignment assignment : assignments) {
            if (assignment.unit() < 0 || assignment.unit() > 15) {
                throw new IllegalStateException("assignment unit outside 0..15");
            }
            if (!expected.contains(assignment.exactName())) {
                throw new IllegalStateException("assignment for undeclared sampler");
            }
            if (!seen.add(assignment.exactName())) {
                throw new IllegalStateException("duplicate assignment");
            }
            if (previous != null
                && compareUnsignedUtf8(assignment.exactName(), previous) < 0) {
                throw new IllegalStateException("assignments not sorted by exact name");
            }
            previous = assignment.exactName();
        }
        if (seen.size() != expected.size()) {
            throw new IllegalStateException("incomplete assignment list");
        }
    }

    private static int compareUnsignedUtf8(String a, String b) {
        byte[] left = a.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] right = b.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        int length = Math.min(left.length, right.length);
        for (int i = 0; i < length; i++) {
            int comparison = Integer.compare(left[i] & 0xFF, right[i] & 0xFF);
            if (comparison != 0) {
                return comparison;
            }
        }
        return Integer.compare(left.length, right.length);
    }

    static ProgramBuildFailure failure(
            ProgramSlotId slot,
            ProgramBuildStage stage,
            Optional<ShaderSourceStage> shaderStage,
            String sanitizedLog,
            List<String> diagnosticIds,
            String diagnosticId) {
        return new ProgramBuildFailure(slot, Optional.empty(), stage, shaderStage,
            sanitizedLog, diagnosticIds, List.of(), "FAILED", diagnosticId, Optional.empty());
    }

    private static ProgramBuildFailure samplerFailure(
            ProgramSlotId slot,
            ProgramSamplerLayout evidence,
            String diagnosticId) {
        return new ProgramBuildFailure(slot, Optional.empty(), ProgramBuildStage.SAMPLER_LAYOUT,
            Optional.empty(), "", List.of(), List.of(), "FAILED", diagnosticId,
            Optional.of(evidence));
    }

    private static ProgramBuildFailure registryPolicyFailure(RuntimeException e) {
        // Returned inside the policy-failure list; converted by the assembler into a
        // RegistryBuildFailure with kind INVALID_SAMPLER_POLICY.
        return new ProgramBuildFailure(new ProgramSlotId("registry"),
            Optional.empty(), ProgramBuildStage.UNEXPECTED_BACKEND, Optional.empty(),
            String.valueOf(e.getMessage()), List.of(), List.of(), "INVALID_SAMPLER_POLICY",
            "INVALID_SAMPLER_POLICY_CALLBACK", Optional.empty());
    }
}
