// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BindingOrigin;
import com.schmaloogium.engine.buffers.BindingOriginKind;
import com.schmaloogium.engine.buffers.BindingPurpose;
import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.ColorAttachment;
import com.schmaloogium.engine.buffers.FixedSamplerPlanResult;
import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.ResolvedSamplerBinding;
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
import com.schmaloogium.engine.buffers.TextureBindingAction;
import com.schmaloogium.engine.buffers.TextureBindingDegradation;
import com.schmaloogium.engine.buffers.TextureBindingDiagnostic;
import com.schmaloogium.engine.buffers.TextureBindingDiagnosticCode;
import com.schmaloogium.engine.buffers.TextureBindingOutcome;
import com.schmaloogium.engine.buffers.TextureBindingRejection;
import com.schmaloogium.engine.buffers.TextureBindingResult;
import com.schmaloogium.engine.buffers.TextureBindingRow;
import com.schmaloogium.engine.buffers.TextureBindingSnapshot;
import com.schmaloogium.engine.buffers.TextureHandleRef;
import com.schmaloogium.engine.buffers.TextureOverlayFingerprint;
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
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

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
        core.device.framebuffers().bind(FramebufferTarget.DRAW, core.shadowFbo);
        ResolvedRows resolved = resolveRows(snapshot);
        if (resolved.mask() != 0) {
            try {
                core.device.textures().prepareUnitBindings(resolved.mask());
                for (TextureBindingRow row : resolved.rows()) {
                    if (row.outcome() instanceof TextureBindingOutcome.BoundObject bound) {
                        core.device.textures().bindToUnit(row.unit(),
                            ((TextureHandleRef.Borrowed) bound.handle()).handle());
                    }
                }
            } catch (RuntimeException bindFailure) {
                return backendFailed("schmaloogium.buffers.error.shadow.bind",
                    String.valueOf(bindFailure));
            }
            List<GLError> errors = core.device.drainErrors();
            if (!errors.isEmpty()) {
                return backendFailed("schmaloogium.buffers.error.shadow.bind",
                    errors.get(0).detail());
            }
        }
        // Row degradation (unbacked demanded units, invalid plans) suppresses only the
        // selected draw through shadowBindings' protocol; the physical pass bind applied.
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
        // The translucent split copy targets shadowtex1; with no second planned depth the
        // copy point has no destination — explicit unsupported result, never a silent no-op.
        if (core.shadowPlannedDepthCount() < 2 || core.shadowDepths.size() < 2) {
            return backendFailed("schmaloogium.buffers.error.shadow.copy.unavailable",
                point.name());
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
        core.checkRenderThread();
        Objects.requireNonNull(snapshot, "snapshot");
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
        if (!issued.contains(snapshot) || openSnapshot == null
                || !openSnapshot.equals(snapshot)) {
            return new TextureBindingResult.Rejected(
                TextureBindingRejection.INVALID_PASS_SNAPSHOT);
        }
        if (snapshot.depthAttachmentEpoch() != core.depthAttachmentEpoch) {
            return new TextureBindingResult.Rejected(
                TextureBindingRejection.STALE_DEPTH_ATTACHMENT_EPOCH);
        }
        if (snapshot.selection() == null) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.INVALID_INPUT);
        }
        // The overlay publication is "unpublished:v0.1" (P13 unavailable); the lease and
        // expected id are accepted as carried and re-answered on the issued snapshot.
        ResolvedRows resolved = resolveRows(snapshot);
        if (resolved.mask() != 0) {
            try {
                core.device.textures().prepareUnitBindings(resolved.mask());
                for (TextureBindingRow row : resolved.rows()) {
                    if (row.outcome() instanceof TextureBindingOutcome.BoundObject bound) {
                        core.device.textures().bindToUnit(row.unit(),
                            ((TextureHandleRef.Borrowed) bound.handle()).handle());
                    }
                }
            } catch (RuntimeException bindFailure) {
                core.diagnostics.report(BufferDiagnostics.shadowBackendFailure(
                    "schmaloogium.buffers.error.shadow.bindings.backend",
                    String.valueOf(bindFailure)));
                return new TextureBindingResult.BackendFailed(core.failure(
                    BufferFailureCode.UNEXPECTED_BACKEND,
                    "schmaloogium.buffers.error.shadow.bindings.backend"));
            }
            List<GLError> errors = core.device.drainErrors();
            if (!errors.isEmpty()) {
                core.diagnostics.report(BufferDiagnostics.shadowBackendFailure(
                    "schmaloogium.buffers.error.shadow.bindings.backend",
                    errors.get(0).detail()));
                return new TextureBindingResult.BackendFailed(core.failure(
                    BufferFailureCode.UNEXPECTED_BACKEND,
                    "schmaloogium.buffers.error.shadow.bindings.backend"));
            }
        }
        if (resolved.degrade()) {
            return new TextureBindingResult.Degraded(new TextureBindingDegradation(
                snapshot.selection(), resolved.diagnostics(),
                TextureBindingAction.SUPPRESS_DRAW));
        }
        return new TextureBindingResult.Bound(new ShadowBindingSnapshot(snapshot,
            resolved.rows(), resolved.diagnostics()));
    }

    /** One demanded row's backing object from the frozen snapshot (neutral-aware). */
    private TextureHandle backingFor(ShadowPassSnapshot snapshot, int unit) {
        return switch (unit) {
            case 4 -> shadowDepthBacking(snapshot, 0);
            case 5 -> core.shadowPlannedDepthCount() >= 2
                ? shadowDepthBacking(snapshot, 1) : null;
            case 13 -> shadowColorBacking(snapshot, 0);
            case 14 -> core.shadowPlannedColorCount() >= 2
                ? shadowColorBacking(snapshot, 1) : null;
            case 15 -> null; // noisetex: P13 overlay at v0.1 (publication unavailable)
            default -> snapshot.readableTextures().get(logicalForUnit(unit));
        };
    }

    private TextureHandle shadowDepthBacking(ShadowPassSnapshot snapshot, int index) {
        if (core.shadowNeutralBacked()) {
            return core.shadowNeutral == null ? null : core.shadowNeutral.depthByUnit(index);
        }
        return snapshot.readableTextures().get(shadowtex(index));
    }

    private TextureHandle shadowColorBacking(ShadowPassSnapshot snapshot, int index) {
        if (core.shadowNeutralBacked()) {
            return core.shadowNeutral == null ? null : core.shadowNeutral.colorByUnit(index);
        }
        return snapshot.readableTextures().get(shadowcolor(index));
    }

    private LogicalBuffer logicalForUnit(int unit) {
        return switch (unit) {
            case 0, 1, 2, 3 -> new LogicalBuffer(BufferDomain.COLORTEX, new BufferIndex(unit));
            case 6 -> depth(0);
            case 7, 8, 9, 10 -> new LogicalBuffer(BufferDomain.COLORTEX,
                new BufferIndex(unit - 3));
            case 11 -> depth(1);
            case 12 -> depth(2);
            default -> null;
        };
    }

    /** The sixteen-row resolution: zero GL until the returned mask is bound. */
    private ResolvedRows resolveRows(ShadowPassSnapshot snapshot) {
        ProgramSamplerLayout layout = snapshot.selection().effectiveDescriptor() == null
            ? null : snapshot.selection().effectiveDescriptor().samplerLayout();
        Map<Integer, List<ResolvedSamplerBinding>> byUnit = new LinkedHashMap<>();
        boolean invalidPlan = false;
        if (layout instanceof ProgramSamplerLayout.Shader shaderLayout) {
            FixedSamplerPlanResult plan = FixedSamplerPolicies.resolver().resolve(shaderLayout,
                snapshot.pass().step().stage(), snapshot.pass().step().band());
            if (plan instanceof FixedSamplerPlanResult.Invalid) {
                invalidPlan = true;
            } else {
                for (ResolvedSamplerBinding binding
                        : ((FixedSamplerPlanResult.Ready) plan).bindings()) {
                    byUnit.computeIfAbsent(binding.unit(), ignored -> new ArrayList<>())
                        .add(binding);
                }
            }
        } // fixed-function and virtual layouts demand no units
        List<TextureBindingRow> rows = new ArrayList<>();
        List<TextureBindingDiagnostic> diagnostics = new ArrayList<>();
        boolean degrade = invalidPlan;
        if (invalidPlan) {
            diagnostics.add(new TextureBindingDiagnostic(
                TextureBindingDiagnosticCode.CONFLICTING_SAMPLER_TYPES, "layout",
                OptionalInt.empty()));
        }
        int mask = 0;
        for (int unit = 0; unit < 16; unit++) {
            List<ResolvedSamplerBinding> names = byUnit.get(unit);
            if (names == null) {
                rows.add(new TextureBindingRow(unit, new TextureBindingOutcome.Unused()));
                continue;
            }
            TextureHandle backing = backingFor(snapshot, unit);
            if (backing == null) {
                for (ResolvedSamplerBinding name : names) {
                    diagnostics.add(new TextureBindingDiagnostic(
                        TextureBindingDiagnosticCode.PUBLICATION_UNAVAILABLE,
                        name.exactName(), OptionalInt.of(unit)));
                }
                degrade = true;
                rows.add(new TextureBindingRow(unit, new TextureBindingOutcome.Unused()));
                continue;
            }
            DeclaredGlslType.Sampler shape = names.get(0).shape();
            rows.add(new TextureBindingRow(unit, new TextureBindingOutcome.BoundObject(
                new TextureHandleRef.Borrowed(backing), shape, names,
                new BindingOrigin(BindingOriginKind.ESTATE, List.of()))));
            mask |= 1 << unit;
        }
        return new ResolvedRows(rows, diagnostics, degrade, mask);
    }

    private record ResolvedRows(List<TextureBindingRow> rows,
            List<TextureBindingDiagnostic> diagnostics, boolean degrade, int mask) {
    }

    /** The closeable sixteen-row lease; current only inside its open shadow pass. */
    private final class ShadowBindingSnapshot implements TextureBindingSnapshot {
        private final ShadowPassSnapshot snapshot;
        private final List<TextureBindingRow> rows;
        private final List<TextureBindingDiagnostic> diagnostics;
        private final long acquiredEpoch = bindingEpoch;
        private boolean closed;

        ShadowBindingSnapshot(ShadowPassSnapshot snapshot, List<TextureBindingRow> rows,
                List<TextureBindingDiagnostic> diagnostics) {
            this.snapshot = snapshot;
            this.rows = rows;
            this.diagnostics = diagnostics;
        }

        @Override
        public long estateGeneration() {
            return snapshot.estateGeneration();
        }

        @Override
        public long depthAttachmentEpoch() {
            return snapshot.depthAttachmentEpoch();
        }

        @Override
        public long frameId() {
            return snapshot.frameId();
        }

        @Override
        public PassDescriptor pass() {
            return snapshot.pass();
        }

        @Override
        public ProgramBindingSelection selection() {
            return snapshot.selection();
        }

        @Override
        public TextureOverlayPublicationId overlayPublication() {
            return new TextureOverlayPublicationId(core.generation,
                new TextureOverlayFingerprint("unpublished:v0.1"));
        }

        @Override
        public BindingPurpose purpose() {
            return BindingPurpose.SHADER;
        }

        @Override
        public List<TextureBindingRow> rows() {
            return rows;
        }

        @Override
        public TextureBindingOutcome outcome(int unit) {
            return rows.get(unit).outcome();
        }

        @Override
        public List<TextureBindingDiagnostic> diagnostics() {
            return diagnostics;
        }

        @Override
        public boolean isCurrent() {
            return !closed && core.usable() && openSnapshot == snapshot
                && bindingEpoch == acquiredEpoch;
        }

        @Override
        public void close() {
            closed = true;
        }
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
        core.diagnostics.report(BufferDiagnostics.shadowBackendFailure(
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
