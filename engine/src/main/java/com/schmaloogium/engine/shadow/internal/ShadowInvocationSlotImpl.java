// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow.internal;

import com.schmaloogium.engine.buffers.BufferEstateView;
import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferSizing;
import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.PublishedBufferEstate;
import com.schmaloogium.engine.buffers.ShadowAbortResult;
import com.schmaloogium.engine.buffers.ShadowBeginResult;
import com.schmaloogium.engine.buffers.ShadowCompletionResult;
import com.schmaloogium.engine.buffers.ShadowDepthCopyPoint;
import com.schmaloogium.engine.buffers.ShadowEstateAvailable;
import com.schmaloogium.engine.buffers.ShadowEstateNotRequested;
import com.schmaloogium.engine.buffers.ShadowEstateResult;
import com.schmaloogium.engine.buffers.ShadowEstateUnavailable;
import com.schmaloogium.engine.buffers.ShadowEstateView;
import com.schmaloogium.engine.buffers.ShadowMipmapOutcome;
import com.schmaloogium.engine.buffers.ShadowMipmapResult;
import com.schmaloogium.engine.buffers.ShadowNeutralReason;
import com.schmaloogium.engine.buffers.ShadowNeutralizationResult;
import com.schmaloogium.engine.buffers.ShadowOperationResult;
import com.schmaloogium.engine.buffers.ShadowPassSnapshot;
import com.schmaloogium.engine.buffers.TextureBindingResult;
import com.schmaloogium.engine.buffers.TextureBindingSnapshot;
import com.schmaloogium.engine.buffers.TextureOverlayLease;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.frame.FailureId;
import com.schmaloogium.engine.frame.ShadowFrameView;
import com.schmaloogium.engine.frame.ShadowInvocationContext;
import com.schmaloogium.engine.frame.ShadowInvocationResult;
import com.schmaloogium.engine.frame.ShadowInvocationSlot;
import com.schmaloogium.engine.frame.ShadowRejection;
import com.schmaloogium.engine.frame.ShadowSlotEpoch;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierResult;
import com.schmaloogium.engine.registry.BarrierParticipantResult;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramBindingSelections;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.PublishedProgramStateBarrier;
import com.schmaloogium.engine.registry.PublishedRegistry;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.registry.StageStep;
import com.schmaloogium.engine.registry.UseProgramRequest;
import com.schmaloogium.engine.shadow.CelestialMath;
import com.schmaloogium.engine.shadow.ShadowBindingSource;
import com.schmaloogium.engine.shadow.ShadowCameraMath;
import com.schmaloogium.engine.shadow.ShadowCameraProjection;
import com.schmaloogium.engine.shadow.ShadowDrawResult;
import com.schmaloogium.engine.shadow.ShadowFrustum;
import com.schmaloogium.engine.shadow.ShadowPlan;
import com.schmaloogium.engine.shadow.ShadowStateLease;
import com.schmaloogium.engine.shadow.ShadowTraversalPlan;
import com.schmaloogium.engine.shadow.ShadowTraversalPlanner;
import com.schmaloogium.engine.shadow.ShadowWorldPort;
import com.schmaloogium.engine.shadow.ShadowWorldSample;
import com.schmaloogium.engine.uniforms.ShadowMatrixSample;
import com.schmaloogium.engine.uniforms.UniformRuntime;

import java.util.List;
import java.util.Optional;

/**
 * The sole invocation slot (PHASE_8_DOC §4.2). Owns the PLANNED → READY → INVOKING →
 * CLOSED state machine and the single-shadow-snapshot-per-frame transaction; the
 * finally block runs every applicable cleanup independently, exactly once, in the
 * documented order. GL is reached only through the borrowed Phase 4/5/6 surfaces.
 */
final class ShadowInvocationSlotImpl implements ShadowInvocationSlot {

    private static final String CHANNEL = LogChannels.SHADOW;
    private static final String REPLAY_DELIVERY_FAILED = "phase6.replay.delivery.failed";
    private static final String CLOUD_HOOK = "H8-CLOUD-01-RESOLVE";

    private enum Lifecycle {
        READY, INVOKING, CLOSED
    }

    private enum SnapshotState {
        NONE, OPEN, COMPLETED, ABORTED
    }

    private final ShadowPlan plan;
    private final RegistryFingerprint registryFingerprint;
    private final UniformRuntime uniforms;
    private final ShadowWorldPort world;
    private final ShadowBindingSource bindings;
    private final java.util.function.BooleanSupplier renderThread;
    private final DiagnosticReporter reporter;
    private final ShadowCameraMath cameraMath;
    private final ShadowTraversalPlanner planner;
    private final ShadowSlotEpoch epoch = ShadowSlotEpochImpl.INSTANCE;

    private Lifecycle state = Lifecycle.READY;
    private boolean cloudsDisabledForPublication;
    private boolean mipmapsDisabled;

    ShadowInvocationSlotImpl(ShadowPlan plan, RegistryFingerprint registryFingerprint,
            UniformRuntime uniforms, ShadowWorldPort world, ShadowBindingSource bindings,
            java.util.function.BooleanSupplier renderThread, DiagnosticReporter reporter,
            ShadowCameraMath cameraMath, ShadowTraversalPlanner planner) {
        this.plan = plan;
        this.registryFingerprint = registryFingerprint;
        this.uniforms = uniforms;
        this.world = world;
        this.bindings = bindings;
        this.renderThread = renderThread;
        this.reporter = reporter;
        this.cameraMath = cameraMath;
        this.planner = planner;
    }

    ShadowSlotEpoch epoch() {
        return epoch;
    }

    /** Terminal release; idempotent per §2.3. */
    void close() {
        switch (state) {
            case READY -> state = Lifecycle.CLOSED;
            case CLOSED -> {
                // already closed
            }
            default -> throw new IllegalStateException("cannot close while invoking");
        }
    }

    boolean isClosed() {
        return state == Lifecycle.CLOSED;
    }

    @Override
    public ShadowSlotEpoch slotEpoch() {
        return epoch;
    }

    @Override
    public ShadowInvocationResult invoke(ShadowInvocationContext context) {
        if (!renderThread.getAsBoolean()) {
            return new ShadowInvocationResult.Rejected(ShadowRejection.WRONG_FRAME);
        }
        if (state != Lifecycle.READY) {
            return new ShadowInvocationResult.Rejected(ShadowRejection.WRONG_FRAME);
        }
        Transaction transaction = new Transaction(context);
        state = Lifecycle.INVOKING;
        try {
            return transaction.run();
        } finally {
            state = Lifecycle.READY;
        }
    }

    /** One §4.2 execution: all preflight, content, cleanup and result resolution. */
    private final class Transaction {

        private final ShadowInvocationContext context;
        private final ShadowFrameView shadowFrame;
        private final long frameId;

        private SnapshotState snapshotState = SnapshotState.NONE;
        private ShadowPassSnapshot snapshot;
        private ShadowEstateView shadow;
        private long estateGeneration;
        private ShadowStateLease stateLease;
        private TextureOverlayLease lease;
        private TextureBindingSnapshot binding;
        private boolean activationHappened;
        private boolean releaseProven;
        private FailureId failure;

        Transaction(ShadowInvocationContext context) {
            this.context = context;
            this.shadowFrame = context.shadowFrame();
            this.frameId = shadowFrame.frameId();
        }

        ShadowInvocationResult run() {
            ShadowInvocationResult preflight = preflight();
            if (preflight != null) {
                return preflight;
            }
            ShadowInvocationResult result = execute();
            return finish(result);
        }

        /** Steps 1-4: mutation-free protocol validation; null means continue. */
        private ShadowInvocationResult preflight() {
            if (context.frame().frameId() != frameId) {
                return reject(ShadowRejection.WRONG_FRAME);
            }
            PublishedRegistry registry = context.registry();
            Optional<ProgramRegistryView> registryView = registry.registry();
            if (registryView.isEmpty()) {
                return reject(ShadowRejection.STALE_PUBLICATION);
            }
            if (!registryView.get().fingerprint().equals(registryFingerprint)) {
                return reject(ShadowRejection.STALE_PUBLICATION);
            }
            if (registry.barrier().isEmpty()) {
                return reject(ShadowRejection.STALE_PUBLICATION);
            }
            if (!selectionValid(context)) {
                return reject(ShadowRejection.STALE_PUBLICATION);
            }
            if (context.selection().registryGeneration() != registry.generation()
                    || !context.selection().registryFingerprint().equals(registryFingerprint)) {
                return reject(ShadowRejection.STALE_PUBLICATION);
            }
            return estatePreflight();
        }

        /** Credential-anchored validation; a foreign selection cannot authenticate. */
        private boolean selectionValid(ShadowInvocationContext context) {
            try {
                return ProgramBindingSelections.validateSelection(
                        context.selection(), context.activationContext())
                        instanceof com.schmaloogium.engine.registry.ProgramSelectionValidation.Valid;
            } catch (RuntimeException unauthenticated) {
                return false;
            }
        }

        private ShadowInvocationResult estatePreflight() {
            PublishedBufferEstate published = context.buffers();
            Optional<BufferEstateView> estateView = published.estate();
            if (estateView.isEmpty()) {
                return reject(ShadowRejection.STALE_PUBLICATION);
            }
            BufferEstateView estate = estateView.get();
            if (estate.generation() != published.generation()
                    || !estate.registryFingerprint().equals(registryFingerprint)) {
                return reject(ShadowRejection.STALE_PUBLICATION);
            }
            ShadowEstateResult shadowResult = estate.shadow();
            if (shadowResult instanceof ShadowEstateNotRequested) {
                return new ShadowInvocationResult.Completed();
            }
            if (shadowResult instanceof ShadowEstateUnavailable unavailable) {
                report(DiagnosticSeverity.WARN,
                        "schmaloogium.shadow.estate.unavailable",
                        unavailable.reason().toString());
                return new ShadowInvocationResult.Completed();
            }
            if (!(shadowResult instanceof ShadowEstateAvailable available)) {
                return reject(ShadowRejection.STALE_PUBLICATION);
            }
            if (available.view().estateGeneration() != published.generation()) {
                return reject(ShadowRejection.STALE_PUBLICATION);
            }
            shadow = available.view();
            estateGeneration = available.view().estateGeneration();
            Optional<PassDescriptor> descriptor = shadowDescriptor();
            if (descriptor.isEmpty()) {
                return reject(ShadowRejection.STALE_PUBLICATION);
            }
            ShadowBeginResult begin = shadow.beginPass(frameId, descriptor.get(),
                    context.selection());
            if (!(begin instanceof ShadowBeginResult.Acquired acquired)) {
                report(DiagnosticSeverity.WARN, "schmaloogium.shadow.begin.rejected",
                        String.valueOf(begin));
                return reject(ShadowRejection.STALE_PUBLICATION);
            }
            snapshot = acquired.snapshot();
            snapshotState = SnapshotState.OPEN;
            return null;
        }

        private Optional<PassDescriptor> shadowDescriptor() {
            ProgramRegistryView view = context.registry().registry().orElseThrow();
            StageRegistry stages = view.stages();
            for (StageStep step : stages.schedule()) {
                if (step.stage() == StageId.SHADOW && step.band() == StageBand.SHADOW) {
                    PassDescriptor descriptor = stages.named(step, context.selection().requested())
                            .orElse(null);
                    if (descriptor == null
                            || !descriptor.slot().equals(context.selection().requested())) {
                        return Optional.empty();
                    }
                    return Optional.of(descriptor);
                }
            }
            return Optional.empty();
        }

        /** Steps 5-12: world sample, camera, state, GL content. */
        private ShadowInvocationResult execute() {
            ShadowWorldSample sample;
            ShadowCameraProjection camera;
            com.schmaloogium.engine.buffers.Extent2i extentForState = null;
            try {
                sample = world.sample(shadowFrame);
                if (!sample.frame().equals(shadowFrame) || !sample.cameraPresent()) {
                    abortSnapshot("schmaloogium.shadow.abort.stale_world_sample");
                    return reject(ShadowRejection.WRONG_FRAME);
                }
                Optional<BufferSizing> sizingShadow = Optional.of(
                        context.buffers().estate().orElseThrow().sizing());
                Optional<com.schmaloogium.engine.buffers.Extent2i> extent =
                        sizingShadow.get().shadowExtent();
                if (extent.isEmpty()) {
                    abortSnapshot("schmaloogium.shadow.abort.missing_extent");
                    return reject(ShadowRejection.STALE_PUBLICATION);
                }
                extentForState = extent.get();
                camera = cameraMath.compute(shadowFrame, context.camera(), plan, extentForState);
            } catch (RuntimeException e) {
                abortSnapshot("schmaloogium.shadow.abort.camera_math");
                report(DiagnosticSeverity.ERROR, "schmaloogium.shadow.camera_math.failed",
                        String.valueOf(e));
                return reject(ShadowRejection.UNSUPPORTED);
            }
            uniforms.events().updateCelestial(CelestialMath.sample(shadowFrame,
                    context.camera(), plan.policy().sunPathRotationDegrees()));
            uniforms.events().updateShadowMatrices(new ShadowMatrixSample(
                    shadowFrame.worldEpoch(), frameId, camera.projection(), camera.modelView()));

            ShadowFrustum frustum = cameraMath.frustum(camera);
            TraversalView traversalView = new TraversalView(
                    planner.plan(frustum, plan.policy(), camera.lightDirectionWorld(),
                            sample.viewDistanceChunks()),
                    frustum);
            switch (world.openState(camera, sample, extentForState)) {
                case ShadowWorldPort.ShadowStateResult.Opened opened -> stateLease = opened.lease();
                case ShadowWorldPort.ShadowStateResult.Rejected rejected -> {
                    abortSnapshot("schmaloogium.shadow.abort.state_capture");
                    report(DiagnosticSeverity.WARN, "schmaloogium.shadow.state.rejected",
                            rejected.reason().name());
                    return suppressedCompletion();
                }
                case ShadowWorldPort.ShadowStateResult.Failed failed -> {
                    abortSnapshot("schmaloogium.shadow.abort.state_capture");
                    return fail("schmaloogium.shadow.fail.state_" + failed.reason());
                }
                default -> throw new IllegalStateException("unknown state result");
            }

            ShadowOperationResult bind = shadow.bind(snapshot);
            if (!applyOperation(bind, ShadowNeutralReason.BIND_BACKEND_FAILURE)) {
                return failure();
            }
            ShadowOperationResult clear = shadow.clear(snapshot,
                    new ClearRequest(frameId, 1f, 1f, 1f, true));
            if (!applyOperation(clear, ShadowNeutralReason.CLEAR_BACKEND_FAILURE)) {
                return failure();
            }
            ShadowInvocationResult bindingOutcome = applyBinding();
            if (bindingOutcome != null) {
                return bindingOutcome;
            }

            PublishedProgramStateBarrier barrier = context.registry().barrier().orElseThrow();
            BarrierResult activation = barrier.activate(
                    new UseProgramRequest(context.selection(), context.activationContext()));
            if (!handleActivation(activation)) {
                return failure();
            }

            return drawContent(traversalView);
        }

        /**
         * §4.2 step 8: shadowtex0/1 binding parity through the separately acquired
         * overlay lease. Suppression (no publication or no lease) aborts the undrawn
         * snapshot without flips or mipmaps and completes after cleanup; backend
         * failure neutralizes.
         */
        private ShadowInvocationResult applyBinding() {
            Optional<com.schmaloogium.engine.buffers.TextureOverlayPublicationId> expected =
                    bindings.expectedOverlay();
            if (expected.isEmpty()) {
                abortSnapshot("schmaloogium.shadow.abort.binding_suppressed");
                report(DiagnosticSeverity.WARN, "schmaloogium.shadow.binding.suppressed",
                        "no texture publication in frame context");
                return suppressedCompletion();
            }
            Optional<TextureOverlayLease> acquired = bindings.acquire();
            if (acquired.isEmpty()) {
                abortSnapshot("schmaloogium.shadow.abort.binding_suppressed");
                report(DiagnosticSeverity.WARN, "schmaloogium.shadow.binding.suppressed",
                        "texture lease rejected");
                return suppressedCompletion();
            }
            lease = acquired.get();
            TextureBindingResult result = shadow.shadowBindings(
                    estateGeneration, frameId, snapshot, lease, expected.get());
            if (result instanceof TextureBindingResult.Bound bound) {
                binding = bound.snapshot();
                lease = null; // ownership transferred into the snapshot
                return null;
            }
            if (result instanceof TextureBindingResult.BackendFailed backendFailed) {
                abortSnapshot("schmaloogium.shadow.abort.binding");
                return neutralize(ShadowNeutralReason.BIND_BACKEND_FAILURE, backendFailed.failure())
                        ? failure() : failure();
            }
            abortSnapshot("schmaloogium.shadow.abort.binding");
            report(DiagnosticSeverity.WARN, "schmaloogium.shadow.binding.suppressed",
                    result.getClass().getSimpleName());
            return suppressedCompletion();
        }

        private boolean handleActivation(BarrierResult activation) {
            if (activation instanceof BarrierResult.Activated activated) {
                activationHappened = true;
                for (BarrierParticipantResult.Degraded degraded : activated.degradations()) {
                    report(DiagnosticSeverity.WARN, degraded.diagnosticId(),
                            degraded.disabledScope());
                    if (REPLAY_DELIVERY_FAILED.equals(degraded.diagnosticId())) {
                        // §6/D-P8-28: P7's receiver prerequisite is unmet for this frame;
                        // the latch forbids the next draw but this activation stays contained.
                        failure = new FailureId("schmaloogium.shadow.fail.replay_delivery");
                    }
                }
                return true;
            }
            if (activation instanceof BarrierResult.FixedFunction) {
                activationHappened = true;
                return true;
            }
            if (activation instanceof BarrierResult.Skipped) {
                abortSnapshot("schmaloogium.shadow.abort.activation_skipped");
                return true;
            }
            abortSnapshot("schmaloogium.shadow.abort.activation_rejected");
            report(DiagnosticSeverity.ERROR, "schmaloogium.shadow.activation.rejected",
                    activation.getClass().getSimpleName());
            failure = new FailureId("schmaloogium.shadow.fail.activation");
            return false;
        }

        /** §4.8 content order over the installed shadow camera. */
        private ShadowInvocationResult drawContent(TraversalView traversal) {
            ShadowInvocationResult setup = setupTerrain(traversal);
            if (setup != null) {
                return setup;
            }
            if (!drawTerrain(ShadowWorldPort.ShadowTerrainBand.SOLID, traversal)
                    || !drawTerrain(ShadowWorldPort.ShadowTerrainBand.CUTOUT_MIPPED, traversal)
                    || !drawTerrain(ShadowWorldPort.ShadowTerrainBand.CUTOUT, traversal)) {
                return failure();
            }
            if (!drawClouds(traversal)) {
                return failure();
            }
            if (!drawEntities(ShadowWorldPort.ShadowEntityPass.OPAQUE_ZERO, traversal)) {
                return failure();
            }
            ShadowOperationResult split = shadow.copyDepth(snapshot,
                    ShadowDepthCopyPoint.SHADOW_PRE_TRANSLUCENT);
            if (split instanceof ShadowOperationResult.Applied) {
                // only Applied permits post-split content
            } else if (split instanceof ShadowOperationResult.BackendFailed backend) {
                neutralize(ShadowNeutralReason.DEPTH_COPY_BACKEND_FAILURE, backend.failure());
                return failure();
            } else {
                abortSnapshot("schmaloogium.shadow.abort.depth_split");
                return fail("schmaloogium.shadow.fail.depth_split");
            }
            if (plan.policy().shadowTranslucent()) {
                if (!drawTerrain(ShadowWorldPort.ShadowTerrainBand.TRANSLUCENT, traversal)) {
                    return failure();
                }
            }
            if (!drawEntities(ShadowWorldPort.ShadowEntityPass.TRANSLUCENT_ONE, traversal)) {
                return failure();
            }
            return finishMipmapsAndComplete();
        }

        private ShadowInvocationResult setupTerrain(TraversalView traversal) {
            switch (world.setupTerrain(traversal)) {
                case ShadowDrawResult.Succeeded succeeded -> {
                    return null;
                }
                case ShadowDrawResult.Rejected rejected -> {
                    abortSnapshot("schmaloogium.shadow.abort.terrain_setup");
                    return fail("schmaloogium.shadow.fail.terrain_setup");
                }
                case ShadowDrawResult.Failed failed -> {
                    abortSnapshot("schmaloogium.shadow.abort.terrain_setup");
                    return fail(failed.diagnosticId());
                }
                default -> throw new IllegalStateException("unknown draw result");
            }
        }

        private boolean drawTerrain(ShadowWorldPort.ShadowTerrainBand band,
                TraversalView traversal) {
            return draw(world.drawTerrain(band, traversal), "terrain_" + band);
        }

        private boolean drawClouds(TraversalView traversal) {
            if (!plan.policy().cloudsInShadow() || cloudsDisabledForPublication
                    || sampleCloudModeOff()) {
                return true;
            }
            if (!isCloudHookHealthy()) {
                return true;
            }
            switch (world.drawClouds(traversal)) {
                case ShadowDrawResult.Succeeded succeeded -> {
                    return true;
                }
                case ShadowDrawResult.Rejected rejected -> {
                    cloudsDisabledForPublication = true;
                    report(DiagnosticSeverity.WARN, "schmaloogium.shadow.clouds.disabled",
                            String.valueOf(rejected.reason()));
                    return true;
                }
                case ShadowDrawResult.Failed failed -> {
                    cloudsDisabledForPublication = true;
                    report(DiagnosticSeverity.WARN, "schmaloogium.shadow.clouds.disabled",
                            failed.diagnosticId());
                    return true;
                }
                default -> throw new IllegalStateException("unknown draw result");
            }
        }

        private boolean sampleCloudModeOff() {
            try {
                return world.sample(shadowFrame).cloudMode()
                        == com.schmaloogium.engine.config.CloudMode.OFF;
            } catch (RuntimeException e) {
                return false;
            }
        }

        private boolean isCloudHookHealthy() {
            return plan.hookHealth().row(CLOUD_HOOK)
                    .map(row -> row.disposition() == com.schmaloogium.engine.shadow.HookDisposition.HEALTHY)
                    .orElse(false);
        }

        private boolean drawEntities(ShadowWorldPort.ShadowEntityPass pass,
                TraversalView traversal) {
            return draw(world.drawEntities(pass, traversal), "entities_" + pass);
        }

        private boolean draw(ShadowDrawResult result, String what) {
            if (result instanceof ShadowDrawResult.Succeeded) {
                return true;
            }
            if (result instanceof ShadowDrawResult.Rejected rejected) {
                abortSnapshot("schmaloogium.shadow.abort." + what);
                failure = new FailureId("schmaloogium.shadow.fail." + what);
                return false;
            }
            ShadowDrawResult.Failed failed = (ShadowDrawResult.Failed) result;
            abortSnapshot("schmaloogium.shadow.abort." + what);
            failure = new FailureId(failed.diagnosticId());
            return false;
        }

        private ShadowInvocationResult finishMipmapsAndComplete() {
            if (!mipmapsDisabled && !plan.policy().mipmaps().buffers().isEmpty()) {
                ShadowMipmapResult mipmaps = shadow.generateShadowMipmaps(estateGeneration,
                        frameId, snapshot, plan.policy().mipmaps());
                if (mipmaps instanceof ShadowMipmapResult.Neutralized neutralized) {
                    mipmapsDisabled = true;
                    report(DiagnosticSeverity.ERROR, neutralized.diagnosticId(),
                            neutralized.buffer().toString());
                    if (neutralized.openSnapshotAborted()) {
                        snapshotState = SnapshotState.ABORTED;
                        return suppressedCompletion();
                    }
                    return suppressedCompletion();
                }
                if (mipmaps instanceof ShadowMipmapResult.Rejected) {
                    abortSnapshot("schmaloogium.shadow.abort.mipmaps");
                    return fail("schmaloogium.shadow.fail.mipmaps");
                }
                if (mipmaps instanceof ShadowMipmapResult.Generated generated) {
                    for (ShadowMipmapOutcome outcome : generated.outcomes()) {
                        if (outcome instanceof ShadowMipmapOutcome.Degraded degraded) {
                            report(DiagnosticSeverity.WARN, degraded.diagnosticId(),
                                    degraded.buffer().toString());
                        }
                    }
                }
            }
            ShadowCompletionResult completion = shadow.completePass(snapshot);
            if (completion instanceof ShadowCompletionResult.Completed) {
                snapshotState = SnapshotState.COMPLETED;
                return new ShadowInvocationResult.Completed();
            }
            report(DiagnosticSeverity.ERROR, "schmaloogium.shadow.complete.rejected",
                    String.valueOf(completion));
            return fail("schmaloogium.shadow.fail.complete");
        }

        /** Applies a bind/clear result; false means the transaction has failed. */
        private boolean applyOperation(ShadowOperationResult result, ShadowNeutralReason reason) {
            if (result instanceof ShadowOperationResult.Applied) {
                return true;
            }
            if (result instanceof ShadowOperationResult.BackendFailed backend) {
                return neutralize(reason, backend.failure());
            }
            abortSnapshot("schmaloogium.shadow.abort.operation");
            report(DiagnosticSeverity.ERROR, "schmaloogium.shadow.operation.rejected",
                    String.valueOf(result));
            failure = new FailureId("schmaloogium.shadow.fail.operation");
            return false;
        }

        /** §6 neutralization: consume the P5 result, latch failure, stop drawing. */
        private boolean neutralize(ShadowNeutralReason reason,
                com.schmaloogium.engine.buffers.BufferFailure failed) {
            ShadowNeutralizationResult neutralization =
                    shadow.degradeToNeutral(estateGeneration, reason);
            if (neutralization instanceof ShadowNeutralizationResult.Neutralized neutralized) {
                if (neutralized.openSnapshotAborted()) {
                    snapshotState = SnapshotState.ABORTED;
                }
                report(DiagnosticSeverity.ERROR, neutralized.diagnosticId(),
                        failed.toString());
            } else if (neutralization instanceof ShadowNeutralizationResult.AlreadyNeutral already) {
                report(DiagnosticSeverity.WARN, already.diagnosticId(), reason.name());
            } else {
                report(DiagnosticSeverity.FATAL, "schmaloogium.shadow.neutralize.rejected",
                        String.valueOf(neutralization));
            }
            failure = new FailureId("schmaloogium.shadow.fail." + reason.name());
            return false;
        }

        /** The single finally: independent, exactly-once cleanup in documented order. */
        private ShadowInvocationResult finish(ShadowInvocationResult drafted) {
            RuntimeException cleanupError = null;
            if (drafted instanceof ShadowInvocationResult.Rejected
                    || drafted instanceof ShadowInvocationResult.Failed) {
                // keep the drafted non-completed result unless cleanup proves otherwise
            }
            // 1. snapshot terminal exactly once
            if (snapshotState == SnapshotState.OPEN) {
                try {
                    ShadowAbortResult abort = shadow.abortPass(snapshot,
                            "schmaloogium.shadow.abort.cleanup");
                    snapshotState = SnapshotState.ABORTED;
                    if (drafted instanceof ShadowInvocationResult.Completed
                            && !(abort instanceof ShadowAbortResult.Aborted)) {
                        failure = new FailureId("schmaloogium.shadow.fail.cleanup_abort");
                    }
                } catch (RuntimeException e) {
                    cleanupError = record(cleanupError, e);
                    failure = new FailureId("schmaloogium.shadow.fail.cleanup_abort");
                }
            }
            // 2. program release proof: exactly when a program was actually bound
            if (activationHappened) {
                try {
                    BarrierResult release = context.registry().barrier().orElseThrow()
                            .releaseToFixedFunction(context.barrierContexts().release());
                    if (release instanceof BarrierResult.FixedFunction) {
                        releaseProven = true;
                    } else if (release instanceof BarrierResult.ShadersOff
                            || release instanceof BarrierResult.FailedSafe
                            || release instanceof BarrierResult.StalePublication) {
                        failure = new FailureId("schmaloogium.shadow.fail.release");
                        report(DiagnosticSeverity.ERROR, "schmaloogium.shadow.release.rejected",
                                release.getClass().getSimpleName());
                    } else {
                        failure = new FailureId("schmaloogium.shadow.fail.release_protocol");
                        report(DiagnosticSeverity.ERROR, "schmaloogium.shadow.release.rejected",
                                release.getClass().getSimpleName());
                    }
                } catch (RuntimeException e) {
                    cleanupError = record(cleanupError, e);
                    failure = new FailureId("schmaloogium.shadow.fail.release");
                }
            }
            // 3. binding/lease closure: exactly the current owner
            try {
                closeBindingOwner();
            } catch (RuntimeException e) {
                cleanupError = record(cleanupError, e);
                failure = new FailureId("schmaloogium.shadow.fail.binding_close");
            }
            // 4. state restoration in reverse acquisition order
            if (stateLease != null) {
                try {
                    stateLease.restore();
                } catch (RuntimeException e) {
                    cleanupError = record(cleanupError, e);
                    failure = new FailureId("schmaloogium.shadow.fail.restore");
                }
                stateLease = null;
            }
            if (cleanupError != null) {
                report(DiagnosticSeverity.ERROR, "schmaloogium.shadow.cleanup.threw",
                        String.valueOf(cleanupError));
            }
            if (failure != null) {
                return new ShadowInvocationResult.Failed(failure);
            }
            if (drafted instanceof ShadowInvocationResult.Completed) {
                if (!activationHappened || releaseProven) {
                    return new ShadowInvocationResult.Completed();
                }
                return new ShadowInvocationResult.Failed(
                        new FailureId("schmaloogium.shadow.fail.unproven_release"));
            }
            return drafted;
        }

        private void closeBindingOwner() {
            if (binding != null) {
                binding.close();
                binding = null;
            } else if (lease != null) {
                lease.close();
                lease = null;
            }
        }

        private ShadowInvocationResult suppressedCompletion() {
            return new ShadowInvocationResult.Completed();
        }

        private void abortSnapshot(String diagnosticId) {
            if (snapshotState != SnapshotState.OPEN) {
                return;
            }
            try {
                shadow.abortPass(snapshot, diagnosticId);
            } finally {
                snapshotState = SnapshotState.ABORTED;
            }
        }

        private ShadowInvocationResult reject(ShadowRejection reason) {
            return new ShadowInvocationResult.Rejected(reason);
        }

        private ShadowInvocationResult fail(String diagnosticId) {
            failure = new FailureId(diagnosticId);
            return new ShadowInvocationResult.Failed(failure);
        }

        private ShadowInvocationResult failure() {
            return new ShadowInvocationResult.Failed(failure == null
                    ? new FailureId("schmaloogium.shadow.fail.unknown")
                    : failure);
        }

        private RuntimeException record(RuntimeException existing, RuntimeException next) {
            if (existing == null) {
                return next;
            }
            existing.addSuppressed(next);
            return existing;
        }

        private void report(DiagnosticSeverity severity, String key, String detail) {
            if (reporter == null) {
                return;
            }
            reporter.report(new EngineDiagnostic(severity, UserChannel.LOG_ONLY, key,
                    List.of(), detail, CHANNEL));
        }
    }

    /** The traversal view handed to the world port. */
    private record TraversalView(ShadowTraversalPlan plan, ShadowFrustum frustum)
            implements ShadowWorldPort.ShadowTraversalView {
    }
}
