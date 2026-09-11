// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.lifecycle;

import com.schmaloogium.engine.buffers.BufferEstateView;
import com.schmaloogium.engine.buffers.ClearExecutionResult;
import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.FrameBeginResult;
import com.schmaloogium.engine.buffers.FrameEndResult;
import com.schmaloogium.engine.buffers.MainDepthPreparation;
import com.schmaloogium.engine.buffers.MainDepthRefreshResult;
import com.schmaloogium.engine.buffers.PassBufferSnapshot;
import com.schmaloogium.engine.buffers.PassCompletionResult;
import com.schmaloogium.engine.buffers.PassDiscardResult;
import com.schmaloogium.engine.buffers.PassDrawTarget;
import com.schmaloogium.engine.buffers.PassSnapshotResult;
import com.schmaloogium.engine.frame.AnaglyphEye;
import com.schmaloogium.engine.frame.CameraSnapshot;
import com.schmaloogium.engine.frame.DrawDisposition;
import com.schmaloogium.engine.frame.FailureId;
import com.schmaloogium.engine.frame.FinalizedFrame;
import com.schmaloogium.engine.frame.FrameAbortReason;
import com.schmaloogium.engine.frame.FrameAbortResult;
import com.schmaloogium.engine.frame.FrameBeginSignal;
import com.schmaloogium.engine.frame.FrameExitKind;
import com.schmaloogium.engine.frame.FrameFinishResult;
import com.schmaloogium.engine.frame.FrameHookSink;
import com.schmaloogium.engine.frame.FrameOpenRejection;
import com.schmaloogium.engine.frame.FrameOpenResult;
import com.schmaloogium.engine.frame.FrameReadiness;
import com.schmaloogium.engine.frame.FrameState;
import com.schmaloogium.engine.frame.FrameStepResult;
import com.schmaloogium.engine.frame.FrameToken;
import com.schmaloogium.engine.frame.HookRejection;
import com.schmaloogium.engine.frame.ScopeCloseResult;
import com.schmaloogium.engine.frame.ScopeOpenResult;
import com.schmaloogium.engine.frame.ScopeToken;
import com.schmaloogium.engine.frame.dispatch.PhaseDispatchTable;
import com.schmaloogium.engine.frame.dispatch.RenderSection;
import com.schmaloogium.engine.frame.dispatch.SectionWindow;
import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;
import com.schmaloogium.engine.frame.spi.AtlasBindingSink;
import com.schmaloogium.engine.frame.spi.FrameCompletionObserver;
import com.schmaloogium.engine.frame.spi.FrameRenderPort;
import com.schmaloogium.engine.frame.spi.PortResult;
import com.schmaloogium.engine.frame.spi.SignalResult;
import com.schmaloogium.engine.frame.spi.UniformSignal;
import com.schmaloogium.engine.frame.spi.UniformSignalBridge;
import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierResult;
import com.schmaloogium.engine.registry.FrameBarrierContexts;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramSelectionResult;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.PublishedProgramStateBarrier;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.registry.StageStep;
import com.schmaloogium.engine.registry.UseProgramRequest;
import com.schmaloogium.engine.uniforms.BlendSample;
import com.schmaloogium.engine.uniforms.CelestialSample;
import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.uniforms.FogSample;
import com.schmaloogium.engine.uniforms.FrameBeginInput;
import com.schmaloogium.engine.uniforms.UniformEventSink;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;

/**
 * The pure render-thread frame driver (PHASE_7_DOC §4.2/§4.3): the exact frame-begin
 * ordering (D-P7-76), the closed hook result algebras, the nested scope stack with the
 * seven-step enter, the composite guarantee, and the NotInstalled shadow slot at v0.1.
 * No Minecraft, Forge, Mixin, LWJGL or raw GL type appears here; the loader glue adapts.
 *
 * <p>Threading: {@link #open} answers {@code WRONG_THREAD} through its algebra; every
 * other sink entry throws {@code IllegalStateException} off the render thread, before
 * reading any state (§7, matching the Phase-6 runtime convention).
 */
public final class FrameDriver implements FrameHookSink {

    private static final String CHANNEL = "schmaloogium.frame";

    private final BooleanSupplier renderThread;
    private final FrameCompositionSource compositions;
    private final PhaseDispatchTable dispatch;
    private final EngineShadowExecutionBridge shadowBridge;
    private final SignalBridge signalBridge = new SignalBridge();
    private final AtlasSink atlasSink = new AtlasSink();

    private long frameIdSource;
    private Frame frame;
    private boolean shadersOff;
    private long consecutiveFinalizedFrames;
    private FailureId lastFailure;

    public FrameDriver(BooleanSupplier renderThread, FrameCompositionSource compositions) {
        this.renderThread = Objects.requireNonNull(renderThread, "renderThread");
        this.compositions = Objects.requireNonNull(compositions, "compositions");
        this.dispatch = PhaseDispatchTable.classic();
        this.shadowBridge = new EngineShadowExecutionBridge(renderThread);
    }

    // ------------------------------------------------------------------ owned seams

    /** The engine-owned uniform signal bridge: the sole hook→Phase-6 event route. */
    public UniformSignalBridge uniformSignals() {
        return signalBridge;
    }

    /** The driver-owned shadow execution bridge (exact open/validate/close law). */
    public EngineShadowExecutionBridge shadowBridge() {
        return shadowBridge;
    }

    /** The authenticated atlas adapter sink (binding-observer route). */
    public AtlasBindingSink atlasBindings() {
        return atlasSink;
    }

    /** True when a previous failure latched shaders-off recovery for the session. */
    public boolean isShadersOff() {
        return shadersOff;
    }

    // ------------------------------------------------------------------ FrameHookSink

    @Override
    public FrameOpenResult open(FrameBeginSignal signal) {
        if (signal == null) {
            return new FrameOpenResult.VanillaOnly(FrameOpenRejection.INVALID_INPUT);
        }
        if (!renderThread.getAsBoolean()) {
            return new FrameOpenResult.VanillaOnly(FrameOpenRejection.WRONG_THREAD);
        }
        if (frame != null && !frame.terminal) {
            return new FrameOpenResult.VanillaOnly(FrameOpenRejection.FRAME_ALREADY_OPEN);
        }
        if (shadersOff) {
            return new FrameOpenResult.VanillaOnly(FrameOpenRejection.SHADERS_OFF);
        }
        Optional<FrameComposition> composition = compositions.current();
        if (composition.isEmpty()) {
            return new FrameOpenResult.VanillaOnly(FrameOpenRejection.SHADERS_OFF);
        }
        if (signal.vanillaPass() != 0) {
            return new FrameOpenResult.VanillaOnly(FrameOpenRejection.NON_WORLD_PASS);
        }
        Extent2i target = signal.targetView();
        if (target.width() <= 0 || target.height() <= 0) {
            return new FrameOpenResult.VanillaOnly(FrameOpenRejection.INVALID_INPUT);
        }
        FrameComposition active = composition.get();
        long frameId = ++frameIdSource;
        com.schmaloogium.engine.uniforms.FrameBeginResult p6 = active.uniforms().beginFrame(
                new FrameBeginInput(
                        active.registry().generation(),
                        frameId,
                        signal.worldEpoch(),
                        signal.logicalTick(),
                        signal.smoothingTimeTicks(),
                        signal.frameTimeSeconds(),
                        target.width(),
                        target.height(),
                        signal.priorCompletedFramebuffer().width(),
                        signal.priorCompletedFramebuffer().height()));
        if (p6 == com.schmaloogium.engine.uniforms.FrameBeginResult.REJECTED_STALE_FRAME
                || p6 == com.schmaloogium.engine.uniforms.FrameBeginResult.REJECTED_GENERATION) {
            // Forbids shader drawing; glue reacquires the current publication.
            return new FrameOpenResult.VanillaOnly(FrameOpenRejection.STALE_PUBLICATION);
        }
        // ACCEPTED and DUPLICATE both continue (a duplicate begin is safe for a live runtime).
        FrameBarrierContexts contexts = active.registry().contexts().beginFrame();
        frame = new Frame(FrameToken.mint(frameId), frameId, active, signal, contexts);
        return new FrameOpenResult.Opened(frame.token);
    }

    @Override
    public FrameStepResult beforeFirstClear(FrameToken token) {
        requireRenderThread();
        Frame f = authenticated(token);
        if (f == null) {
            return rejected(HookRejection.WRONG_TOKEN);
        }
        if (f.phase != Phase.SAMPLED) {
            return rejected(HookRejection.WRONG_ORDER);
        }
        PortResult normalized = f.composition.port().normalizeForEngine();
        if (!(normalized instanceof PortResult.Completed)) {
            return failed(f, "schmaloogium.frame.error.normalize");
        }
        return new FrameStepResult.Advanced(FrameState.SAMPLED);
    }

    @Override
    public FrameStepResult afterFirstClear(FrameToken token, MainDepthPreparation depth) {
        requireRenderThread();
        Objects.requireNonNull(depth, "depth");
        Frame f = authenticated(token);
        if (f == null) {
            return rejected(HookRejection.WRONG_TOKEN);
        }
        if (f.phase != Phase.SAMPLED) {
            return rejected(HookRejection.WRONG_ORDER);
        }
        // Abandon before Phase 5 is touched when the borrowed main depth is not ready.
        if (!(depth instanceof MainDepthPreparation.Ready)) {
            abortInternal(f, FrameAbortReason.RESIZE_EPOCH, CHANNEL + ".abort.depth-not-ready");
            return new FrameStepResult.Aborted(FrameAbortReason.RESIZE_EPOCH);
        }
        BufferEstateView view = f.estateView();
        MainDepthRefreshResult refresh = view.refreshMainDepth();
        if (refresh instanceof MainDepthRefreshResult.ResizeRequired) {
            abortInternal(f, FrameAbortReason.RESIZE_EPOCH, CHANNEL + ".abort.resize");
            return new FrameStepResult.Aborted(FrameAbortReason.RESIZE_EPOCH);
        }
        if (refresh instanceof MainDepthRefreshResult.Failed) {
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.depth-refresh");
            return new FrameStepResult.Aborted(FrameAbortReason.BACKEND_FAILURE);
        }
        // Unchanged and Reattached both continue.
        FrameBeginResult begun = view.beginFrame(f.frameId);
        if (!(begun instanceof FrameBeginResult.Begun)) {
            FrameAbortReason reason = begun instanceof FrameBeginResult.BackendFailed
                    ? FrameAbortReason.BACKEND_FAILURE : FrameAbortReason.PROTOCOL_REJECTION;
            abortInternal(f, reason, CHANNEL + ".abort.p5-begin");
            return new FrameStepResult.Aborted(reason);
        }
        f.phase = Phase.BUFFER_OPEN;
        return new FrameStepResult.Advanced(FrameState.BUFFER_OPEN);
    }

    @Override
    public FrameStepResult captureMainCamera(FrameToken token, CameraSnapshot camera) {
        requireRenderThread();
        Objects.requireNonNull(camera, "camera");
        Frame f = authenticated(token);
        if (f == null) {
            return rejected(HookRejection.WRONG_TOKEN);
        }
        if (f.phase != Phase.BUFFER_OPEN) {
            return rejected(HookRejection.WRONG_ORDER);
        }
        if (f.matricesCaptured) {
            return rejected(HookRejection.WRONG_ORDER);
        }
        // Exactly once per frame, at the exact post-camera point (D-P6-19).
        f.composition.uniforms().events().captureGbufferMatrices(
                f.frameId, camera.modelView(), camera.projection());
        f.matricesCaptured = true;
        f.phase = Phase.MATRICES_CAPTURED;
        // D-P7-76: immediately bind the main estate and run P5's one clear plan, before
        // vanilla sky. Only SUCCESS admits any gbuffers scope.
        ClearExecutionResult clear = executeMainClear(f);
        if (clear != ClearExecutionResult.SUCCESS) {
            FrameAbortReason reason = clear == ClearExecutionResult.BACKEND_FAILED
                    ? FrameAbortReason.BACKEND_FAILURE : FrameAbortReason.PROTOCOL_REJECTION;
            abortInternal(f, reason, CHANNEL + ".abort.main-clear");
            return new FrameStepResult.Aborted(reason);
        }
        f.phase = Phase.ESTATE_CLEARED;
        return new FrameStepResult.Advanced(FrameState.ESTATE_CLEARED);
    }

    @Override
    public FrameStepResult afterTerrainSetup(FrameToken token) {
        requireRenderThread();
        Frame f = authenticated(token);
        if (f == null) {
            return rejected(HookRejection.WRONG_TOKEN);
        }
        if (f.phase != Phase.ESTATE_CLEARED) {
            return rejected(HookRejection.WRONG_ORDER);
        }
        if (!f.scopes.isEmpty()) {
            // Sky scopes must have closed before the shadow slot.
            return rejected(HookRejection.WRONG_ORDER);
        }
        // The v0.1 slot is absent/NotInstalled: no shadow GL ran, the main-estate rebind
        // is trivially successful, and SHADOW_DONE admits terrain and later scopes.
        f.phase = Phase.SHADOW_DONE;
        return new FrameStepResult.Advanced(FrameState.SHADOW_DONE);
    }

    @Override
    public ScopeOpenResult enter(FrameToken token, RenderSection section) {
        requireRenderThread();
        Objects.requireNonNull(section, "section");
        Frame f = authenticated(token);
        if (f == null) {
            return new ScopeOpenResult.Rejected(HookRejection.WRONG_TOKEN);
        }
        if (f.phase != Phase.ESTATE_CLEARED && f.phase != Phase.SHADOW_DONE
                && f.phase != Phase.GBUFFERS && f.phase != Phase.DEFERRED_DONE
                && f.phase != Phase.GBUFFERS_TRANS) {
            return new ScopeOpenResult.Rejected(HookRejection.WRONG_ORDER);
        }
        PhaseDispatchTable.Routing route = dispatch.route(section)
                .orElseThrow(() -> new IllegalStateException("uncatalogued section: " + section));
        switch (route.window()) {
            case ESTATE_CLEARED -> {
                if (f.phase != Phase.ESTATE_CLEARED) {
                    return new ScopeOpenResult.Rejected(HookRejection.WRONG_ORDER);
                }
            }
            case GBUFFERS_OPAQUE -> {
                if (f.phase != Phase.SHADOW_DONE && f.phase != Phase.GBUFFERS) {
                    return new ScopeOpenResult.Rejected(HookRejection.WRONG_ORDER);
                }
                f.phase = Phase.GBUFFERS;
            }
            case GBUFFERS_TRANSLUCENT -> {
                if (f.phase != Phase.DEFERRED_DONE && f.phase != Phase.GBUFFERS_TRANS) {
                    return new ScopeOpenResult.Rejected(HookRejection.WRONG_ORDER);
                }
                f.phase = Phase.GBUFFERS_TRANS;
            }
            case TRANSLUCENT_TRIGGER -> {
                if (f.phase != Phase.GBUFFERS && f.phase != Phase.DEFERRED_DONE) {
                    return new ScopeOpenResult.Rejected(HookRejection.WRONG_ORDER);
                }
                // H-TERRAIN-02 trigger: close the opaque scope, run the deferred family,
                // then open the water scope.
                FrameStepResult closed = closeTopForTrigger(f);
                if (closed != null) {
                    return new ScopeOpenResult.Aborted(((FrameStepResult.Aborted) closed).reason());
                }
                f.phase = Phase.DEFERRED_DONE;
            }
        }
        // Suspend an open parent before pushing the child scope (§4.4 step 1).
        OpenScope parent = f.scopes.peek();
        if (parent != null && parent.snapshot != null) {
            BarrierResult released = f.barrier().releaseToFixedFunction(f.contexts.release());
            if (!(released instanceof BarrierResult.FixedFunction)) {
                return suspensionFailed(f);
            }
            parent.suspended = true;
        }
        try {
            OpenScope scope = openScope(f, section, route.requested());
            f.scopes.push(scope);
            return new ScopeOpenResult.Opened(scope.token, scope.disposition);
        } catch (StalePublicationSignal signal) {
            // Mutation-free; the glue reacquires the current publication.
            return new ScopeOpenResult.Rejected(HookRejection.STALE_PUBLICATION);
        } catch (RuntimeFailureSignal signal) {
            return new ScopeOpenResult.Failed(signal.failure);
        }
    }

    @Override
    public ScopeCloseResult exit(FrameToken token, ScopeToken scope) {
        requireRenderThread();
        Objects.requireNonNull(scope, "scope");
        Frame f = authenticated(token);
        if (f == null) {
            return new ScopeCloseResult.Rejected(HookRejection.WRONG_TOKEN);
        }
        OpenScope top = f.scopes.peek();
        if (top == null) {
            return new ScopeCloseResult.Rejected(HookRejection.WRONG_TOKEN);
        }
        if (top.token != scope) {
            // A valid token that is not the innermost open scope closes out of order.
            boolean known = f.scopes.stream().anyMatch(s -> s.token == scope);
            return new ScopeCloseResult.Rejected(known ? HookRejection.WRONG_ORDER : HookRejection.WRONG_TOKEN);
        }
        f.scopes.pop();
        if (!closeScope(f, top)) {
            return new ScopeCloseResult.Aborted(FrameAbortReason.BACKEND_FAILURE);
        }
        DrawDisposition resumed = DrawDisposition.DRAW_FIXED_FUNCTION;
        OpenScope parent = f.scopes.peek();
        if (parent != null && parent.suspended) {
            parent.suspended = false;
            if (!reactivate(f, parent)) {
                return new ScopeCloseResult.Aborted(FrameAbortReason.BACKEND_FAILURE);
            }
            resumed = parent.disposition;
        }
        return new ScopeCloseResult.Closed(resumed);
    }

    @Override
    public FrameFinishResult finish(FrameToken token, FrameExitKind exitKind) {
        requireRenderThread();
        Objects.requireNonNull(exitKind, "exitKind");
        Frame f = frame;
        if (f == null || f.terminal) {
            return new FrameFinishResult.AlreadyTerminal();
        }
        if (token == null || f.token != token) {
            // A corrupted token aborts instead of finalizing (§8 behavioral case 2).
            abortInternal(f, FrameAbortReason.PROTOCOL_REJECTION, CHANNEL + ".abort.finish-token");
            return new FrameFinishResult.Aborted(FrameAbortReason.PROTOCOL_REJECTION);
        }
        if (f.finalizationStarted) {
            // The losing TAIL/finally call: mandatory no-op.
            return new FrameFinishResult.AlreadyTerminal();
        }
        f.finalizationStarted = true;
        f.phase = Phase.FINALIZING;
        try {
            drainScopes(f);
            runDeferredAndFinal(f);
        } catch (RuntimeException e) {
            String cause = e instanceof RuntimeFailureSignal signal
                    ? signal.failure.diagnosticId() : e.toString();
            if (!(e instanceof RuntimeFailureSignal)) {
                com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.FRAME)
                        .error(e, "frame {} finish work threw", f.frameId);
            }
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.finish-work:" + cause);
            return new FrameFinishResult.Failed(failure(CHANNEL + ".failure.finish-work:" + cause));
        }
        FrameEndResult committed = f.estateView().commitFrame(f.frameId);
        if (!(committed instanceof FrameEndResult.Committed)) {
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.commit");
            return new FrameFinishResult.Failed(failure(CHANNEL + ".failure.commit"));
        }
        // Release Phase 4 to fixed function with the frame's canonical release context.
        BarrierResult released = f.barrier().releaseToFixedFunction(f.contexts.release());
        if (!(released instanceof BarrierResult.FixedFunction)) {
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.release");
            return new FrameFinishResult.Failed(failure(CHANNEL + ".failure.release"));
        }
        f.phase = Phase.COMMITTED;
        f.terminal = true;
        consecutiveFinalizedFrames++;
        FinalizedFrame summary = f.summary(exitKind == FrameExitKind.NORMAL,
                (int) Math.min(Integer.MAX_VALUE, consecutiveFinalizedFrames + 1),
                lastFailure);

        Optional<FrameCompletionObserver> observer = f.composition.completionObserver();
        if (observer.isPresent()) {
            // H-CAPTURE-01: after final pixels exist, before presentation.
            observer.get().beforePresent(summary);
        }
        frame = null;
        return new FrameFinishResult.Finalized(summary);
    }

    @Override
    public FrameAbortResult abort(FrameToken token, FrameAbortReason reason) {
        requireRenderThread();
        Objects.requireNonNull(reason, "reason");
        Frame f = frame;
        if (f == null || f.terminal) {
            return new FrameAbortResult.AlreadyTerminal();
        }
        // A corrupted token aborts the live frame instead of doing nothing (§8 case 2).
        abortInternal(f, reason, CHANNEL + ".abort.requested");
        return new FrameAbortResult.Aborted(reason);
    }

    // ------------------------------------------------------------------ internals

    private Frame authenticated(FrameToken token) {
        Frame f = frame;
        if (f == null || f.terminal || token == null || f.token != token) {
            return null;
        }
        return f;
    }

    private void requireRenderThread() {
        if (!renderThread.getAsBoolean()) {
            throw new IllegalStateException(CHANNEL + " is render-thread confined");
        }
    }

    private static FrameStepResult rejected(HookRejection reason) {
        return new FrameStepResult.Rejected(reason);
    }

    private static FailureId failure(String diagnosticId) {
        return new FailureId(diagnosticId);
    }

    private FrameStepResult failed(Frame f, String diagnosticId) {
        abortInternal(f, FrameAbortReason.BACKEND_FAILURE, diagnosticId);
        return new FrameStepResult.Failed(failure(diagnosticId));
    }

    private ClearExecutionResult executeMainClear(Frame f) {
        Float3 fog = f.fogColor;
        ClearRequest request = new ClearRequest(f.frameId, fog.x(), fog.y(), fog.z(), false);
        return f.estateView().executeClear(f.estateView().clearPlan(request));
    }

    private OpenScope openScope(Frame f, RenderSection section, ProgramSlotId requested) {
        BarrierContext context = f.contexts.activation(f.stepFor(requested), false);
        OpenScope scope = new OpenScope(ScopeToken.mint(), section, requested, context);
        PublishedProgramStateBarrier barrier = f.barrier();
        ProgramSelectionResult selected = barrier.select(requested, context);
        if (selected instanceof ProgramSelectionResult.StalePublication) {
            // Mutation-free; the glue reacquires the current publication.
            throw new StalePublicationSignal();
        }
        if (selected instanceof ProgramSelectionResult.ShadersOff off) {
            latchShadersOff();
            throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.select-off"));
        }
        if (selected instanceof ProgramSelectionResult.Skipped) {
            scope.disposition = DrawDisposition.OMIT_OPERATION;
            return scope;
        }
        ProgramBindingSelection selection = ((ProgramSelectionResult.Selected) selected).selection();
        scope.selection = selection;
        PassDescriptor descriptor = f.descriptorFor(requested);
        PassSnapshotResult snap = f.estateView().snapshot(descriptor, selection);
        if (snap instanceof PassSnapshotResult.Failed failed) {
            // D-P7-45: the transaction is already unhealthy; containment then shaders-off.
            latchShadersOff();
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, failed.diagnosticId());
            throw new RuntimeFailureSignal(failure(failed.diagnosticId()));
        }
        if (snap instanceof PassSnapshotResult.Rejected) {
            // Ordinary live-frame protocol recovery: suppress only this operation.
            scope.disposition = DrawDisposition.OMIT_OPERATION;
            return scope;
        }
        PassBufferSnapshot snapshot = ((PassSnapshotResult.Acquired) snap).snapshot();
        scope.snapshot = snapshot;
        PortResult bound = f.composition.port().bind(snapshot.drawTarget(), f.eye());
        if (!(bound instanceof PortResult.Completed)) {
            // Degrade locally: vanilla draws, the engine pass stays undrawn.
            scope.disposition = DrawDisposition.DRAW_FIXED_FUNCTION;
            scope.discardSnapshotOnClose = true;
            return scope;
        }
        // v0.1: the explicit empty texture publication — no lease or texture-row work
        // until Phase 13 lands; P5's textureBindings call joins then.
        BarrierResult activation = barrier.activate(new UseProgramRequest(selection, context));
        if (activation instanceof BarrierResult.Activated) {
            scope.disposition = DrawDisposition.DRAW_SHADER;
            scope.activated = true;
            return scope;
        }
        if (activation instanceof BarrierResult.FixedFunction) {
            scope.disposition = DrawDisposition.DRAW_FIXED_FUNCTION;
            scope.discardSnapshotOnClose = true;
            return scope;
        }
        if (activation instanceof BarrierResult.Skipped) {
            scope.disposition = DrawDisposition.OMIT_OPERATION;
            scope.discardSnapshotOnClose = true;
            return scope;
        }
        if (activation instanceof BarrierResult.StalePublication) {
            throw new StalePublicationSignal();
        }
        // Carry the barrier's own diagnostic so the failure names its cause.
        String cause = activation instanceof BarrierResult.ShadersOff off ? off.diagnosticId()
                : activation instanceof BarrierResult.FailedSafe safe ? safe.diagnosticId()
                : activation.getClass().getSimpleName();
        latchShadersOff();
        abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.activate");
        throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.activate:" + cause));
    }

    private boolean closeScope(Frame f, OpenScope scope) {
        if (scope.snapshot != null) {
            boolean drawn = scope.disposition == DrawDisposition.DRAW_SHADER && scope.activated;
            if (drawn) {
                PassCompletionResult completed = f.estateView().completePass(scope.snapshot);
                if (!(completed instanceof PassCompletionResult.Completed)) {
                    logCloseFailure(f, scope, "completePass", completed);
                    return false;
                }
                scope.snapshot = null;
            } else if (scope.discardSnapshotOnClose || scope.disposition != DrawDisposition.DRAW_SHADER) {
                PassDiscardResult discarded = f.estateView().discardPass(scope.snapshot);
                if (!(discarded instanceof PassDiscardResult.Discarded)) {
                    logCloseFailure(f, scope, "discardPass", discarded);
                    return false;
                }
                scope.snapshot = null;
            }
        }
        if (scope.activated) {
            // Release takes a RELEASE-kind context minted from this frame's contexts (P4
            // §4.10), never the scope's activation context.
            BarrierResult released = f.barrier().releaseToFixedFunction(f.contexts.release());
            if (!(released instanceof BarrierResult.FixedFunction)) {
                logCloseFailure(f, scope, "releaseToFixedFunction", released);
                return false;
            }
            scope.activated = false;
        }
        return true;
    }

    /** Evidence for a failed scope close: the step and the closed result it answered. */
    private static void logCloseFailure(Frame f, OpenScope scope, String step, Object result) {
        com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.FRAME)
                .warn("frame {} scope {} close failed at {}: {}", f.frameId, scope.section, step, result);
    }

    private boolean reactivate(Frame f, OpenScope parent) {
        if (parent.selection == null || parent.snapshot == null) {
            return parent.selection == null; // an omitted operation has nothing to reactivate
        }
        PortResult bound = f.composition.port().bind(parent.snapshot.drawTarget(), f.eye());
        if (!(bound instanceof PortResult.Completed)) {
            return false;
        }
        BarrierResult activation = f.barrier()
                .activate(new UseProgramRequest(parent.selection, parent.context));
        if (activation instanceof BarrierResult.Activated) {
            parent.disposition = DrawDisposition.DRAW_SHADER;
            parent.activated = true;
            return true;
        }
        if (activation instanceof BarrierResult.FixedFunction) {
            parent.disposition = DrawDisposition.DRAW_FIXED_FUNCTION;
            return true;
        }
        return false;
    }

    private FrameStepResult closeTopForTrigger(Frame f) {
        OpenScope top = f.scopes.peek();
        if (top == null) {
            return null;
        }
        f.scopes.pop();
        if (!closeScope(f, top)) {
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.trigger-close");
            return new FrameStepResult.Aborted(FrameAbortReason.BACKEND_FAILURE);
        }
        return null;
    }

    private void drainScopes(Frame f) {
        while (!f.scopes.isEmpty()) {
            OpenScope scope = f.scopes.pop();
            if (!closeScope(f, scope)) {
                throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.scope-close"));
            }
        }
    }

    /** Executes the deferred/composite family and the FINAL screen pass exactly once. */
    private void runDeferredAndFinal(Frame f) {
        StageRegistry registry = f.registryView();
        boolean sawFinal = false;
        for (StageStep step : registry.schedule()) {
            if (step.band() == StageBand.BETWEEN_GBUFFERS) {
                for (PassDescriptor descriptor : registry.passes(step)) {
                    if (isVirtualPrelude(descriptor)) {
                        applyVirtualPrelude(f, descriptor);
                        continue;
                    }
                    executeFullscreen(f, descriptor);
                }
                f.phase = Phase.DEFERRED_DONE;
            } else if (step.band() == StageBand.SCREEN) {
                for (PassDescriptor descriptor : registry.passes(step)) {
                    if (isVirtualPrelude(descriptor)) {
                        applyVirtualPrelude(f, descriptor);
                        continue;
                    }
                    executeFullscreen(f, descriptor);
                    sawFinal = true;
                }
            }
        }
        // A registry without a SCREEN step composites nothing (internal pack always has one).
    }

    /**
     * The programless {@code deferred_pre}/{@code composite_pre} prelude (§4.5, §4.7): no
     * index, no resources, no compute slots. It bypasses selection and only forwards its
     * flips to Phase 5's {@code applyVirtualTransition}.
     */
    private static boolean isVirtualPrelude(PassDescriptor descriptor) {
        return descriptor.index().isEmpty()
                && descriptor.computeSlots().isEmpty()
                && descriptor.resources().readable().isEmpty()
                && descriptor.resources().writes().isEmpty()
                && descriptor.resources().mipmappedBeforeRead().isEmpty()
                && descriptor.slot().packName().endsWith("_pre");
    }

    private void applyVirtualPrelude(Frame f, PassDescriptor descriptor) {
        var result = f.estateView().applyVirtualTransition(f.frameId, descriptor);
        if (result instanceof com.schmaloogium.engine.buffers.VirtualTransitionResult.Rejected rejected) {
            boolean requestedFlip = descriptor.resources().explicitFlips().containsValue(Boolean.TRUE);
            if (requestedFlip) {
                latchShadersOff();
                throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.virtual-prelude:"
                        + descriptor.slot().packName() + ":" + rejected.reason()));
            }
            // A flip-less prelude the estate does not plan is a no-op by construction.
        }
    }

    private void executeFullscreen(Frame f, PassDescriptor descriptor) {
        BarrierContext context = f.contexts.activation(descriptor.step(), false);
        ProgramSelectionResult selected = f.barrier().select(descriptor.slot(), context);
        if (selected instanceof ProgramSelectionResult.Skipped) {
            return;
        }
        if (selected instanceof ProgramSelectionResult.ShadersOff off) {
            latchShadersOff();
            throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.fullscreen-select:"
                    + descriptor.slot().packName() + ":" + off.diagnosticId()));
        }
        if (selected instanceof ProgramSelectionResult.StalePublication) {
            throw new StalePublicationSignal();
        }
        ProgramBindingSelection selection = ((ProgramSelectionResult.Selected) selected).selection();
        PassSnapshotResult snap = f.estateView().snapshot(descriptor, selection);
        if (snap instanceof PassSnapshotResult.Rejected) {
            return;
        }
        if (snap instanceof PassSnapshotResult.Failed failed) {
            latchShadersOff();
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, failed.diagnosticId());
            throw new RuntimeFailureSignal(failure(failed.diagnosticId()));
        }
        PassBufferSnapshot snapshot = ((PassSnapshotResult.Acquired) snap).snapshot();
        // Fullscreen passes always generate main mipmaps before target binding; Phase 5
        // decides internally whether regeneration is needed (Completed vs Degraded).
        var mipmaps = f.estateView().generateMainMipmaps(snapshot);
        if (mipmaps instanceof com.schmaloogium.engine.buffers.MainMipmapResult.Failed) {
            latchShadersOff();
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.mipmaps");
            throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.mipmaps"));
        }
        PortResult bound = f.composition.port().bind(snapshot.drawTarget(), f.eye());
        if (!(bound instanceof PortResult.Completed)) {
            f.estateView().discardPass(snapshot);
            return;
        }
        f.composition.port().drawFullscreen(new com.schmaloogium.engine.frame.spi.FullscreenDraw(
                descriptor,
                com.schmaloogium.engine.frame.spi.MipmapSet.EMPTY,
                com.schmaloogium.engine.frame.spi.ViewportScale.full(),
                0, 1,
                com.schmaloogium.engine.frame.spi.FullscreenPrimitive.QUADS));
        PassCompletionResult completed = f.estateView().completePass(snapshot);
        if (!(completed instanceof PassCompletionResult.Completed)) {
            throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.fullscreen-complete"));
        }
        BarrierResult released = f.barrier().releaseToFixedFunction(f.contexts.release());
        if (!(released instanceof BarrierResult.FixedFunction)) {
            throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.fullscreen-release"));
        }
    }

    private void abortInternal(Frame f, FrameAbortReason reason, String diagnosticId) {
        if (f.terminal) {
            return;
        }
        f.terminal = true;
        // One line per aborted frame (never per hook): the reason and the step diagnostic.
        com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.FRAME)
                .warn("frame {} aborted: {} ({})", f.frameId, reason, diagnosticId);
        // Best-effort, order-stable drain: scopes, then the open Phase-5 frame.
        try {
            drainScopes(f);
        } catch (RuntimeException ignored) {
            // containment: every remaining close is best-effort during an abort
        }
        try {
            f.estateView().abortFrame(f.frameId, diagnosticId);
        } catch (RuntimeException ignored) {
            // containment as above
        }
        if (reason == FrameAbortReason.BACKEND_FAILURE) {
            latchShadersOff();
            lastFailure = failure(diagnosticId);
        }
        frame = null;
    }

    private void latchShadersOff() {
        shadersOff = true;
    }

    /**
     * Composition-root entry (§4.1 step 9 "admit frames"): a newly accepted publication
     * re-opens admission after an earlier failure latched shaders-off. Render thread, with
     * no frame open; the latch stays set while the failed publication remains installed.
     */
    public void resetShadersOffLatch() {
        requireRenderThread();
        if (frame != null && !frame.terminal) {
            throw new IllegalStateException("resetShadersOffLatch requires no open frame");
        }
        shadersOff = false;
    }

    private ScopeOpenResult suspensionFailed(Frame f) {
        latchShadersOff();
        abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.suspend");
        return new ScopeOpenResult.Failed(failure(CHANNEL + ".failure.suspend"));
    }

    // ------------------------------------------------------------------ frame records

    private enum Phase {
        SAMPLED,
        BUFFER_OPEN,
        MATRICES_CAPTURED,
        ESTATE_CLEARED,
        SHADOW_DONE,
        GBUFFERS,
        DEFERRED_DONE,
        GBUFFERS_TRANS,
        FINALIZING,
        COMMITTED
    }

    private static final class Frame {
        final FrameToken token;
        final long frameId;
        final FrameComposition composition;
        final FrameBeginSignal signal;
        final FrameBarrierContexts contexts;
        final Deque<OpenScope> scopes = new ArrayDeque<>();
        Phase phase = Phase.SAMPLED;
        boolean matricesCaptured;
        boolean finalizationStarted;
        boolean terminal;
        Float3 fogColor = new Float3(0f, 0f, 0f);
        Float3 cameraPosition;

        Frame(FrameToken token, long frameId, FrameComposition composition,
                FrameBeginSignal signal, FrameBarrierContexts contexts) {
            this.token = token;
            this.frameId = frameId;
            this.composition = composition;
            this.signal = signal;
            this.contexts = contexts;
        }

        BufferEstateView estateView() {
            return composition.estate().estate()
                    .orElseThrow(() -> new IllegalStateException("active composition without estate view"));
        }

        PublishedProgramStateBarrier barrier() {
            return composition.registry().barrier()
                    .orElseThrow(() -> new IllegalStateException("active composition without barrier"));
        }

        StageRegistry registryView() {
            return composition.registry().registry()
                    .orElseThrow(() -> new IllegalStateException("active composition without registry view"))
                    .stages();
        }

        AnaglyphEye eye() {
            return signal.eye();
        }

        StageStep stepFor(ProgramSlotId requested) {
            // The first schedule step that contains this slot carries the exact StageStep.
            // Scanned through passes(): P4's named() rejects wrong keys/kinds (sparse and
            // singleton steps, undeclared names) rather than answering absence.
            for (StageStep step : registryView().schedule()) {
                if (contained(step, requested).isPresent()) {
                    return step;
                }
            }
            // Unknown to this registry: Phase 4's fixed/skip terminal answers at select.
            return registryView().schedule().isEmpty() ? null : registryView().schedule().get(0);
        }

        PassDescriptor descriptorFor(ProgramSlotId requested) {
            StageStep step = stepFor(requested);
            Optional<PassDescriptor> found = step == null ? Optional.empty()
                    : contained(step, requested);
            return found.orElseGet(() -> new PassDescriptor(
                    step,
                    requested,
                    Optional.empty(),
                    com.schmaloogium.engine.registry.PassResourceAccess.empty(),
                    java.util.Set.of()));
        }

        private Optional<PassDescriptor> contained(StageStep step, ProgramSlotId requested) {
            for (PassDescriptor descriptor : registryView().passes(step)) {
                if (descriptor.slot().equals(requested)) {
                    return Optional.of(descriptor);
                }
            }
            return Optional.empty();
        }

        FinalizedFrame summary(boolean healthy, int consecutive, FailureId failure) {
            FrameReadiness readiness = new FrameReadiness(
                    composition.identity(),
                    composition.registry().generation(),
                    composition.estate().generation(),
                    java.util.OptionalLong.empty(),
                    composition.texturePublication(),
                    composition.resourceReloadEpoch(),
                    consecutive,
                    healthy ? java.util.Optional.empty()
                            : java.util.Optional.ofNullable(failure));
            return new FinalizedFrame(
                    frameId,
                    composition.identity(),
                    composition.version(),
                    signal.targetView(),
                    signal.eye(),
                    readiness);
        }
    }

    private static final class OpenScope {
        final ScopeToken token;
        final RenderSection section;
        final ProgramSlotId requested;
        final BarrierContext context;
        ProgramBindingSelection selection;
        PassBufferSnapshot snapshot;
        DrawDisposition disposition = DrawDisposition.DRAW_SHADER;
        boolean activated;
        boolean suspended;
        boolean discardSnapshotOnClose;

        OpenScope(ScopeToken token, RenderSection section, ProgramSlotId requested,
                BarrierContext context) {
            this.token = token;
            this.section = section;
            this.requested = requested;
            this.context = context;
        }
    }

    /** Signals a mutation-free stale publication out of the scope-construction path. */
    private static final class StalePublicationSignal extends RuntimeException {
        StalePublicationSignal() {
            super(null, null, false, false);
        }
    }

    /** Signals a contained runtime failure out of the scope-construction path. */
    private static final class RuntimeFailureSignal extends RuntimeException {
        final FailureId failure;

        RuntimeFailureSignal(FailureId failure) {
            super(failure.diagnosticId(), null, false, false);
            this.failure = failure;
        }
    }

    // ------------------------------------------------------------------ owned bridge impls

    /** The uniform signal bridge: records the fog fallback, forwards verbatim to Phase 6. */
    private final class SignalBridge implements UniformSignalBridge {

        @Override
        public SignalResult frame(FrameBeginSignal signal) {
            Objects.requireNonNull(signal, "signal");
            Frame f = frame;
            if (f == null || f.terminal || f.signal == null
                    || f.signal.worldEpoch() != signal.worldEpoch()
                    || f.signal.logicalTick() != signal.logicalTick()
                    || f.signal.mainTerrainFrameToken() != signal.mainTerrainFrameToken()) {
                return new SignalResult.Rejected(HookRejection.WRONG_TOKEN);
            }
            return new SignalResult.Accepted();
        }

        @Override
        public SignalResult camera(FrameToken token, CameraSnapshot camera) {
            Objects.requireNonNull(token, "token");
            Objects.requireNonNull(camera, "camera");
            Frame f = authenticated(token);
            if (f == null) {
                return new SignalResult.Rejected(HookRejection.WRONG_TOKEN);
            }
            return new SignalResult.Accepted();
        }

        @Override
        public SignalResult event(FrameToken token, UniformSignal signal) {
            Objects.requireNonNull(token, "token");
            Objects.requireNonNull(signal, "signal");
            Frame f = authenticated(token);
            if (f == null) {
                return new SignalResult.Rejected(HookRejection.WRONG_TOKEN);
            }
            UniformEventSink events = f.composition.uniforms().events();
            if (signal instanceof UniformSignal.Celestial celestial) {
                events.updateCelestial(new CelestialSample(
                        f.signal.worldEpoch(), f.frameId,
                        celestial.sunPosition(), celestial.moonPosition(),
                        celestial.shadowLightPosition(), celestial.upPosition()));
                return new SignalResult.Accepted();
            }
            if (signal instanceof UniformSignal.Fog fog) {
                f.fogColor = fog.color();
                events.updateFog(new FogSample(
                        f.signal.worldEpoch(), f.frameId, fog.fogMode(), fog.density(), fog.color()));
                return new SignalResult.Accepted();
            }
            UniformSignal.Blend blend = (UniformSignal.Blend) signal;
            events.updateBlend(new BlendSample(
                    f.signal.worldEpoch(), f.frameId,
                    blend.value().enabled(),
                    blend.value().srcRgb(), blend.value().dstRgb(),
                    blend.value().srcAlpha(), blend.value().dstAlpha()));
            return new SignalResult.Accepted();
        }
    }

    /** The atlas adapter sink: validates the live frame, forwards to the shadow bridge. */
    private final class AtlasSink implements AtlasBindingSink {

        @Override
        public SignalResult currentBinding(AtlasBindingEvidence evidence) {
            Objects.requireNonNull(evidence, "evidence");
            Frame f = frame;
            if (f == null || f.terminal) {
                return new SignalResult.Rejected(HookRejection.STALE_PUBLICATION);
            }
            return shadowBridge.publishBaseBinding(evidence);
        }
    }
}
