// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BufferEstateView;
import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferInventory;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.BufferSizing;
import com.schmaloogium.engine.buffers.ClearExecutionPlan;
import com.schmaloogium.engine.buffers.ClearExecutionResult;
import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.ColorAttachment;
import com.schmaloogium.engine.buffers.DepthCopyPoint;
import com.schmaloogium.engine.buffers.DepthCopyResult;
import com.schmaloogium.engine.buffers.DrawBuffersNoneCloseResult;
import com.schmaloogium.engine.buffers.DrawBuffersNoneLease;
import com.schmaloogium.engine.buffers.DrawBuffersNoneOpenResult;
import com.schmaloogium.engine.buffers.FrameBeginResult;
import com.schmaloogium.engine.buffers.FrameEndResult;
import com.schmaloogium.engine.buffers.FrameProtocolRejection;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.MainDepthRefreshResult;
import com.schmaloogium.engine.buffers.MainDepthSnapshot;
import com.schmaloogium.engine.buffers.MainMipmapOutcome;
import com.schmaloogium.engine.buffers.MainMipmapResult;
import com.schmaloogium.engine.buffers.PassBufferSnapshot;
import com.schmaloogium.engine.buffers.PassCompletionResult;
import com.schmaloogium.engine.buffers.PassDiscardResult;
import com.schmaloogium.engine.buffers.PassDrawTarget;
import com.schmaloogium.engine.buffers.PassSnapshotResult;
import com.schmaloogium.engine.buffers.PhysicalSide;
import com.schmaloogium.engine.buffers.PublishedBufferEstate;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.buffers.ShadowEstateResult;
import com.schmaloogium.engine.buffers.TextureBindingResult;
import com.schmaloogium.engine.buffers.TextureOverlayLease;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.buffers.VirtualTransitionResult;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.ColorClearValue;
import com.schmaloogium.engine.gl.FramebufferDrawSlot;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.DrawRoutingSlot;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageStep;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * The accepted estate's runtime operation surface (PHASE_5_DOC §4.4-§4.13): the flip state
 * machine, pass snapshot protocol, typed clears, depth copies, mipmap transaction and frame
 * end rebase. Public only for the flat package split; never consumed outside the buffers
 * implementation.
 */
public final class EstateViewImpl implements BufferEstateView {

    private final EstateCore core;
    private final ClearExecutor clearExecutor;
    private final TextureBinder textureBinder;
    private final ShadowOperator shadowOperator;
    private PublishedBufferEstate publication;

    public EstateViewImpl(EstateCore core, ClearExecutor clearExecutor,
            TextureBinder textureBinder, ShadowOperator shadowOperator) {
        this.core = core;
        this.clearExecutor = clearExecutor;
        this.textureBinder = textureBinder;
        this.shadowOperator = shadowOperator;
    }

    public void attachPublication(PublishedBufferEstate publication) {
        this.publication = publication;
    }

    // ------------------------------------------------------------------ metadata

    @Override
    public long generation() {
        return core.generation;
    }

    @Override
    public RegistryFingerprint registryFingerprint() {
        return core.registryFingerprint;
    }

    @Override
    public BufferSizing sizing() {
        return core.plan.sizing();
    }

    @Override
    public BufferInventory inventory() {
        return core.plan.inventory();
    }

    @Override
    public BufferResourceSnapshot.Available resources() {
        return core.realized;
    }

    // ------------------------------------------------------------------ main depth

    @Override
    public MainDepthRefreshResult refreshMainDepth() {
        core.checkRenderThread();
        if (!core.usable()) {
            // §2.2: every Failed outcome advances the attachment epoch and retains the
            // cached prior identity; the estate stays stale until safe-point replacement.
            core.depthAttachmentEpoch++;
            return new MainDepthRefreshResult.Failed(staleFailure());
        }
        MainDepthSnapshot current = core.mainDepthSource.current();
        if (!(current instanceof MainDepthSnapshot.Available available)) {
            // §4.8: mark stale, advance the epoch once, retain the cached prior identity.
            core.stale = true;
            core.depthAttachmentEpoch++;
            return new MainDepthRefreshResult.Failed(failure(BufferFailureCode.MAIN_DEPTH_UNAVAILABLE,
                "schmaloogium.buffers.error.main-depth.unavailable"));
        }
        if (available.version() == core.cachedDepth.version()) {
            return new MainDepthRefreshResult.Unchanged(available.version());
        }
        if (!available.extent().equals(core.plan.sizing().mainExtent())) {
            return new MainDepthRefreshResult.ResizeRequired(failure(
                BufferFailureCode.MAIN_DEPTH_RESIZE_REQUIRED,
                "schmaloogium.buffers.error.main-depth.resize"));
        }
        // Six-step reattachment (§4.8): epoch advance + invalidate, close open snapshots,
        // reattach every owned main-depth FBO, reallocate copy targets, recheck, force full
        // clear — fail-closed on any failure.
        core.depthAttachmentEpoch++;
        closeOpenPass();
        closeOpenLease();
        core.cachedDepth = available;
        List<FramebufferHandle> affected = new ArrayList<>();
        core.passFbos.values().forEach(affected::add);
        core.copyDestinations.forEach(destination -> affected.add(destination.destinationFbo));
        try {
            for (FramebufferHandle fbo : affected) {
                if (available.format() == com.schmaloogium.engine.gl.DepthAttachmentFormat.DEPTH24_STENCIL8) {
                    core.device.framebuffers().attachDepthStencil(fbo, available.texture());
                } else {
                    core.device.framebuffers().attachDepth(fbo, available.texture());
                }
            }
            for (FramebufferHandle fbo : affected) {
                if (core.device.framebuffers().check(fbo) != FramebufferStatus.COMPLETE) {
                    throw new IllegalStateException("reattached FBO incomplete");
                }
            }
        } catch (RuntimeException reattachmentFailure) {
            core.stale = true;
            core.fullClearRequired = true;
            core.diagnostics.report(BufferDiagnostics.backendFailure(
                "schmaloogium.buffers.error.main-depth.reattach",
                String.valueOf(reattachmentFailure)));
            return new MainDepthRefreshResult.Failed(failure(
                BufferFailureCode.FRAMEBUFFER_INCOMPLETE,
                "schmaloogium.buffers.error.main-depth.reattach"));
        }
        core.fullClearRequired = true;
        return new MainDepthRefreshResult.Reattached(available.version(),
            core.depthAttachmentEpoch);
    }

    // ------------------------------------------------------------------ frame protocol

    @Override
    public FrameBeginResult beginFrame(long frameId) {
        core.checkRenderThread();
        if (!core.usable()) {
            return new FrameBeginResult.Rejected(FrameProtocolRejection.STALE_GENERATION);
        }
        if (core.openFrameId != -1) {
            return new FrameBeginResult.Rejected(FrameProtocolRejection.FRAME_ALREADY_OPEN);
        }
        if (core.flippedAnywhere()) {
            return new FrameBeginResult.Rejected(
                FrameProtocolRejection.NON_NORMALIZED_FLIP_STATE);
        }
        core.openFrameId = frameId;
        core.consumedVirtuals.clear();
        core.consumedPoints.clear();
        return new FrameBeginResult.Begun(core.generation, core.depthAttachmentEpoch, frameId);
    }

    @Override
    public VirtualTransitionResult applyVirtualTransition(long frameId,
            PassDescriptor pass) {
        core.checkRenderThread();
        FrameProtocolRejection rejection = frameCheck(frameId, true);
        if (rejection != null) {
            return new VirtualTransitionResult.Rejected(rejection);
        }
        if (core.openPass != null || core.openLease != null) {
            return new VirtualTransitionResult.Rejected(
                FrameProtocolRejection.INVALID_VIRTUAL_TRANSITION);
        }
        if (!isPlannedVirtual(pass) || core.consumedVirtuals.contains(pass.slot())) {
            return new VirtualTransitionResult.Rejected(
                core.consumedVirtuals.contains(pass.slot())
                    ? FrameProtocolRejection.DUPLICATE_VIRTUAL_TRANSITION
                    : FrameProtocolRejection.INVALID_VIRTUAL_TRANSITION);
        }
        List<LogicalBuffer> toggled = new ArrayList<>();
        for (Map.Entry<com.schmaloogium.engine.registry.BufferRef, Boolean> flip
                : pass.resources().explicitFlips().entrySet()) {
            if (Boolean.TRUE.equals(flip.getValue())
                    && flip.getKey().domain() == BufferDomain.COLORTEX) {
                EstateCore.ColorPair pair = core.pair(buffer(flip.getKey().domain(),
                    flip.getKey().index()));
                if (pair != null) {
                    pair.flipped = !pair.flipped;
                    toggled.add(pair.logical);
                }
            }
        }
        core.consumedVirtuals.add(pass.slot());
        if (toggled.isEmpty()) {
            return new VirtualTransitionResult.NoChange(frameId, pass.slot());
        }
        return new VirtualTransitionResult.Applied(frameId, pass.slot(), toggled);
    }

    private boolean isPlannedVirtual(PassDescriptor pass) {
        return pass.resources().readable().isEmpty()
            && pass.resources().writes().isEmpty()
            && pass.resources().mipmappedBeforeRead().isEmpty()
            && pass.computeSlots().isEmpty()
            && pass.step().stage() == StageId.DEFERRED
            && pass.step().band() == com.schmaloogium.engine.registry.StageBand.BETWEEN_GBUFFERS;
    }

    // ------------------------------------------------------------------ snapshots

    @Override
    public PassSnapshotResult snapshot(PassDescriptor pass, ProgramBindingSelection selection) {
        core.checkRenderThread();
        Objects.requireNonNull(selection, "selection");
        if (!core.usable()) {
            return new PassSnapshotResult.Failed(staleFailure(), diagnostic(), true);
        }
        if (core.openFrameId == -1) {
            return new PassSnapshotResult.Rejected(FrameProtocolRejection.NO_OPEN_FRAME);
        }
        if (core.openPass != null) {
            return new PassSnapshotResult.Rejected(FrameProtocolRejection.OPEN_PASS_SNAPSHOT);
        }
        if (core.openLease != null) {
            return new PassSnapshotResult.Rejected(
                FrameProtocolRejection.OPEN_DRAW_BUFFERS_NONE_LEASE);
        }
        PlanningArtifacts.PlannedRoute route = core.plan.routes().get(pass.slot());
        if (route == null) {
            return new PassSnapshotResult.Failed(failure(BufferFailureCode.INVALID_INPUT,
                "schmaloogium.buffers.error.snapshot.route"), diagnostic(), true);
        }

        // §4.4.2 steps 2-6: freeze read/write sides, derive FBO/attachment data, calculate
        // the post-pass flip set (applied only at completion).
        List<ColorAttachment> attachments = new ArrayList<>();
        Map<LogicalBuffer, TextureHandle> readable = new LinkedHashMap<>();
        Set<LogicalBuffer> flipAfterPass = new HashSet<>();
        int physical = 0;
        for (DrawRoutingSlot slot : route.positional()) {
            if (slot instanceof DrawRoutingSlot.Attachment attachment) {
                LogicalBuffer logical = buffer(attachment.buffer().domain(),
                    attachment.buffer().index());
                EstateCore.ColorPair pair = core.pair(logical);
                attachments.add(new ColorAttachment(attachments.size(), physical,
                    logical, pair.readSide()));
                physical++;
            }
        }
        for (com.schmaloogium.engine.registry.BufferRef read : pass.resources().readable()) {
            EstateCore.ColorPair pair = core.pair(buffer(read.domain(), read.index()));
            if (pair != null) {
                readable.put(pair.logical, pair.readSide());
            } else if (read.domain() == BufferDomain.DEPTH) {
                core.copyDestinations.stream()
                    .filter(destination -> destination.logical.index().value() == read.index())
                    .map(destination -> destination)
                    .findFirst()
                    .ifPresent(destination -> readable.put(destination.logical,
                        bindingTexture(destination)));
            }
        }
        List<LogicalBuffer> writes = new ArrayList<>();
        boolean gbuffers = pass.step().stage() == StageId.GBUFFERS
            || pass.step().stage() == StageId.SHADOW;
        for (com.schmaloogium.engine.registry.BufferRef write : pass.resources().writes()) {
            if (write.domain() != BufferDomain.COLORTEX) {
                continue;
            }
            EstateCore.ColorPair pair = core.pair(buffer(write.domain(), write.index()));
            if (pair == null) {
                continue;
            }
            writes.add(pair.logical);
            Boolean explicit = pass.resources().explicitFlips().get(write);
            if (explicit == null) {
                if (!gbuffers) {
                    flipAfterPass.add(pair.logical); // deferred/composite: toggle after draw
                }
            } else if (explicit) {
                flipAfterPass.add(pair.logical);
            }
        }

        PassDrawTarget drawTarget = pass.step().stage() == StageId.FINAL
            ? PassDrawTarget.Screen.INSTANCE
            : new PassDrawTarget.EngineFramebuffer(core.passFbos.get(CandidateBuilder.passKey(route)));
        if (!(drawTarget instanceof PassDrawTarget.Screen)) {
            Objects.requireNonNull(((PassDrawTarget.EngineFramebuffer) drawTarget).framebuffer(),
                "pass FBO missing");
        }

        // §4.2.1 snapshot preparation: invalidate frozen write sides' chains, advance write
        // revisions, restore base parameters — before Acquired is exposed.
        for (LogicalBuffer written : writes) {
            EstateCore.ColorPair pair = core.pair(written);
            pair.writeRevision++;
            pair.chainFresh = false;
            restoreBaseParameters(pair);
        }

        PassBufferSnapshot snapshot = new PassBufferSnapshot(core.generation,
            core.depthAttachmentEpoch, core.openFrameId, pass, selection, attachments,
            readable, Set.copyOf(flipAfterPass), drawTarget);
        core.openPass = new EstateCore.OpenPass(snapshot, List.copyOf(flipAfterPass));
        return new PassSnapshotResult.Acquired(snapshot);
    }

    private TextureHandle bindingTexture(EstateCore.DepthDestination destination) {
        // While UNINITIALIZED or DEGRADED the destination's fixed unit resolves to the
        // current borrowed depthtex0 (§4.9 destination state machine).
        return destination.initialized
            ? destination.texture
            : core.cachedDepth.texture();
    }

    private void restoreBaseParameters(EstateCore.ColorPair pair) {
        core.device.textures().setParameters(pair.readSide(),
            CandidateBuilder.baseParameters(pair.baseMinFilter, pair.mipLevels));
        core.device.textures().setParameters(pair.writeSide(),
            CandidateBuilder.baseParameters(pair.baseMinFilter, pair.mipLevels));
    }

    @Override
    public PassCompletionResult completePass(PassBufferSnapshot snapshot) {
        core.checkRenderThread();
        Objects.requireNonNull(snapshot, "snapshot");
        FrameProtocolRejection rejection = openPassCheck(snapshot);
        if (rejection != null) {
            return new PassCompletionResult.Rejected(rejection);
        }
        List<LogicalBuffer> flips = core.openPass.flipAfterPass;
        for (LogicalBuffer logical : flips) {
            EstateCore.ColorPair pair = core.pair(logical);
            if (pair != null) {
                pair.flipped = !pair.flipped;
                pair.chainFresh = false; // completion conservatively invalidates writes
            }
        }
        core.openPass = null;
        return new PassCompletionResult.Completed(core.openFrameId);
    }

    @Override
    public PassDiscardResult discardPass(PassBufferSnapshot snapshot) {
        core.checkRenderThread();
        Objects.requireNonNull(snapshot, "snapshot");
        FrameProtocolRejection rejection = openPassCheck(snapshot);
        if (rejection != null) {
            return new PassDiscardResult.Rejected(rejection);
        }
        core.openPass = null;
        return new PassDiscardResult.Discarded(core.openFrameId);
    }

    private FrameProtocolRejection openPassCheck(PassBufferSnapshot snapshot) {
        if (!core.usable()) {
            return FrameProtocolRejection.STALE_GENERATION;
        }
        if (core.openFrameId == -1) {
            return FrameProtocolRejection.NO_OPEN_FRAME;
        }
        if (core.openPass == null || !core.openPass.snapshot.equals(snapshot)) {
            return FrameProtocolRejection.INVALID_PASS_SNAPSHOT;
        }
        return null;
    }

    // ------------------------------------------------------------------ draw-buffers-none

    @Override
    public DrawBuffersNoneOpenResult openDrawBuffersNone(long frameId) {
        core.checkRenderThread();
        FrameProtocolRejection rejection = frameCheck(frameId, true);
        if (rejection != null) {
            return new DrawBuffersNoneOpenResult.Rejected(rejection);
        }
        if (core.openPass != null) {
            return new DrawBuffersNoneOpenResult.Rejected(FrameProtocolRejection.OPEN_PASS_SNAPSHOT);
        }
        if (core.openLease != null) {
            return new DrawBuffersNoneOpenResult.Rejected(
                FrameProtocolRejection.OPEN_DRAW_BUFFERS_NONE_LEASE);
        }
        EstateCore.DrawBuffersNoneLeaseState lease =
            new EstateCore.DrawBuffersNoneLeaseState(frameId);
        core.openLease = lease;
        return new DrawBuffersNoneOpenResult.Opened(new DrawBuffersNoneLease() {
            @Override
            public DrawBuffersNoneCloseResult close() {
                return EstateViewImpl.this.closeLease(lease);
            }
        });
    }

    private DrawBuffersNoneCloseResult closeLease(EstateCore.DrawBuffersNoneLeaseState lease) {
        core.checkRenderThread();
        if (lease != core.openLease || !lease.open) {
            return new DrawBuffersNoneCloseResult.Rejected(
                FrameProtocolRejection.INVALID_DRAW_BUFFERS_NONE_LEASE);
        }
        lease.open = false;
        core.openLease = null;
        return new DrawBuffersNoneCloseResult.Restored(lease.frameId);
    }

    // ------------------------------------------------------------------ typed clears

    @Override
    public ClearExecutionPlan clearPlan(ClearRequest request) {
        core.checkRenderThread();
        Objects.requireNonNull(request, "request");
        return clearExecutor.plan(core, request, core.openFrameId);
    }

    @Override
    public ClearExecutionResult executeClear(ClearExecutionPlan plan) {
        core.checkRenderThread();
        Objects.requireNonNull(plan, "plan");
        if (!core.usable() || plan.estateGeneration() != core.generation
            || plan.depthAttachmentEpoch() != core.depthAttachmentEpoch) {
            return ClearExecutionResult.STALE_OR_PROTOCOL_REJECTED;
        }
        return clearExecutor.execute(core, plan);
    }

    // ------------------------------------------------------------------ depth copies

    @Override
    public DepthCopyResult copyDepth(DepthCopyPoint point, long frameId) {
        core.checkRenderThread();
        FrameProtocolRejection rejection = frameCheck(frameId, false);
        if (rejection != null) {
            return new DepthCopyResult.Rejected(rejection);
        }
        // Scheduled order: PRE_WEATHER then PRE_TRANSLUCENT; duplicates (already consumed,
        // including a failed PRE_WEATHER retried after PRE_TRANSLUCENT) are diagnosed and
        // ignored; out-of-order rejects (§4.9).
        int scheduledIndex = point.ordinal();
        if (core.consumedPoints.contains(point)) {
            core.diagnostics.report(BufferDiagnostics.duplicateIgnored(point.name()));
            return new DepthCopyResult.DuplicateIgnored(point, diagnostic());
        }
        for (DepthCopyPoint previous : DepthCopyPoint.values()) {
            if (previous.ordinal() < scheduledIndex && !core.consumedPoints.contains(previous)) {
                return new DepthCopyResult.Rejected(FrameProtocolRejection.DEPTH_COPY_OUT_OF_ORDER);
            }
        }
        int depthCount = core.plan.depthTextureCount();
        int destinationIndex = point == DepthCopyPoint.PRE_WEATHER
            ? (depthCount >= 3 ? 2 : 1)
            : 1;
        if (destinationIndex >= depthCount) {
            core.consumedPoints.add(point);
            return new DepthCopyResult.BackendDegraded(point, failure(
                BufferFailureCode.DEPTH_COPY_UNAVAILABLE,
                "schmaloogium.buffers.error.depth-copy.unavailable"), diagnostic());
        }
        EstateCore.DepthDestination destination = core.copyDestinations.get(destinationIndex - 1);
        core.consumedPoints.add(point);
        FramebufferHandle mainDepthFbo = mainDepthFbo();
        boolean initialized = !destination.initialized;
        try {
            if (initialized) {
                core.device.framebuffers().initializeDepthTextureFromFramebuffer(mainDepthFbo,
                    destination.texture, region());
                destination.initialized = true;
            } else {
                core.device.framebuffers().copyDepthToTexture(mainDepthFbo,
                    destination.texture, region());
            }
        } catch (RuntimeException copyFailure) {
            destination.initialized = false; // DEGRADED_TO_DEPTHTEX0
            core.diagnostics.report(BufferDiagnostics.depthCopyDegraded(point.name(),
                String.valueOf(copyFailure)));
            return new DepthCopyResult.BackendDegraded(point, failure(
                BufferFailureCode.UNEXPECTED_BACKEND,
                "schmaloogium.buffers.error.depth-copy.failed"), diagnostic());
        }
        return new DepthCopyResult.Copied(point, initialized);
    }

    private FramebufferHandle mainDepthFbo() {
        // The copy source is the pass FBO carrying the borrowed depthtex0 as its depth
        // attachment; the FIRST planned engine FBO serves as the canonical main-depth FBO.
        return core.passFbos.values().iterator().next();
    }

    private com.schmaloogium.engine.gl.TextureRegion region() {
        return new com.schmaloogium.engine.gl.TextureRegion(0, 0, 0,
            core.plan.sizing().mainExtent().width(), core.plan.sizing().mainExtent().height(), 1);
    }

    // ------------------------------------------------------------------ mipmaps

    @Override
    public MainMipmapResult generateMainMipmaps(PassBufferSnapshot snapshot) {
        core.checkRenderThread();
        Objects.requireNonNull(snapshot, "snapshot");
        if (!core.usable()) {
            return new MainMipmapResult.Rejected(FrameProtocolRejection.STALE_GENERATION);
        }
        if (core.openFrameId == -1 || core.openPass == null
            || !core.openPass.snapshot.equals(snapshot)) {
            return new MainMipmapResult.Rejected(FrameProtocolRejection.INVALID_PASS_SNAPSHOT);
        }
        StageId stage = snapshot.pass().step().stage();
        if (stage != StageId.DEFERRED && stage != StageId.COMPOSITE && stage != StageId.FINAL) {
            return new MainMipmapResult.Rejected(FrameProtocolRejection.INVALID_PASS_SNAPSHOT);
        }
        // Canonical COLORTEX-only request: the descriptor's planned mipmappedBeforeRead
        // intersected with the COLORTEX inventory, in ascending order.
        List<LogicalBuffer> requested = new ArrayList<>();
        for (com.schmaloogium.engine.registry.BufferRef ref
                : snapshot.pass().resources().mipmappedBeforeRead()) {
            if (ref.domain() == BufferDomain.COLORTEX) {
                EstateCore.ColorPair pair = core.pair(buffer(ref.domain(), ref.index()));
                if (pair != null) {
                    requested.add(pair.logical);
                }
            }
        }
        if (requested.isEmpty()) {
            return new MainMipmapResult.Completed(List.of());
        }
        List<MainMipmapOutcome> outcomes = new ArrayList<>();
        for (LogicalBuffer logical : requested) {
            EstateCore.ColorPair pair = core.pair(logical);
            if (pair.generatedRevision == pair.writeRevision && pair.chainFresh) {
                outcomes.add(new MainMipmapOutcome.AlreadyFresh(logical));
                continue;
            }
            if (!core.device.capabilities().supportsMipmapGeneration()) {
                outcomes.add(new MainMipmapOutcome.Degraded(logical,
                    "schmaloogium.buffers.error.mipmap.capability"));
                continue;
            }
            TextureHandle readSide = pair.readSide(); // frozen readable physical side
            try {
                core.device.textures().generateMipmap(readSide);
                core.device.textures().setParameters(readSide,
                    CandidateBuilder.baseParameters(
                        pair.baseMinFilter == TextureMinFilter.NEAREST
                            ? TextureMinFilter.NEAREST_MIPMAP_NEAREST
                            : TextureMinFilter.LINEAR_MIPMAP_LINEAR,
                        pair.mipLevels));
                pair.generatedRevision = pair.writeRevision;
                pair.chainFresh = true;
                outcomes.add(new MainMipmapOutcome.Generated(logical));
            } catch (RuntimeException generationFailure) {
                core.device.textures().setParameters(readSide,
                    CandidateBuilder.baseParameters(pair.baseMinFilter, pair.mipLevels));
                pair.chainFresh = false;
                outcomes.add(new MainMipmapOutcome.Degraded(logical,
                    "schmaloogium.buffers.error.mipmap.generation"));
            }
        }
        return new MainMipmapResult.Completed(List.copyOf(outcomes));
    }

    // ------------------------------------------------------------------ bindings + shadow

    @Override
    public TextureBindingResult textureBindings(PassBufferSnapshot snapshot,
            TextureOverlayLease overlay, TextureOverlayPublicationId expectedOverlay) {
        core.checkRenderThread();
        return textureBinder.bind(core, snapshot, overlay, expectedOverlay);
    }

    @Override
    public ShadowEstateResult shadow() {
        core.checkRenderThread();
        return shadowOperator.shadow(core);
    }

    // ------------------------------------------------------------------ frame end

    @Override
    public FrameEndResult commitFrame(long frameId) {
        core.checkRenderThread();
        FrameProtocolRejection rejection = frameCheck(frameId, true);
        if (rejection != null) {
            return new FrameEndResult.Rejected(rejection);
        }
        if (core.openPass != null) {
            return new FrameEndResult.Rejected(FrameProtocolRejection.OPEN_PASS_SNAPSHOT);
        }
        if (core.openLease != null) {
            return new FrameEndResult.Rejected(
                FrameProtocolRejection.OPEN_DRAW_BUFFERS_NONE_LEASE);
        }
        rebase();
        core.openFrameId = -1;
        return new FrameEndResult.Committed(frameId);
    }

    @Override
    public FrameEndResult abortFrame(long frameId, String diagnosticId) {
        core.checkRenderThread();
        Objects.requireNonNull(diagnosticId, "diagnosticId");
        FrameProtocolRejection rejection = frameCheck(frameId, true);
        if (rejection != null) {
            return new FrameEndResult.Rejected(rejection);
        }
        closeOpenPass();
        closeOpenLease();
        rebase();
        core.fullClearRequired = true;
        core.openFrameId = -1;
        return new FrameEndResult.Aborted(frameId, diagnosticId, true);
    }

    /** §4.4.3: carryover without copy-back — flip the committed-main metadata, reset. */
    private void rebase() {
        for (EstateCore.ColorPair pair : core.colorPairs) {
            if (pair.flipped) {
                pair.committedMain = pair.committedMain.opposite();
                pair.flipped = false;
            }
        }
    }

    private void closeOpenPass() {
        core.openPass = null;
    }

    private void closeOpenLease() {
        if (core.openLease != null) {
            core.openLease.open = false;
            core.openLease = null;
        }
    }

    // ------------------------------------------------------------------ shared checks

    private FrameProtocolRejection frameCheck(long frameId, boolean requireExactFrame) {
        if (!core.usable()) {
            return FrameProtocolRejection.STALE_GENERATION;
        }
        if (core.depthAttachmentEpoch != epochOfLastSnapshot()) {
            return FrameProtocolRejection.STALE_DEPTH_ATTACHMENT_EPOCH;
        }
        if (core.openFrameId == -1) {
            return FrameProtocolRejection.NO_OPEN_FRAME;
        }
        if (core.openFrameId != frameId) {
            return FrameProtocolRejection.WRONG_FRAME_ID;
        }
        return null;
    }

    private long epochOfLastSnapshot() {
        // Snapshots stamp the epoch at acquisition; live checks compare the CURRENT epoch
        // against any open snapshot's stamp. Without an open snapshot the current epoch is
        // authoritative and trivially matches itself.
        if (core.openPass != null
            && core.openPass.snapshot.depthAttachmentEpoch() != core.depthAttachmentEpoch) {
            return core.openPass.snapshot.depthAttachmentEpoch();
        }
        return core.depthAttachmentEpoch;
    }

    private LogicalBuffer buffer(BufferDomain domain, int index) {
        return new LogicalBuffer(domain, new com.schmaloogium.engine.buffers.BufferIndex(index));
    }

    private BufferFailure failure(BufferFailureCode code, String messageKey) {
        return new BufferFailure(code, messageKey, messageKey, List.of(), Optional.empty(),
            Optional.empty());
    }

    private BufferFailure staleFailure() {
        return failure(BufferFailureCode.STALE_ESTATE,
            "schmaloogium.buffers.error.stale-estate");
    }

    private String diagnostic() {
        return "schmaloogium.buffers.diagnostic." + core.generation;
    }

}
