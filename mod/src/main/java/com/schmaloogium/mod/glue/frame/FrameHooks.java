// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.frame.AnaglyphEye;
import com.schmaloogium.engine.frame.CameraSnapshot;
import com.schmaloogium.engine.frame.FrameBeginSignal;
import com.schmaloogium.engine.frame.lifecycle.FrameDriver;
import com.schmaloogium.engine.frame.FrameExitKind;
import com.schmaloogium.engine.frame.FrameOpenResult;
import com.schmaloogium.engine.frame.FrameToken;
import com.schmaloogium.engine.frame.ScopeOpenResult;
import com.schmaloogium.engine.frame.ScopeToken;
import com.schmaloogium.engine.frame.dispatch.RenderSection;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.uniforms.Matrix4Value;

import java.nio.FloatBuffer;

/**
 * The dumb vanilla-side bridge (PHASE_7_DOC §4.10.2): the mixin catalog calls exactly
 * these static methods; every policy decision lives in the engine {@link FrameDriver}.
 * The bridge translates Minecraft primitives (frame counter, terrain token, GL matrices,
 * window extents) into engine-owned values and retains the per-frame token in a
 * render-thread call-local slot — no mixin instance field ever owns state.
 *
 * <p>This class references Minecraft/LWJGL only through the small seam methods at the
 * bottom ({@link McFrameState}), which keeps the call bodies reviewable against the
 * catalog. All methods are render-thread confined by the driver's own algebra.
 */
public final class FrameHooks {

    private static volatile FrameToken currentFrame;
    private static volatile boolean firstFrameLogged;
    private static volatile int reservedTerrainToken;
    private static volatile long installEpoch;
    private static volatile boolean firstOpenAfterInstallLogged = true;
    private static volatile String lastOpenVerdict = "";
    private static volatile String lastClearVerdict = "";
    private static volatile boolean containmentLogged;
    private static volatile String lastFinishVerdict = "";
    private static final java.util.Set<String> scopeVerdictsLogged =
            java.util.concurrent.ConcurrentHashMap.newKeySet();

    private FrameHooks() {
    }

    /**
     * H-FRAME-01: HEAD of {@code EntityRenderer.func_175068_a(IFJ)V}. Vanilla numbers the
     * mono world pass 2 and the two anaglyph eyes 0/1; the engine's {@code vanillaPass} is
     * zero for the one shader-bearing world pass (PHASE_7_DOC §5.1: the non-anaglyph pass
     * carries LEFT), so the bridge normalizes 2 → 0 and leaves anaglyph passes vanilla-only.
     */
    public static void open(int pass, float partialTicks, long finishTimeNano, int frameCounter) {
        int enginePass = pass == 2 ? 0 : 1;
        // Under the controlled capture clock the elapsed input is the plan's fixed seconds
        // (PHASE_2_DOC §5.1.1); otherwise vanilla's finish time flows through unchanged.
        float frameTimeSeconds = com.schmaloogium.mod.conformance.ControlledClock.isArmed()
                ? com.schmaloogium.mod.conformance.ControlledClock.frameTimeSeconds()
                : (float) finishTimeNano;
        FrameBeginSignal signal = new FrameBeginSignal(
                McFrameState.worldEpoch(),
                McFrameState.logicalTick(),
                McFrameState.smoothingTimeTicks(),
                frameTimeSeconds,
                McFrameState.dimension(),
                enginePass,
                frameCounter,
                partialTicks,
                McFrameState.targetView(),
                McFrameState.priorCompletedFramebuffer(),
                McFrameState.anaglyphEye());
        FrameOpenResult result;
        try {
            result = driver().open(signal);
        } catch (RuntimeException e) {
            contain("open", null, e);
            currentFrame = null;
            return;
        }
        if (!firstFrameLogged) {
            firstFrameLogged = true;
            com.schmaloogium.engine.log.Logs.channel(
                    com.schmaloogium.engine.log.LogChannels.FRAME).info(
                    "H-FRAME-01 first frame-begin hook observed: pass {}, counter {}, result {}",
                    pass, frameCounter, result.getClass().getSimpleName());
        }
        String verdict = result.getClass().getSimpleName()
                + (result instanceof FrameOpenResult.VanillaOnly vanilla
                        ? " (" + vanilla.reason() + ")" : "");
        if (!firstOpenAfterInstallLogged) {
            firstOpenAfterInstallLogged = true;
            lastOpenVerdict = verdict;
            com.schmaloogium.engine.log.Logs.channel(
                    com.schmaloogium.engine.log.LogChannels.FRAME).info(
                    "H-FRAME-01 first frame-begin after composition install #{}: result {}",
                    installEpoch, verdict);
        } else if (!verdict.equals(lastOpenVerdict)) {
            // Steady-state evidence: every change of the open verdict, never per frame.
            lastOpenVerdict = verdict;
            com.schmaloogium.engine.log.Logs.channel(
                    com.schmaloogium.engine.log.LogChannels.FRAME).info(
                    "H-FRAME-01 open verdict changed (install #{}, counter {}): now {}",
                    installEpoch, frameCounter, verdict);
        }
        if (result instanceof FrameOpenResult.Opened opened) {
            currentFrame = opened.token();
            reservedTerrainToken = frameCounter;
            acceptedFrames++;
        } else {
            currentFrame = null;
        }
    }

    /** H-FRAME-02: BEFORE the ordinal-0 {@code GlStateManager.clear(I)} in the pass. */
    public static void normalizeVanillaState() {
        FrameToken token = currentFrame;
        if (token != null) {
            try {
                driver().beforeFirstClear(token);
            } catch (RuntimeException e) {
                contain("beforeFirstClear", token, e);
            }
        }
    }

    /** H-FRAME-03: AFTER the ordinal-0 clear. */
    public static void afterFirstClear() {
        FrameToken token = currentFrame;
        if (token != null) {
            com.schmaloogium.engine.frame.FrameStepResult result;
            try {
                result = driver().afterFirstClear(token, McFrameState.mainDepthPreparation());
            } catch (RuntimeException e) {
                contain("afterFirstClear", token, e);
                return;
            }
            String verdict = result.getClass().getSimpleName()
                    + (result instanceof com.schmaloogium.engine.frame.FrameStepResult.Aborted aborted
                            ? " (" + aborted.reason() + ")" : "");
            if (!verdict.equals(lastClearVerdict)) {
                lastClearVerdict = verdict;
                com.schmaloogium.engine.log.Logs.channel(
                        com.schmaloogium.engine.log.LogChannels.FRAME).info(
                        "H-FRAME-03 after-first-clear verdict changed (install #{}): now {}",
                        installEpoch, verdict);
            }
            if (result instanceof com.schmaloogium.engine.frame.FrameStepResult.Aborted) {
                currentFrame = null;
                restoreVanilla();
            }
        }
    }

    /** H-FRAME-04: AFTER INVOKE {@code setupCameraTransform}. */
    public static void captureMainCamera() {
        FrameToken token = currentFrame;
        if (token == null) {
            return;
        }
        // glGetFloatv takes the *_MATRIX query enums (0x0BA6/0x0BA7), not the matrix-mode
        // enums GL_MODELVIEW/GL_PROJECTION (0x1700/0x1701): the latter raise INVALID_ENUM,
        // leave the scratch zeroed (singular matrices) and poison the next error drain.
        Matrix4Value modelView = readMatrix(0x0BA6); // GL_MODELVIEW_MATRIX
        Matrix4Value projection = readMatrix(0x0BA7); // GL_PROJECTION_MATRIX
        try {
            CameraSnapshot camera = new CameraSnapshot(modelView, projection);
            var result = driver().captureMainCamera(token, camera);
            noteScopeVerdict("captureMainCamera",
                    result instanceof com.schmaloogium.engine.frame.FrameStepResult.Aborted
                            ? result.toString() : null);
            if (result instanceof com.schmaloogium.engine.frame.FrameStepResult.Aborted) {
                currentFrame = null;
                restoreVanilla();
                return;
            }
            emitCelestial(token, camera);
        } catch (RuntimeException e) {
            contain("captureMainCamera", token, e);
        }
    }

    private static volatile int lastFogMode = 9729; // GL_LINEAR until vanilla says otherwise
    private static volatile com.schmaloogium.engine.uniforms.Float3 lastFogColor;
    private static volatile boolean fogLogged = true;

    /**
     * H-FOG-02 (PHASE_7_DOC §4.10 hook catalog, Forge event observer): vanilla's fog colour
     * for this frame, published as the P6 fog signal. Vanilla computes it in
     * {@code updateFogColor} before the ordinal-0 clear, so the estate's fog-RGB clear and
     * the {@code fogColor} uniform see this frame's value; the mode arrives with
     * {@link #onFogMode} at {@code setupFog} and re-publishes.
     */
    public static void onFogColors(float red, float green, float blue) {
        lastFogColor = new com.schmaloogium.engine.uniforms.Float3(
                clamp01(red), clamp01(green), clamp01(blue));
        publishFog();
    }

    public static void onFogMode(int fogMode) {
        lastFogMode = fogMode;
        publishFog();
    }

    private static void publishFog() {
        FrameToken token = currentFrame;
        com.schmaloogium.engine.uniforms.Float3 color = lastFogColor;
        if (token == null || color == null) {
            return;
        }
        try {
            var accepted = driver().uniformSignals().event(token,
                    new com.schmaloogium.engine.frame.spi.UniformSignal.Fog(lastFogMode, 0f, color));
            if (!fogLogged) {
                fogLogged = true;
                com.schmaloogium.engine.log.Logs.channel(
                        com.schmaloogium.engine.log.LogChannels.FRAME).info(
                        "H-FOG-02 first fog signal (install #{}): {} mode {} color {}",
                        installEpoch, accepted.getClass().getSimpleName(), lastFogMode, color);
            }
        } catch (RuntimeException e) {
            contain("fog", token, e);
        }
    }

    private static float clamp01(float value) {
        return !Float.isFinite(value) || value < 0f ? 0f : value > 1f ? 1f : value;
    }

    private static volatile java.util.function.DoubleSupplier sunPathRotationDegrees = () -> 0d;
    private static volatile boolean celestialLogged = true;

    /** Composition root: the active pack's {@code sunPathRotation} (degrees), 0 when none. */
    public static void installSunPathRotation(java.util.function.DoubleSupplier degrees) {
        sunPathRotationDegrees = java.util.Objects.requireNonNull(degrees, "degrees");
    }

    /**
     * H-SKY-02: the same-frame post-camera celestial event (PHASE_7_DOC §4.3, PHASE_8_DOC
     * §4.5.4). The four w=0 eye vectors come from Phase 8's pure {@code CelestialMath} over
     * the captured main model-view and vanilla's celestial angle; the driver forwards them
     * to Phase 6 as {@code sunPosition}/{@code moonPosition}/{@code shadowLightPosition}/
     * {@code upPosition}. Before this the cells held their zero neutrals and every classic
     * pack's lighting collapsed to black.
     */
    private static void emitCelestial(FrameToken token, CameraSnapshot camera) {
        McFrameState.CelestialInputs inputs = McFrameState.celestialInputs();
        if (inputs == null) {
            return;
        }
        float sunPath = (float) sunPathRotationDegrees.getAsDouble();
        com.schmaloogium.engine.frame.ShadowFrameView view = new com.schmaloogium.engine.frame.ShadowFrameView(
                McFrameState.worldEpoch(), 0L, inputs.partialTicks(), reservedTerrainToken,
                inputs.cameraPosition(),
                com.schmaloogium.mod.glue.uniforms.CelestialAngles.skyAngle(inputs.celestialAngle()),
                com.schmaloogium.mod.glue.uniforms.CelestialAngles.sunAngle(inputs.celestialAngle()));
        com.schmaloogium.engine.uniforms.CelestialSample sample =
                com.schmaloogium.engine.shadow.CelestialMath.sample(view, camera,
                        Float.isFinite(sunPath) ? sunPath : 0f);
        var accepted = driver().uniformSignals().event(token,
                new com.schmaloogium.engine.frame.spi.UniformSignal.Celestial(
                        sample.sunPosition(), sample.moonPosition(),
                        sample.shadowLightPosition(), sample.upPosition()));
        if (!celestialLogged) {
            celestialLogged = true;
            com.schmaloogium.engine.log.Logs.channel(
                    com.schmaloogium.engine.log.LogChannels.FRAME).info(
                    "H-SKY-02 first celestial signal (install #{}): {} sky {} sun {} sunPathRotation {} sunPosition {} upPosition {}",
                    installEpoch, accepted.getClass().getSimpleName(), view.skyAngle(),
                    view.sunAngle(), sunPath, sample.sunPosition(), sample.upPosition());
        }
    }

    /**
     * H-FRAME-05: AROUND the exact {@code RenderGlobal.func_174970_a} invocation. The
     * caller verifies the terrain token; a wrong token contains (no engine call, original
     * still runs exactly once) and the main-estate rebind is issued inside the scope.
     */
    public static void invokeShadowSlotThenRestoreMain(int terrainToken, Runnable original) {
        FrameToken token = currentFrame;
        if (token == null || terrainToken != reservedTerrainToken) {
            // Wrong token contains: never reconstruct; the original still runs exactly once.
            original.run();
            return;
        }
        original.run();
        try {
            driver().afterTerrainSetup(token);
        } catch (RuntimeException e) {
            contain("afterTerrainSetup", token, e);
        }
    }

    /** H-FRAME-06: TAIL of {@code func_175068_a} on normal return. */
    public static void finishNormal() {
        finish(FrameExitKind.NORMAL);
    }

    /** H-FRAME-07: the wrapper's outer {@code finally}, strictly after H-FRAME-06. */
    public static void finishGuaranteed(Throwable thrown) {
        finish(thrown == null ? FrameExitKind.EARLY_RETURN : FrameExitKind.THROWN);
    }

    /** H-SKY-01/TERRAIN/DAMAGE/LINE/CLOUD/ENTITY/PARTICLE/BORDER scope push. */
    public static ScopeOpenResult enterSection(RenderSection section) {
        FrameToken token = currentFrame;
        if (token == null) {
            return null; // vanilla-only frame: the mixin skips its scope entirely
        }
        try {
            ScopeOpenResult result = driver().enter(token, section);
            noteScopeVerdict("enter " + section, result instanceof ScopeOpenResult.Opened ? null
                    : result.toString());
            noteHookObserved(section, result);
            return result;
        } catch (RuntimeException e) {
            contain("enter " + section, token, e);
            return null;
        }
    }

    /**
     * H-HAND-01: the first-person item draw under the HAND_SOLID scope, closed in finally
     * so vanilla's hand always draws whatever the engine answered.
     */
    public static void aroundHand(Runnable original) {
        ScopeOpenResult opened = enterSection(RenderSection.HAND_SOLID);
        try {
            original.run();
        } finally {
            exitSection(RenderSection.HAND_SOLID, opened);
        }
    }

    /** One line per install proving the Task B hooks fire (never per frame). */
    private static void noteHookObserved(RenderSection section, ScopeOpenResult result) {
        String hook = switch (section) {
            case TERRAIN_TRANSLUCENT -> "H-TERRAIN-02 translucent trigger observed";
            case HAND_SOLID -> "H-HAND-01 hand scope observed";
            default -> null;
        };
        if (hook == null || !hooksObservedLogged.add(hook)) {
            return;
        }
        com.schmaloogium.engine.log.Logs.channel(
                com.schmaloogium.engine.log.LogChannels.FRAME).info(
                "{} (install #{}): result {}", hook, installEpoch,
                result instanceof ScopeOpenResult.Opened opened
                        ? "Opened " + opened.draw() : String.valueOf(result));
    }

    private static final java.util.Set<String> hooksObservedLogged =
            java.util.concurrent.ConcurrentHashMap.newKeySet();

    /** Balanced scope pop; {@code opened} is the exact result this thread's enter produced. */
    public static void exitSection(RenderSection section, ScopeOpenResult opened) {
        if (!(opened instanceof ScopeOpenResult.Opened)) {
            return;
        }
        ScopeToken scope = ((ScopeOpenResult.Opened) opened).scope();
        FrameToken token = currentFrame;
        if (token != null) {
            try {
                var result = driver().exit(token, scope);
                noteScopeVerdict("exit " + section,
                        result instanceof com.schmaloogium.engine.frame.ScopeCloseResult.Closed
                                ? null : result.toString());
            } catch (RuntimeException e) {
                contain("exit " + section, token, e);
            }
        }
    }

    /** H-RESIZE-01/02: window or framebuffer extent changed off any open frame. */
    public static void onFramebufferExtentChanged() {
        McFrameState.noteExtentChanged();
    }

    /** Composition root: a new publication was installed; log the next open's verdict. */
    public static void noteCompositionInstalled() {
        installEpoch++;
        firstOpenAfterInstallLogged = false;
        celestialLogged = false;
        fogLogged = false;
        containmentLogged = false;
        scopeVerdictsLogged.clear();
        hooksObservedLogged.clear();
    }

    /** The count of composition installs so far (one-shot evidence lines key on it). */
    public static long installEpoch() {
        return installEpoch;
    }

    private static long acceptedFrames;

    /** Count of frames the driver accepted (Opened) since boot; the capture agent diffs it. */
    public static long acceptedFrames() {
        return acceptedFrames;
    }

    /** True while a frame token is held (the reload drain must not run then). */
    public static boolean hasOpenFrame() {
        return currentFrame != null;
    }

    private static FrameDriver driver() {
        return FrameRuntime.driver();
    }

    private static void finish(FrameExitKind kind) {
        FrameToken token = currentFrame;
        currentFrame = null;
        if (token != null) {
            try {
                com.schmaloogium.engine.frame.FrameFinishResult result = driver().finish(token, kind);
                String verdict = result instanceof com.schmaloogium.engine.frame.FrameFinishResult.Finalized
                        ? "Finalized" : result.toString();
                if (!verdict.equals(lastFinishVerdict)) {
                    lastFinishVerdict = verdict;
                    com.schmaloogium.engine.log.Logs.channel(
                            com.schmaloogium.engine.log.LogChannels.FRAME).info(
                            "H-FRAME-06 finish verdict changed (install #{}): now {}",
                            installEpoch, verdict);
                }
                if (!(result instanceof com.schmaloogium.engine.frame.FrameFinishResult.Finalized)) {
                    restoreVanilla();
                }
            } catch (RuntimeException e) {
                contain("finish " + kind, token, e);
            }
        }
    }

    /** One line per distinct non-success scope verdict per install (never per frame). */
    private static void noteScopeVerdict(String hook, String failure) {
        if (failure == null) {
            return;
        }
        String key = hook + ": " + failure;
        if (scopeVerdictsLogged.add(key)) {
            com.schmaloogium.engine.log.Logs.channel(
                    com.schmaloogium.engine.log.LogChannels.FRAME).warn(
                    "H-SCOPE scope verdict (install #{}) {}", installEpoch, key);
        }
    }

    /**
     * Hands the frame back to vanilla after an abort or containment: fixed function, no
     * engine framebuffer bound, Minecraft's own main framebuffer re-bound for the rest of the
     * frame. The estate's own abort already released its objects; this only re-establishes
     * the vanilla-visible bindings.
     */
    private static void restoreVanilla() {
        try {
            DeviceHolder.current().ifPresent(device -> {
                device.shaders().useFixedFunction();
                device.framebuffers().bindDefault(com.schmaloogium.engine.gl.FramebufferTarget.READ_AND_DRAW);
            });
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
            if (mc != null && mc.getFramebuffer() != null) {
                mc.getFramebuffer().bindFramebuffer(true);
            }
        } catch (RuntimeException e) {
            com.schmaloogium.engine.log.Logs.channel(
                    com.schmaloogium.engine.log.LogChannels.FRAME).warn(
                    "vanilla restore after abort failed: {}", e.toString());
        }
    }

    /**
     * Containment (PHASE_7_DOC §4.10: a hook never propagates into vanilla): the failing
     * frame is aborted as HOOK_UNHEALTHY, its token dropped so the rest of the vanilla frame
     * runs untouched, and the defect is logged once per composition install.
     */
    private static void contain(String hook, FrameToken token, RuntimeException failure) {
        currentFrame = null;
        if (!containmentLogged) {
            containmentLogged = true;
            com.schmaloogium.engine.log.Logs.channel(
                    com.schmaloogium.engine.log.LogChannels.FRAME).error(failure,
                    "H-FRAME-00 engine exception contained at {} (install #{}); the frame is "
                            + "aborted and later frames of this publication stay vanilla-only until "
                            + "the driver latches or a reload replaces it",
                    hook, installEpoch);
        }
        if (token != null) {
            try {
                driver().abort(token, com.schmaloogium.engine.frame.FrameAbortReason.HOOK_UNHEALTHY);
            } catch (RuntimeException ignored) {
                // The driver is already unhealthy; nothing further can be done this frame.
            }
        }
        restoreVanilla();
    }

    /**
     * {@code glGetFloatv} returns the sixteen floats column-major, which is exactly the
     * order {@link Matrix4Value} stores and uploads ({@code transpose = false}). The value
     * must be built in that order: the earlier row-by-row construction uploaded every
     * captured matrix transposed, so each pack's {@code gbufferModelViewInverse * gl_ModelViewMatrix}
     * round trip mis-placed every vertex and classic terrain vanished (Task D 2026-09-12).
     */
    private static Matrix4Value readMatrix(int glMatrixEnum) {
        FloatBuffer buf = McFrameState.matrixScratch();
        McFrameState.getFloat(glMatrixEnum, buf);
        float[] columnMajor = new float[16];
        for (int i = 0; i < 16; i++) {
            columnMajor[i] = buf.get(i);
        }
        return Matrix4Value.ofColumnMajor(columnMajor);
    }
}
