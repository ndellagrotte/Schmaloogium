// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BufferBuildRequest;
import com.schmaloogium.engine.buffers.BufferBuildResult;
import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.BufferPlanRequest;
import com.schmaloogium.engine.buffers.BufferPlanResult;
import com.schmaloogium.engine.buffers.BufferResourceProjection;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.MainDepthPreparation;
import com.schmaloogium.engine.buffers.MainDepthSnapshot;
import com.schmaloogium.engine.buffers.PhysicalSide;
import com.schmaloogium.engine.buffers.RealizedColorAllocation;
import com.schmaloogium.engine.buffers.ResourceEvidenceStage;
import com.schmaloogium.engine.buffers.ShadowResourceProjection;
import com.schmaloogium.engine.buffers.ShadowTextureResource;
import com.schmaloogium.engine.config.ColorAttachmentFormat;
import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.FramebufferDrawSlot;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.PixelLayout;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureBorderColor;
import com.schmaloogium.engine.gl.TextureCompareFunction;
import com.schmaloogium.engine.gl.TextureCompareMode;
import com.schmaloogium.engine.gl.TextureExtent;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureParameters;
import com.schmaloogium.engine.gl.TextureSpec;
import com.schmaloogium.engine.gl.TextureSwizzle;
import com.schmaloogium.engine.gl.TextureWrap;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.DrawRoutingSlot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The candidate builder (PHASE_5_DOC §4.3/§4.7/§4.8): reruns the pure planning operation
 * against the prepared main-depth snapshot, realizes every planned object through the
 * ownership ledger, and hands the immutable candidate to the publisher. The shadow estate
 * (§4.10) is realized per attempt: the bounded neutral cache first (fail-closed), then the
 * contained real-estate attempt whose failure disables the shadow feature with neutral
 * bindings supplied while the main pipeline continues.
 */
public final class CandidateBuilder {

    private CandidateBuilder() {
    }

    static BufferBuildResult build(BufferBuildRequest request) {
        Objects.requireNonNull(request, "request");
        BufferPlanResult planned = BufferPlanner.plan(new BufferPlanRequest(
            request.configuration(), request.registry(), request.registryFingerprint(),
            request.capabilities(), request.runtime()));
        if (planned instanceof BufferPlanResult.Invalid invalid) {
            return new BufferBuildResult.ShadersOff(invalid.failure());
        }
        PlanningArtifacts artifacts = ((BufferPlanResult.Valid) planned).plan().artifacts();

        // §4.8: prepare at this no-draw safe point; only a Ready snapshot at the exact
        // planned extent may be attached.
        MainDepthPreparation preparation =
            request.mainDepth().prepare(artifacts.sizing().mainExtent());
        if (preparation instanceof MainDepthPreparation.Pending pending) {
            return new BufferBuildResult.AwaitingMainDepth(pending.expectedVersion());
        }
        if (preparation instanceof MainDepthPreparation.Failed failed) {
            return new BufferBuildResult.ShadersOff(failure(BufferFailureCode.MAIN_DEPTH_UNAVAILABLE,
                "schmaloogium.buffers.error.build.main-depth", failed.diagnosticId()));
        }
        MainDepthSnapshot.Available depth = ((MainDepthPreparation.Ready) preparation).snapshot();
        if (!depth.extent().equals(artifacts.sizing().mainExtent())) {
            request.diagnostics().report(BufferDiagnostics.backendFailure(
                "schmaloogium.buffers.error.build.extent-mismatch",
                "prepared " + depth.extent() + " planned " + artifacts.sizing().mainExtent()));
            return new BufferBuildResult.AwaitingMainDepth(depth.version());
        }

        GLDevice device = request.device();
        Ledger ledger = new Ledger(device);
        Attempt outcome = attempt(device, artifacts, depth, ledger, false);
        if (!outcome.complete()) {
            ledger.walkReverse();
            request.diagnostics().report(BufferDiagnostics.rgbaFallbackWarning(
                requestedFormatNames(artifacts)));
            ledger.reset();
            outcome = attempt(device, artifacts, depth, ledger, true);
            if (!outcome.complete()) {
                ledger.walkReverse();
                request.diagnostics().report(BufferDiagnostics.fallbackFailed(
                    String.valueOf(outcome.failure())));
                return new BufferBuildResult.ShadersOff(outcome.failure());
            }
        }

        BufferResourceSnapshot.Available realized = realizedEvidence(artifacts,
            outcome.fallback());
        EstateCore core = new EstateCore(device, request.diagnostics(),
            request.registryFingerprint(), artifacts, realized, request.mainDepth(), depth);
        core.fullClearRequired = true;
        // Color pairs install in deterministic creation order; before any flip the
        // committed main is side A (§4.4.1).
        TextureExtent extent = extent(artifacts);
        for (int row = 0; row < outcome.sidesA().size(); row++) {
            PlanningArtifacts.ColorPlan color = artifacts.colors().get(row);
            com.schmaloogium.engine.gl.ColorInternalFormat allocated = outcome.fallback()
                ? FormatTable.fallbackFormat()
                : glFormat(color);
            core.colorPairs.add(new EstateCore.ColorPair(color.buffer(),
                outcome.sidesA().get(row), outcome.sidesB().get(row), PhysicalSide.A,
                !FormatTable.isIntegerStorage(allocated), mipLevelsFor(extent),
                baseMinFilter(FormatTable.row(allocated).baseFilter())));
        }
        // Copy destinations: DEPTH index 1..depthCount-1 map to depthtex1/depthtex2 (§4.9).
        for (int index = 1; index < outcome.copyTargets().size() + 1; index++) {
            LogicalBuffer logical = new LogicalBuffer(BufferDomain.DEPTH,
                new BufferIndex(index));
            FramebufferHandle destination = device.framebuffers().create(
                "pass:copy-destination:depthtex" + index);
            ledger.add(destination);
            device.framebuffers().attachDepth(destination, outcome.copyTargets().get(index - 1));
            device.framebuffers().drawBuffers(destination, List.of());
            if (device.framebuffers().check(destination) != FramebufferStatus.COMPLETE) {
                ledger.walkReverse();
                request.diagnostics().report(BufferDiagnostics.backendFailure(
                    "schmaloogium.buffers.error.build.copy-destination-fbo", "incomplete"));
                return new BufferBuildResult.ShadersOff(failure(
                    BufferFailureCode.FRAMEBUFFER_INCOMPLETE,
                    "schmaloogium.buffers.error.build.copy-destination-fbo",
                    "schmaloogium.buffers.error.build.copy-destination-fbo"));
            }
            core.copyDestinations.add(core.new DepthDestination(logical,
                outcome.copyTargets().get(index - 1), destination));
        }
        core.passFbos.putAll(outcome.passFbos());

        // §4.10 shadow estate installation: the neutral cache always survives; the real
        // estate (depths, color pairs, sfb) only when its attempt contained no failure.
        ShadowEstate shadow = outcome.shadow();
        if (shadow.planned()) {
            core.shadowNeutral = shadow.neutrals();
            if (shadow.operable()) {
                core.shadowDepths.addAll(shadow.depths());
                for (int index = 0; index < shadow.colorSidesA().size(); index++) {
                core.shadowColorPairs.add(new EstateCore.ShadowColorPair(
                    shadowColorLogical(index),
                    shadow.colorSidesA().get(index), shadow.colorSidesB().get(index)));
                }
                core.shadowFbo = shadow.sfb();
                core.shadowView = new ShadowEstateImpl(core);
            } else {
                core.shadowFailure = shadow.failure();
                request.diagnostics().report(BufferDiagnostics.shadowBackendFailure(
                    "schmaloogium.buffers.error.shadow.estate-failed",
                    shadow.failure().diagnosticId()));
            }
        }

        CandidateImpl candidate = new CandidateImpl(core, ledger);
        request.diagnostics().report(BufferDiagnostics.candidateReady(
            artifacts.colors().size(), artifacts.depthTextureCount(),
            artifacts.sizing().mainExtent().width(), artifacts.sizing().mainExtent().height()));
        return new BufferBuildResult.Ready(
            com.schmaloogium.engine.buffers.BufferEstateCandidate.wrap(candidate));
    }

    static LogicalBuffer shadowColorLogical(int index) {
        return new LogicalBuffer(BufferDomain.SHADOWCOLOR, new BufferIndex(index));
    }

    // ------------------------------------------------------------------ attempts

    private record Attempt(boolean complete, boolean fallback, BufferFailure failure,
            List<TextureHandle> sidesA, List<TextureHandle> sidesB,
            List<TextureHandle> copyTargets, Map<String, FramebufferHandle> passFbos,
            ShadowEstate shadow) {

        static Attempt failed(BufferFailure failure) {
            return new Attempt(false, false, failure, List.of(), List.of(), List.of(),
                Map.of(), ShadowEstate.none());
        }
    }

    private static Attempt attempt(GLDevice device, PlanningArtifacts artifacts,
            MainDepthSnapshot.Available depth, Ledger ledger, boolean fallback) {
        // ALLOCATING_TEXTURES: every color side, then the depth-copy targets.
        List<PlanningArtifacts.ColorPlan> colors = artifacts.colors();
        TextureExtent extent = extent(artifacts);
        List<TextureHandle> sidesA = new ArrayList<>();
        List<TextureHandle> sidesB = new ArrayList<>();
        for (PlanningArtifacts.ColorPlan color : colors) {
            com.schmaloogium.engine.gl.ColorInternalFormat format = fallback
                ? FormatTable.fallbackFormat()
                : glFormat(color);
            for (PhysicalSide side : PhysicalSide.values()) {
                TextureHandle texture = allocateColor(device, ledger,
                    "colortex" + color.buffer().index().value() + "/" + side, format, extent);
                if (texture == null) {
                    return Attempt.failed(failure(BufferFailureCode.TEXTURE_ALLOCATION,
                        "schmaloogium.buffers.error.build.texture",
                        "schmaloogium.buffers.error.build.texture"));
                }
                (side == PhysicalSide.A ? sidesA : sidesB).add(texture);
            }
        }

        // Depth-copy targets: depthtex1 at depth count >= 2, depthtex2 at 3 (§4.9), always
        // allocated in the borrowed main-depth format.
        List<TextureHandle> copyTargets = new ArrayList<>();
        for (int index = 1; index < artifacts.depthTextureCount(); index++) {
            TextureHandle texture = allocateDepthCopy(device, ledger, depth, extent, index);
            if (texture == null) {
                return Attempt.failed(failure(BufferFailureCode.TEXTURE_ALLOCATION,
                    "schmaloogium.buffers.error.build.copy-target",
                    "schmaloogium.buffers.error.build.copy-target"));
            }
            copyTargets.add(texture);
        }

        // §4.10 shadow estate: neutral cache first (fail-closed on neutral failure), then
        // the contained real-estate attempt whose failure disables the shadow feature.
        ShadowEstate shadow;
        try {
            shadow = allocateShadow(device, artifacts, depth, ledger);
        } catch (RuntimeException neutralFailure) {
            return Attempt.failed(failure(BufferFailureCode.TEXTURE_ALLOCATION,
                "schmaloogium.buffers.error.build.shadow-neutral",
                "schmaloogium.buffers.error.build.shadow-neutral"));
        }

        // ATTACHING_FRAMEBUFFERS + CHECKING: one pass FBO per planned route.
        Map<String, FramebufferHandle> passFbos = new LinkedHashMap<>();
        for (PlanningArtifacts.PlannedRoute route : artifacts.routes().values()) {
            FramebufferHandle created = createPassFbo(device, ledger, route, sidesA, sidesB,
                colors, depth);
            if (created == null) {
                return Attempt.failed(failure(BufferFailureCode.FRAMEBUFFER_INCOMPLETE,
                    "schmaloogium.buffers.error.build.framebuffer",
                    "schmaloogium.buffers.error.build.framebuffer"));
            }
            passFbos.put(passKey(route), created);
        }
        return new Attempt(true, fallback, null, sidesA, sidesB, copyTargets, passFbos,
            shadow);
    }

    static String passKey(PlanningArtifacts.PlannedRoute route) {
        return "pass:" + route.slot() + ":" + EstateCore.routeTag(route.positional());
    }

    private static com.schmaloogium.engine.gl.ColorInternalFormat glFormat(
            PlanningArtifacts.ColorPlan color) {
        if (color.requestedFormat() instanceof ColorAttachmentFormat.Explicit explicit) {
            return com.schmaloogium.engine.gl.ColorInternalFormat.valueOf(
                explicit.format().name());
        }
        return FormatTable.fallbackFormat(); // DefaultRgba allocates RGBA_COMPAT (§4.2)
    }

    private static String requestedFormatNames(PlanningArtifacts artifacts) {
        List<String> names = new ArrayList<>();
        for (PlanningArtifacts.ColorPlan color : artifacts.colors()) {
            names.add(color.requestedFormat() instanceof ColorAttachmentFormat.Explicit explicit
                ? explicit.format().name()
                : "DEFAULT_RGBA");
        }
        return String.join(",", names);
    }

    private static TextureExtent extent(PlanningArtifacts artifacts) {
        return new TextureExtent(artifacts.sizing().mainExtent().width(),
            artifacts.sizing().mainExtent().height(), 1);
    }

    private static TextureHandle allocateColor(GLDevice device, Ledger ledger, String label,
            com.schmaloogium.engine.gl.ColorInternalFormat format, TextureExtent extent) {
        FormatTable.FormatRow row = FormatTable.row(format);
        int mipLevels = mipLevelsFor(extent);
        ledger.beginAllocation();
        try {
            TextureHandle texture = device.textures().create("schmaloogium.buffers/" + label);
            ledger.add(texture);
            device.textures().allocate(texture, new TextureSpec.ColorTextureSpec(
                TextureAllocationTarget.TEXTURE_2D, format, row.allocationLayout(), extent,
                mipLevels));
            device.textures().setParameters(texture,
                baseParameters(baseMinFilter(row.baseFilter()), mipLevels));
            ledger.commitAllocation();
            return texture;
        } catch (RuntimeException allocationFailure) {
            ledger.dropAllocation();
            return null;
        }
    }

    static TextureMinFilter baseMinFilter(TextureMagFilter magFilter) {
        return magFilter == TextureMagFilter.NEAREST
            ? TextureMinFilter.NEAREST
            : TextureMinFilter.LINEAR;
    }

    private static TextureHandle allocateDepthCopy(GLDevice device, Ledger ledger,
            MainDepthSnapshot.Available depth, TextureExtent extent, int index) {
        ledger.beginAllocation();
        try {
            TextureHandle texture = device.textures().create(
                "schmaloogium.buffers/depthtex" + index);
            ledger.add(texture);
            device.textures().allocate(texture, new TextureSpec.DepthTextureSpec(
                TextureAllocationTarget.TEXTURE_2D, depth.format(),
                new PixelLayout.Depth(transferLayout(depth.format())), extent, 1));
            device.textures().setParameters(texture,
                baseParameters(TextureMinFilter.NEAREST, 1));
            ledger.commitAllocation();
            return texture;
        } catch (RuntimeException allocationFailure) {
            ledger.dropAllocation();
            return null;
        }
    }

    private static com.schmaloogium.engine.gl.DepthTransferLayout transferLayout(
            DepthAttachmentFormat format) {
        return format == DepthAttachmentFormat.DEPTH24_STENCIL8
            ? com.schmaloogium.engine.gl.DepthTransferLayout.DEPTH_STENCIL_UNSIGNED_INT_24_8
            : com.schmaloogium.engine.gl.DepthTransferLayout.DEPTH_COMPONENT_FLOAT;
    }

    static TextureParameters baseParameters(TextureMinFilter minFilter, int mipLevels) {
        return new TextureParameters(minFilter, TextureMagFilter.NEAREST,
            TextureWrap.CLAMP_TO_EDGE, TextureWrap.CLAMP_TO_EDGE, TextureWrap.CLAMP_TO_EDGE,
            TextureCompareMode.NONE, TextureCompareFunction.LEQUAL,
            new TextureBorderColor(0.0f, 0.0f, 0.0f, 0.0f), 0.0f,
            mipLevels > 1 ? mipLevels - 1 : 0.0f, 0.0f, 1.0f, 0, mipLevels - 1,
            TextureSwizzle.IDENTITY);
    }

    static int mipLevelsFor(TextureExtent extent) {
        int largest = Math.max(extent.width(), extent.height());
        return 32 - Integer.numberOfLeadingZeros(Math.max(1, largest));
    }

    // ------------------------------------------------------------------ shadow estate

    /**
     * Base (non-mipmap) parameters of one shadow depth texture: §4.10's legacy R,R,R,1
     * swizzle, per-depth hardware compare when requested, NEAREST/LINEAR per the pack.
     */
    static TextureParameters shadowDepthParameters(ShadowTextureResource resource,
            int mipLevels) {
        return new TextureParameters(
            resource.nearest() ? TextureMinFilter.NEAREST : TextureMinFilter.LINEAR,
            resource.nearest() ? TextureMagFilter.NEAREST : TextureMagFilter.LINEAR,
            TextureWrap.CLAMP_TO_EDGE, TextureWrap.CLAMP_TO_EDGE, TextureWrap.CLAMP_TO_EDGE,
            resource.hardwareFiltering()
                ? TextureCompareMode.REF_TO_TEXTURE : TextureCompareMode.NONE,
            TextureCompareFunction.LEQUAL,
            new TextureBorderColor(0.0f, 0.0f, 0.0f, 0.0f), 0.0f,
            mipLevels > 1 ? mipLevels - 1 : 0.0f, 0.0f, 1.0f, 0, mipLevels - 1,
            TextureSwizzle.LEGACY_DEPTH_LUMINANCE);
    }

    /** Base (non-mipmap) parameters of one shadow color texture. */
    static TextureParameters shadowColorParameters(ShadowTextureResource resource,
            int mipLevels) {
        return new TextureParameters(
            resource.nearest() ? TextureMinFilter.NEAREST : TextureMinFilter.LINEAR,
            resource.nearest() ? TextureMagFilter.NEAREST : TextureMagFilter.LINEAR,
            TextureWrap.CLAMP_TO_EDGE, TextureWrap.CLAMP_TO_EDGE, TextureWrap.CLAMP_TO_EDGE,
            TextureCompareMode.NONE, TextureCompareFunction.LEQUAL,
            new TextureBorderColor(0.0f, 0.0f, 0.0f, 0.0f), 0.0f,
            mipLevels > 1 ? mipLevels - 1 : 0.0f, 0.0f, 1.0f, 0, mipLevels - 1,
            TextureSwizzle.IDENTITY);
    }

    /**
     * One attempt's shadow estate (§4.10). The neutral cache is allocated first and
     * retained through any real-estate failure; real shadowtex/shadowcolor objects and the
     * sfb are one ledger allocation unit so their failure is contained: the shadow feature
     * disables with neutral bindings supplied (rung 2a) while the main pipeline continues.
     * Throws only on neutral initialization failure — no compatible safe fallback exists,
     * so the whole candidate fails closed.
     */
    record ShadowEstate(ShadowNeutralCache neutrals, List<TextureHandle> depths,
            List<TextureHandle> colorSidesA, List<TextureHandle> colorSidesB,
            FramebufferHandle sfb, BufferFailure failure) {

        static ShadowEstate none() {
            return new ShadowEstate(null, List.of(), List.of(), List.of(), null, null);
        }

        boolean planned() {
            return neutrals != null;
        }

        boolean operable() {
            return planned() && failure == null;
        }
    }

    static ShadowEstate allocateShadow(GLDevice device, PlanningArtifacts artifacts,
            MainDepthSnapshot.Available depth, Ledger ledger) {
        ShadowResourceProjection shadow = artifacts.plannedProjection().shadow();
        if (shadow.depthTextures() == 0 && shadow.colorTextures() == 0) {
            return ShadowEstate.none();
        }
        Extent2i extent2i = artifacts.sizing().shadowExtent().orElseThrow(
            () -> new IllegalStateException("planned shadow estate without a shadow extent"));
        TextureExtent extent = new TextureExtent(extent2i.width(), extent2i.height(), 1);
        int mipLevels = mipLevelsFor(extent);

        // Neutral cache first, keyed by each required unit's complete effective sampling
        // policy with extent-dependent final-mip values normalized to zero (§4.10).
        ShadowNeutralCache neutrals = new ShadowNeutralCache(device, ledger::add);
        ledger.beginAllocation();
        try {
            for (int index = 0; index < shadow.depthTextures(); index++) {
                TextureParameters real = shadowDepthParameters(shadow.depth().get(index),
                    shadow.depth().get(index).mipmap() ? mipLevels : 1);
                neutrals.requireDepthUnit(depth.format(), transferLayout(depth.format()),
                    neutralParameters(real));
            }
            for (int index = 0; index < shadow.colorTextures(); index++) {
                TextureParameters real = shadowColorParameters(shadow.color().get(index),
                    shadow.color().get(index).mipmap() ? mipLevels : 1);
                neutrals.requireColorUnit(neutralParameters(real));
            }
            ledger.commitAllocation();
        } catch (RuntimeException neutralFailure) {
            ledger.dropAllocation();
            throw neutralFailure; // no compatible safe fallback: fail the candidate
        }

        // Real estate: shadowtex0 (plus shadowtex1 at depth count >= 2), shadowcolor
        // pairs, then the sfb with shadowtex0 as the real depth attachment.
        List<TextureHandle> depths = new ArrayList<>();
        List<TextureHandle> sidesA = new ArrayList<>();
        List<TextureHandle> sidesB = new ArrayList<>();
        FramebufferHandle sfb;
        String stage = "texture";
        ledger.beginAllocation();
        try {
            for (int index = 0; index < shadow.depthTextures(); index++) {
                depths.add(allocateShadowDepth(device, ledger, depth, extent, index,
                    shadow.depth().get(index), shadow.depth().get(index).mipmap()
                        ? mipLevels : 1));
            }
            for (int index = 0; index < shadow.colorTextures(); index++) {
                sidesA.add(allocateShadowColor(device, ledger, extent, index, 'a',
                    shadow.color().get(index), shadow.color().get(index).mipmap()
                        ? mipLevels : 1));
                sidesB.add(allocateShadowColor(device, ledger, extent, index, 'b',
                    shadow.color().get(index), shadow.color().get(index).mipmap()
                        ? mipLevels : 1));
            }
            stage = "sfb";
            sfb = device.framebuffers().create("shadow:sfb");
            ledger.add(sfb);
            if (depth.format() == DepthAttachmentFormat.DEPTH24_STENCIL8) {
                device.framebuffers().attachDepthStencil(sfb, depths.get(0));
            } else {
                device.framebuffers().attachDepth(sfb, depths.get(0));
            }
            for (int index = 0; index < sidesA.size(); index++) {
                device.framebuffers().attachColor(sfb, index, sidesA.get(index));
            }
            device.framebuffers().drawBuffers(sfb, denseSlots(sidesA.size()));
            if (device.framebuffers().check(sfb) != FramebufferStatus.COMPLETE) {
                throw new IllegalStateException("shadow framebuffer incomplete");
            }
            ledger.commitAllocation();
        } catch (RuntimeException realFailure) {
            ledger.dropAllocation(); // contained: real handles go, neutral cache stays
            return new ShadowEstate(neutrals, List.of(), List.of(), List.of(), null,
                failure(stage.equals("sfb")
                        ? BufferFailureCode.FRAMEBUFFER_INCOMPLETE
                        : BufferFailureCode.TEXTURE_ALLOCATION,
                    "schmaloogium.buffers.error.shadow.estate-failed",
                    "schmaloogium.buffers.error.shadow.estate-failed"));
        }
        return new ShadowEstate(neutrals, depths, sidesA, sidesB, sfb, null);
    }

    /** Effective 1x1 neutral parameters: extent-dependent final-mip values to zero. */
    private static TextureParameters neutralParameters(TextureParameters real) {
        return new TextureParameters(real.minFilter(), real.magFilter(), real.wrapS(),
            real.wrapT(), real.wrapR(), real.compareMode(), real.compareFunction(),
            real.borderColor(), real.minLod(), 0.0f, real.lodBias(), real.maxAnisotropy(),
            real.baseLevel(), 0, real.swizzle());
    }

    /** One shadow depth texture in the borrowed main-depth format (§4.10); bracketed by
     *  the caller's real-estate allocation unit. */
    private static TextureHandle allocateShadowDepth(GLDevice device, Ledger ledger,
            MainDepthSnapshot.Available depth, TextureExtent extent, int index,
            ShadowTextureResource resource, int mipLevels) {
        TextureHandle texture = device.textures().create(
            "schmaloogium.buffers/shadowtex" + index);
        ledger.add(texture);
        device.textures().allocate(texture, new TextureSpec.DepthTextureSpec(
            TextureAllocationTarget.TEXTURE_2D, depth.format(),
            new PixelLayout.Depth(transferLayout(depth.format())), extent, mipLevels));
        device.textures().setParameters(texture, shadowDepthParameters(resource, mipLevels));
        return texture;
    }

    /** One shadow color pair side (§4.10, generic pair state; RGBA_COMPAT storage);
     *  bracketed by the caller's real-estate allocation unit. */
    private static TextureHandle allocateShadowColor(GLDevice device, Ledger ledger,
            TextureExtent extent, int index, char side, ShadowTextureResource resource,
            int mipLevels) {
        TextureHandle texture = device.textures().create(
            "schmaloogium.buffers/shadowcolor" + index + "/" + side);
        ledger.add(texture);
        device.textures().allocate(texture, new TextureSpec.ColorTextureSpec(
            TextureAllocationTarget.TEXTURE_2D, FormatTable.fallbackFormat(),
            FormatTable.row(FormatTable.fallbackFormat()).allocationLayout(),
            extent, mipLevels));
        device.textures().setParameters(texture, shadowColorParameters(resource, mipLevels));
        return texture;
    }

    private static List<FramebufferDrawSlot> denseSlots(int count) {
        List<FramebufferDrawSlot> slots = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            slots.add(new FramebufferDrawSlot.Attachment(index));
        }
        return slots;
    }

    /**
     * One pass FBO per planned route (§4.5): the 0N2 rule — for each Attachment slot in
     * order allocate the next dense physical attachment index and append
     * {@code Attachment(physical)}; None appends {@code None} and attaches nothing. The
     * borrowed main-depth attaches depth-only or combined per its format; completeness is
     * checked here at candidate build.
     */
    private static FramebufferHandle createPassFbo(GLDevice device, Ledger ledger,
            PlanningArtifacts.PlannedRoute route, List<TextureHandle> sidesA,
            List<TextureHandle> sidesB, List<PlanningArtifacts.ColorPlan> colors,
            MainDepthSnapshot.Available depth) {
        FramebufferHandle fbo = device.framebuffers().create(passKey(route));
        ledger.add(fbo);
        int physical = 0;
        List<FramebufferDrawSlot> slots = new ArrayList<>();
        for (DrawRoutingSlot slot : route.positional()) {
            if (slot instanceof DrawRoutingSlot.Attachment attachment) {
                LogicalBuffer logical = new LogicalBuffer(attachment.buffer().domain(),
                    new BufferIndex(attachment.buffer().index()));
                int row = colorRowOf(logical, colors);
                device.framebuffers().attachColor(fbo, physical, sidesA.get(row));
                slots.add(new FramebufferDrawSlot.Attachment(physical));
                physical++;
            } else {
                slots.add(new FramebufferDrawSlot.None());
            }
        }
        BorrowedDepthAttachmentHandle borrowed = depth.texture();
        if (depth.format() == DepthAttachmentFormat.DEPTH24_STENCIL8) {
            device.framebuffers().attachDepthStencil(fbo, borrowed);
        } else {
            device.framebuffers().attachDepth(fbo, borrowed);
        }
        device.framebuffers().drawBuffers(fbo, List.copyOf(slots));
        if (device.framebuffers().check(fbo) != FramebufferStatus.COMPLETE) {
            return null;
        }
        return fbo;
    }

    private static int colorRowOf(LogicalBuffer logical,
            List<PlanningArtifacts.ColorPlan> colors) {
        for (int index = 0; index < colors.size(); index++) {
            if (colors.get(index).buffer().equals(logical)) {
                return index;
            }
        }
        throw new IllegalArgumentException("route references non-color buffer: " + logical);
    }

    /**
     * The canonical REALIZED evidence (§4.1.1): every color row gains its
     * RealizedColorAllocation — actual internal-format name and REQUESTED or RGBA_FALLBACK
     * origin (emitted for every row after whole-estate fallback, even if the request was
     * already plain RGBA).
     */
    static BufferResourceSnapshot.Available realizedEvidence(PlanningArtifacts artifacts,
            boolean fallback) {
        BufferResourceProjection planned = artifacts.plannedProjection();
        com.schmaloogium.engine.buffers.ColorAllocationOrigin origin = fallback
            ? com.schmaloogium.engine.buffers.ColorAllocationOrigin.RGBA_FALLBACK
            : com.schmaloogium.engine.buffers.ColorAllocationOrigin.REQUESTED;
        List<com.schmaloogium.engine.buffers.ColorBufferResource> realizedRows =
            new ArrayList<>();
        for (int index = 0; index < planned.colorBuffers().size(); index++) {
            PlanningArtifacts.ColorPlan color = artifacts.colors().get(index);
            com.schmaloogium.engine.buffers.ColorBufferResource row =
                planned.colorBuffers().get(index);
            String name = fallback
                ? FormatTable.fallbackFormat().name()
                : glFormat(color).name();
            realizedRows.add(new com.schmaloogium.engine.buffers.ColorBufferResource(
                row.requestedFormat(), Optional.of(new RealizedColorAllocation(name, origin)),
                row.clear(), row.clearPolicy()));
        }
        BufferResourceProjection realized = new BufferResourceProjection(
            ResourceEvidenceStage.REALIZED, realizedRows, planned.depthTextures(),
            planned.shadow(), planned.centerDepthSmoothEnabled(), planned.noiseResolution(),
            planned.vertexAttributes(), planned.instances(), planned.capabilityGate(),
            planned.capabilityShortfalls());
        return new BufferResourceSnapshot.Available(realized);
    }

    private static BufferFailure failure(BufferFailureCode code, String messageKey,
            String diagnosticId) {
        return new BufferFailure(code, messageKey, diagnosticId, List.of(), Optional.empty(),
            Optional.empty());
    }

    /**
     * The ownership ledger (§4.7): every acquired handle enters immediately; failure walks
     * entries in reverse; the in-flight allocation unit is dropped atomically.
     */
    static final class Ledger {
        private final GLDevice device;
        private final List<Object> owned = new ArrayList<>();
        private int pendingMark = -1;

        Ledger(GLDevice device) {
            this.device = device;
        }

        void beginAllocation() {
            pendingMark = owned.size();
        }

        void add(Object handle) {
            owned.add(handle);
        }

        void commitAllocation() {
            pendingMark = -1;
        }

        void dropAllocation() {
            if (pendingMark >= 0) {
                for (int index = owned.size() - 1; index >= pendingMark; index--) {
                    delete(owned.get(index));
                }
                owned.subList(pendingMark, owned.size()).clear();
                pendingMark = -1;
            }
        }

        void walkReverse() {
            for (int index = owned.size() - 1; index >= 0; index--) {
                delete(owned.get(index));
            }
            owned.clear();
        }

        void reset() {
            owned.clear();
            pendingMark = -1;
        }

        boolean isEmpty() {
            return owned.isEmpty();
        }

        private void delete(Object handle) {
            if (handle instanceof TextureHandle texture) {
                device.textures().delete(texture);
            } else if (handle instanceof FramebufferHandle fbo) {
                device.framebuffers().delete(fbo);
            }
        }
    }
}
