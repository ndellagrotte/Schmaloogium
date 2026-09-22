// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.lifecycle;

import com.schmaloogium.engine.buffers.BufferEstateView;
import com.schmaloogium.engine.buffers.ClearExecutionResult;
import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.DepthCopyPoint;
import com.schmaloogium.engine.buffers.DepthCopyResult;
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
import com.schmaloogium.engine.buffers.TextureBindingResult;
import com.schmaloogium.engine.buffers.TextureBindingSnapshot;
import com.schmaloogium.engine.buffers.TextureOverlayLease;
import com.schmaloogium.engine.textures.TextureLeaseResult;
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
import com.schmaloogium.engine.frame.ShadowExecutionIdentity;
import com.schmaloogium.engine.frame.ShadowExecutionOpenResult;
import com.schmaloogium.engine.frame.ShadowFrameView;
import com.schmaloogium.engine.frame.ShadowInvocationContext;
import com.schmaloogium.engine.frame.ShadowInvocationResult;
import com.schmaloogium.engine.frame.ShadowInvocationSlot;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageStep;
import com.schmaloogium.engine.registry.UseProgramRequest;
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
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
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
        f.camera = camera;
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
        return afterTerrainSetup(token, null);
    }

    @Override
    public FrameStepResult afterTerrainSetup(FrameToken token, ShadowFrameView shadowFrame) {
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
        Optional<ShadowInvocationSlot> slot = f.composition.shadowSlot();
        if (slot.isPresent()) {
            FrameStepResult aborted = invokeShadowSlot(f, slot.get(), shadowFrame);
            if (aborted != null) {
                return aborted;
            }
        }
        // Slot absent, skipped or completed: the main estate is bound again by every scope
        // it opens (no second clear), and SHADOW_DONE admits terrain and later scopes.
        f.phase = Phase.SHADOW_DONE;
        return new FrameStepResult.Advanced(FrameState.SHADOW_DONE);
    }

    /**
     * PHASE_8_DOC §4.2 from Phase 7's side: select root shadow once, open the execution
     * bridge, invoke, close in {@code finally}. {@code Completed}/{@code Rejected}/
     * {@code NotInstalled} advance the frame; {@code Failed} aborts it. Returns the abort
     * result, or null to continue.
     */
    private FrameStepResult invokeShadowSlot(Frame f, ShadowInvocationSlot slot,
            ShadowFrameView inputs) {
        if (inputs == null || f.camera == null) {
            noteShadow(f, "skipped: no shadow frame inputs");
            return null;
        }
        if (inputs.worldEpoch() != f.signal.worldEpoch()
                || inputs.mainTerrainFrameToken() != f.signal.mainTerrainFrameToken()) {
            noteShadow(f, "skipped: shadow frame inputs are not this frame's");
            return null;
        }
        ShadowFrameView shadowFrame = new ShadowFrameView(f.signal.worldEpoch(), f.frameId,
                f.signal.partialTicks(), f.signal.mainTerrainFrameToken(),
                inputs.cameraPosition(), inputs.skyAngle(), inputs.sunAngle());
        StageStep shadowStep = null;
        PassDescriptor descriptor = null;
        for (StageStep step : f.registryView().schedule()) {
            if (step.stage() == StageId.SHADOW && step.band() == StageBand.SHADOW) {
                shadowStep = step;
                var passes = f.registryView().passes(step);
                descriptor = passes.isEmpty() ? null : passes.get(0);
                break;
            }
        }
        if (descriptor == null) {
            noteShadow(f, "skipped: no shadow pass in the schedule");
            return null;
        }
        BarrierContext context = f.contexts.activation(shadowStep, true);
        ProgramSelectionResult selected = f.barrier().select(descriptor.slot(), context);
        if (selected instanceof ProgramSelectionResult.Skipped) {
            noteShadow(f, "skipped: select Skipped (no shadow program)");
            return null;
        }
        if (selected instanceof ProgramSelectionResult.ShadersOff off) {
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE,
                    CHANNEL + ".abort.shadow-select:" + off.diagnosticId());
            return new FrameStepResult.Aborted(FrameAbortReason.BACKEND_FAILURE);
        }
        if (selected instanceof ProgramSelectionResult.StalePublication) {
            abortInternal(f, FrameAbortReason.PROTOCOL_REJECTION, CHANNEL + ".abort.shadow-stale");
            return new FrameStepResult.Aborted(FrameAbortReason.PROTOCOL_REJECTION);
        }
        ProgramBindingSelection selection = ((ProgramSelectionResult.Selected) selected).selection();
        ShadowExecutionOpenResult opened = shadowBridge.open(f, slot.slotEpoch());
        if (!(opened instanceof ShadowExecutionOpenResult.Opened live)) {
            abortInternal(f, FrameAbortReason.PROTOCOL_REJECTION,
                    CHANNEL + ".abort.shadow-bridge:" + opened);
            return new FrameStepResult.Aborted(FrameAbortReason.PROTOCOL_REJECTION);
        }
        ShadowInvocationResult result;
        f.phase = Phase.SHADOW_INVOKING;
        try {
            result = slot.invoke(new ShadowInvocationContext(f.token, shadowFrame, f.camera,
                    f.composition.registry(), f.composition.estate(), f.contexts, live.view(),
                    selection, context, f.composition.texturePublication(), f.composition.textureLeases(),
                    f.composition.port().textureEvidence(f.composition.version(),
                            f.composition.texturePublication().resourceReloadEpoch(), true)));
        } catch (RuntimeException thrown) {
            f.phase = Phase.ESTATE_CLEARED;
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.shadow-threw:" + thrown);
            return new FrameStepResult.Aborted(FrameAbortReason.BACKEND_FAILURE);
        } finally {
            shadowBridge.close(live.view());
            if (f.phase == Phase.SHADOW_INVOKING) {
                f.phase = Phase.ESTATE_CLEARED;
            }
        }
        if (result instanceof ShadowInvocationResult.Failed failed) {
            noteShadow(f, "Failed " + failed.failure().diagnosticId());
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, failed.failure().diagnosticId());
            return new FrameStepResult.Aborted(FrameAbortReason.BACKEND_FAILURE);
        }
        noteShadow(f, result instanceof ShadowInvocationResult.Rejected rejected
                ? "Rejected " + rejected.reason() : result.getClass().getSimpleName());
        return null;
    }

    private String lastShadowVerdict;

    /** H8-SLOT-01-FRAME-05 evidence: the first invocation and every verdict change. */
    private void noteShadow(Frame f, String verdict) {
        if (verdict.equals(lastShadowVerdict)) {
            return;
        }
        lastShadowVerdict = verdict;
        com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.FRAME)
                .info("H8-SLOT-01-FRAME-05 shadow invocation verdict (frame {}): {}", f.frameId, verdict);
    }

    @Override
    public boolean skyTextureAllowed(FrameToken token, boolean sun) {
        requireRenderThread();
        Frame f = authenticated(token);
        if (f == null) {
            return true;
        }
        var flags = f.composition.engineFlags();
        return (sun ? flags.sun() : flags.moon()) != com.schmaloogium.engine.config.TriState.FALSE;
    }

    @Override
    public java.util.OptionalDouble handDepthScale(FrameToken token) {
        requireRenderThread();
        Frame f = authenticated(token);
        if (f == null || (f.phase != Phase.DEFERRED_DONE && f.phase != Phase.GBUFFERS_TRANS)) {
            return java.util.OptionalDouble.empty();
        }
        return java.util.OptionalDouble.of(f.composition.handDepthMultiplier());
    }

    @Override
    public FrameStepResult beforeWeather(FrameToken token) {
        requireRenderThread();
        Frame f = authenticated(token);
        if (f == null) {
            return rejected(HookRejection.WRONG_TOKEN);
        }
        if (f.phase == Phase.SHADOW_INVOKING) {
            return rejected(HookRejection.SHADOW_EXECUTION_ACTIVE);
        }
        if (f.phase != Phase.SHADOW_DONE && f.phase != Phase.GBUFFERS) {
            return rejected(HookRejection.WRONG_ORDER);
        }
        FrameStepResult closed = closeTopForTrigger(f);
        if (closed != null) {
            return closed;
        }
        f.phase = Phase.GBUFFERS;
        return copyDepth(f, DepthCopyPoint.PRE_WEATHER);
    }

    private FrameStepResult copyDepth(Frame f, DepthCopyPoint point) {
        return switch (f.estateView().copyDepth(point, f.frameId)) {
            case DepthCopyResult.Copied ignored -> new FrameStepResult.Advanced(FrameState.GBUFFERS);
            case DepthCopyResult.DuplicateIgnored duplicate -> {
                com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.FRAME)
                        .warn("frame {} depth copy duplicate: {}", f.frameId, duplicate.diagnosticId());
                yield new FrameStepResult.Advanced(FrameState.GBUFFERS);
            }
            case DepthCopyResult.BackendDegraded degraded -> {
                com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.FRAME)
                        .debug("frame {} depth copy degraded: {}", f.frameId, degraded.diagnosticId());
                yield new FrameStepResult.Advanced(FrameState.GBUFFERS);
            }
            case DepthCopyResult.Rejected rejection -> {
                abortInternal(f, FrameAbortReason.PROTOCOL_REJECTION,
                        CHANNEL + ".abort.depth-copy:" + point + ":" + rejection.reason());
                yield new FrameStepResult.Aborted(FrameAbortReason.PROTOCOL_REJECTION);
            }
        };
    }

    @Override
    public ScopeOpenResult enter(FrameToken token, RenderSection section) {
        requireRenderThread();
        Objects.requireNonNull(section, "section");
        Frame f = authenticated(token);
        if (f == null) {
            return new ScopeOpenResult.Rejected(HookRejection.WRONG_TOKEN);
        }
        if (f.phase == Phase.SHADOW_INVOKING) {
            // Vanilla's block-layer/entity/cloud calls issued by the shadow world port:
            // no main scope, no translucent trigger (PHASE_8_DOC §4.8.1).
            return new ScopeOpenResult.Rejected(HookRejection.SHADOW_EXECUTION_ACTIVE);
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
                if (f.phase != Phase.DEFERRED_DONE) {
                    FrameStepResult copied = copyDepth(f, DepthCopyPoint.PRE_TRANSLUCENT);
                    if (copied instanceof FrameStepResult.Aborted aborted) {
                        return new ScopeOpenResult.Aborted(aborted.reason());
                    }
                    // §4.5 step 4: the DEFERRED/BETWEEN_GBUFFERS family runs here, once,
                    // before gbuffers_water; finish runs it only when no trigger fired.
                    try {
                        runBand(f, StageBand.BETWEEN_GBUFFERS);
                    } catch (StalePublicationSignal signal) {
                        return new ScopeOpenResult.Rejected(HookRejection.STALE_PUBLICATION);
                    } catch (RuntimeFailureSignal signal) {
                        if (f.terminal) {
                            return new ScopeOpenResult.Aborted(FrameAbortReason.BACKEND_FAILURE);
                        }
                        return new ScopeOpenResult.Failed(signal.failure);
                    }
                    f.deferredRan = true;
                }
                f.phase = Phase.DEFERRED_DONE;
            }
        }
        // Suspend an open parent before pushing the child scope (§4.4 step 1).
        OpenScope parent = f.scopes.peek();
        if (parent != null && parent.snapshot != null) {
            if (!closeScope(f, parent)) {
                return suspensionFailed(f);
            }
            parent.suspended = true;
        }
        try {
            OpenScope scope = openScope(f, section, route.requested());
            f.scopes.push(scope);
            return new ScopeOpenResult.Opened(scope.token, scope.disposition);
        } catch (StalePublicationSignal signal) {
            if (parent != null && parent.suspended && !reactivate(f, parent)) {
                return new ScopeOpenResult.Aborted(FrameAbortReason.BACKEND_FAILURE);
            }
            // Mutation-free; the glue reacquires the current publication.
            return new ScopeOpenResult.Rejected(HookRejection.STALE_PUBLICATION);
        } catch (RuntimeFailureSignal signal) {
            if (!f.terminal && parent != null && parent.suspended && !reactivate(f, parent)) {
                return new ScopeOpenResult.Aborted(FrameAbortReason.BACKEND_FAILURE);
            }
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
            f.scopes.push(top);
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.scope-close");
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
        try {
            return acquireScope(f, scope);
        } catch (RuntimeException failure) {
            closeScope(f, scope);
            throw failure;
        }
    }

    /** Acquire new physical sides/bindings while retaining the logical selection and context. */
    private OpenScope acquireScope(Frame f, OpenScope scope) {
        ProgramSlotId requested = scope.requested;
        RenderSection section = scope.section;
        ProgramBindingSelection selection = scope.selection;
        BarrierContext context = scope.context;
        PublishedProgramStateBarrier barrier = f.barrier();
        scope.discardSnapshotOnClose = false;
        PassDescriptor descriptor = f.descriptorFor(requested);
        PassSnapshotResult snap = f.estateView().snapshot(descriptor, selection);
        if (snap instanceof PassSnapshotResult.Failed failed) {
            f.estateFrameConsumed = true;
            // D-P7-45: the transaction is already unhealthy; containment then shaders-off.
            latchShadersOff();
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, failed.diagnosticId());
            throw new RuntimeFailureSignal(failure(failed.diagnosticId()));
        }
        if (snap instanceof PassSnapshotResult.Rejected rejected) {
            String diagnostic = CHANNEL + ".abort.scope-snapshot:" + rejected.reason();
            abortInternal(f, FrameAbortReason.PROTOCOL_REJECTION, diagnostic);
            throw new RuntimeFailureSignal(failure(diagnostic));
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
        var publication = f.composition.texturePublication();
        TextureLeaseResult leased = f.composition.textureLeases().lease(publication.id(), selection,
                f.composition.port().textureEvidence(f.composition.version(),
                        publication.resourceReloadEpoch(), true));
        if (!(leased instanceof TextureLeaseResult.Acquired acquired)) {
            noteScopeBindings(section, "lease " + leased);
            scope.disposition = DrawDisposition.OMIT_OPERATION;
            scope.discardSnapshotOnClose = true;
            return scope;
        }
        TextureBindingResult bindings = bindTextures(f, snapshot, acquired.lease());
        if (bindings instanceof TextureBindingResult.BackendFailed) {
            latchShadersOff();
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.scope-bindings");
            throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.scope-bindings"));
        }
        if (!(bindings instanceof TextureBindingResult.Bound boundTextures)) {
            // Degraded/rejected bindings: the pass stays undrawn, vanilla draws.
            noteScopeBindings(section, "bindings " + bindings);
            scope.disposition = DrawDisposition.OMIT_OPERATION;
            scope.discardSnapshotOnClose = true;
            return scope;
        }
        scope.bindings = boundTextures.snapshot();
        noteScopeBindings(section, "Bound rows=" + boundUnits(boundTextures.snapshot()));
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

    /** Only a Bound result transfers lease ownership to the binding snapshot. */
    private TextureBindingResult bindTextures(Frame f, PassBufferSnapshot snapshot,
            TextureOverlayLease lease) {
        boolean transferred = false;
        try {
            TextureBindingResult result = f.estateView().textureBindings(snapshot, lease,
                    f.composition.texturePublication().id());
            transferred = result instanceof TextureBindingResult.Bound;
            return result;
        } finally {
            if (!transferred) {
                lease.close();
            }
        }
    }

    private boolean closeScope(Frame f, OpenScope scope) {
        closeQuietly(scope.bindings);
        scope.bindings = null;
        if (f.estateFrameConsumed) {
            scope.snapshot = null;
        }
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

    /** One line per distinct scope-binding verdict per section per driver (never per frame):
     *  the H-TERRAIN-01 evidence that gbuffers programs now receive their estate units. */
    private final java.util.Set<String> scopeBindingsLogged =
            java.util.concurrent.ConcurrentHashMap.newKeySet();

    private void noteScopeBindings(RenderSection section, String verdict) {
        String key = section + ": " + verdict;
        if (scopeBindingsLogged.add(key)) {
            com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.FRAME)
                    .info("H-TERRAIN-01 scope bindings {}", key);
        }
    }

    /** Bound units, then every non-Unused row as {@code unit:kind(names)} — the evidence a
     *  log reader needs to see which samplers each pass received and which stayed foreign. */
    private static String boundUnits(TextureBindingSnapshot snapshot) {
        StringBuilder units = new StringBuilder("[");
        StringBuilder rows = new StringBuilder();
        for (com.schmaloogium.engine.buffers.TextureBindingRow row : snapshot.rows()) {
            String kind;
            String names;
            if (row.outcome() instanceof com.schmaloogium.engine.buffers.TextureBindingOutcome.BoundObject bound) {
                if (units.length() > 1) {
                    units.append(',');
                }
                units.append(row.unit());
                kind = bound.origin().kind().name();
                names = String.valueOf(bound.names().stream()
                        .map(com.schmaloogium.engine.buffers.ResolvedSamplerBinding::exactName).toList());
            } else if (row.outcome() instanceof com.schmaloogium.engine.buffers.TextureBindingOutcome.ForeignRetained foreign) {
                kind = "FOREIGN";
                names = String.valueOf(foreign.names().stream()
                        .map(com.schmaloogium.engine.buffers.ResolvedSamplerBinding::exactName).toList());
            } else {
                continue;
            }
            rows.append(' ').append(row.unit()).append(':').append(kind).append(names);
        }
        return units.append(']').toString() + " purpose " + snapshot.purpose() + " rows" + rows;
    }

    /** Evidence for a failed scope close: the step and the closed result it answered. */
    private static void logCloseFailure(Frame f, OpenScope scope, String step, Object result) {
        com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.FRAME)
                .warn("frame {} scope {} close failed at {}: {}", f.frameId, scope.section, step, result);
    }

    private boolean reactivate(Frame f, OpenScope parent) {
        parent.suspended = false;
        if (parent.selection == null) {
            return true;
        }
        try {
            acquireScope(f, parent);
            return true;
        } catch (RuntimeException failure) {
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.resume");
            return false;
        }
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

    /**
     * Frame end (§4.6, PHASE_7_DOC:1478-1488): the deferred family only when no translucent
     * trigger ran it (§4.5), then the COMPOSITE/FRAME_END family ascending, then the
     * FINAL/SCREEN pass exactly once. Every band traverses Phase 4's one sparse
     * population: contained virtual prelude first, then populated raster descriptors.
     */
    private void runDeferredAndFinal(Frame f) {
        if (!f.deferredRan) {
            runBand(f, StageBand.BETWEEN_GBUFFERS);
            f.deferredRan = true;
        }
        runBand(f, StageBand.FRAME_END);
        runBand(f, StageBand.SCREEN);
        // A registry without a SCREEN step composites nothing (internal pack always has one).
    }

    /** One D-P7-61 traversal of every schedule step in the band (prelude, then raster). */
    private void runBand(Frame f, StageBand band) {
        StageRegistry registry = f.registryView();
        for (StageStep step : registry.schedule()) {
            if (step.band() != band) {
                continue;
            }
            for (PassDescriptor descriptor : registry.passes(step)) {
                if (isVirtualPrelude(descriptor)) {
                    applyVirtualPrelude(f, descriptor);
                    continue;
                }
                executeFullscreen(f, descriptor);
            }
        }
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

    /**
     * One deferred/composite/final pass in the §4.6 order: select → snapshot →
     * generateMainMipmaps → bind target → textureBindings → activate → draw → release →
     * completePass. Only a {@code Completed} draw completes the pass (flips commit there);
     * an undrawn pass is discarded; backend failure is containment plus the shaders-off
     * latch. The absent {@code final} slot resolves to the fixed-function terminal, whose
     * activation answers FixedFunction and whose bindings put colortex0 at unit 0: the
     * passthrough draw is the same textured fullscreen quad.
     */
    private void executeFullscreen(Frame f, PassDescriptor descriptor) {
        BarrierContext context = f.contexts.activation(descriptor.step(), false);
        ProgramSelectionResult selected = f.barrier().select(descriptor.slot(), context);
        if (selected instanceof ProgramSelectionResult.Skipped) {
            noteFullscreen(descriptor, "select Skipped");
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
        if (snap instanceof PassSnapshotResult.Rejected rejectedSnapshot) {
            noteFullscreen(descriptor, "snapshot " + rejectedSnapshot);
            return;
        }
        if (snap instanceof PassSnapshotResult.Failed failed) {
            f.estateFrameConsumed = true;
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
            noteFullscreen(descriptor, "bind " + bound);
            f.estateView().discardPass(snapshot);
            return;
        }
        var publication = f.composition.texturePublication();
        TextureLeaseResult leased = f.composition.textureLeases().lease(publication.id(), selection,
                f.composition.port().textureEvidence(f.composition.version(),
                        publication.resourceReloadEpoch(), false));
        if (!(leased instanceof TextureLeaseResult.Acquired acquired)) {
            noteFullscreen(descriptor, "lease " + leased);
            f.estateView().discardPass(snapshot);
            return;
        }
        TextureBindingResult bindings = bindTextures(f, snapshot, acquired.lease());
        if (bindings instanceof TextureBindingResult.BackendFailed) {
            latchShadersOff();
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.fullscreen-bindings");
            throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.fullscreen-bindings"));
        }
        if (!(bindings instanceof TextureBindingResult.Bound boundTextures)) {
            noteFullscreen(descriptor, "bindings " + bindings);
            f.estateView().discardPass(snapshot);
            return;
        }
        boolean activated = false;
        try {
            BarrierResult activation = f.barrier().activate(new UseProgramRequest(selection, context));
            if (activation instanceof BarrierResult.StalePublication) {
                f.estateView().discardPass(snapshot);
                throw new StalePublicationSignal();
            }
            if (activation instanceof BarrierResult.Skipped) {
                noteFullscreen(descriptor, "activate Skipped");
                f.estateView().discardPass(snapshot);
                return;
            }
            if (!(activation instanceof BarrierResult.Activated)
                    && !(activation instanceof BarrierResult.FixedFunction)) {
                String cause = activation instanceof BarrierResult.ShadersOff off ? off.diagnosticId()
                        : activation instanceof BarrierResult.FailedSafe safe ? safe.diagnosticId()
                        : activation.getClass().getSimpleName();
                latchShadersOff();
                abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.fullscreen-activate");
                throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.fullscreen-activate:" + cause));
            }
            activated = true;
            PortResult drawn = f.composition.port().drawFullscreen(
                    new com.schmaloogium.engine.frame.spi.FullscreenDraw(
                            descriptor,
                            mipmapSet(mipmaps),
                            viewportFor(selection),
                            0, 1,
                            com.schmaloogium.engine.frame.spi.FullscreenPrimitive.QUADS));
            if (drawn instanceof PortResult.Failed failedDraw) {
                releaseQuietly(f);
                activated = false;
                latchShadersOff();
                abortInternal(f, FrameAbortReason.BACKEND_FAILURE,
                        CHANNEL + ".abort.fullscreen-draw:" + failedDraw.failure().diagnosticId());
                throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.fullscreen-draw:"
                        + failedDraw.failure().diagnosticId()));
            }
            BarrierResult released = f.barrier().releaseToFixedFunction(f.contexts.release());
            activated = false;
            if (!(released instanceof BarrierResult.FixedFunction)) {
                throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.fullscreen-release"));
            }
            if (!(drawn instanceof PortResult.Completed)) {
                // Rejected: mutation-free on the port side; the pass stays undrawn.
                noteFullscreen(descriptor, "draw " + drawn);
                f.estateView().discardPass(snapshot);
                return;
            }
            PassCompletionResult completed = f.estateView().completePass(snapshot);
            if (!(completed instanceof PassCompletionResult.Completed)) {
                throw new RuntimeFailureSignal(failure(CHANNEL + ".failure.fullscreen-complete"));
            }
            noteFullscreen(descriptor, "drawn and completed (" + activation.getClass().getSimpleName()
                    + ", flips " + snapshot.flipAfterPass().size() + ")");
        } finally {
            if (activated) {
                releaseQuietly(f);
            }
            closeQuietly(boundTextures.snapshot());
        }
    }

    /** One line per distinct fullscreen-pass verdict per driver (never per frame). */
    private final java.util.Set<String> fullscreenVerdictsLogged =
            java.util.concurrent.ConcurrentHashMap.newKeySet();

    /**
     * The colortex attachments that actually carry a fresh chain for this pass, so the
     * port is told the truth instead of a constant empty set. Degraded rows are excluded:
     * a request Phase 5 could not satisfy must not read as a usable chain.
     */
    private static com.schmaloogium.engine.frame.spi.MipmapSet mipmapSet(
            com.schmaloogium.engine.buffers.MainMipmapResult result) {
        if (!(result instanceof com.schmaloogium.engine.buffers.MainMipmapResult.Completed done)) {
            return com.schmaloogium.engine.frame.spi.MipmapSet.EMPTY;
        }
        java.util.Set<Integer> indices = new java.util.LinkedHashSet<>();
        for (com.schmaloogium.engine.buffers.MainMipmapOutcome outcome : done.outcomes()) {
            com.schmaloogium.engine.buffers.LogicalBuffer buffer =
                outcome instanceof com.schmaloogium.engine.buffers.MainMipmapOutcome.Generated g
                    ? g.buffer()
                    : outcome instanceof com.schmaloogium.engine.buffers.MainMipmapOutcome.AlreadyFresh f
                        ? f.buffer()
                        : null;
            if (buffer != null
                    && buffer.domain() == com.schmaloogium.engine.registry.BufferDomain.COLORTEX) {
                indices.add(buffer.index().value());
            }
        }
        return indices.isEmpty()
            ? com.schmaloogium.engine.frame.spi.MipmapSet.EMPTY
            : new com.schmaloogium.engine.frame.spi.MipmapSet(indices);
    }

    private void noteFullscreen(PassDescriptor descriptor, String verdict) {
        String key = descriptor.slot().packName() + ": " + verdict;
        if (fullscreenVerdictsLogged.add(key)) {
            com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.FRAME)
                    .info("H-FULLSCREEN pass verdict {}", key);
        }
    }

    private void releaseQuietly(Frame f) {
        try {
            f.barrier().releaseToFixedFunction(f.contexts.release());
        } catch (RuntimeException ignored) {
            // best-effort on an already-failing path
        }
    }

    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
            // the binding snapshot's close is evidence-only at v0.1
        }
    }

    /** The per-program viewport scale (P4 state bundle) as the port's normalized request. */
    private static com.schmaloogium.engine.frame.spi.ViewportScale viewportFor(
            ProgramBindingSelection selection) {
        ResolvedProgramDescriptor descriptor = selection.effectiveDescriptor();
        if (descriptor == null || descriptor.state() == null
                || descriptor.state().viewportScale().isEmpty()) {
            return com.schmaloogium.engine.frame.spi.ViewportScale.full();
        }
        com.schmaloogium.engine.config.ViewportScale scale = descriptor.state().viewportScale().get();
        return new com.schmaloogium.engine.frame.spi.ViewportScale(
                scale.offsetX(), scale.offsetY(), scale.scale(), scale.scale());
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
            if (!f.estateFrameConsumed) {
                f.estateView().abortFrame(f.frameId, diagnosticId);
                f.estateFrameConsumed = true;
            }
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
        SHADOW_INVOKING,
        SHADOW_DONE,
        GBUFFERS,
        DEFERRED_DONE,
        GBUFFERS_TRANS,
        FINALIZING,
        COMMITTED
    }

    private static final class Frame implements ShadowExecutionIdentity {
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
        boolean estateFrameConsumed;
        /** The deferred family ran at the translucent trigger (§4.5) or at finish. */
        boolean deferredRan;
        Float3 fogColor = new Float3(0f, 0f, 0f);
        Float3 cameraPosition;
        /** The accepted post-camera capture (PHASE_8_DOC §4.2 step 1); null before H-FRAME-04. */
        CameraSnapshot camera;

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
                    Optional.of(composition.texturePublication().id()),
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
        TextureBindingSnapshot bindings;

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

    /** Refresh the active physical snapshot after an authenticated vanilla base change. */
    private final class AtlasSink implements AtlasBindingSink {

        @Override
        public SignalResult currentBinding(AtlasBindingEvidence evidence) {
            requireRenderThread();
            Objects.requireNonNull(evidence, "evidence");
            Frame f = frame;
            if (f == null || f.terminal) {
                return new SignalResult.Accepted();
            }
            if (f.phase == Phase.SHADOW_INVOKING) {
                return shadowBridge.publishBaseBinding(evidence);
            }
            OpenScope scope = f.scopes.peek();
            if (scope == null || scope.suspended || scope.bindings == null) {
                return new SignalResult.Accepted();
            }
            TextureLeaseResult result = f.composition.textureLeases().lease(
                    f.composition.texturePublication().id(), scope.selection, evidence);
            if (!(result instanceof TextureLeaseResult.Acquired acquired)) {
                return new SignalResult.Rejected(HookRejection.STALE_PUBLICATION);
            }
            try {
                TextureBindingResult refreshed = bindTextures(f, scope.snapshot, acquired.lease());
                if (refreshed instanceof TextureBindingResult.Bound bound) {
                    TextureBindingSnapshot previous = scope.bindings;
                    scope.bindings = bound.snapshot();
                    closeQuietly(previous);
                    return new SignalResult.Accepted();
                }
            } catch (RuntimeException failure) {
                abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.base-refresh");
                return new SignalResult.Failed(failure(CHANNEL + ".failure.base-refresh"));
            }
            abortInternal(f, FrameAbortReason.BACKEND_FAILURE, CHANNEL + ".abort.base-refresh");
            return new SignalResult.Failed(failure(CHANNEL + ".failure.base-refresh"));
        }
    }
}
