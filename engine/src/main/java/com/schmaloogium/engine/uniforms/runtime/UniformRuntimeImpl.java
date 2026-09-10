// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.runtime;

import com.schmaloogium.engine.buffers.FixedSamplerPlanResult;
import com.schmaloogium.engine.buffers.FixedSamplerResolver;
import com.schmaloogium.engine.buffers.ResolvedSamplerBinding;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.expr.api.BuiltInExpressionView;
import com.schmaloogium.engine.expr.api.BuiltInLookup;
import com.schmaloogium.engine.expr.api.BuiltInValue;
import com.schmaloogium.engine.expr.api.CustomRefreshResult;
import com.schmaloogium.engine.expr.api.CustomSubmitResult;
import com.schmaloogium.engine.expr.api.CustomUniformBridge;
import com.schmaloogium.engine.expr.api.CustomUniformUploadSink;
import com.schmaloogium.engine.expr.api.CustomUploadCommand;
import com.schmaloogium.engine.expr.api.FixedExpressionInputSchema;
import com.schmaloogium.engine.expr.api.FixedInputKind;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.GLError;
import com.schmaloogium.engine.gl.ReplayAwareGLError;
import com.schmaloogium.engine.gl.UniformLocation;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierParticipantResult;
import com.schmaloogium.engine.registry.BoundProgramUniformAccess;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.registry.ProgramBindingParticipant;
import com.schmaloogium.engine.registry.ProgramUniformCacheKey;
import com.schmaloogium.engine.registry.ProgramUniformDeclaration;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramUniformLayout;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import com.schmaloogium.engine.uniforms.BlendSample;
import com.schmaloogium.engine.uniforms.CelestialSample;
import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.uniforms.Float4;
import com.schmaloogium.engine.uniforms.FogSample;
import com.schmaloogium.engine.uniforms.FrameBeginInput;
import com.schmaloogium.engine.uniforms.FrameBeginResult;
import com.schmaloogium.engine.uniforms.HeldItemSample;
import com.schmaloogium.engine.uniforms.Int2;
import com.schmaloogium.engine.uniforms.Matrix4Value;
import com.schmaloogium.engine.uniforms.catalog.Milestone;
import com.schmaloogium.engine.uniforms.OptionalFloat;
import com.schmaloogium.engine.uniforms.OptionalValue;
import com.schmaloogium.engine.uniforms.RegistryGenerationAdoptionResult;
import com.schmaloogium.engine.uniforms.ShadowMatrixSample;
import com.schmaloogium.engine.uniforms.UniformConfiguration;
import com.schmaloogium.engine.uniforms.UniformEventSink;
import com.schmaloogium.engine.uniforms.UniformFrameTiming;
import com.schmaloogium.engine.uniforms.UniformResetReason;
import com.schmaloogium.engine.uniforms.UniformRetirementReason;
import com.schmaloogium.engine.uniforms.UniformRetirementResult;
import com.schmaloogium.engine.uniforms.UniformReplayReport;
import com.schmaloogium.engine.uniforms.UniformRuntime;
import com.schmaloogium.engine.uniforms.catalog.BuiltInUniform;
import com.schmaloogium.engine.uniforms.catalog.BuiltInUniformCatalog;
import com.schmaloogium.engine.uniforms.catalog.BuiltInUniformType;
import com.schmaloogium.engine.uniforms.matrix.Matrix4;
import com.schmaloogium.engine.uniforms.spi.CenterDepthRequest;
import com.schmaloogium.engine.uniforms.spi.CenterDepthResult;
import com.schmaloogium.engine.uniforms.spi.CenterDepthSource;
import com.schmaloogium.engine.uniforms.spi.FrameSampleRequest;
import com.schmaloogium.engine.uniforms.spi.FrameUniformSample;
import com.schmaloogium.engine.uniforms.spi.OnceUniformSample;
import com.schmaloogium.engine.uniforms.spi.TickUniformSample;
import com.schmaloogium.engine.uniforms.spi.UniformPlatformProvider;
import com.schmaloogium.engine.uniforms.smooth.TickEma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * The only operational {@link UniformRuntime} implementation (PHASE_6_DOC §4). Render
 * thread confined; constructed from pure configuration; zero GL work at construction.
 * All §4 invariants — cadence buckets, the frame-begin ordering, the rung-2 attempt
 * protocol, immediate signal uploads, sampler plan reuse, the retirement algebra and the
 * replay-delivery failure latch — live here.
 */
public final class UniformRuntimeImpl implements UniformRuntime, UniformCore {

    /** The engine's single production factory (§2.2). */
    public static final com.schmaloogium.engine.uniforms.UniformRuntimeFactory FACTORY
            = new com.schmaloogium.engine.uniforms.UniformRuntimeFactory() {
        @Override
        public com.schmaloogium.engine.uniforms.UniformBuildResult create(
                long initialRegistryGeneration,
                UniformConfiguration configuration,
                FixedSamplerResolver samplerResolver,
                UniformPlatformProvider platform,
                CenterDepthSource centerDepth,
                GLDevice gl,
                DiagnosticReporter diagnostics,
                com.schmaloogium.engine.uniforms.UniformReplayErrorSink replayErrors) {
            if (configuration == null) {
                return new com.schmaloogium.engine.uniforms.UniformBuildResult.Failure(
                        "phase6.factory.null.configuration");
            }
            if (samplerResolver == null) {
                return new com.schmaloogium.engine.uniforms.UniformBuildResult.Failure(
                        "phase6.factory.null.samplerResolver");
            }
            if (platform == null) {
                return new com.schmaloogium.engine.uniforms.UniformBuildResult.Failure(
                        "phase6.factory.null.platform");
            }
            if (centerDepth == null) {
                return new com.schmaloogium.engine.uniforms.UniformBuildResult.Failure(
                        "phase6.factory.null.centerDepthSource");
            }
            if (gl == null) {
                return new com.schmaloogium.engine.uniforms.UniformBuildResult.Failure(
                        "phase6.factory.null.glDevice");
            }
            if (diagnostics == null) {
                return new com.schmaloogium.engine.uniforms.UniformBuildResult.Failure(
                        "phase6.factory.null.diagnostics");
            }
            if (replayErrors == null) {
                return new com.schmaloogium.engine.uniforms.UniformBuildResult.Failure(
                        "phase6.factory.null.replayErrors");
            }
            return new com.schmaloogium.engine.uniforms.UniformBuildResult.Success(
                    new UniformRuntimeImpl(initialRegistryGeneration, configuration,
                            samplerResolver, platform, centerDepth, gl, diagnostics,
                            replayErrors));
        }
    };

    /** The no-op default bridge until Phase 11 installs one (§4.13); never stored. */
    private static final CustomUniformBridge NO_CUSTOMS = new CustomUniformBridge() {
        @Override
        public CustomRefreshResult refresh(BuiltInExpressionView values,
                CustomUniformUploadSink uploads) {
            return new CustomRefreshResult.NoCustoms();
        }
    };

    private static final int FRAME_COUNTER_MODULUS = 720720;
    private static final float FRAME_TIME_COUNTER_WRAP = 3600f;
    private static final float WORLD_TICKS_PER_DAY = 24000f;

    private final UniformConfiguration configuration;
    private final FixedSamplerResolver samplerResolver;
    private final UniformPlatformProvider platform;
    private final CenterDepthSource centerDepthSource;
    private final GLDevice gl;
    private final DiagnosticReporter diagnostics;
    private final com.schmaloogium.engine.uniforms.UniformReplayErrorSink replayErrors;

    private final ProgramBindingParticipant samplerParticipant = new SamplerParticipant();
    private final ProgramBindingParticipant builtInParticipant = new BuiltInParticipant();
    private final ProgramBindingParticipant customParticipant = new CustomParticipant();
    private final UniformEventSink events = new RuntimeEventSink();
    private final BuiltInExpressionView expressionView = new BuiltInView();
    private final FixedExpressionInputSchema schema;

    private final Thread renderThread;
    private final Map<ProgramUniformCacheKey, ProgramCache> caches = new LinkedHashMap<>();

    private boolean retired;
    private int callbackDepth;
    private long currentGeneration;
    private ActivePair activePair;

    private CustomUniformBridge bridge;
    private boolean bridgeActivityObserved;

    // ---- cell store: exact catalog name -> canonical value; absent key = invalid ----
    private final Map<String, UniformValue> cells = new HashMap<>();

    // ---- temporal state (§4.6) ----
    private boolean frameAcceptedEver;
    private boolean worldKnown;
    private long currentWorldEpoch;
    private long latestFrameId;
    private long lastSampledTick;
    private boolean lastSampledTickValid;
    private boolean cameraResetPending;
    private boolean matricesResetPending;
    private Long lastCaptureFrameId;
    private boolean matricesCurrentForFrame;
    private long lastHeldTick = Long.MIN_VALUE;

    // ---- smoothers and counters (§4.5/§4.6) ----
    private final TickEma wetnessSmooth = new TickEma();
    private final TickEma eyeBrightnessSmoothBlock = new TickEma();
    private final TickEma eyeBrightnessSmoothSky = new TickEma();
    private final TickEma centerDepthSmooth = new TickEma();
    private long lastSmoothingTicks;
    private boolean lastSmoothingTicksValid;
    private int frameCounter;
    private float frameTimeCounter;

    // ---- latest accepted frame (§2.2, D-P6-25) ----
    private boolean timingValid;
    private UniformFrameTiming cachedTiming;
    private FrameBeginInput latestInput;

    // ---- replay-delivery failure latch (§4.11) ----
    private String deliveryFailedDiagnostic;
    private final List<UniformReplayReport> undeliveredReports = new ArrayList<>();
    private boolean centerDepthDeclaredSeen;
    /** Headless-verification seam: replaces the uniform service the batch drives. */
    private com.schmaloogium.engine.gl.UniformService replayUniformsOverride;

    // ---- once-latched diagnostic keys ----
    private final Set<String> reportedKeys = new LinkedHashSet<>();

    // ---- fog provenance (§4.12): updateFog always wins over frame fallbacks ----
    private boolean fogSignalSourced;

    private UniformRuntimeImpl(long initialRegistryGeneration,
            UniformConfiguration configuration,
            FixedSamplerResolver samplerResolver,
            UniformPlatformProvider platform,
            CenterDepthSource centerDepthSource,
            GLDevice gl,
            DiagnosticReporter diagnostics,
            com.schmaloogium.engine.uniforms.UniformReplayErrorSink replayErrors) {
        this.configuration = configuration;
        this.samplerResolver = samplerResolver;
        this.platform = platform;
        this.centerDepthSource = centerDepthSource;
        this.gl = gl;
        this.diagnostics = diagnostics;
        this.replayErrors = replayErrors;
        this.renderThread = Thread.currentThread();
        this.currentGeneration = initialRegistryGeneration;
        this.schema = buildSchema();

        // ONCE bucket: configuration/world-provider install at construction (§4.4.1).
        OnceUniformSample once = platform.sampleOnce();
        cells.put("near", new UniformValue.F(once.nearPlane()));
    }

    // ===================================================================
    // UniformRuntime — lifecycle
    // ===================================================================

    @Override
    public FrameBeginResult beginFrame(FrameBeginInput input) {
        requireRenderThread();
        Objects.requireNonNull(input, "input");
        bridgeActivityObserved = true;
        callbackDepth++;
        try {
            if (retired) {
                return FrameBeginResult.REJECTED_GENERATION;
            }
            // (1) validate registry generation and world epoch.
            if (input.registryGeneration() != currentGeneration) {
                return FrameBeginResult.REJECTED_GENERATION;
            }
            boolean newWorld = false;
            if (worldKnown) {
                if (input.worldEpoch() < currentWorldEpoch) {
                    return FrameBeginResult.REJECTED_STALE_FRAME;
                }
                newWorld = input.worldEpoch() > currentWorldEpoch;
                if (!newWorld) {
                    if (input.frameId() == latestFrameId) {
                        return FrameBeginResult.DUPLICATE;
                    }
                    if (input.frameId() < latestFrameId) {
                        return FrameBeginResult.REJECTED_STALE_FRAME;
                    }
                }
            }
            if (newWorld) {
                applyWorldEpochTransition(input.worldEpoch());
            }
            acceptFrame(input);
            return FrameBeginResult.ACCEPTED;
        } finally {
            callbackDepth--;
        }
    }

    // ===================================================================
    // UniformRuntime — published views
    // ===================================================================

    @Override
    public RegistryGenerationAdoptionResult adoptRegistryGeneration(long generation,
            UniformResetReason reason) {
        requireRenderThread();
        Objects.requireNonNull(reason, "reason");
        if (reason == UniformResetReason.WORLD_EPOCH) {
            throw new IllegalArgumentException(
                    "WORLD_EPOCH is a temporal reset, not a generation adoption");
        }
        if (retired) {
            return RegistryGenerationAdoptionResult.REJECTED_RETIRED_GENERATION;
        }
        if (generation == currentGeneration) {
            return RegistryGenerationAdoptionResult.ALREADY_CURRENT;
        }
        currentGeneration = generation;
        caches.clear();
        activePair = null;
        timingValid = false;
        cachedTiming = null;
        return RegistryGenerationAdoptionResult.ADOPTED;
    }

    @Override
    public FixedExpressionInputSchema fixedExpressionInputSchema() {
        return schema;
    }

    @Override
    public Optional<UniformFrameTiming> frameTiming(long registryGeneration, long frameId) {
        requireRenderThread(); // throws before reading state, even after retirement
        if (retired || !timingValid) {
            return Optional.empty();
        }
        if (registryGeneration != currentGeneration || frameId != latestFrameId
                || latestInput == null) {
            return Optional.empty();
        }
        if (cachedTiming == null) {
            FrameBeginInput input = latestInput;
            cachedTiming = new UniformFrameTiming(
                    input.registryGeneration(),
                    input.frameId(),
                    input.worldEpoch(),
                    input.logicalTick(),
                    input.smoothingTimeTicks(),
                    input.frameTimeSeconds(),
                    frameCounter,
                    frameTimeCounter);
        }
        return Optional.of(cachedTiming);
    }

    @Override
    public UniformEventSink events() {
        return events;
    }

    @Override
    public ProgramBindingParticipant samplerParticipant() {
        return samplerParticipant;
    }

    @Override
    public ProgramBindingParticipant builtInParticipant() {
        return builtInParticipant;
    }

    @Override
    public ProgramBindingParticipant customParticipant() {
        return customParticipant;
    }

    @Override
    public com.schmaloogium.engine.preprocess.MacroContributor centerDepthMacroContributor() {
        return configurationValue -> new com.schmaloogium.engine.preprocess.MacroContribution.Empty();
    }

    @Override
    public void installCustomUniformBridge(CustomUniformBridge bridge) {
        if (bridge == null) {
            throw new IllegalArgumentException("bridge must be non-null; the default is implicit");
        }
        if (retired) {
            throw new IllegalStateException("runtime is retired; bridge installation rejected");
        }
        if (this.bridge != null) {
            if (this.bridge == bridge) {
                return; // repeating the exact instance is a no-op
            }
            throw new IllegalStateException("a different custom bridge is already installed");
        }
        if (bridgeActivityObserved) {
            throw new IllegalStateException(
                    "bridge installation must precede the first beginFrame/activation");
        }
        this.bridge = bridge;
    }

    @Override
    public void reset(UniformResetReason reason) {
        requireRenderThread();
        Objects.requireNonNull(reason, "reason");
        if (reason != UniformResetReason.WORLD_EPOCH) {
            throw new IllegalArgumentException(
                    "reset accepts only WORLD_EPOCH; use adoptRegistryGeneration for " + reason);
        }
        ensureNotRetired();
        if (callbackDepth > 0) {
            throw new IllegalStateException("reset during active callback");
        }
        applyWorldEpochTransition(worldKnown ? currentWorldEpoch : 0);
    }

    @Override
    public com.schmaloogium.engine.uniforms.UniformRetirementResult retire(
            com.schmaloogium.engine.uniforms.UniformRetirementReason reason) {
        requireRenderThread();
        Objects.requireNonNull(reason, "reason");
        if (callbackDepth > 0) {
            return new UniformRetirementResult.Rejected(
                    com.schmaloogium.engine.uniforms.UniformRetirementRejection.ACTIVE_CALLBACK);
        }
        if (retired) {
            return new UniformRetirementResult.AlreadyRetired();
        }
        retired = true;
        caches.clear();
        cells.clear();
        activePair = null;
        bridge = null;
        bridgeActivityObserved = false;
        undeliveredReports.clear();
        reportedKeys.clear();
        timingValid = false;
        cachedTiming = null;
        latestInput = null;
        lastCaptureFrameId = null;
        return new UniformRetirementResult.Retired();
    }

    /** Headless-verification seam (§8): drive the batch through a wrapping service. */
    public void replayUniformServiceOverride(com.schmaloogium.engine.gl.UniformService override) {
        this.replayUniformsOverride = override;
    }

    @Override
    public boolean isRetired() {
        return retired;
    }

    // ===================================================================
    // Event-sink execution paths (§4.12; the sink delegates here)
    // ===================================================================

    private void executeCaptureGbufferMatrices(long frameId, Matrix4Value modelView,
            Matrix4Value projection) {
        requireRenderThread();
        ensureNotRetired();
        callbackDepth++;
        try {
            if (!frameAcceptedEver || frameId != latestFrameId) {
                warnOnce("phase6.capture.stale.frame",
                        "gbuffer matrix capture does not match the latest accepted frame");
                return;
            }
            if (lastCaptureFrameId != null && lastCaptureFrameId == frameId) {
                warnOnce("phase6.capture.duplicate",
                        "second gbuffer matrix capture for the same frame is ignored");
                return;
            }
            float[] mv = modelView.toColumnMajorArray();
            float[] proj = projection.toColumnMajorArray();
            installMatrixCells(mv, proj);
            if (matricesResetPending) {
                // First valid new-world capture initializes previous-current.
                installPreviousMatrices(mv, proj);
                matricesResetPending = false;
            }
            lastCaptureFrameId = frameId;
            matricesCurrentForFrame = true;
        } finally {
            callbackDepth--;
        }
    }

    private void executeUpdateShadowMatrices(ShadowMatrixSample sample) {
        requireRenderThread();
        ensureNotRetired();
        callbackDepth++;
        try {
            if (!validateEventIdentity(sample.worldEpoch(), sample.frameId(),
                    "shadow matrices")) {
                return;
            }
            cells.put("shadowProjection", m4(sample.projection()));
            cells.put("shadowModelView", m4(sample.modelView()));
            installInverse("shadowProjectionInverse",
                    sample.projection().toColumnMajorArray());
            installInverse("shadowModelViewInverse", sample.modelView().toColumnMajorArray());
        } finally {
            callbackDepth--;
        }
    }

    private void executeUpdateCelestial(CelestialSample sample) {
        requireRenderThread();
        ensureNotRetired();
        callbackDepth++;
        try {
            if (!validateEventIdentity(sample.worldEpoch(), sample.frameId(), "celestial")) {
                return;
            }
            cells.put("sunPosition", f3(sample.sunPosition()));
            cells.put("moonPosition", f3(sample.moonPosition()));
            cells.put("shadowLightPosition", f3(sample.shadowLightPosition()));
            cells.put("upPosition", f3(sample.upPosition()));
            immediateUpload("sunPosition", "moonPosition", "shadowLightPosition", "upPosition");
        } finally {
            callbackDepth--;
        }
    }

    private void executeUpdateFog(FogSample sample) {
        requireRenderThread();
        ensureNotRetired();
        callbackDepth++;
        try {
            if (!validateEventIdentity(sample.worldEpoch(), sample.frameId(), "fog")) {
                return;
            }
            cells.put("fogMode", new UniformValue.I(sample.fogMode()));
            cells.put("fogDensity", new UniformValue.F(sample.density()));
            cells.put("fogColor", f3(sample.color()));
            fogSignalSourced = true;
            immediateUpload("fogMode", "fogDensity", "fogColor");
        } finally {
            callbackDepth--;
        }
    }

    private void executeUpdateBlend(BlendSample sample) {
        requireRenderThread();
        ensureNotRetired();
        callbackDepth++;
        try {
            if (!validateEventIdentity(sample.worldEpoch(), sample.frameId(), "blend")) {
                return;
            }
            int sRgb = sample.enabled() ? sample.srcRgb() : 0;
            int dRgb = sample.enabled() ? sample.dstRgb() : 0;
            int sAlpha = sample.enabled() ? sample.srcAlpha() : 0;
            int dAlpha = sample.enabled() ? sample.dstAlpha() : 0;
            cells.put("blendFunc", new UniformValue.I4(sRgb, dRgb, sAlpha, dAlpha));
            immediateUpload("blendFunc");
        } finally {
            callbackDepth--;
        }
    }

    private void executeUpdateEntityColor(Float4 value) {
        requireRenderThread();
        ensureNotRetired();
        callbackDepth++;
        try {
            cells.put("entityColor", new UniformValue.F4(value.x(), value.y(), value.z(),
                    value.w()));
            immediateUpload("entityColor");
        } finally {
            callbackDepth--;
        }
    }

    private void executeUpdateEntityId(int value) {
        requireRenderThread();
        ensureNotRetired();
        callbackDepth++;
        try {
            cells.put("entityId", new UniformValue.I(value));
            immediateUpload("entityId");
        } finally {
            callbackDepth--;
        }
    }

    private void executeUpdateBlockEntityId(int value) {
        requireRenderThread();
        ensureNotRetired();
        callbackDepth++;
        try {
            cells.put("blockEntityId", new UniformValue.I(value));
            immediateUpload("blockEntityId");
        } finally {
            callbackDepth--;
        }
    }

    private void executeUpdateInstanceId(int value) {
        requireRenderThread();
        ensureNotRetired();
        callbackDepth++;
        try {
            cells.put("instanceId", new UniformValue.I(value));
            immediateUpload("instanceId");
        } finally {
            callbackDepth--;
        }
    }

    private void executeUpdateAtlasSize(Int2 value) {
        requireRenderThread();
        ensureNotRetired();
        Objects.requireNonNull(value, "value");
        callbackDepth++;
        try {
            cells.put("atlasSize", new UniformValue.I2(value.x(), value.y()));
            immediateUpload("atlasSize");
        } finally {
            callbackDepth--;
        }
    }

    private void executeUpdateHeldItems(HeldItemSample sample) {
        requireRenderThread();
        ensureNotRetired();
        Objects.requireNonNull(sample, "sample");
        callbackDepth++;
        try {
            validateEventWorld(sample.worldEpoch(), "held items");
            if (lastHeldTick != Long.MIN_VALUE && sample.logicalTick() < lastHeldTick) {
                return; // stale: a sample replaces the value for its logical tick only
            }
            lastHeldTick = sample.logicalTick();
            cells.put("heldItemId", new UniformValue.I(sample.heldItemId()));
            cells.put("heldBlockLightValue", new UniformValue.I(sample.heldBlockLightValue()));
            cells.put("heldItemId2", new UniformValue.I(sample.heldItemId2()));
            cells.put("heldBlockLightValue2", new UniformValue.I(sample.heldBlockLightValue2()));
        } finally {
            callbackDepth--;
        }
    }

    // ===================================================================
    // Frame acceptance (§4.6) — the exact accepted-frame sequence
    // ===================================================================

    private void acceptFrame(FrameBeginInput input) {
        latestInput = input;
        latestFrameId = input.frameId();

        // (2) rotate camera previous <- current before sampling (skip on reset-pending).
        if (!cameraResetPending) {
            rotateCell("previousCameraPosition", "cameraPosition");
        }

        // (3) rotate gbuffer current -> previous (last valid capture only).
        if (!matricesResetPending) {
            rotateMatrixCell("gbufferPreviousModelView", "gbufferModelView");
            rotateMatrixCell("gbufferPreviousProjection", "gbufferProjection");
            rotateMatrixCell("gbufferPreviousModelViewInverse", "gbufferModelViewInverse");
            rotateMatrixCell("gbufferPreviousProjectionInverse", "gbufferProjectionInverse");
        }
        matricesCurrentForFrame = false;

        // (4) tick-domain cadence sample when the logical tick changed.
        if (!lastSampledTickValid || input.logicalTick() != lastSampledTick) {
            sampleTick(input);
        }

        // (5) frame-domain cadence sample, exactly once per accepted frame.
        sampleFrame(input);

        // (6) synchronous center-depth read of the completed prior image.
        CenterDepthResult center = readCenterDepth(input);

        // (7) advance the four half-life smoothers.
        advanceSmoothers(input, center);

        // (8) counters advance and the timing snapshot becomes queryable.
        frameCounter = (frameCounter + 1) % FRAME_COUNTER_MODULUS;
        frameTimeCounter += input.frameTimeSeconds();
        if (frameTimeCounter >= FRAME_TIME_COUNTER_WRAP) {
            frameTimeCounter = 0f;
        }
        cells.put("frameCounter", new UniformValue.I(frameCounter));
        cells.put("frameTimeCounter", new UniformValue.F(normalizeFloat(frameTimeCounter)));

        worldKnown = true;
        currentWorldEpoch = input.worldEpoch();
        lastSampledTick = input.logicalTick();
        lastSampledTickValid = true;
        frameAcceptedEver = true;
        timingValid = true;
        cachedTiming = null;
    }

    private void sampleTick(FrameBeginInput input) {
        TickUniformSample sample = platform.sampleTick(input.worldEpoch(), input.logicalTick());
        if (sample.worldEpoch() != input.worldEpoch()
                || sample.logicalTick() != input.logicalTick()) {
            warnOnce("phase6.tick.identity.mismatch",
                    "tick sample identity does not echo the call arguments");
            return;
        }
        cells.put("worldTime", new UniformValue.I(
                (int) Math.floorMod(sample.worldTicks(), 24000L)));
        cells.put("worldDay", new UniformValue.I(
                (int) Math.floorDiv(sample.worldTicks(), 24000L)));
        cells.put("moonPhase", new UniformValue.I(sample.moonPhase()));
        cells.put("rainStrength", new UniformValue.F(sample.rainStrength()));
    }

    private void sampleFrame(FrameBeginInput input) {
        FrameSampleRequest request = new FrameSampleRequest(currentGeneration, input.worldEpoch(),
                input.frameId(), input.logicalTick(), input.frameTimeSeconds(),
                input.targetViewWidth(), input.targetViewHeight());
        FrameUniformSample sample = platform.sampleFrame(request);
        if (sample.worldEpoch() != request.worldEpoch()
                || sample.frameId() != request.frameId()) {
            throw new IllegalStateException(
                    "frame sample identity does not echo the accepted frame");
        }

        cells.put("frameTime", new UniformValue.F(
                normalizeFloat(input.frameTimeSeconds())));
        cells.put("viewWidth", new UniformValue.F(input.targetViewWidth()));
        cells.put("viewHeight", new UniformValue.F(input.targetViewHeight()));
        cells.put("aspectRatio", new UniformValue.F(input.targetViewWidth()
                / (float) input.targetViewHeight()));
        cells.put("far", new UniformValue.F(sample.farPlane()));
        cells.put("cameraPosition", new UniformValue.F3(
                normalizeFloat((float) sample.cameraPosition().x()),
                normalizeFloat((float) sample.cameraPosition().y()),
                normalizeFloat((float) sample.cameraPosition().z())));
        cells.put("eyeAltitude", new UniformValue.F(sample.eyeAltitude()));
        cells.put("eyeBrightness", new UniformValue.I2(
                sample.eyeBrightness().x(), sample.eyeBrightness().y()));
        cells.put("isEyeInWater", new UniformValue.I(sample.isEyeInWater()));
        cells.put("nightVision", new UniformValue.F(sample.nightVision()));
        cells.put("blindness", new UniformValue.F(sample.blindness()));
        cells.put("screenBrightness", new UniformValue.F(sample.screenBrightness()));
        cells.put("hideGUI", new UniformValue.I(sample.hideGui() ? 1 : 0));
        cells.put("sunAngle", new UniformValue.F(sample.sunAngle()));
        if (sample.shadowAngle() instanceof OptionalFloat.Present present) {
            cells.put("shadowAngle", new UniformValue.F(present.value()));
        } else {
            cells.remove("shadowAngle");
        }
        cells.put("skyColor", f3(sample.skyColor()));

        // Fog fallback installs only while no fog signal value exists yet (§4.12).
        if (!fogSignalSourced) {
            if (sample.fogFallback() instanceof OptionalValue.Present<FogSample> fallback) {
                cells.put("fogMode", new UniformValue.I(fallback.value().fogMode()));
                cells.put("fogDensity", new UniformValue.F(fallback.value().density()));
                cells.put("fogColor", f3(fallback.value().color()));
            } else {
                cells.remove("fogMode");
                cells.remove("fogDensity");
                cells.remove("fogColor");
            }
        }

        if (cameraResetPending) {
            // First valid new-world camera sample initializes previous to current.
            UniformValue camera = cells.get("cameraPosition");
            if (camera != null) {
                cells.put("previousCameraPosition", camera);
            }
            cameraResetPending = false;
        }
    }

    private CenterDepthResult readCenterDepth(FrameBeginInput input) {
        if (!configuration.centerDepthRequired() || !centerDepthDeclaredSeen) {
            return null; // never uploads; no read occurs
        }
        int width = input.priorFramebufferWidth();
        int height = input.priorFramebufferHeight();
        if (width <= 0 || height <= 0) {
            return new CenterDepthResult.Unavailable("phase6.center.depth.empty.framebuffer");
        }
        CenterDepthRequest request = new CenterDepthRequest(currentGeneration, input.worldEpoch(),
                input.frameId(), width, height, width >> 1, height >> 1);
        return centerDepthSource.readCenter(request);
    }

    private void advanceSmoothers(FrameBeginInput input, CenterDepthResult center) {
        long now = (long) Math.floor(input.smoothingTimeTicks());
        long elapsed = lastSmoothingTicksValid ? Math.max(0L, now - lastSmoothingTicks) : 0L;
        advanceWetness(elapsed);
        advanceEyeBrightness(elapsed);
        advanceCenterDepth(center, elapsed);
        if (!lastSmoothingTicksValid || now >= lastSmoothingTicks) {
            lastSmoothingTicks = now;
            lastSmoothingTicksValid = true;
        }
    }

    /** target &gt; accumulator uses the wetness half-life, otherwise dryness; equality
     *  leaves the accumulator unchanged (§4.5). */
    private void advanceWetness(long elapsed) {
        if (!(cells.get("rainStrength") instanceof UniformValue.F rain)) {
            return; // target invalid: retain, no advance
        }
        double target = rain.x();
        double halflife;
        if (wetnessSmooth.isValid()) {
            double current = wetnessSmooth.value().orElseThrow();
            if (target == current) {
                return; // equality leaves the accumulator unchanged
            }
            halflife = target > current
                    ? configuration.wetnessHalflifeTicks()
                    : configuration.drynessHalflifeTicks();
        } else {
            halflife = configuration.wetnessHalflifeTicks();
        }
        wetnessSmooth.advance(target, elapsed, halflife);
        if (wetnessSmooth.isValid()) {
            cells.put("wetness", new UniformValue.F(wetnessSmooth.floatValue()));
        }
    }

    private void advanceEyeBrightness(long elapsed) {
        if (!(cells.get("eyeBrightness") instanceof UniformValue.I2 brightness)) {
            return;
        }
        double halflife = configuration.eyeBrightnessHalflifeTicks();
        TickEma.Advance block = eyeBrightnessSmoothBlock.advance(brightness.x(), elapsed,
                halflife);
        TickEma.Advance sky = eyeBrightnessSmoothSky.advance(brightness.y(), elapsed, halflife);
        if (block.changedValue() || sky.changedValue()) {
            cells.put("eyeBrightnessSmooth", new UniformValue.I2(
                    clampBrightness(eyeBrightnessSmoothBlock.intValue()),
                    clampBrightness(eyeBrightnessSmoothSky.intValue())));
        }
    }

    private static int clampBrightness(int value) {
        return Math.max(0, Math.min(240, value));
    }

    private void advanceCenterDepth(CenterDepthResult center, long elapsed) {
        if (!configuration.centerDepthRequired()) {
            return;
        }
        if (center instanceof CenterDepthResult.Sample sample) {
            float depth = sample.depth();
            if (!Float.isFinite(depth) || depth < 0f || depth > 1f) {
                warnOnce("phase6.center.depth.out.of.range",
                        "center depth sample outside [0,1] retained without advancing");
                return;
            }
            centerDepthSmooth.advance(depth, elapsed, configuration.centerDepthHalflifeTicks());
            if (centerDepthSmooth.isValid()) {
                cells.put("centerDepthSmooth",
                        new UniformValue.F(centerDepthSmooth.floatValue()));
            }
            return;
        }
        if (center instanceof CenterDepthResult.Unavailable unavailable) {
            warnOnce(unavailable.diagnosticId(),
                    "center depth unavailable; accumulator retained without advancing");
        }
    }

    private void applyWorldEpochTransition(long newEpoch) {
        currentWorldEpoch = newEpoch;
        worldKnown = true;
        cameraResetPending = true;
        matricesResetPending = true;
        lastHeldTick = Long.MIN_VALUE;
        wetnessSmooth.invalidate();
        eyeBrightnessSmoothBlock.invalidate();
        eyeBrightnessSmoothSky.invalidate();
        centerDepthSmooth.invalidate();
        cells.remove("wetness");
        cells.remove("eyeBrightnessSmooth");
        cells.remove("centerDepthSmooth");
        cells.remove("previousCameraPosition");
        cells.remove("gbufferPreviousModelView");
        cells.remove("gbufferPreviousProjection");
        cells.remove("gbufferPreviousModelViewInverse");
        cells.remove("gbufferPreviousProjectionInverse");
        lastSmoothingTicksValid = false;
        timingValid = false;
        cachedTiming = null;
    }

    private void rotateCell(String previousName, String currentName) {
        UniformValue current = cells.get(currentName);
        if (current != null) {
            cells.put(previousName, current);
        }
    }

    private void rotateMatrixCell(String previousName, String currentName) {
        if (cells.get(currentName) instanceof UniformValue.M4 matrix) {
            cells.put(previousName, new UniformValue.M4(matrix.columnMajor()));
        }
    }

    private void installMatrixCells(float[] modelView, float[] projection) {
        cells.put("gbufferModelView", new UniformValue.M4(modelView));
        cells.put("gbufferProjection", new UniformValue.M4(projection));
        installInverse("gbufferModelViewInverse", modelView);
        installInverse("gbufferProjectionInverse", projection);
    }

    private void installPreviousMatrices(float[] modelView, float[] projection) {
        cells.put("gbufferPreviousModelView", new UniformValue.M4(modelView));
        cells.put("gbufferPreviousProjection", new UniformValue.M4(projection));
        installInverse("gbufferPreviousModelViewInverse", modelView);
        installInverse("gbufferPreviousProjectionInverse", projection);
    }

    private void installInverse(String name, float[] columnMajor) {
        Optional<float[]> inverse = Matrix4.invertColumnMajor(columnMajor);
        if (inverse.isPresent()) {
            cells.put(name, new UniformValue.M4(inverse.get()));
        } else {
            cells.remove(name);
            warnOnce("phase6.matrix.singular." + name,
                    "singular matrix; " + name + " cell invalidated");
        }
    }

    /** World identity must match once known; a stale frame id is ignored with a
     *  once-per-runtime diagnostic (§4.12). Returns false when the event is stale. */
    private boolean validateEventIdentity(long worldEpoch, long frameId, String what) {
        validateEventWorld(worldEpoch, what);
        if (frameAcceptedEver && frameId != latestFrameId) {
            warnOnce("phase6.event.stale.frame." + what,
                    what + " event does not match the latest accepted frame; ignored");
            return false;
        }
        return true;
    }

    private void validateEventWorld(long worldEpoch, String what) {
        if (worldKnown && worldEpoch != currentWorldEpoch) {
            throw new IllegalStateException(what + " world epoch " + worldEpoch
                    + " does not match the runtime's current world epoch " + currentWorldEpoch);
        }
    }


    // ===================================================================
    // §4.11 attempt protocol
    // ===================================================================

    @Override
    public BarrierParticipantResult runBatch(ProgramUniformCacheKey key, ProgramCache cache,
            List<UploadCommand> batch, String degradationScope) {
        if (deliveryFailedDiagnostic != null) {
            return new BarrierParticipantResult.Degraded(deliveryFailedDiagnostic,
                    "uniform replay evidence delivery");
        }
        com.schmaloogium.engine.gl.UniformService uniforms = replayUniformsOverride != null
                ? replayUniformsOverride
                : gl.uniforms();
        List<UniformReplayReport> reports = new ArrayList<>();

        // Pre-upload cleanup drain: never attributed to this batch.
        List<GLError> pre = gl.drainErrors();
        if (!pre.isEmpty()) {
            reports.add(new UniformReplayReport(key, toEvidence(pre, false)));
        }

        // The attempt batch: every command uploads exactly once.
        for (UploadCommand command : batch) {
            uploadOne(uniforms, command);
        }

        // The trigger drain decides the branch.
        List<GLError> trigger = gl.drainErrors();
        if (trigger.isEmpty()) {
            if (!deliver(reports)) {
                return new BarrierParticipantResult.Degraded(deliveryFailedDiagnostic,
                        "uniform replay evidence delivery");
            }
            commit(cache, batch);
            cache.resetRecurrence();
            return new BarrierParticipantResult.Continue();
        }

        // Rung 2: isolation replay, one probe per batch entry.
        boolean[] attributed = new boolean[trigger.size()];
        List<UniformReplayReport> probeReports = new ArrayList<>();
        Set<String> isolatedNames = new LinkedHashSet<>();
        for (UploadCommand command : batch) {
            uploadOne(uniforms, command);
            List<GLError> window = gl.drainErrors();
            if (window.isEmpty()) {
                continue;
            }
            boolean anyMatched = false;
            for (GLError error : window) {
                for (int i = 0; i < trigger.size(); i++) {
                    if (!attributed[i] && trigger.get(i).equals(error)) {
                        attributed[i] = true;
                        anyMatched = true;
                    }
                }
            }
            if (anyMatched) {
                isolatedNames.add(command.name());
            } else {
                probeReports.add(new UniformReplayReport(key, toEvidence(window, false)));
            }
        }

        // The triggering report: one evidence row per original entry, in drain order.
        reports.add(new UniformReplayReport(key, toEvidence(trigger, attributed)));

        if (!deliver(reports) || !deliver(probeReports)) {
            return new BarrierParticipantResult.Degraded(deliveryFailedDiagnostic,
                    "uniform replay evidence delivery");
        }

        if (isolatedNames.isEmpty()) {
            cache.incrementRecurrence();
            warn("phase6.builtin.upload.unattributable",
                    "non-empty drain with clean replay in " + degradationScope);
            return new BarrierParticipantResult.Degraded("phase6.builtin.upload.unattributable",
                    degradationScope);
        }

        for (String name : isolatedNames) {
            cache.disable(name);
            warn("phase6.builtin.upload.disabled." + name,
                    "upload isolated by replay; disabled for generation+program+name");
        }
        cache.resetRecurrence();
        return new BarrierParticipantResult.Continue();
    }

    private void uploadOne(com.schmaloogium.engine.gl.UniformService uniforms,
            UploadCommand command) {
        UniformLocation location = command.location();
        UniformValue value = command.value();
        switch (command.kind()) {
            case INT -> uniforms.upload(location, ((UniformValue.I) value).x());
            case FLOAT -> uniforms.upload(location, ((UniformValue.F) value).x());
            case INT2 -> {
                UniformValue.I2 v = (UniformValue.I2) value;
                uniforms.upload(location, v.x(), v.y());
            }
            case INT4 -> {
                UniformValue.I4 v = (UniformValue.I4) value;
                uniforms.upload(location, v.x(), v.y(), v.z(), v.w());
            }
            case FLOAT2 -> {
                UniformValue.F2 v = (UniformValue.F2) value;
                uniforms.upload(location, v.x(), v.y());
            }
            case FLOAT3 -> {
                UniformValue.F3 v = (UniformValue.F3) value;
                uniforms.upload(location, v.x(), v.y(), v.z());
            }
            case FLOAT4 -> {
                UniformValue.F4 v = (UniformValue.F4) value;
                uniforms.upload(location, v.x(), v.y(), v.z(), v.w());
            }
            case MAT4 -> uniforms.uploadMatrix4(location,
                    ((UniformValue.M4) value).columnMajorCopy(), false);
        }
    }

    private static void commit(ProgramCache cache, List<UploadCommand> batch) {
        for (UploadCommand command : batch) {
            cache.noteUploaded(command.name(), command.value());
        }
    }

    private static List<ReplayAwareGLError> toEvidence(List<GLError> errors,
            boolean[] attributed) {
        List<ReplayAwareGLError> evidence = new ArrayList<>(errors.size());
        for (int i = 0; i < errors.size(); i++) {
            evidence.add(new ReplayAwareGLError(errors.get(i), attributed[i]));
        }
        return evidence;
    }

    private static List<ReplayAwareGLError> toEvidence(List<GLError> errors, boolean attributed) {
        List<ReplayAwareGLError> evidence = new ArrayList<>(errors.size());
        for (GLError error : errors) {
            evidence.add(new ReplayAwareGLError(error, attributed));
        }
        return evidence;
    }

    /** Delivers reports through the sink; latches permanently on the first throw. */
    private boolean deliver(List<UniformReplayReport> reports) {
        for (UniformReplayReport report : reports) {
            try {
                replayErrors.accept(report);
            } catch (RuntimeException e) {
                deliveryFailedDiagnostic = "phase6.replay.delivery.failed";
                undeliveredReports.add(report);
                warn(deliveryFailedDiagnostic,
                        "replay sink threw; runtime latched until disposal: " + e);
                return false;
            }
        }
        return true;
    }

    /** Immediate signal upload path (§4.12): only while an activity token is current. */
    private void immediateUpload(String... names) {
        if (deliveryFailedDiagnostic != null) {
            throw new IllegalStateException(deliveryFailedDiagnostic);
        }
        ActivePair pair = activePair;
        if (pair == null || !pair.token().isCurrent()) {
            return; // carried to the next activation
        }
        List<UploadCommand> batch = new ArrayList<>(names.length);
        for (String name : names) {
            UniformLocation location = pair.cache().peekLocation(name);
            if (location == null || location.isAbsent() || pair.cache().isDisabled(name)) {
                continue;
            }
            UniformValue value = cells.get(name);
            if (value == null || pair.cache().lastUploadedEquals(name, value)) {
                continue;
            }
            batch.add(new UploadCommand(name, kindOf(value), value, location));
        }
        if (!batch.isEmpty()) {
            runBatch(pair.key(), pair.cache(), batch, "signal uniforms for this activation");
        }
    }

    private static UploadKind kindOf(UniformValue value) {
        if (value instanceof UniformValue.I) {
            return UploadKind.INT;
        }
        if (value instanceof UniformValue.F) {
            return UploadKind.FLOAT;
        }
        if (value instanceof UniformValue.I2) {
            return UploadKind.INT2;
        }
        if (value instanceof UniformValue.I4) {
            return UploadKind.INT4;
        }
        if (value instanceof UniformValue.F2) {
            return UploadKind.FLOAT2;
        }
        if (value instanceof UniformValue.F3) {
            return UploadKind.FLOAT3;
        }
        if (value instanceof UniformValue.F4) {
            return UploadKind.FLOAT4;
        }
        if (value instanceof UniformValue.M4) {
            return UploadKind.MAT4;
        }
        throw new IllegalStateException("unknown value variant");
    }

    // ===================================================================
    // UniformCore (participant-facing facade)
    // ===================================================================

    @Override
    public UniformConfiguration configuration() {
        return configuration;
    }

    @Override
    public BarrierParticipantResult retiredResult(String scope) {
        return new BarrierParticipantResult.Degraded("phase6.runtime.retired", scope);
    }

    @Override
    public ProgramCache cacheFor(ProgramUniformCacheKey key, ProgramUniformLayout layout) {
        if (layout.declarations().containsKey("centerDepthSmooth")) {
            centerDepthDeclaredSeen = true;
        }
        return caches.computeIfAbsent(key, k -> new ProgramCache(k, layout));
    }

    @Override
    public Optional<ActivePair> activePair() {
        return Optional.ofNullable(activePair);
    }

    @Override
    public UniformValue cellValue(String name) {
        return cells.get(name);
    }

    @Override
    public boolean matricesCurrentForFrame() {
        return matricesCurrentForFrame;
    }

    @Override
    public void warnOnce(String diagnosticKey, String detail) {
        if (reportedKeys.add(diagnosticKey)) {
            diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN,
                    UserChannel.LOG_ONLY, diagnosticKey, List.of(), detail,
                    LogChannels.UNIFORMS));
        }
    }

    @Override
    public void warn(String diagnosticKey, String detail) {
        diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.LOG_ONLY,
                diagnosticKey, List.of(), detail, LogChannels.UNIFORMS));
    }

    @Override
    public CustomUniformBridge customBridge() {
        return bridge != null ? bridge : NO_CUSTOMS;
    }

    @Override
    public BuiltInExpressionView expressionView() {
        return expressionView;
    }

    /** The publisher's binding-scope hook: installations register the active pair for
     *  the immediate-event path. Called by Phase 4 glue; tests may call it directly. */
    public void noteActivation(ProgramUniformCacheKey key, ProgramCache cache,
            BoundProgramUniformAccess uniforms) {
        requireRenderThread();
        this.activePair = new ActivePair(key, cache, uniforms.activityToken());
    }

    // ===================================================================
    // Participants (§4.10: sampler -> built-in -> custom)
    // ===================================================================

    private static UploadKind uploadKindOf(BuiltInUniformType type) {
        return switch (type) {
            case INT -> UploadKind.INT;
            case FLOAT -> UploadKind.FLOAT;
            case IVEC2 -> UploadKind.INT2;
            case VEC3 -> UploadKind.FLOAT3;
            case VEC4 -> UploadKind.FLOAT4;
            case IVEC4 -> UploadKind.INT4;
            case MAT4 -> UploadKind.MAT4;
        };
    }

    /** First position: fixed sampler integer re-point (§4.9). */
    private final class SamplerParticipant implements ProgramBindingParticipant {
        @Override
        public BarrierParticipantResult afterBind(ResolvedProgramDescriptor binding,
                BarrierContext context, BoundProgramUniformAccess uniforms) {
            if (isRetired()) {
                return retiredResult("fixed sampler repoint for this activation");
            }
            callbackDepth++;
            try {
                ProgramUniformCacheKey key = uniforms.cacheKey();
                ProgramCache cache = cacheFor(key, binding.uniformLayout());
                noteActivation(key, cache, uniforms);
                SamplerPlanKey planKey = new SamplerPlanKey(context.stage(), context.band(),
                        binding.samplerLayout().fingerprint(),
                        binding.samplerLayout().policyFingerprint());
                SamplerPlan plan = cache.samplerPlan(planKey);
                if (plan == null) {
                    plan = buildSamplerPlan(binding.samplerLayout(), context, cache, uniforms);
                    cache.noteSamplerPlan(planKey, plan);
                }
                if (!plan.isValid()) {
                    return new BarrierParticipantResult.Degraded(plan.invalidDiagnostic(),
                            "fixed sampler repoint for this activation");
                }
                List<UploadCommand> batch = new ArrayList<>(plan.rows().size());
                for (SamplerPlan.Row row : plan.rows()) {
                    UniformValue unit = new UniformValue.I(row.unit());
                    if (cache.lastUploadedEquals(row.name(), unit)) {
                        continue; // the effective program already points at this unit
                    }
                    batch.add(new UploadCommand(row.name(), UploadKind.INT, unit,
                            row.location()));
                }
                if (batch.isEmpty()) {
                    return new BarrierParticipantResult.Continue();
                }
                return runBatch(key, cache, batch, "fixed sampler repoint for this activation");
            } finally {
                callbackDepth--;
            }
        }
    }

    private SamplerPlan buildSamplerPlan(ProgramSamplerLayout layout, BarrierContext context,
            ProgramCache cache, BoundProgramUniformAccess uniforms) {
        FixedSamplerPlanResult result = samplerResolver.resolve(layout, context.stage(),
                context.band());
        if (result instanceof FixedSamplerPlanResult.Invalid invalid) {
            String diagnostic = samplerDiagnostic(invalid.reason());
            warnOnce(diagnostic, "fixed sampler resolution failed for this layout");
            return SamplerPlan.invalid(diagnostic);
        }
        FixedSamplerPlanResult.Ready ready = (FixedSamplerPlanResult.Ready) result;
        LinkedHashMap<Integer, SamplerPlan.Row> rows = new LinkedHashMap<>();
        for (ResolvedSamplerBinding binding : ready.bindings()) {
            UniformLocation location = cache.location(binding.exactName(), uniforms::locate);
            if (location.isAbsent()) {
                continue; // optimized out: cached absent, never re-queried
            }
            rows.putIfAbsent(binding.exactName().hashCode(),
                    new SamplerPlan.Row(binding.exactName(), location, binding.unit()));
        }
        // Conflict: the same location receiving different units is impossible for
        // distinct names sharing a location in one program.
        Map<Integer, String> locationUnits = new HashMap<>();
        for (SamplerPlan.Row row : rows.values()) {
            int identity = System.identityHashCode(row.location());
            String previous = locationUnits.putIfAbsent(identity,
                    row.name() + "#" + row.unit());
            if (previous != null && !previous.endsWith("#" + row.unit())) {
                return SamplerPlan.invalid("phase6.sampler.location.conflict");
            }
        }
        List<SamplerPlan.Row> ordered = new ArrayList<>(rows.values());
        ordered.sort((left, right) -> {
            int byUnit = Integer.compare(left.unit(), right.unit());
            return byUnit != 0 ? byUnit : left.name().compareTo(right.name());
        });
        return SamplerPlan.valid(ordered);
    }

    private static String samplerDiagnostic(SamplerLayoutValidation reason) {
        if (reason instanceof SamplerLayoutValidation.ConflictingTypes) {
            return "phase6.sampler.layout.conflict";
        }
        return "phase6.sampler.layout.unsupported";
    }

    /** Second position: built-in refresh; plan built in catalog (name) order (§4.9). */
    private final class BuiltInParticipant implements ProgramBindingParticipant {
        @Override
        public BarrierParticipantResult afterBind(ResolvedProgramDescriptor binding,
                BarrierContext context, BoundProgramUniformAccess uniforms) {
            if (isRetired()) {
                return retiredResult("built-in uniforms for this activation");
            }
            callbackDepth++;
            try {
                ProgramUniformLayout layout = binding.uniformLayout();
                ProgramUniformCacheKey key = uniforms.cacheKey();
                ProgramCache cache = cacheFor(key, layout);
                List<UploadCommand> batch = new ArrayList<>();
                for (Map.Entry<String, ProgramUniformDeclaration> entry
                        : layout.declarations().entrySet()) {
                    String name = entry.getKey();
                    Optional<BuiltInUniform> row = BuiltInUniformCatalog.byName(name);
                    if (row.isEmpty()) {
                        continue; // custom-uniform territory
                    }
                    appendBuiltInCommand(row.get(), cache, uniforms, batch);
                }
                if (batch.isEmpty()) {
                    return new BarrierParticipantResult.Continue();
                }
                return runBatch(key, cache, batch, "built-in uniforms for this activation");
            } finally {
                callbackDepth--;
            }
        }

        private void appendBuiltInCommand(BuiltInUniform row, ProgramCache cache,
                BoundProgramUniformAccess uniforms, List<UploadCommand> batch) {
            String name = row.name();
            UniformValue value = cellValue(name);
            if (value == null) {
                if (row.milestone() != Milestone.V0_1) {
                    warnOnce("phase6.producer.pending." + name,
                            "declared built-in " + name + " awaits its " + row.milestone()
                                    + " producer; neutral withheld");
                }
                return;
            }
            if (isCurrentMatrixName(name) && !matricesCurrentForFrame) {
                return; // no capture for the latest frame: only those uniforms disabled
            }
            UniformLocation location = cache.location(name, uniforms::locate);
            if (location.isAbsent() || cache.isDisabled(name)) {
                return;
            }
            if (cache.lastUploadedEquals(name, value)) {
                return;
            }
            batch.add(new UploadCommand(name, uploadKindOf(row.type()), value, location));
        }
    }

    private static boolean isCurrentMatrixName(String name) {
        return name.equals("gbufferModelView") || name.equals("gbufferProjection")
                || name.equals("gbufferModelViewInverse")
                || name.equals("gbufferProjectionInverse");
    }

    /** Third position: custom-expression refresh (§4.8/§4.13). */
    private final class CustomParticipant implements ProgramBindingParticipant {
        @Override
        public BarrierParticipantResult afterBind(ResolvedProgramDescriptor binding,
                BarrierContext context, BoundProgramUniformAccess uniforms) {
            if (isRetired()) {
                return retiredResult("custom uniforms for this activation");
            }
            callbackDepth++;
            try {
                ProgramUniformLayout layout = binding.uniformLayout();
                ProgramUniformCacheKey key = uniforms.cacheKey();
                ProgramCache cache = cacheFor(key, layout);
                CustomUniformBridge activeBridge = customBridge();
                if (activeBridge == NO_CUSTOMS) {
                    return new BarrierParticipantResult.Continue();
                }
                CustomLedger ledger = new CustomLedger(layout, cache, uniforms);
                CustomRefreshResult result;
                try {
                    result = activeBridge.refresh(expressionView(), ledger);
                } catch (RuntimeException e) {
                    warn("phase6.custom.bridge.exception",
                            "custom bridge threw through refresh: " + e);
                    return new BarrierParticipantResult.Degraded(
                            "phase6.custom.bridge.exception",
                            "custom uniforms for this activation");
                }
                long accepted;
                long skipped;
                long rejected;
                if (result instanceof CustomRefreshResult.Completed completed) {
                    accepted = completed.accepted();
                    skipped = completed.skippedAbsent();
                    rejected = completed.rejected();
                } else if (result instanceof CustomRefreshResult.Aborted aborted) {
                    accepted = aborted.accepted();
                    skipped = aborted.skippedAbsent();
                    rejected = aborted.rejected();
                } else {
                    accepted = 0;
                    skipped = 0;
                    rejected = 0;
                }
                if (accepted < 0 || skipped < 0 || rejected < 0
                        || accepted != ledger.accepted || skipped != ledger.skipped
                        || rejected != ledger.rejected) {
                    return new BarrierParticipantResult.Degraded(
                            "phase6.custom.counter.contract",
                            "custom uniforms for this activation");
                }
                if (ledger.acceptedCommands.isEmpty()) {
                    return new BarrierParticipantResult.Continue();
                }
                return runBatch(key, cache, ledger.acceptedCommands,
                        "custom uniforms for this activation");
            } finally {
                callbackDepth--;
            }
        }
    }

    /** Count/location ledger enforced between the bridge and its refresh result. */
    private final class CustomLedger implements CustomUniformUploadSink {
        private final ProgramUniformLayout layout;
        private final ProgramCache cache;
        private final BoundProgramUniformAccess uniforms;
        private final Set<String> seen = new LinkedHashSet<>();
        private final List<UploadCommand> acceptedCommands = new ArrayList<>();
        private long accepted;
        private long skipped;
        private long rejected;

        CustomLedger(ProgramUniformLayout layout, ProgramCache cache,
                BoundProgramUniformAccess uniforms) {
            this.layout = layout;
            this.cache = cache;
            this.uniforms = uniforms;
        }

        @Override
        public CustomSubmitResult submit(CustomUploadCommand command) {
            String name = command.name();
            if (name == null || name.isEmpty() || !name.matches("[A-Za-z_][A-Za-z0-9_]*")) {
                rejected++;
                return new CustomSubmitResult.Rejected("phase6.custom.invalid.name");
            }
            if (!seen.add(name)) {
                rejected++;
                return new CustomSubmitResult.Rejected("phase6.custom.duplicate.name");
            }
            ProgramUniformDeclaration declaration = layout.declarations().get(name);
            if (declaration == null) {
                skipped++;
                return new CustomSubmitResult.SkippedAbsent();
            }
            UploadKind kind = customKind(declaration.type());
            if (kind == null) {
                rejected++;
                return new CustomSubmitResult.Rejected("phase6.custom.type.mismatch");
            }
            UniformLocation location = cache.location(name, uniforms::locate);
            if (location.isAbsent()) {
                skipped++;
                return new CustomSubmitResult.SkippedAbsent();
            }
            accepted++;
            acceptedCommands.add(toCommand(command, kind, location));
            return new CustomSubmitResult.Accepted();
        }

        private UploadCommand toCommand(CustomUploadCommand command, UploadKind kind,
                UniformLocation location) {
            if (command instanceof CustomUploadCommand.Float1 f) {
                return new UploadCommand(command.name(), UploadKind.FLOAT,
                        new UniformValue.F(f.value()), location);
            }
            if (command instanceof CustomUploadCommand.Int1 i) {
                return new UploadCommand(command.name(), UploadKind.INT,
                        new UniformValue.I(i.value()), location);
            }
            if (command instanceof CustomUploadCommand.Bool1 b) {
                return new UploadCommand(command.name(), UploadKind.INT,
                        new UniformValue.I(b.value() ? 1 : 0), location);
            }
            if (command instanceof CustomUploadCommand.Float2 v) {
                return new UploadCommand(command.name(), UploadKind.FLOAT2,
                        new UniformValue.F2(v.x(), v.y()), location);
            }
            if (command instanceof CustomUploadCommand.Float3 v) {
                return new UploadCommand(command.name(), UploadKind.FLOAT3,
                        new UniformValue.F3(v.x(), v.y(), v.z()), location);
            }
            if (command instanceof CustomUploadCommand.Float4 v) {
                return new UploadCommand(command.name(), UploadKind.FLOAT4,
                        new UniformValue.F4(v.x(), v.y(), v.z(), v.w()), location);
            }
            throw new IllegalStateException("unknown custom command variant");
        }
    }

    private static UploadKind customKind(DeclaredGlslType type) {
        if (type instanceof DeclaredGlslType.Scalar scalar) {
            return switch (scalar.kind()) {
                case FLOAT -> UploadKind.FLOAT;
                case SIGNED_INT, UNSIGNED_INT -> UploadKind.INT;
                case BOOL -> UploadKind.INT; // encoded by Phase 6 (D-P6-17)
                case DOUBLE -> null; // not a Phase 11 submission variant
            };
        }
        if (type instanceof DeclaredGlslType.Vector vector
                && vector.component() == com.schmaloogium.engine.preprocess.ScalarKind.FLOAT) {
            return switch (vector.width()) {
                case 2 -> UploadKind.FLOAT2;
                case 3 -> UploadKind.FLOAT3;
                case 4 -> UploadKind.FLOAT4;
                default -> null;
            };
        }
        return null;
    }

    // ===================================================================
    // Event sink view
    // ===================================================================

    /** The runtime's single guarded sink instance (§2.2). */
    private final class RuntimeEventSink implements UniformEventSink {
        @Override
        public void captureGbufferMatrices(long frameId, Matrix4Value modelView,
                Matrix4Value projection) {
            executeCaptureGbufferMatrices(frameId, modelView, projection);
        }

        @Override
        public void updateCelestial(CelestialSample sample) {
            executeUpdateCelestial(sample);
        }

        @Override
        public void updateShadowMatrices(ShadowMatrixSample sample) {
            executeUpdateShadowMatrices(sample);
        }

        @Override
        public void updateFog(FogSample sample) {
            executeUpdateFog(sample);
        }

        @Override
        public void updateBlend(BlendSample sample) {
            executeUpdateBlend(sample);
        }

        @Override
        public void updateEntityColor(Float4 value) {
            executeUpdateEntityColor(value);
        }

        @Override
        public void updateEntityId(int value) {
            executeUpdateEntityId(value);
        }

        @Override
        public void updateBlockEntityId(int value) {
            executeUpdateBlockEntityId(value);
        }

        @Override
        public void updateInstanceId(int value) {
            executeUpdateInstanceId(value);
        }

        @Override
        public void updateAtlasSize(Int2 value) {
            executeUpdateAtlasSize(value);
        }

        @Override
        public void updateHeldItems(HeldItemSample value) {
            executeUpdateHeldItems(value);
        }
    }

    // ===================================================================
    // Built-in expression view (§5.3)
    // ===================================================================

    private final class BuiltInView implements BuiltInExpressionView {
        @Override
        public BuiltInLookup lookup(String name) {
            if (!BuiltInUniformCatalog.expressionPermittedNames().contains(name)) {
                return new BuiltInLookup.Absent();
            }
            UniformValue value = cells.get(name);
            if (value == null) {
                return new BuiltInLookup.Absent(); // transient unavailability
            }
            return new BuiltInLookup.Present(toBuiltInValue(value));
        }

        private BuiltInValue toBuiltInValue(UniformValue value) {
            if (value instanceof UniformValue.I i) {
                return new BuiltInValue.Int1(i.x());
            }
            if (value instanceof UniformValue.F f) {
                return new BuiltInValue.Float1(f.x());
            }
            if (value instanceof UniformValue.I2 v) {
                return new BuiltInValue.Int2(v.x(), v.y());
            }
            if (value instanceof UniformValue.I4 v) {
                return new BuiltInValue.Int4(v.x(), v.y(), v.z(), v.w());
            }
            if (value instanceof UniformValue.F2 v) {
                return new BuiltInValue.Float2(v.x(), v.y());
            }
            if (value instanceof UniformValue.F3 v) {
                return new BuiltInValue.Float3(v.x(), v.y(), v.z());
            }
            if (value instanceof UniformValue.F4 v) {
                return new BuiltInValue.Float4(v.x(), v.y(), v.z(), v.w());
            }
            if (value instanceof UniformValue.M4 m) {
                return new BuiltInValue.Mat4(toRowMajor(m.columnMajor()));
            }
            throw new IllegalStateException("unknown value variant");
        }

        private float[] toRowMajor(float[] columnMajor) {
            float[] rowMajor = new float[16];
            for (int col = 0; col < 4; col++) {
                for (int row = 0; row < 4; row++) {
                    rowMajor[row * 4 + col] = columnMajor[col * 4 + row];
                }
            }
            return rowMajor;
        }
    }

    private FixedExpressionInputSchema buildSchema() {
        Map<String, FixedInputKind> inputs = new LinkedHashMap<>();
        for (String name : BuiltInUniformCatalog.expressionPermittedNames()) {
            BuiltInUniform row = BuiltInUniformCatalog.byName(name).orElseThrow();
            inputs.put(name, schemaKind(row.type()));
        }
        return new FixedExpressionInputSchema(String.valueOf(configuration.catalogVersion()),
                java.util.Collections.unmodifiableMap(inputs));
    }

    private static FixedInputKind schemaKind(BuiltInUniformType type) {
        return switch (type) {
            case INT -> FixedInputKind.INT;
            case FLOAT -> FixedInputKind.FLOAT;
            case IVEC2 -> FixedInputKind.IVEC2;
            case VEC3 -> FixedInputKind.VEC3;
            case VEC4 -> FixedInputKind.VEC4;
            case IVEC4 -> FixedInputKind.IVEC4;
            case MAT4 -> FixedInputKind.MAT4;
        };
    }

    // ===================================================================
    // Helpers
    // ===================================================================

    private static UniformValue.M4 m4(Matrix4Value matrix) {
        return new UniformValue.M4(matrix.toColumnMajorArray());
    }

    private static UniformValue.F3 f3(Float3 vector) {
        return new UniformValue.F3(vector.x(), vector.y(), vector.z());
    }

    private static float normalizeFloat(float value) {
        return value == 0.0f ? 0.0f : value;
    }

    private void requireRenderThread() {
        if (Thread.currentThread() != renderThread) {
            throw new IllegalStateException(
                    "uniform runtime operations are render-thread confined");
        }
    }

    private void ensureNotRetired() {
        if (retired) {
            throw new IllegalStateException("uniform runtime is retired");
        }
    }
}
