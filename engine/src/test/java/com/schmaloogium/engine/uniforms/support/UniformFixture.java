// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.support;

import com.schmaloogium.engine.buffers.FixedSamplerPlanResult;
import com.schmaloogium.engine.buffers.FixedSamplerResolver;
import com.schmaloogium.engine.buffers.ResolvedSamplerBinding;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.expr.api.CustomRefreshResult;
import com.schmaloogium.engine.expr.api.CustomUniformBridge;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.record.GLCall;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.UniformLocation;
import com.schmaloogium.engine.gl.record.RecordingGLDevice;
import com.schmaloogium.engine.gl.record.ScriptedResponses;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierParticipantResult;
import com.schmaloogium.engine.registry.BoundProgramActivityToken;
import com.schmaloogium.engine.registry.BoundProgramUniformAccess;
import com.schmaloogium.engine.registry.DrawRouting;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.GeometryInputRequirement;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.ProgramSamplerDeclaration;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramStateBundle;
import com.schmaloogium.engine.registry.ProgramUniformCacheKey;
import com.schmaloogium.engine.registry.ProgramUniformDeclaration;
import com.schmaloogium.engine.registry.ProgramUniformLayout;
import com.schmaloogium.engine.registry.ProgramUniformLayoutFingerprint;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.uniforms.BlendSample;
import com.schmaloogium.engine.uniforms.Double3;
import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.uniforms.Float4;
import com.schmaloogium.engine.uniforms.FogSample;
import com.schmaloogium.engine.uniforms.FrameBeginInput;
import com.schmaloogium.engine.uniforms.FrameBeginResult;
import com.schmaloogium.engine.uniforms.HeldItemSample;
import com.schmaloogium.engine.uniforms.Int2;
import com.schmaloogium.engine.uniforms.OptionalFloat;
import com.schmaloogium.engine.uniforms.OptionalValue;
import com.schmaloogium.engine.uniforms.ShadowMatrixSample;
import com.schmaloogium.engine.uniforms.UniformBuildResult;
import com.schmaloogium.engine.uniforms.UniformConfiguration;
import com.schmaloogium.engine.uniforms.UniformEventSink;
import com.schmaloogium.engine.uniforms.UniformReplayErrorSink;
import com.schmaloogium.engine.uniforms.UniformReplayReport;
import com.schmaloogium.engine.uniforms.UniformRuntime;
import com.schmaloogium.engine.uniforms.UniformRuntimeFactory;
import com.schmaloogium.engine.uniforms.spi.CenterDepthRequest;
import com.schmaloogium.engine.uniforms.spi.CenterDepthResult;
import com.schmaloogium.engine.uniforms.spi.CenterDepthSource;
import com.schmaloogium.engine.uniforms.spi.FrameSampleRequest;
import com.schmaloogium.engine.uniforms.spi.FrameUniformSample;
import com.schmaloogium.engine.uniforms.spi.OnceUniformSample;
import com.schmaloogium.engine.uniforms.spi.TickUniformSample;
import com.schmaloogium.engine.uniforms.spi.UniformPlatformProvider;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * Shared headless fixture for the Phase 6 uniform tests: a recording GL device, scripted
 * platform/center-depth providers, fixture layouts and descriptors. No GL context
 * anywhere; the recorder is bound to the test (render) thread.
 */
public final class UniformFixture {

    public static final long GENERATION = 7L;
    public static final long WORLD = 100L;
    public static final FixedSamplerPolicyFingerprint POLICY =
            new FixedSamplerPolicyFingerprint("policy/test/v1");

    private UniformFixture() {
    }

    public static GLCapabilityProfile profile() {
        return new GLCapabilityProfile(3, 3, "3.30 NVIDIA via GL_ARB...", "vendor", "renderer",
                8, 8, 16, 16, 4096, 256, 0, Set.of());
    }

    public static UniformConfiguration configuration() {
        return new UniformConfiguration("pack/test/v1", 10.0, 10.0, 10.0, 10.0, true, 1);
    }
    public static UniformConfiguration configuration(boolean centerDepthRequired) {
        return new UniformConfiguration("pack/test/v1", 10.0d, 10.0d, 10.0d, 10.0d,
                centerDepthRequired, 1);
    }

    // ---------------------------------------------------------------- values

    public static DeclaredGlslType int1() {
        return new DeclaredGlslType.Scalar(com.schmaloogium.engine.preprocess.ScalarKind.SIGNED_INT);
    }

    public static DeclaredGlslType float1() {
        return new DeclaredGlslType.Scalar(com.schmaloogium.engine.preprocess.ScalarKind.FLOAT);
    }

    public static DeclaredGlslType vec2() {
        return new DeclaredGlslType.Vector(com.schmaloogium.engine.preprocess.ScalarKind.FLOAT, 2);
    }

    public static DeclaredGlslType vec3() {
        return new DeclaredGlslType.Vector(com.schmaloogium.engine.preprocess.ScalarKind.FLOAT, 3);
    }

    public static DeclaredGlslType vec4() {
        return new DeclaredGlslType.Vector(com.schmaloogium.engine.preprocess.ScalarKind.FLOAT, 4);
    }

    public static DeclaredGlslType mat4() {
        return new DeclaredGlslType.Matrix(
                com.schmaloogium.engine.preprocess.ScalarKind.FLOAT, 4, 4);
    }

    public static DeclaredGlslType ivec2() {
        return new DeclaredGlslType.Vector(com.schmaloogium.engine.preprocess.ScalarKind.SIGNED_INT, 2);
    }

    public static DeclaredGlslType ivec4() {
        return new DeclaredGlslType.Vector(com.schmaloogium.engine.preprocess.ScalarKind.SIGNED_INT, 4);
    }

    public static DeclaredGlslType sampler2d() {
        return new DeclaredGlslType.Sampler(SampledKind.FLOAT, TextureDimension.D2, false,
                false, false);
    }

    // ---------------------------------------------------------------- layouts

    /** A deterministic layout from exact name -> declared type (sites elided). */
    public static ProgramUniformLayout layout(Map<String, DeclaredGlslType> declarations) {
        TreeMap<String, ProgramUniformDeclaration> ordered = new TreeMap<>();
        for (Map.Entry<String, DeclaredGlslType> entry : declarations.entrySet()) {
            ordered.put(entry.getKey(), new ProgramUniformDeclaration(
                    entry.getKey(), entry.getValue(), List.of()));
        }
        ProgramUniformLayoutFingerprint fingerprint = new ProgramUniformLayoutFingerprint(
                "layout/" + String.join(",", ordered.keySet()));
        return new ProgramUniformLayout(fingerprint, ordered);
    }

    public static ProgramSamplerLayout samplerLayout(List<ProgramSamplerDeclaration> declarations,
            StageId stage, StageBand band) {
        return samplerLayout(declarations, stage, band, POLICY);
    }

    public static ProgramSamplerLayout samplerLayout(List<ProgramSamplerDeclaration> declarations,
            StageId stage, StageBand band, FixedSamplerPolicyFingerprint policy) {
        return new ProgramSamplerLayout.Shader(
                new ProgramSamplerLayoutFingerprint("samplerlayout/" + stage + "/"
                        + declarations.size()),
                policy,
                stage,
                Set.of(band),
                declarations,
                new SamplerLayoutValidation.Valid());
    }

    public static ProgramSamplerDeclaration samplerDeclaration(String name, int order) {
        return new ProgramSamplerDeclaration(name, sampler2d(), order, List.of());
    }

    public static ResolvedProgramDescriptor descriptor(ProgramUniformLayout uniformLayout,
            ProgramSamplerLayout samplerLayout) {
        ProgramSlotId slot = new ProgramSlotId("pack");
        return new ResolvedProgramDescriptor(
                slot,
                slot,
                defaultState(),
                uniformLayout,
                samplerLayout,
                List.of(),
                List.of());
    }

    public static ProgramStateBundle defaultState() {
        return new ProgramStateBundle(
                new DrawRouting.AllUsedBuffers(BufferDomain.COLORTEX),
                Set.of(), 1, Set.of(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Map.of(), Optional.empty(),
                GeometryInputRequirement.NONE);
    }

    public static BarrierContext context(StageId stage, StageBand band) {
        return new BarrierContext() {
            @Override
            public boolean shadowPass() {
                return band == StageBand.SHADOW;
            }

            @Override
            public StageId stage() {
                return stage;
            }

            @Override
            public StageBand band() {
                return band;
            }
        };
    }

    // ---------------------------------------------------------------- inputs

    public static FrameBeginInput frameInput(long generation, long frameId, long worldEpoch,
            long logicalTick, double smoothingTicks, float seconds, int width, int height,
            int priorWidth, int priorHeight) {
        return new FrameBeginInput(generation, frameId, worldEpoch, logicalTick, smoothingTicks,
                seconds, width, height, priorWidth, priorHeight);
    }

    /** The canonical frame sample used unless a test overrides individual fields. */
    public static FrameUniformSample defaultFrameSample() {
        return new FrameUniformSample(
                WORLD, 1L,
                new Double3(1.0d, 2.0d, 3.0d),
                63.0f,
                new Int2(240, 120),
                0,
                0.0f,
                0.0f,
                0.0f,
                false,
                1000.0f,
                0.25f,
                new OptionalFloat.Absent(),
                new Float3(0.5f, 0.6f, 0.7f),
                new OptionalValue.Absent<FogSample>());
    }

    // ---------------------------------------------------------------- providers

    /** Scripted loader-neutral platform with call counters. */
    public static final class ScriptedPlatform implements UniformPlatformProvider {
        public OnceUniformSample once = new OnceUniformSample(0.05f);
        /** Tests may install a full tick sample; null builds the scripted default. */
        public TickUniformSample tick;
        public int onceCalls;
        public int tickCalls;
        public int frameCalls;
        public boolean echoWrongTickIdentity;
        public boolean echoWrongFrameIdentity;

        @Override
        public OnceUniformSample sampleOnce() {
            onceCalls++;
            return once;
        }

        @Override
        public TickUniformSample sampleTick(long worldEpoch, long logicalTick) {
            tickCalls++;
            if (echoWrongTickIdentity) {
                return new TickUniformSample(worldEpoch + 1, logicalTick, 0L, 0, 0.0f);
            }
            if (tick != null) {
                return tick;
            }
            return new TickUniformSample(worldEpoch, logicalTick,
                    tickWorldTicks(logicalTick), 0, 0.0f);
        }

        @Override
        public FrameUniformSample sampleFrame(FrameSampleRequest request) {
            frameCalls++;
            if (echoWrongFrameIdentity) {
                return frameSample(request.worldEpoch() + 1, request.frameId() + 1);
            }
            return frameSample(request.worldEpoch(), request.frameId());
        }

        private FrameUniformSample frameSample(long worldEpoch, long frameId) {
            FrameUniformSample defaults = defaultFrameSample();
            return new FrameUniformSample(worldEpoch, frameId, camera,
                    defaults.eyeAltitude(), defaults.eyeBrightness(), defaults.isEyeInWater(),
                    defaults.nightVision(), defaults.blindness(), defaults.screenBrightness(),
                    defaults.hideGui(), defaults.farPlane(), defaults.sunAngle(),
                    defaults.shadowAngle(), defaults.skyColor(), defaults.fogFallback());
        }

        /** Camera cell source; tests mutate between frames to observe rotation. */
        public Double3 camera = new Double3(1.0d, 2.0d, 3.0d);

        private static long tickWorldTicks(long logicalTick) {
            return logicalTick * 24000L; // one day per tick keeps worldTime arithmetic exact
        }
    }

    /** Scripted synchronous center-depth source with a request log. */
    public static final class ScriptedCenterDepth implements CenterDepthSource {
        public final Deque<CenterDepthResult> answers = new ArrayDeque<>();
        public final List<CenterDepthRequest> requests = new ArrayList<>();

        public ScriptedCenterDepth() {
            answers.add(new CenterDepthResult.Sample(0.5f));
        }

        @Override
        public CenterDepthResult readCenter(CenterDepthRequest request) {
            requests.add(request);
            return answers.isEmpty() ? new CenterDepthResult.Sample(0.5f) : answers.poll();
        }
    }

    /** Collects diagnostics for assertions. */
    public static final class CollectingDiagnostics implements DiagnosticReporter {
        public final List<EngineDiagnostic> reported = new ArrayList<>();

        @Override
        public void report(EngineDiagnostic d) {
            reported.add(d);
        }

        public long count(String messageKey) {
            return reported.stream().filter(d -> d.messageKey().equals(messageKey)).count();
        }
    }

    /** Collects replay reports; optionally throws to simulate a broken observer. */
    public static final class CollectingReplayErrors implements UniformReplayErrorSink {
        public final List<UniformReplayReport> reports = new ArrayList<>();
        public boolean throwOnAccept;
        public RuntimeException failure = new IllegalStateException("sink exploded");

        @Override
        public void accept(UniformReplayReport report) {
            if (throwOnAccept) {
                throw failure;
            }
            reports.add(report);
        }
    }

    /** A fixed resolver stub for tests that do not need Phase 5's table. */
    public static FixedSamplerResolver fixedUnits(List<ResolvedSamplerBinding> bindings) {
        return (layout, stage, band) -> new FixedSamplerPlanResult.Ready(bindings, POLICY);
    }

    public static FixedSamplerResolver failingResolver(SamplerLayoutValidation reason) {
        return (layout, stage, band) -> new FixedSamplerPlanResult.Invalid(reason);
    }

    /** Activity token with a mutable current flag. */
    public static final class Token implements BoundProgramActivityToken {
        public boolean current = true;

        @Override
        public boolean isCurrent() {
            return current;
        }
    }

    /** Callback-scoped access backed by the recorder, counting locate calls. */
    public static final class TestAccess implements BoundProgramUniformAccess {
        public final ProgramUniformCacheKey key;
        public final ProgramHandle program;
        public final RecordingGLDevice device;
        public final Token token;
        public int locateCalls;

        public TestAccess(ProgramUniformCacheKey key, ProgramHandle program,
                RecordingGLDevice device, Token token) {
            this.key = key;
            this.program = program;
            this.device = device;
            this.token = token;
        }

        @Override
        public ProgramUniformCacheKey cacheKey() {
            return key;
        }

        @Override
        public UniformLocation locate(String exactName) {
            locateCalls++;
            return device.uniforms().locate(program, exactName);
        }

        public BoundProgramActivityToken activityToken() {
            return token;
        }
    }

    // ---------------------------------------------------------------- full runtime

    /** One assembled runtime plus every handle a test needs. */
    public static final class Harness {
        public final RecordingGLDevice device;
        public final ScriptedPlatform platform;
        public final ScriptedCenterDepth center;
        public final CollectingReplayErrors replay;
        public final CollectingDiagnostics diagnostics;
        public final Token token;
        public final ScriptedResponses responses;
        public final UniformRuntime runtime;
        public final UniformEventSink events;
        public final ProgramHandle program;
        public ProgramUniformLayout uniformLayout;
        public ProgramSamplerLayout samplerLayout;
        public TestAccess access;
        public ResolvedProgramDescriptor descriptor;
        public BarrierContext barrierContext;
        public String programLabel = "program";

        Harness(RecordingGLDevice device, ScriptedPlatform platform,
                ScriptedCenterDepth center, CollectingDiagnostics diagnostics,
                CollectingReplayErrors replay, UniformRuntime runtime,
                ProgramHandle program, Token token, ScriptedResponses responses) {
            this.device = device;
            this.platform = platform;
            this.center = center;
            this.diagnostics = diagnostics;
            this.replay = replay;
            this.runtime = runtime;
            this.events = runtime.events();
            this.program = program;
            this.token = token;
            this.responses = responses;
        }

        /** Points the activation fixtures at a concrete program layout. */
        public Harness program(Map<String, DeclaredGlslType> declarations,
                List<ProgramSamplerDeclaration> samplers, StageId stage, StageBand band) {
            return program(declarations, samplers, stage, band, POLICY);
        }

        /** As {@link #program} but with an explicit sampler policy fingerprint. */
        public Harness program(Map<String, DeclaredGlslType> declarations,
                List<ProgramSamplerDeclaration> samplers, StageId stage, StageBand band,
                FixedSamplerPolicyFingerprint policy) {
            this.uniformLayout = layout(declarations);
            this.samplerLayout = samplerLayout(samplers, stage, band, policy);
            this.descriptor = descriptor(uniformLayout, samplerLayout);
            this.access = new TestAccess(
                    new ProgramUniformCacheKey(GENERATION, new ProgramSlotId("pack"),
                            uniformLayout.fingerprint()),
                    program, device, token);
            this.barrierContext = context(stage, band);
            // A materialized program declares its layout ahead of the frame (P3's
            // report stand-in) and the label probe yields the recorder's stable name.
            ((com.schmaloogium.engine.uniforms.runtime.UniformCore) runtime)
                    .cacheFor(access.key, uniformLayout);
            this.programLabel = access.locate("__probe").toString()
                    .substring(0, access.locate("__probe").toString().lastIndexOf('.'));
            return this;
        }

        public FrameBeginResult beginFrame(long frameId, long tick) {
            return runtime.beginFrame(frameInput(GENERATION, frameId, WORLD, tick,
                    tick * 10.0, 0.05f, 800, 600, 800, 600));
        }

        /** sampler -> built-in -> custom, Phase 4's order. */
        public List<BarrierParticipantResult> activate() {
            return List.of(
                    runtime.samplerParticipant().afterBind(descriptor, barrierContext, access),
                    runtime.builtInParticipant().afterBind(descriptor, barrierContext, access),
                    runtime.customParticipant().afterBind(descriptor, barrierContext, access));
        }

        public List<GLCall> uploads() {
            return device.log().calls().stream()
                    .filter(call -> call.op().startsWith("uniforms.upload"))
                    .toList();
        }
    }

    /** A linked (locatable) program on the recording device. */
    public static ProgramHandle linkedProgram(RecordingGLDevice device) {
        var vertex = device.shaders().createShader(
                com.schmaloogium.engine.gl.ShaderStage.VERTEX, "void main(){}");
        var fragment = device.shaders().createShader(
                com.schmaloogium.engine.gl.ShaderStage.FRAGMENT, "void main(){}");
        ProgramHandle program = device.shaders().createProgram();
        device.shaders().attach(program, vertex);
        device.shaders().attach(program, fragment);
        device.shaders().link(program);
        return program;
    }

    /** Assembles a full harness: recording device + scripted providers + live runtime. */
    public static Harness harness() {
        return harness(configuration(), fixedUnits(List.of()));
    }

    public static Harness harness(UniformConfiguration configuration,
            FixedSamplerResolver resolver) {
        ScriptedResponses responses = new ScriptedResponses();
        RecordingGLDevice device = new RecordingGLDevice(profile(), responses);
        ScriptedPlatform platform = new ScriptedPlatform();
        ScriptedCenterDepth center = new ScriptedCenterDepth();
        CollectingDiagnostics diagnostics = new CollectingDiagnostics();
        CollectingReplayErrors replay = new CollectingReplayErrors();
        UniformBuildResult result = UniformRuntimeFactory.factory().create(
                GENERATION, configuration, resolver, platform, center, device,
                diagnostics, replay);
        if (!(result instanceof UniformBuildResult.Success ok)) {
            throw new IllegalStateException("harness construction failed");
        }
        ProgramHandle program = linkedProgram(device);
        return new Harness(device, platform, center, diagnostics, replay, ok.runtime(),
                program, new Token(), responses);
    }
}
