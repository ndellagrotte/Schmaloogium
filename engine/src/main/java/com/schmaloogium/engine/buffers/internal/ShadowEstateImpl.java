// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.ColorAttachment;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.ShadowAbortResult;
import com.schmaloogium.engine.buffers.ShadowBeginResult;
import com.schmaloogium.engine.buffers.ShadowCompletionResult;
import com.schmaloogium.engine.buffers.ShadowDepthCopyPoint;
import com.schmaloogium.engine.buffers.ShadowEstateView;
import com.schmaloogium.engine.buffers.ShadowMipmapOutcome;
import com.schmaloogium.engine.buffers.ShadowMipmapPolicy;
import com.schmaloogium.engine.buffers.ShadowMipmapResult;
import com.schmaloogium.engine.buffers.ShadowNeutralReason;
import com.schmaloogium.engine.buffers.ShadowNeutralizationResult;
import com.schmaloogium.engine.buffers.ShadowOperationResult;
import com.schmaloogium.engine.buffers.ShadowPassSnapshot;
import com.schmaloogium.engine.buffers.ShadowProtocolRejection;
import com.schmaloogium.engine.buffers.ShadowTextureResource;
import com.schmaloogium.engine.buffers.TextureBindingRejection;
import com.schmaloogium.engine.buffers.TextureBindingResult;
import com.schmaloogium.engine.buffers.TextureOverlayLease;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.gl.ColorClearValue;
import com.schmaloogium.engine.gl.FramebufferDrawSlot;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.FramebufferTarget;
import com.schmaloogium.engine.gl.GLError;
import com.schmaloogium.engine.gl.TextureExtent;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureParameters;
import com.schmaloogium.engine.gl.TextureRegion;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramBindingSelection;

/**
 * The live shadow-estate operation surface (PHASE_5_DOC §4.10): sole open pass token over
 * the sfb, the sixteen-row shadow physical bind, typed single-sided shadowcolor clears, the
 * translucent depth copy into shadowtex1, per-policy mipmap generation with fail-closed
 * degradation, and the atomic degrade-to-neutral transition onto the bounded neutral cache.
 * Every non-texture operation rejects before GL with the exact
 * {@link ShadowProtocolRejection}; backend failures leave the token open and flip state
 * unchanged for abort. Real shadow handles stay Phase-5-owned through neutralization and are
 * deleted only by the ordinary reverse estate teardown.
 */
public final class ShadowEstateImpl implements ShadowEstateView {

    /** Bounded memory of issued snapshots: consumed ones answer CLOSED, unknown FOREIGN. */
    private static final int ISSUED_HISTORY = 8;

    private final EstateCore core;
    private final TextureBinder textureBinder = new TextureBinder();
    private ShadowPassSnapshot openSnapshot;
    private final ArrayDeque<ShadowPassSnapshot> issued = new ArrayDeque<>();
    /** Bumped on completion/abort/neutralization; invalidates outstanding binding leases. */
    private long bindingEpoch;
    private boolean shadowFullClearRequired = true;
    private boolean shadowtex1Initialized;

    public ShadowEstateImpl(EstateCore core) {
        this.core = core;
    }

    @Override
    public long estateGeneration() {
        return core.generation;
    }

    // ------------------------------------------------------------------ pass protocol

    @Override
    public ShadowBeginResult beginPass(long frameId, PassDescriptor pass,
            ProgramBindingSelection selection) {
        core.checkRenderThread();
        Objects.requireNonNull(pass, "pass");
        Objects.requireNonNull(selection, "selection");
        if (!core.usable()) {
            return new ShadowBeginResult.Rejected(ShadowProtocolRejection.STALE_GENERATION);
        }
        if (openSnapshot != null) {
            return new ShadowBeginResult.Rejected(ShadowProtocolRejection.PASS_ALREADY_OPEN);
        }
        if (core.openFrameId == -1 || core.openFrameId != frameId) {
            return new ShadowBeginResult.Rejected(ShadowProtocolRejection.WRONG_FRAME_ID);
        }
        ShadowPassSnapshot snapshot = new ShadowPassSnapshot(core.generation,
            core.depthAttachmentEpoch, frameId, pass, selection, core.shadowFbo,
            frozenColorAttachments(), frozenReadableTextures(), flipAfterPass(pass));
        openSnapshot = snapshot;
        remember(snapshot);
        return new ShadowBeginResult.Acquired(snapshot);
    }

    /** §4.10: acquisition freezes all ordinary and shadow readable sides; no live consults. */
    private Map<LogicalBuffer, TextureHandle> frozenReadableTextures() {
        Map<LogicalBuffer, TextureHandle> readable = new LinkedHashMap<>();
        for (EstateCore.ColorPair pair : core.colorPairs) {
            readable.put(pair.logical, pair.readSide());
        }
        readable.put(depth(0), core.cachedDepth.texture());
        for (EstateCore.DepthDestination destination : core.copyDestinations) {
            readable.put(destination.logical, destination.boundTexture());
        }
        for (int index = 0; index < core.shadowDepths.size(); index++) {
            readable.put(shadowtex(index), core.shadowDepths.get(index));
        }
        for (EstateCore.ShadowColorPair pair : core.shadowColorPairs) {
            readable.put(pair.logical, pair.readSide());
        }
        return readable;
    }

    private List<ColorAttachment> frozenColorAttachments() {
        List<ColorAttachment> attachments = new ArrayList<>();
        for (EstateCore.ShadowColorPair pair : core.shadowColorPairs) {
            attachments.add(new ColorAttachment(attachments.size(), attachments.size(),
                pair.logical, pair.readSide()));
        }
        return attachments;
    }

    /** Shadow is a gbuffers-family stage: only explicit flips apply at completion. */
    private Set<LogicalBuffer> flipAfterPass(PassDescriptor pass) {
        Set<LogicalBuffer> flips = new HashSet<>();
        for (com.schmaloogium.engine.registry.BufferRef write : pass.resources().writes()) {
            if (write.domain() == BufferDomain.SHADOWCOLOR
                    && Boolean.TRUE.equals(pass.resources().explicitFlips().get(write))) {
                flips.add(shadowcolor(write.index()));
            }
        }
        return flips;
    }

    @Override
    public ShadowOperationResult bind(ShadowPassSnapshot snapshot) {
        core.checkRenderThread();
        Objects.requireNonNull(snapshot, "snapshot");
        ShadowProtocolRejection rejection = protocolCheck(snapshot);
        if (rejection != null) {
            return new ShadowOperationResult.Rejected(rejection);
        }
        try {
            core.device.framebuffers().bind(FramebufferTarget.DRAW, core.shadowFbo);
        } catch (RuntimeException bindFailure) {
            return backendFailed("schmaloogium.buffers.error.shadow.bind",
                String.valueOf(bindFailure));
        }
        List<GLError> errors = core.device.drainErrors();
        if (!errors.isEmpty()) {
            return backendFailed("schmaloogium.buffers.error.shadow.bind",
                errors.get(0).detail());
        }
        return new ShadowOperationResult.Applied();
    }

    @Override
    public ShadowOperationResult clear(ShadowPassSnapshot snapshot, ClearRequest request) {
        core.checkRenderThread();
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(request, "request");
        ShadowProtocolRejection rejection = protocolCheck(snapshot);
        if (rejection != null) {
            return new ShadowOperationResult.Rejected(rejection);
        }
        // §4.6 conversion through the realized class, single-sided: shadowcolor has no alt
        // write side at v0.1, so every clear takes only the current read side. No shadow
        // clear policy is declared pre-shadowcomp, so the payload is transparent black.
        FormatTable.ConvertedClear converted = FormatTable.convert(
            FormatTable.fallbackFormat(), 0.0, 0.0, 0.0, 0.0);
        ColorClearValue value;
        if (converted instanceof FormatTable.ConvertedClear.Floating floating) {
            value = new ColorClearValue.Floating(floating.r(), floating.g(), floating.b(),
                floating.a());
        } else if (converted instanceof FormatTable.ConvertedClear.Signed signed) {
            value = new ColorClearValue.Signed(signed.r(), signed.g(), signed.b(), signed.a());
        } else {
            FormatTable.ConvertedClear.Unsigned unsigned =
                (FormatTable.ConvertedClear.Unsigned) converted;
            value = new ColorClearValue.Unsigned(unsigned.r(), unsigned.g(), unsigned.b(),
                unsigned.a());
        }
        for (EstateCore.ShadowColorPair pair : core.shadowColorPairs) {
            FramebufferHandle fbo = shadowClearFbo(pair.readSide());
            if (fbo == null) {
                return backendFailed("schmaloogium.buffers.error.shadow.clear.framebuffer",
                    String.valueOf(pair.logical));
            }
            try {
                core.device.framebuffers().bind(FramebufferTarget.DRAW, fbo);
                core.device.framebuffers().clearColorAttachment(fbo, 0, value);
            } catch (RuntimeException clearFailure) {
                return backendFailed("schmaloogium.buffers.error.shadow.clear.backend",
                    String.valueOf(clearFailure));
            }
            List<GLError> errors = core.device.drainErrors();
            if (!errors.isEmpty()) {
                return backendFailed("schmaloogium.buffers.error.shadow.clear.backend",
                    errors.get(0).detail());
            }
        }
        // The sfb's own depth (shadowtex0) is cleared to far, and the sfb is left bound: the
        // pass draws right after this operation (Task E; the colour clears above bind their
        // own single-attachment FBOs and previously left the last of them bound, so every
        // shadow draw landed in a depth-less scratch framebuffer).
        try {
            core.device.framebuffers().clearDepthAttachment(core.shadowFbo, 1.0f);
            core.device.framebuffers().bind(FramebufferTarget.DRAW, core.shadowFbo);
        } catch (RuntimeException clearFailure) {
            return backendFailed("schmaloogium.buffers.error.shadow.clear.backend",
                String.valueOf(clearFailure));
        }
        List<GLError> depthErrors = core.device.drainErrors();
        if (!depthErrors.isEmpty()) {
            return backendFailed("schmaloogium.buffers.error.shadow.clear.backend",
                depthErrors.get(0).detail());
        }
        shadowFullClearRequired = false; // all-success consumption (§4.6/D-P5-46)
        return new ShadowOperationResult.Applied();
    }

    /** One cached single-attachment clear FBO per shadowcolor read side, checked once;
     *  owned by the estate's shared clear-FBO map so reverse teardown covers it. */
    private FramebufferHandle shadowClearFbo(TextureHandle texture) {
        String key = "shadow-clear:" + System.identityHashCode(texture);
        FramebufferHandle fbo = core.clearFbos.get(key);
        if (fbo != null) {
            return fbo;
        }
        fbo = core.device.framebuffers().create(key);
        core.device.framebuffers().attachColor(fbo, 0, texture);
        core.device.framebuffers().drawBuffers(fbo,
            List.of(new FramebufferDrawSlot.Attachment(0)));
        if (core.device.framebuffers().check(fbo) != FramebufferStatus.COMPLETE) {
            core.diagnostics.report(BufferDiagnostics.shadowBackendFailure(
                "schmaloogium.buffers.error.shadow.clear.framebuffer", key));
            return null;
        }
        core.clearFbos.put(key, fbo);
        return fbo;
    }

    @Override
    public ShadowOperationResult copyDepth(ShadowPassSnapshot snapshot,
            ShadowDepthCopyPoint point) {
        core.checkRenderThread();
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(point, "point");
        ShadowProtocolRejection rejection = protocolCheck(snapshot);
        if (rejection != null) {
            return new ShadowOperationResult.Rejected(rejection);
        }
        // The translucent split copy targets shadowtex1. A one-depth estate has no
        // destination: PHASE_8_DOC §4.8.3 rules that "a one-depth estate treats the typed
        // operation as a successful no-op owned by Phase 5" (the pack never declared
        // shadowtex1, so nothing can observe the missing copy). Task E amendment: Applied,
        // not BackendFailed — the latter neutralized every one-depth pack's shadow pass.
        if (core.shadowPlannedDepthCount() < 2 || core.shadowDepths.size() < 2) {
            return new ShadowOperationResult.Applied();
        }
        // The sfb carries shadowtex0 as its real depth attachment (§4.10), so the sfb is
        // the copy source; a neutralized estate never reaches here (its view is gone).
        if (core.shadowFbo == null) {
            return backendFailed("schmaloogium.buffers.error.shadow.copy.source", point.name());
        }
        FramebufferHandle mainDepthFbo = core.shadowFbo;
        TextureHandle destination = core.shadowDepths.get(1);
        try {
            if (!shadowtex1Initialized) {
                core.device.framebuffers().initializeDepthTextureFromFramebuffer(
                    mainDepthFbo, destination, shadowRegion());
                shadowtex1Initialized = true;
            } else {
                core.device.framebuffers().copyDepthToTexture(
                    mainDepthFbo, destination, shadowRegion());
            }
        } catch (RuntimeException copyFailure) {
            shadowtex1Initialized = false; // degraded back to the shadowtex0 fallback
            return backendFailed("schmaloogium.buffers.error.shadow.copy.failed",
                String.valueOf(copyFailure));
        }
        List<GLError> errors = core.device.drainErrors();
        if (!errors.isEmpty()) {
            return backendFailed("schmaloogium.buffers.error.shadow.copy.failed",
                errors.get(0).detail());
        }
        return new ShadowOperationResult.Applied();
    }

    private TextureRegion shadowRegion() {
        var extent = core.plan.sizing().shadowExtent().orElseThrow(
            () -> new IllegalStateException("shadow estate without a planned shadow extent"));
        return new TextureRegion(0, 0, 0, extent.width(), extent.height(), 1);
    }

    // ------------------------------------------------------------------ shadow bindings

    @Override
    public TextureBindingResult shadowBindings(long generation, long frameId,
            ShadowPassSnapshot snapshot, TextureOverlayLease overlay,
            TextureOverlayPublicationId expectedOverlay) {
        if (snapshot == null || overlay == null || expectedOverlay == null) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.INVALID_INPUT);
        }
        if (Thread.currentThread() != core.renderThread) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.WRONG_THREAD);
        }
        // §4.12.3 order: generation before frame before snapshot before depth epoch.
        if (!core.usable() || generation != core.generation) {
            return new TextureBindingResult.Rejected(
                TextureBindingRejection.STALE_ESTATE_GENERATION);
        }
        if (core.openFrameId == -1) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.NO_OPEN_FRAME);
        }
        if (core.openFrameId != frameId) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.WRONG_FRAME_ID);
        }
        if (openSnapshot != snapshot || snapshot.frameId() != frameId
                || snapshot.estateGeneration() != generation) {
            return new TextureBindingResult.Rejected(
                TextureBindingRejection.INVALID_PASS_SNAPSHOT);
        }
        if (snapshot.depthAttachmentEpoch() != core.depthAttachmentEpoch) {
            return new TextureBindingResult.Rejected(
                TextureBindingRejection.STALE_DEPTH_ATTACHMENT_EPOCH);
        }
        long acquiredEpoch = bindingEpoch;
        return textureBinder.bindValidated(core, snapshot.pass(), snapshot.selection(),
            snapshot.depthAttachmentEpoch(), frameId, snapshot.readableTextures(), overlay,
            expectedOverlay, () -> core.usable() && openSnapshot == snapshot
                && core.generation == generation && core.openFrameId == frameId
                && core.depthAttachmentEpoch == snapshot.depthAttachmentEpoch()
                && bindingEpoch == acquiredEpoch);
    }


    // ------------------------------------------------------------------ mipmaps

    @Override
    public ShadowMipmapResult generateShadowMipmaps(long generation, long frameId,
            ShadowPassSnapshot snapshot, ShadowMipmapPolicy policy) {
        core.checkRenderThread();
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(policy, "policy");
        if (!core.usable() || generation != core.generation) {
            return new ShadowMipmapResult.Rejected(ShadowProtocolRejection.STALE_GENERATION);
        }
        if (core.openFrameId == -1 || core.openFrameId != frameId) {
            return new ShadowMipmapResult.Rejected(ShadowProtocolRejection.WRONG_FRAME_ID);
        }
        ShadowProtocolRejection rejection = protocolCheck(snapshot);
        if (rejection != null) {
            return new ShadowMipmapResult.Rejected(rejection);
        }
        List<ShadowMipmapOutcome> outcomes = new ArrayList<>();
        for (LogicalBuffer logical : policy.buffers()) {
            ShadowMipmapOutcome outcome = generateOne(logical);
            if (outcome == null) {
                // The min-filter restore failed: containment already ran, stop before
                // later buffers and report the result-level neutralization (§4.10).
                return new ShadowMipmapResult.Neutralized(logical,
                    failure(BufferFailureCode.UNEXPECTED_BACKEND,
                        "schmaloogium.buffers.error.shadow.mipmap.restore"),
                    neutralDiagnostic(ShadowNeutralReason.MIPMAP_FILTER_RESTORE_FAILURE),
                    true);
            }
            outcomes.add(outcome);
        }
        return new ShadowMipmapResult.Generated(List.copyOf(outcomes));
    }

    /**
     * One buffer's generation outcome; {@code null} means the min-filter restore failed and
     * the estate already performed the atomic degrade-to-neutral containment (§4.10).
     */
    private ShadowMipmapOutcome generateOne(LogicalBuffer logical) {
        int index = logical.index().value();
        boolean depthRow = logical.domain() == BufferDomain.SHADOWTEX;
        if (index >= (depthRow ? core.shadowDepths.size() : core.shadowColorPairs.size())
                || core.shadowNeutralBacked() || !plannedMipmap(logical)) {
            return new ShadowMipmapOutcome.NotAllocated(logical);
        }
        TextureHandle texture = depthRow
            ? core.shadowDepths.get(index)
            : core.shadowColorPairs.get(index).readSide();
        TextureParameters base = plannedParameters(logical);
        try {
            core.device.textures().generateMipmap(texture);
            requireClean();
            core.device.textures().setParameters(texture, mipmapParameters(base));
            requireClean();
        } catch (RuntimeException generationFailure) {
            core.diagnostics.report(BufferDiagnostics.shadowBackendFailure(
                "schmaloogium.buffers.error.shadow.mipmap.generation",
                String.valueOf(generationFailure)));
            try {
                // Restore the configured non-mipmap min filter before reporting Degraded.
                core.device.textures().setParameters(texture, base);
                requireClean();
            } catch (RuntimeException restoreFailure) {
                core.diagnostics.report(BufferDiagnostics.shadowBackendFailure(
                    "schmaloogium.buffers.error.shadow.mipmap.restore",
                    String.valueOf(restoreFailure)));
                neutralize(ShadowNeutralReason.MIPMAP_FILTER_RESTORE_FAILURE);
                return null;
            }
            return new ShadowMipmapOutcome.Degraded(logical,
                failure(BufferFailureCode.UNEXPECTED_BACKEND,
                    "schmaloogium.buffers.error.shadow.mipmap.generation"),
                diagnostic("mipmap", logical));
        }
        if (!depthRow) {
            core.shadowColorPairs.get(index).chainFresh = true;
        }
        return new ShadowMipmapOutcome.Generated(logical);
    }

    private void requireClean() {
        List<GLError> errors = core.device.drainErrors();
        if (!errors.isEmpty()) {
            throw new IllegalStateException("shadow mipmap backend error: "
                + errors.get(0).detail());
        }
    }

    private boolean plannedMipmap(LogicalBuffer logical) {
        var shadow = core.plan.plannedProjection().shadow();
        int index = logical.index().value();
        if (logical.domain() == BufferDomain.SHADOWTEX) {
            return index < shadow.depth().size() && shadow.depth().get(index).mipmap();
        }
        return index < shadow.color().size() && shadow.color().get(index).mipmap();
    }

    /** The allocated base (non-mipmap) parameters of one planned shadow texture. */
    private TextureParameters plannedParameters(LogicalBuffer logical) {
        var extent = core.plan.sizing().shadowExtent().orElseThrow(
            () -> new IllegalStateException("shadow estate without a planned shadow extent"));
        int mipLevels = plannedMipmap(logical)
            ? CandidateBuilder.mipLevelsFor(new TextureExtent(extent.width(), extent.height(), 1))
            : 1;
        ShadowTextureResource resource = plannedResource(logical);
        return logical.domain() == BufferDomain.SHADOWTEX
            ? CandidateBuilder.shadowDepthParameters(resource, mipLevels)
            : CandidateBuilder.shadowColorParameters(resource, mipLevels);
    }

    private ShadowTextureResource plannedResource(LogicalBuffer logical) {
        var shadow = core.plan.plannedProjection().shadow();
        int index = logical.index().value();
        return logical.domain() == BufferDomain.SHADOWTEX
            ? shadow.depth().get(index)
            : shadow.color().get(index);
    }

    private TextureParameters mipmapParameters(TextureParameters base) {
        return new TextureParameters(
            base.minFilter() == TextureMinFilter.NEAREST
                ? TextureMinFilter.NEAREST_MIPMAP_NEAREST
                : TextureMinFilter.LINEAR_MIPMAP_LINEAR,
            base.magFilter(), base.wrapS(), base.wrapT(), base.wrapR(),
            base.compareMode(), base.compareFunction(), base.borderColor(),
            base.minLod(), base.maxLod(), base.lodBias(), base.maxAnisotropy(),
            base.baseLevel(), base.maxLevel(), base.swizzle());
    }

    // ------------------------------------------------------------------ neutralization

    @Override
    public ShadowNeutralizationResult degradeToNeutral(long generation,
            ShadowNeutralReason reason) {
        core.checkRenderThread();
        Objects.requireNonNull(reason, "reason");
        if (!core.usable() || generation != core.generation) {
            return new ShadowNeutralizationResult.Rejected(
                ShadowProtocolRejection.STALE_GENERATION);
        }
        if (core.shadowNeutralized) {
            return new ShadowNeutralizationResult.AlreadyNeutral(core.generation,
                core.shadowNeutralDiagnostic);
        }
        boolean abortedOpen = neutralize(reason);
        return new ShadowNeutralizationResult.Neutralized(core.generation,
            core.shadowNeutralDiagnostic, abortedOpen);
    }

    /** The atomic transition; returns whether an open shadow snapshot was aborted. */
    private boolean neutralize(ShadowNeutralReason reason) {
        boolean abortedOpen = openSnapshot != null;
        openSnapshot = null; // abort without applying flips
        bindingEpoch++; // invalidate every outstanding shadow binding lease
        shadowFullClearRequired = true;
        try {
            core.device.framebuffers().bindDefault(FramebufferTarget.DRAW);
            core.device.textures().prepareUnitBindings(0);
        } catch (RuntimeException restoreFailure) {
            core.diagnostics.report(BufferDiagnostics.shadowBackendFailure(
                "schmaloogium.buffers.error.shadow.neutralize.restore",
                String.valueOf(restoreFailure)));
        }
        core.shadowNeutralized = true;
        core.shadowView = null; // every later shadow() call reports Unavailable
        core.shadowFailure = failure(
            reason == ShadowNeutralReason.EXPLICIT_FEATURE_DISABLE
                ? BufferFailureCode.CAPABILITY_LIMIT
                : BufferFailureCode.UNEXPECTED_BACKEND,
            "schmaloogium.buffers.error.shadow.neutralized");
        core.shadowNeutralDiagnostic = neutralDiagnostic(reason);
        // An explicit feature disable (the v0.1 D-P7-46 disposition: no shadow pass exists yet)
        // is the designed outcome, reported at INFO; every other reason is a backend failure.
        core.diagnostics.report(reason == ShadowNeutralReason.EXPLICIT_FEATURE_DISABLE
            ? BufferDiagnostics.shadowNeutralizedByDesign(core.shadowNeutralDiagnostic)
            : BufferDiagnostics.shadowBackendFailure(
                "schmaloogium.buffers.error.shadow.neutralized", core.shadowNeutralDiagnostic));
        return abortedOpen;
    }

    private String neutralDiagnostic(ShadowNeutralReason reason) {
        return "schmaloogium.buffers.shadow.neutral." + reason.name().toLowerCase()
            + "." + core.generation;
    }

    private String diagnostic(String what, LogicalBuffer logical) {
        return "schmaloogium.buffers.shadow.diagnostic." + what + "."
            + logical.domain().name().toLowerCase() + logical.index().value()
            + "." + core.generation;
    }

    // ------------------------------------------------------------------ completion

    @Override
    public ShadowCompletionResult completePass(ShadowPassSnapshot snapshot) {
        core.checkRenderThread();
        Objects.requireNonNull(snapshot, "snapshot");
        ShadowProtocolRejection rejection = protocolCheck(snapshot);
        if (rejection != null) {
            return new ShadowCompletionResult.Rejected(rejection);
        }
        for (LogicalBuffer logical : snapshot.flipAfterPass()) {
            EstateCore.ShadowColorPair pair = core.shadowColorPair(logical.index().value());
            if (pair != null) {
                pair.flipped = !pair.flipped;
                pair.chainFresh = false; // completion conservatively invalidates writes
            }
        }
        openSnapshot = null;
        bindingEpoch++;
        return new ShadowCompletionResult.Completed(core.openFrameId);
    }

    @Override
    public ShadowAbortResult abortPass(ShadowPassSnapshot snapshot, String diagnosticId) {
        core.checkRenderThread();
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(diagnosticId, "diagnosticId");
        ShadowProtocolRejection rejection = protocolCheck(snapshot);
        if (rejection != null) {
            return new ShadowAbortResult.Rejected(rejection);
        }
        openSnapshot = null; // consumed without flips
        bindingEpoch++;
        shadowFullClearRequired = true;
        return new ShadowAbortResult.Aborted(core.openFrameId, diagnosticId, true);
    }

    // ------------------------------------------------------------------ shared checks

    /**
     * The pre-GL shadow protocol order: stale estate, frame identity, depth epoch, then
     * snapshot identity (FOREIGN for snapshots this view never issued, CLOSED for consumed
     * ones, NO_OPEN_PASS otherwise).
     */
    private ShadowProtocolRejection protocolCheck(ShadowPassSnapshot snapshot) {
        if (!core.usable()) {
            return ShadowProtocolRejection.STALE_GENERATION;
        }
        if (core.openFrameId == -1 || core.openFrameId != snapshot.frameId()) {
            return ShadowProtocolRejection.WRONG_FRAME_ID;
        }
        if (snapshot.depthAttachmentEpoch() != core.depthAttachmentEpoch) {
            return ShadowProtocolRejection.STALE_DEPTH_ATTACHMENT_EPOCH;
        }
        if (!issued.contains(snapshot)) {
            return ShadowProtocolRejection.FOREIGN_SNAPSHOT;
        }
        if (openSnapshot == null) {
            return ShadowProtocolRejection.CLOSED_SNAPSHOT;
        }
        if (!openSnapshot.equals(snapshot)) {
            return ShadowProtocolRejection.FOREIGN_SNAPSHOT;
        }
        return null;
    }

    private void remember(ShadowPassSnapshot snapshot) {
        issued.addLast(snapshot);
        while (issued.size() > ISSUED_HISTORY) {
            issued.removeFirst();
        }
    }

    private ShadowOperationResult.BackendFailed backendFailed(String messageKey,
            String detail) {
        core.diagnostics.report(BufferDiagnostics.shadowBackendFailure(messageKey, detail));
        return new ShadowOperationResult.BackendFailed(
            failure(BufferFailureCode.UNEXPECTED_BACKEND, messageKey));
    }

    private BufferFailure failure(BufferFailureCode code, String messageKey) {
        return new BufferFailure(code, messageKey, messageKey, List.of(), Optional.empty(),
            Optional.empty());
    }

    private LogicalBuffer shadowtex(int index) {
        return new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(index));
    }

    private LogicalBuffer shadowcolor(int index) {
        return new LogicalBuffer(BufferDomain.SHADOWCOLOR, new BufferIndex(index));
    }

    private LogicalBuffer depth(int index) {
        return new LogicalBuffer(BufferDomain.DEPTH, new BufferIndex(index));
    }
}
