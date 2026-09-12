// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.buffers.BufferEstateView;
import com.schmaloogium.engine.buffers.BufferInventory;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.BufferSizing;
import com.schmaloogium.engine.buffers.ClearExecutionPlan;
import com.schmaloogium.engine.buffers.ClearExecutionResult;
import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.DepthCopyPoint;
import com.schmaloogium.engine.buffers.DepthCopyResult;
import com.schmaloogium.engine.buffers.DrawBuffersNoneOpenResult;
import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.FrameBeginResult;
import com.schmaloogium.engine.buffers.FrameEndResult;
import com.schmaloogium.engine.buffers.MainDepthPreparation;
import com.schmaloogium.engine.buffers.MainDepthRefreshResult;
import com.schmaloogium.engine.buffers.MainDepthSnapshot;
import com.schmaloogium.engine.buffers.MainMipmapResult;
import com.schmaloogium.engine.buffers.PassBufferSnapshot;
import com.schmaloogium.engine.buffers.PassCompletionResult;
import com.schmaloogium.engine.buffers.PassDiscardResult;
import com.schmaloogium.engine.buffers.PassSnapshotResult;
import com.schmaloogium.engine.buffers.PublishedBufferEstate;
import com.schmaloogium.engine.buffers.ResourceProjectionUnavailableReason;
import com.schmaloogium.engine.buffers.ShadowEstateResult;
import com.schmaloogium.engine.buffers.TextureBindingResult;
import com.schmaloogium.engine.buffers.TextureOverlayLease;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.buffers.VirtualTransitionResult;
import com.schmaloogium.engine.frame.lifecycle.FrameComposition;
import com.schmaloogium.engine.frame.lifecycle.FrameCompositionSource;
import com.schmaloogium.engine.frame.lifecycle.FrameDriver;
import com.schmaloogium.engine.frame.spi.FrameRenderPort;
import com.schmaloogium.engine.frame.spi.FullscreenDraw;
import com.schmaloogium.engine.frame.spi.PortResult;
import com.schmaloogium.engine.frame.spi.StateSnapshot;
import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.ConfigurationFingerprint;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackIdentity;
import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierContextSource;
import com.schmaloogium.engine.registry.BarrierResult;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.FrameBarrierContexts;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.PassIndex;
import com.schmaloogium.engine.registry.PassPopulation;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.frame.dispatch.RenderSection;
import com.schmaloogium.engine.registry.ProgramBindingParticipant;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.ProgramResolutionProjection;
import com.schmaloogium.engine.registry.ProgramSelectionResult;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.PublishedProgramStateBarrier;
import com.schmaloogium.engine.registry.PublishedRegistry;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.registry.StageStep;
import com.schmaloogium.engine.registry.UseProgramRequest;
import com.schmaloogium.engine.uniforms.BlendSample;
import com.schmaloogium.engine.uniforms.CelestialSample;
import com.schmaloogium.engine.expr.api.CustomUniformBridge;
import com.schmaloogium.engine.expr.api.FixedExpressionInputSchema;
import com.schmaloogium.engine.uniforms.FogSample;
import com.schmaloogium.engine.uniforms.Float4;
import com.schmaloogium.engine.uniforms.FrameBeginInput;
import com.schmaloogium.engine.uniforms.Int2;
import com.schmaloogium.engine.uniforms.HeldItemSample;
import com.schmaloogium.engine.uniforms.Matrix4Value;
import com.schmaloogium.engine.uniforms.RegistryGenerationAdoptionResult;
import com.schmaloogium.engine.uniforms.ShadowMatrixSample;
import com.schmaloogium.engine.uniforms.UniformEventSink;
import com.schmaloogium.engine.uniforms.UniformFrameTiming;
import com.schmaloogium.engine.uniforms.UniformResetReason;
import com.schmaloogium.engine.preprocess.MacroContribution;
import com.schmaloogium.engine.uniforms.UniformRetirementReason;
import com.schmaloogium.engine.uniforms.UniformRetirementResult;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Headless frame-driver ordering tests (PHASE_7_DOC §8.1): the exact frame-begin ordering
 * D-P7-76 and the closed hook algebras over a fully scripted composition — no Minecraft,
 * Forge, Mixin, LWJGL or GL type. The scripted P4 barrier answers Skipped for every slot,
 * so scope opens exercise the OMIT_OPERATION branch; unreachable estate methods fail the
 * test loudly, proving the driver never touches them on these paths.
 */
class FrameDriverScriptedTest {

    // ------------------------------------------------------------------ scripted seams

    private static final class Ctx implements BarrierContext {
        @Override
        public boolean shadowPass() {
            return false;
        }

        @Override
        public StageId stage() {
            return StageId.SETUP;
        }

        @Override
        public StageBand band() {
            return StageBand.FRAME_BEGIN;
        }
    }

    private static final class FakeContexts implements FrameBarrierContexts {
        @Override
        public BarrierContext activation(StageStep step, boolean shadowPass) {
            return new Ctx();
        }

        @Override
        public BarrierContext release() {
            return new Ctx();
        }
    }

    private static final class FakeContextSource implements BarrierContextSource {
        final FakeContexts contexts = new FakeContexts();

        @Override
        public FrameBarrierContexts beginFrame() {
            return contexts;
        }
    }

    /** Scripted P4 barrier: Skipped unless a slot is scripted Selected; records the order. */
    private static final class FakeBarrier implements PublishedProgramStateBarrier {
        final List<String> calls;
        final java.util.Set<String> selectedSlots = new java.util.HashSet<>();
        java.util.function.Function<String, BarrierResult> activation =
                slot -> new BarrierResult.FixedFunction(List.of());

        FakeBarrier(List<String> calls) {
            this.calls = calls;
        }

        @Override
        public long generation() {
            return 9L;
        }

        @Override
        public ProgramSelectionResult select(ProgramSlotId requested, BarrierContext context) {
            calls.add("select:" + requested.packName());
            if (selectedSlots.contains(requested.packName())) {
                return new ProgramSelectionResult.Selected(inertSelection(requested));
            }
            return new ProgramSelectionResult.Skipped(requested);
        }

        @Override
        public BarrierResult activate(UseProgramRequest request) {
            calls.add("activate:" + request.selection().requested().packName());
            return activation.apply(request.selection().requested().packName());
        }

        @Override
        public BarrierResult releaseToFixedFunction(BarrierContext context) {
            calls.add("release");
            return new BarrierResult.FixedFunction(List.of());
        }
    }

    /**
     * {@link ProgramBindingSelection} is mint-only inside Phase 4 barriers, so the scripted
     * barrier hands out an inert instance whose {@code requested()} the fakes read back;
     * the driver's viewport derivation tolerates its null descriptor.
     */
    private static ProgramBindingSelection inertSelection(ProgramSlotId requested) {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            java.lang.reflect.Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Object unsafe = theUnsafe.get(null);
            java.lang.reflect.Method allocateInstance =
                    unsafeClass.getMethod("allocateInstance", Class.class);
            ProgramBindingSelection selection = (ProgramBindingSelection) allocateInstance
                    .invoke(unsafe, ProgramBindingSelection.class);
            java.lang.reflect.Field field =
                    ProgramBindingSelection.class.getDeclaredField("requested");
            field.setAccessible(true);
            field.set(selection, requested);
            return selection;
        } catch (ReflectiveOperationException failure) {
            throw new AssertionError("cannot allocate an inert selection", failure);
        }
    }

    private static final class FakeStageRegistry implements StageRegistry {
        final List<StageStep> steps = new java.util.ArrayList<>();
        final Map<StageStep, List<PassDescriptor>> descriptors = new java.util.HashMap<>();

        void add(StageStep step, PassDescriptor... passes) {
            steps.add(step);
            descriptors.put(step, List.of(passes));
        }

        @Override
        public List<StageStep> schedule() {
            return List.copyOf(steps);
        }

        @Override
        public List<PassDescriptor> passes(StageStep step) {
            return descriptors.getOrDefault(step, List.of());
        }

        @Override
        public Optional<PassDescriptor> named(StageStep step, ProgramSlotId id) {
            return Optional.empty();
        }

        @Override
        public Optional<PassDescriptor> indexed(StageStep step, PassIndex index) {
            return Optional.empty();
        }

        @Override
        public boolean stepExists(StageStep step) {
            return false;
        }
    }

    private static final class FakeRegistryView implements ProgramRegistryView {
        final FakeStageRegistry stages;

        FakeRegistryView(FakeStageRegistry stages) {
            this.stages = stages;
        }

        @Override
        public StageRegistry stages() {
            return stages;
        }

        @Override
        public Optional<ResolvedProgramDescriptor> resolve(ProgramSlotId requested) {
            return Optional.empty();
        }

        @Override
        public List<ProgramResolutionProjection> resolutions() {
            return List.of();
        }

        @Override
        public RegistryFingerprint fingerprint() {
            return new RegistryFingerprint("scripted");
        }

        @Override
        public FixedSamplerPolicyFingerprint samplerPolicyFingerprint() {
            return null; // never consulted by the driver
        }
    }

    private static final class ScriptedHandle implements BorrowedDepthAttachmentHandle {
    }

    /** Scripted estate: records the P5 protocol order; unreachable methods fail loudly. */
    private static final class FakeEstate implements BufferEstateView {
        @Override
        public long generation() {
            return 2L;
        }

        final List<String> calls;
        java.util.function.Function<PassDescriptor, TextureBindingResult> bindings =
                pass -> new TextureBindingResult.Bound(new InertBindingSnapshot());
        final AtomicInteger completeCalls = new AtomicInteger();
        final AtomicInteger discardCalls = new AtomicInteger();

        FakeEstate(List<String> calls) {
            this.calls = calls;
        }

        final AtomicInteger beginCalls = new AtomicInteger();
        final AtomicInteger commitCalls = new AtomicInteger();
        final AtomicInteger abortCalls = new AtomicInteger();
        final AtomicInteger clearCalls = new AtomicInteger();
        MainDepthRefreshResult nextRefresh = new MainDepthRefreshResult.Unchanged(1L);
        FrameBeginResult nextBegin = new FrameBeginResult.Begun(2L, 0L, 1L);
        ClearExecutionResult nextClear = ClearExecutionResult.SUCCESS;
        long lastFrameId;

        @Override
        public RegistryFingerprint registryFingerprint() {
            return new RegistryFingerprint("scripted");
        }

        @Override
        public BufferSizing sizing() {
            throw new AssertionError("unreachable in this fixture");
        }

        @Override
        public BufferInventory inventory() {
            throw new AssertionError("unreachable in this fixture");
        }

        @Override
        public BufferResourceSnapshot.Available resources() {
            throw new AssertionError("unreachable in this fixture");
        }

        @Override
        public MainDepthRefreshResult refreshMainDepth() {
            return nextRefresh;
        }

        @Override
        public FrameBeginResult beginFrame(long frameId) {
            beginCalls.incrementAndGet();
            lastFrameId = frameId;
            return nextBegin;
        }

        @Override
        public VirtualTransitionResult applyVirtualTransition(long frameId, PassDescriptor pass) {
            calls.add("virtual:" + pass.slot().packName());
            return new VirtualTransitionResult.NoChange(frameId, pass.slot());
        }

        @Override
        public DrawBuffersNoneOpenResult openDrawBuffersNone(long frameId) {
            throw new AssertionError("unreachable in this fixture");
        }

        @Override
        public PassSnapshotResult snapshot(PassDescriptor pass,
                com.schmaloogium.engine.registry.ProgramBindingSelection selection) {
            calls.add("snapshot:" + pass.slot().packName());
            com.schmaloogium.engine.buffers.PassDrawTarget target =
                    pass.step().stage() == StageId.FINAL
                            ? com.schmaloogium.engine.buffers.PassDrawTarget.Screen.INSTANCE
                            : new com.schmaloogium.engine.buffers.PassDrawTarget.EngineFramebuffer(
                                    new com.schmaloogium.engine.gl.FramebufferHandle() {
                                    });
            return new PassSnapshotResult.Acquired(new PassBufferSnapshot(2L, 0L, lastFrameId,
                    pass, selection, List.of(), Map.of(), java.util.Set.of(), target));
        }

        @Override
        public MainMipmapResult generateMainMipmaps(PassBufferSnapshot snapshot) {
            calls.add("mipmaps:" + snapshot.pass().slot().packName());
            return new MainMipmapResult.Completed(List.of());
        }

        @Override
        public PassCompletionResult completePass(PassBufferSnapshot snapshot) {
            calls.add("complete:" + snapshot.pass().slot().packName());
            completeCalls.incrementAndGet();
            return new PassCompletionResult.Completed(lastFrameId);
        }

        @Override
        public PassDiscardResult discardPass(PassBufferSnapshot snapshot) {
            calls.add("discard:" + snapshot.pass().slot().packName());
            discardCalls.incrementAndGet();
            return new PassDiscardResult.Discarded(lastFrameId);
        }

        @Override
        public ClearExecutionPlan clearPlan(ClearRequest request) {
            return new ClearExecutionPlan(2L, 0L, request.frameId(), List.of());
        }

        @Override
        public ClearExecutionResult executeClear(ClearExecutionPlan plan) {
            clearCalls.incrementAndGet();
            return nextClear;
        }

        @Override
        public DepthCopyResult copyDepth(DepthCopyPoint point, long frameId) {
            throw new AssertionError("unreachable in this fixture");
        }

        @Override
        public FrameEndResult commitFrame(long frameId) {
            commitCalls.incrementAndGet();
            return new FrameEndResult.Committed(frameId);
        }

        @Override
        public FrameEndResult abortFrame(long frameId, String diagnosticId) {
            abortCalls.incrementAndGet();
            return new FrameEndResult.Aborted(frameId, diagnosticId, false);
        }

        @Override
        public TextureBindingResult textureBindings(PassBufferSnapshot snapshot,
                TextureOverlayLease overlay, TextureOverlayPublicationId expectedOverlay) {
            calls.add("bindings:" + snapshot.pass().slot().packName());
            return bindings.apply(snapshot.pass());
        }

        @Override
        public ShadowEstateResult shadow() {
            throw new AssertionError("unreachable in this fixture");
        }
    }

    /** The v0.1 Bound answer: evidence-only rows, closed by the driver in finally. */
    private static final class InertBindingSnapshot
            implements com.schmaloogium.engine.buffers.TextureBindingSnapshot {
        boolean closed;

        @Override
        public long estateGeneration() {
            return 2L;
        }

        @Override
        public long depthAttachmentEpoch() {
            return 0L;
        }

        @Override
        public long frameId() {
            return 0L;
        }

        @Override
        public PassDescriptor pass() {
            return null;
        }

        @Override
        public ProgramBindingSelection selection() {
            return null;
        }

        @Override
        public TextureOverlayPublicationId overlayPublication() {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.BindingPurpose purpose() {
            return null;
        }

        @Override
        public List<com.schmaloogium.engine.buffers.TextureBindingRow> rows() {
            return List.of();
        }

        @Override
        public com.schmaloogium.engine.buffers.TextureBindingOutcome outcome(int unit) {
            return null;
        }

        @Override
        public List<com.schmaloogium.engine.buffers.TextureBindingDiagnostic> diagnostics() {
            return List.of();
        }

        @Override
        public boolean isCurrent() {
            return !closed;
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    private static final class FakePort implements FrameRenderPort {
        final List<String> calls;
        java.util.function.Function<FullscreenDraw, PortResult> draw =
                d -> new PortResult.Completed();

        FakePort(List<String> calls) {
            this.calls = calls;
        }

        @Override
        public StateSnapshot snapshotState() {
            return new StateSnapshot() {
            };
        }

        @Override
        public PortResult normalizeForEngine() {
            return new PortResult.Completed();
        }

        @Override
        public PortResult bind(com.schmaloogium.engine.buffers.PassDrawTarget target,
                AnaglyphEye eye) {
            calls.add("bind:" + (target instanceof com.schmaloogium.engine.buffers.PassDrawTarget.Screen
                    ? "screen" : "fbo"));
            return new PortResult.Completed();
        }

        @Override
        public PortResult drawFullscreen(FullscreenDraw draw) {
            calls.add("draw:" + draw.pass().slot().packName());
            return this.draw.apply(draw);
        }

        @Override
        public PortResult restore(StateSnapshot snapshot) {
            return new PortResult.Completed();
        }
    }

    /** Recording uniform runtime: counts frame begins and matrix captures. */
    private static final class RecordingRuntime
            implements com.schmaloogium.engine.uniforms.UniformRuntime {
        final AtomicInteger beginCalls = new AtomicInteger();
        final AtomicInteger matrixCaptures = new AtomicInteger();

        @Override
        public RegistryGenerationAdoptionResult adoptRegistryGeneration(long generation,
                UniformResetReason reason) {
            return RegistryGenerationAdoptionResult.ADOPTED;
        }

        @Override
        public FixedExpressionInputSchema fixedExpressionInputSchema() {
            return new FixedExpressionInputSchema("v1", Map.of());
        }

        @Override
        public com.schmaloogium.engine.uniforms.FrameBeginResult beginFrame(FrameBeginInput input) {
            beginCalls.incrementAndGet();
            return com.schmaloogium.engine.uniforms.FrameBeginResult.ACCEPTED;
        }

        @Override
        public Optional<UniformFrameTiming> frameTiming(long registryGeneration, long frameId) {
            return Optional.empty();
        }

        @Override
        public UniformEventSink events() {
            return new UniformEventSink() {
                @Override
                public void captureGbufferMatrices(long frameId, Matrix4Value modelView,
                        Matrix4Value projection) {
                    matrixCaptures.incrementAndGet();
                }

                @Override
                public void updateCelestial(CelestialSample sample) {
                }

                @Override
                public void updateFog(FogSample sample) {
                }

                @Override
                public void updateBlend(BlendSample sample) {
                }

                @Override
                public void updateHeldItems(HeldItemSample value) {
                }

                @Override
                public void updateShadowMatrices(ShadowMatrixSample sample) {
                }

                @Override
                public void updateEntityColor(Float4 value) {
                }

                @Override
                public void updateEntityId(int value) {
                }

                @Override
                public void updateBlockEntityId(int value) {
                }

                @Override
                public void updateInstanceId(int value) {
                }

                @Override
                public void updateAtlasSize(Int2 value) {
                }
            };
        }

        @Override
        public ProgramBindingParticipant samplerParticipant() {
            return null;
        }

        @Override
        public ProgramBindingParticipant builtInParticipant() {
            return null;
        }

        @Override
        public ProgramBindingParticipant customParticipant() {
            return null;
        }

        @Override
        public com.schmaloogium.engine.preprocess.MacroContributor centerDepthMacroContributor() {
            return new com.schmaloogium.engine.preprocess.MacroContributor() {
                @Override
                public MacroContribution contribute(PackConfiguration configuration) {
                    return new MacroContribution.Empty();
                }
            };
        }

        @Override
        public void installCustomUniformBridge(CustomUniformBridge bridge) {
        }

        @Override
        public void reset(UniformResetReason reason) {
        }

        @Override
        public UniformRetirementResult retire(UniformRetirementReason reason) {
            return new UniformRetirementResult.Retired();
        }
    }

    // ------------------------------------------------------------------ composition

    private static final Extent2i EXTENT = new Extent2i(64, 48);

    private FrameBeginSignal signal() {
        return new FrameBeginSignal(
                100L, 5L, 0.5d, 1f,
                new DimensionKey(OptionalInt.empty()),
                0, 7, 0.25f,
                EXTENT, EXTENT, AnaglyphEye.LEFT);
    }

    private MainDepthPreparation readyDepth() {
        return new MainDepthPreparation.Ready(new MainDepthSnapshot.Available(
                1L, new ScriptedHandle(), DepthAttachmentFormat.DEPTH_COMPONENT, EXTENT));
    }

    private record Handle(FrameDriver driver, FakeEstate estate, RecordingRuntime runtime,
            FakeBarrier barrier, FakeStageRegistry stages, FakePort port, List<String> calls) {
    }

    private Handle composition() {
        List<String> calls = new java.util.ArrayList<>();
        FakeEstate estate = new FakeEstate(calls);
        RecordingRuntime runtime = new RecordingRuntime();
        FakeBarrier barrier = new FakeBarrier(calls);
        FakeStageRegistry stages = new FakeStageRegistry();
        FakePort port = new FakePort(calls);
        FrameComposition composition = new FrameComposition() {
            @Override
            public PipelineIdentity identity() {
                return new PipelineIdentity(
                        new PackIdentity(new NormalizedPackPath("pack/scripted"), Map.of()),
                        new DimensionKey(OptionalInt.empty()),
                        new ConfigurationFingerprint("cfg"));
            }

            @Override
            public PipelineVersion version() {
                return new PipelineVersion(1L);
            }

            @Override
            public PublishedRegistry registry() {
                return new PublishedRegistry(9L,
                        Optional.of(new FakeRegistryView(stages)),
                        Optional.of(barrier),
                        new FakeContextSource());
            }

            @Override
            public PublishedBufferEstate estate() {
                return new PublishedBufferEstate(2L, Optional.of(estate),
                        new BufferResourceSnapshot.Unavailable(
                                ResourceProjectionUnavailableReason.SHADERS_OFF));
            }

            @Override
            public com.schmaloogium.engine.uniforms.UniformRuntime uniforms() {
                return runtime;
            }

            @Override
            public FrameRenderPort port() {
                return port;
            }

            @Override
            public Optional<ShadowInvocationSlot> shadowSlot() {
                return Optional.empty();
            }

            @Override
            public Optional<com.schmaloogium.engine.frame.spi.FrameCompletionObserver>
                    completionObserver() {
                return Optional.empty();
            }

            @Override
            public Optional<TextureOverlayPublicationId> texturePublication() {
                return Optional.empty();
            }

            @Override
            public long resourceReloadEpoch() {
                return 0L;
            }
        };
        FrameCompositionSource source = new FrameCompositionSource();
        source.install(Optional.of(composition));
        return new Handle(new FrameDriver(() -> true, source), estate, runtime, barrier,
                stages, port, calls);
    }

    // ------------------------------------------------------------------ fullscreen schedule

    private static final StageStep DEFERRED_STEP = new StageStep(StageId.DEFERRED,
            StageBand.BETWEEN_GBUFFERS, new PassPopulation.Singleton());
    private static final StageStep COMPOSITE_STEP = new StageStep(StageId.COMPOSITE,
            StageBand.FRAME_END, new PassPopulation.Singleton());
    private static final StageStep FINAL_STEP = new StageStep(StageId.FINAL,
            StageBand.SCREEN, new PassPopulation.Singleton());

    private static PassDescriptor raster(StageStep step, String slot, int index) {
        return new PassDescriptor(step, new ProgramSlotId(slot), Optional.of(new PassIndex(index)),
                com.schmaloogium.engine.registry.PassResourceAccess.empty(), java.util.Set.of());
    }

    private static PassDescriptor finalPass() {
        return new PassDescriptor(FINAL_STEP, new ProgramSlotId("final"), Optional.empty(),
                com.schmaloogium.engine.registry.PassResourceAccess.empty(), java.util.Set.of());
    }

    private static PassDescriptor prelude(StageStep step, String slot) {
        return new PassDescriptor(step, new ProgramSlotId(slot), Optional.empty(),
                com.schmaloogium.engine.registry.PassResourceAccess.empty(), java.util.Set.of());
    }

    /** deferred_pre, deferred, composite_pre, composite, composite1, final — all selectable. */
    private static void scheduleFullChain(Handle h) {
        h.stages().add(DEFERRED_STEP, prelude(DEFERRED_STEP, "deferred_pre"),
                raster(DEFERRED_STEP, "deferred", 0));
        h.stages().add(COMPOSITE_STEP, prelude(COMPOSITE_STEP, "composite_pre"),
                raster(COMPOSITE_STEP, "composite", 0), raster(COMPOSITE_STEP, "composite1", 1));
        h.stages().add(FINAL_STEP, finalPass());
        h.barrier().selectedSlots.addAll(List.of("deferred", "composite", "composite1", "final"));
    }

    private static List<String> slotsIn(List<String> calls, String prefix) {
        return calls.stream().filter(c -> c.startsWith(prefix + ":"))
                .map(c -> c.substring(prefix.length() + 1)).toList();
    }

    /** Drives the driver to ESTATE_CLEARED (post-clear) and returns the token. */
    private FrameToken toEstateCleared(Handle h) {
        FrameOpenResult opened = h.driver().open(signal());
        assertTrue(opened instanceof FrameOpenResult.Opened, "open must succeed");
        FrameToken token = ((FrameOpenResult.Opened) opened).token();
        assertTrue(h.driver().beforeFirstClear(token)
                instanceof FrameStepResult.Advanced);
        assertTrue(h.driver().afterFirstClear(token, readyDepth())
                instanceof FrameStepResult.Advanced);
        assertTrue(h.driver().captureMainCamera(token, new CameraSnapshot(
                Matrix4Value.identity(), Matrix4Value.identity()))
                instanceof FrameStepResult.Advanced);
        return token;
    }

    // ------------------------------------------------------------------ tests

    @Test
    void frameBeginOrderingRunsExactDP76Sequence() {
        Handle h = composition();
        FrameOpenResult opened = h.driver().open(signal());
        FrameToken token = ((FrameOpenResult.Opened) opened).token();

        assertTrue(h.driver().beforeFirstClear(token) instanceof FrameStepResult.Advanced);
        assertEquals(0, h.estate().beginCalls.get(), "P5 must not begin before depth");

        assertTrue(h.driver().afterFirstClear(token, readyDepth())
                instanceof FrameStepResult.Advanced);
        assertEquals(1, h.estate().beginCalls.get(), "P5 begins exactly once");
        assertEquals(1, h.runtime().beginCalls.get(),
                "P6 frame begin runs at open, before the estate");

        // Matrix capture at the exact post-camera point: P5's clear runs immediately
        // after (D-P7-76), before any gbuffers scope.
        assertTrue(h.driver().captureMainCamera(token, new CameraSnapshot(
                Matrix4Value.identity(), Matrix4Value.identity()))
                instanceof FrameStepResult.Advanced);
        assertEquals(1, h.runtime().matrixCaptures.get(), "capture exactly once");
        assertEquals(1, h.estate().clearCalls.get(), "main clear after matrices");

        assertTrue(h.driver().afterTerrainSetup(token) instanceof FrameStepResult.Advanced);
    }

    @Test
    void matrixCaptureIsExactlyOncePerFrame() {
        Handle h = composition();
        FrameToken token = toEstateCleared(h);
        // A second capture attempt is a wrong-order rejection, not a re-capture.
        assertTrue(h.driver().captureMainCamera(token, new CameraSnapshot(
                Matrix4Value.identity(), Matrix4Value.identity()))
                instanceof FrameStepResult.Rejected);
        assertEquals(1, h.runtime().matrixCaptures.get());
    }

    @Test
    void finishIsIdempotentAndCommitsOnce() {
        Handle h = composition();
        FrameToken token = toEstateCleared(h);
        assertTrue(h.driver().afterTerrainSetup(token) instanceof FrameStepResult.Advanced);

        FrameFinishResult finish = h.driver().finish(token, FrameExitKind.NORMAL);
        assertTrue(finish instanceof FrameFinishResult.Finalized);
        assertEquals(1, h.estate().commitCalls.get());

        // The losing second finish (TAIL then finally) is a mandatory no-op.
        assertTrue(h.driver().finish(token, FrameExitKind.EARLY_RETURN)
                instanceof FrameFinishResult.AlreadyTerminal);
        assertEquals(1, h.estate().commitCalls.get());
    }

    @Test
    void wrongOrderIsRejectedBeforeMutation() {
        Handle h = composition();
        FrameToken token = ((FrameOpenResult.Opened) h.driver().open(signal())).token();
        // Matrix capture requires the BUFFER_OPEN phase; straight after open the frame
        // is still SAMPLED, so the capture is rejected before P6 sees anything.
        assertTrue(h.driver().captureMainCamera(token, new CameraSnapshot(
                Matrix4Value.identity(), Matrix4Value.identity()))
                instanceof FrameStepResult.Rejected);
        assertEquals(0, h.runtime().matrixCaptures.get(),
                "rejected step must not mutate P6");
        assertEquals(0, h.estate().clearCalls.get(), "no clear without capture");
    }

    @Test
    void depthNotReadyAbandonsBeforePhase5() {
        Handle h = composition();
        FrameToken token = ((FrameOpenResult.Opened) h.driver().open(signal())).token();
        assertTrue(h.driver().beforeFirstClear(token) instanceof FrameStepResult.Advanced);

        FrameStepResult result = h.driver().afterFirstClear(token,
                new MainDepthPreparation.Pending(1L));
        assertTrue(result instanceof FrameStepResult.Aborted);
        assertEquals(FrameAbortReason.RESIZE_EPOCH,
                ((FrameStepResult.Aborted) result).reason());
        assertEquals(0, h.estate().beginCalls.get(),
                "abandon happens before Phase 5 is touched");
        assertEquals(0, h.estate().clearCalls.get());
    }

    @Test
    void clearFailureAbortsTheFrameAndLatchesShadersOff() {
        Handle h = composition();
        FrameToken token = ((FrameOpenResult.Opened) h.driver().open(signal())).token();
        h.driver().beforeFirstClear(token);
        h.driver().afterFirstClear(token, readyDepth());
        h.estate().nextClear = ClearExecutionResult.BACKEND_FAILED;

        FrameStepResult result = h.driver().captureMainCamera(token, new CameraSnapshot(
                Matrix4Value.identity(), Matrix4Value.identity()));
        assertTrue(result instanceof FrameStepResult.Aborted);
        assertEquals(FrameAbortReason.BACKEND_FAILURE,
                ((FrameStepResult.Aborted) result).reason());
        assertEquals(1, h.estate().abortCalls.get(), "open frame aborted");

        // CORE latch: a backend failure keeps shaders off for the session.
        FrameOpenResult next = h.driver().open(signal());
        assertTrue(next instanceof FrameOpenResult.VanillaOnly);
        assertEquals(FrameOpenRejection.SHADERS_OFF,
                ((FrameOpenResult.VanillaOnly) next).reason());
        assertTrue(h.driver().isShadersOff());

        // The latch belongs to the failed publication: the composition root re-opens
        // admission when it installs an accepted replacement (§4.1 step 9).
        h.driver().resetShadersOffLatch();
        assertTrue(!h.driver().isShadersOff());
        assertTrue(h.driver().open(signal()) instanceof FrameOpenResult.Opened);
    }

    @Test
    void secondFrameIsRejectedWhileFirstIsOpen() {
        Handle h = composition();
        h.driver().open(signal());
        FrameOpenResult second = h.driver().open(signal());
        assertTrue(second instanceof FrameOpenResult.VanillaOnly);
        assertEquals(FrameOpenRejection.FRAME_ALREADY_OPEN,
                ((FrameOpenResult.VanillaOnly) second).reason());
    }

    @Test
    void nonWorldPassIsVanillaOnly() {
        Handle h = composition();
        FrameBeginSignal guiPass = new FrameBeginSignal(
                100L, 5L, 0.5d, 1f,
                new DimensionKey(OptionalInt.empty()),
                1, 7, 0.25f,
                EXTENT, EXTENT, AnaglyphEye.LEFT);
        FrameOpenResult result = h.driver().open(guiPass);
        assertTrue(result instanceof FrameOpenResult.VanillaOnly);
        assertEquals(FrameOpenRejection.NON_WORLD_PASS,
                ((FrameOpenResult.VanillaOnly) result).reason());
    }

    @Test
    void offThreadOpenIsVanillaOnlyWrongThread() {
        FrameCompositionSource source = new FrameCompositionSource();
        source.install(Optional.empty());
        FrameDriver driver = new FrameDriver(() -> false, source);
        FrameBeginSignal s = new FrameBeginSignal(
                100L, 5L, 0.5d, 1f,
                new DimensionKey(OptionalInt.empty()),
                0, 7, 0.25f, EXTENT, EXTENT, AnaglyphEye.LEFT);
        FrameOpenResult result = driver.open(s);
        assertTrue(result instanceof FrameOpenResult.VanillaOnly);
        assertEquals(FrameOpenRejection.WRONG_THREAD,
                ((FrameOpenResult.VanillaOnly) result).reason());
    }

    @Test
    void offThreadStepThrowsBeforeReadingState() {
        FrameCompositionSource source = new FrameCompositionSource();
        source.install(Optional.empty());
        FrameDriver driver = new FrameDriver(() -> false, source);
        assertThrows(IllegalStateException.class,
                () -> driver.beforeFirstClear(FrameToken.mint(1L)));
    }

    @Test
    void scopesRequireAnOpenFrameAndCloseInLifoOrder() {
        Handle h = composition();
        // No frame: scope opens reject.
        assertTrue(h.driver().enter(FrameToken.mint(99L),
                com.schmaloogium.engine.frame.dispatch.RenderSection.TERRAIN_SOLID)
                instanceof ScopeOpenResult.Rejected);

        FrameToken token = toEstateCleared(h);
        h.driver().afterTerrainSetup(token);

        ScopeOpenResult outer = h.driver().enter(token,
                com.schmaloogium.engine.frame.dispatch.RenderSection.TERRAIN_SOLID);
        assertTrue(outer instanceof ScopeOpenResult.Opened);
        ScopeToken outerScope = ((ScopeOpenResult.Opened) outer).scope();

        ScopeOpenResult inner = h.driver().enter(token,
                com.schmaloogium.engine.frame.dispatch.RenderSection.ENTITIES);
        assertTrue(inner instanceof ScopeOpenResult.Opened);
        ScopeToken innerScope = ((ScopeOpenResult.Opened) inner).scope();

        // Closing the outer while the inner is open is out-of-order.
        assertTrue(h.driver().exit(token, outerScope) instanceof ScopeCloseResult.Rejected);
        // LIFO close resumes the parent.
        ScopeCloseResult innerClose = h.driver().exit(token, innerScope);
        assertTrue(innerClose instanceof ScopeCloseResult.Closed);
        ScopeCloseResult outerClose = h.driver().exit(token, outerScope);
        assertTrue(outerClose instanceof ScopeCloseResult.Closed);
    }

    @Test
    void afterTerrainSetupRejectsWhileSkyScopesRemainOpen() {
        Handle h = composition();
        FrameToken token = toEstateCleared(h);
        assertTrue(h.driver().enter(token,
                com.schmaloogium.engine.frame.dispatch.RenderSection.SKY_BASIC)
                instanceof ScopeOpenResult.Opened);
        assertTrue(h.driver().afterTerrainSetup(token) instanceof FrameStepResult.Rejected);
    }

    @Test
    void finishRunsDeferredCompositeAndFinalBandsInOrder() {
        Handle h = composition();
        scheduleFullChain(h);
        FrameToken token = toEstateCleared(h);
        h.driver().afterTerrainSetup(token);

        FrameFinishResult finished = h.driver().finish(token, FrameExitKind.NORMAL);
        assertTrue(finished instanceof FrameFinishResult.Finalized, "got " + finished);
        assertEquals(List.of("deferred", "composite", "composite1", "final"),
                slotsIn(h.calls(), "draw"), "deferred, then composite ascending, then final once");
        assertEquals(List.of("deferred_pre", "composite_pre"), slotsIn(h.calls(), "virtual"),
                "each contained prelude runs once before its band's raster passes");
        assertEquals(4, h.estate().completeCalls.get(), "only Completed draws complete passes");
        assertEquals(0, h.estate().discardCalls.get());
    }

    @Test
    void fullscreenPassFollowsTheSpecOrderAndBindsThenActivates() {
        Handle h = composition();
        h.stages().add(FINAL_STEP, finalPass());
        h.barrier().selectedSlots.add("final");
        FrameToken token = toEstateCleared(h);
        h.driver().afterTerrainSetup(token);
        h.calls().clear();

        assertTrue(h.driver().finish(token, FrameExitKind.NORMAL)
                instanceof FrameFinishResult.Finalized);
        List<String> passCalls = h.calls().stream()
                .filter(c -> !c.equals("release") || h.calls().indexOf(c) == h.calls().lastIndexOf(c))
                .toList();
        int select = h.calls().indexOf("select:final");
        int snapshot = h.calls().indexOf("snapshot:final");
        int mipmaps = h.calls().indexOf("mipmaps:final");
        int bind = h.calls().indexOf("bind:screen");
        int bindings = h.calls().indexOf("bindings:final");
        int activate = h.calls().indexOf("activate:final");
        int draw = h.calls().indexOf("draw:final");
        int release = h.calls().indexOf("release");
        int complete = h.calls().indexOf("complete:final");
        assertTrue(select >= 0 && select < snapshot && snapshot < mipmaps && mipmaps < bind
                && bind < bindings && bindings < activate && activate < draw && draw < release
                && release < complete,
                "PHASE_7_DOC §4.6 order: select, snapshot, mipmaps, bind, bindings, activate, "
                        + "draw, release, complete; got " + h.calls());
        assertTrue(passCalls.contains("bind:screen"), "final draws to the Screen target");
    }

    @Test
    void fixedFunctionFinalStillDrawsThePassthrough() {
        Handle h = composition();
        h.stages().add(FINAL_STEP, finalPass());
        h.barrier().selectedSlots.add("final");
        h.barrier().activation = slot -> new BarrierResult.FixedFunction(List.of());
        FrameToken token = toEstateCleared(h);
        h.driver().afterTerrainSetup(token);

        assertTrue(h.driver().finish(token, FrameExitKind.NORMAL)
                instanceof FrameFinishResult.Finalized);
        assertEquals(List.of("final"), slotsIn(h.calls(), "draw"),
                "the fixed-function terminal draws the colortex0 passthrough quad");
        assertEquals(1, h.estate().completeCalls.get());
    }

    @Test
    void fullscreenDrawFailureAbortsAndLatchesShadersOff() {
        Handle h = composition();
        scheduleFullChain(h);
        h.port().draw = d -> d.pass().slot().packName().equals("composite")
                ? new PortResult.Failed(new FailureId("schmaloogium.test.draw"))
                : new PortResult.Completed();
        FrameToken token = toEstateCleared(h);
        h.driver().afterTerrainSetup(token);

        FrameFinishResult result = h.driver().finish(token, FrameExitKind.NORMAL);
        assertTrue(result instanceof FrameFinishResult.Failed, "got " + result);
        assertEquals(List.of("deferred", "composite"), slotsIn(h.calls(), "draw"),
                "nothing after the failed draw runs");
        assertEquals(1, h.estate().completeCalls.get(), "the failed pass never completes");
        assertEquals(1, h.estate().abortCalls.get(), "the frame is aborted (containment)");
        assertTrue(h.driver().isShadersOff(), "backend failure latches shaders off");
    }

    @Test
    void fullscreenDrawRejectionDiscardsWithoutFlip() {
        Handle h = composition();
        scheduleFullChain(h);
        h.port().draw = d -> d.pass().slot().packName().equals("composite")
                ? new PortResult.Rejected(com.schmaloogium.engine.frame.spi.PortRejection.UNSUPPORTED)
                : new PortResult.Completed();
        FrameToken token = toEstateCleared(h);
        h.driver().afterTerrainSetup(token);

        assertTrue(h.driver().finish(token, FrameExitKind.NORMAL)
                instanceof FrameFinishResult.Finalized);
        assertEquals(List.of("deferred", "composite", "composite1", "final"),
                slotsIn(h.calls(), "draw"));
        assertEquals(List.of("composite"), slotsIn(h.calls(), "discard"),
                "a rejected draw discards that pass only");
        assertEquals(3, h.estate().completeCalls.get());
        assertFalse(h.driver().isShadersOff());
    }

    @Test
    void degradedBindingsDiscardThePassBeforeActivation() {
        Handle h = composition();
        scheduleFullChain(h);
        h.estate().bindings = pass -> pass.slot().packName().equals("composite1")
                ? new TextureBindingResult.Rejected(
                        com.schmaloogium.engine.buffers.TextureBindingRejection.INVALID_INPUT)
                : new TextureBindingResult.Bound(new InertBindingSnapshot());
        FrameToken token = toEstateCleared(h);
        h.driver().afterTerrainSetup(token);

        assertTrue(h.driver().finish(token, FrameExitKind.NORMAL)
                instanceof FrameFinishResult.Finalized);
        assertEquals(List.of("deferred", "composite", "final"), slotsIn(h.calls(), "activate"),
                "no activation for a pass whose bindings were refused");
        assertEquals(List.of("composite1"), slotsIn(h.calls(), "discard"));
    }

    @Test
    void translucentTriggerRunsDeferredOnceBeforeWater() {
        Handle h = composition();
        scheduleFullChain(h);
        FrameToken token = toEstateCleared(h);
        h.driver().afterTerrainSetup(token);
        assertTrue(h.driver().enter(token, RenderSection.TERRAIN_SOLID)
                instanceof ScopeOpenResult.Opened);
        h.calls().clear();

        ScopeOpenResult water = h.driver().enter(token, RenderSection.TERRAIN_TRANSLUCENT);
        assertTrue(water instanceof ScopeOpenResult.Opened, "got " + water);
        assertEquals(List.of("deferred"), slotsIn(h.calls(), "draw"),
                "the deferred family runs at the translucent HEAD");
        assertTrue(h.calls().indexOf("draw:deferred") < h.calls().indexOf("select:gbuffers_water"),
                "deferred draws before the water scope selects: " + h.calls());

        // Hand and overlay scopes are legal once the trigger has fired.
        ScopeToken waterScope = ((ScopeOpenResult.Opened) water).scope();
        assertTrue(h.driver().exit(token, waterScope) instanceof ScopeCloseResult.Closed);
        ScopeOpenResult hand = h.driver().enter(token, RenderSection.HAND_SOLID);
        assertTrue(hand instanceof ScopeOpenResult.Opened, "got " + hand);
        assertTrue(h.driver().exit(token, ((ScopeOpenResult.Opened) hand).scope())
                instanceof ScopeCloseResult.Closed);
        ScopeOpenResult overlay = h.driver().enter(token, RenderSection.FIRST_PERSON_OVERLAY);
        assertTrue(overlay instanceof ScopeOpenResult.Opened, "got " + overlay);
        assertTrue(h.driver().exit(token, ((ScopeOpenResult.Opened) overlay).scope())
                instanceof ScopeCloseResult.Closed);

        h.calls().clear();
        assertTrue(h.driver().finish(token, FrameExitKind.NORMAL)
                instanceof FrameFinishResult.Finalized);
        assertEquals(List.of("composite", "composite1", "final"), slotsIn(h.calls(), "draw"),
                "finish does not run the deferred family a second time");
    }

    @Test
    void handAndOverlayScopesAreWrongOrderBeforeTheTrigger() {
        Handle h = composition();
        FrameToken token = toEstateCleared(h);
        h.driver().afterTerrainSetup(token);
        ScopeOpenResult hand = h.driver().enter(token, RenderSection.HAND_SOLID);
        assertTrue(hand instanceof ScopeOpenResult.Rejected);
        assertEquals(HookRejection.WRONG_ORDER, ((ScopeOpenResult.Rejected) hand).reason());
    }
}
