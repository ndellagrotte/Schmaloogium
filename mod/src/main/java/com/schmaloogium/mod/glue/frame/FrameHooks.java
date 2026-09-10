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
    private static volatile int reservedTerrainToken;

    private FrameHooks() {
    }

    /** H-FRAME-01: HEAD of {@code EntityRenderer.func_175068_a(IFJ)V}. */
    public static void open(int pass, float partialTicks, long finishTimeNano, int frameCounter) {
        FrameBeginSignal signal = new FrameBeginSignal(
                McFrameState.worldEpoch(),
                McFrameState.logicalTick(),
                McFrameState.smoothingTimeTicks(),
                finishTimeNano,
                McFrameState.dimension(),
                pass,
                frameCounter,
                partialTicks,
                McFrameState.targetView(),
                McFrameState.priorCompletedFramebuffer(),
                McFrameState.anaglyphEye());
        FrameOpenResult result = driver().open(signal);
        if (result instanceof FrameOpenResult.Opened opened) {
            currentFrame = opened.token();
            reservedTerrainToken = frameCounter;
        } else {
            currentFrame = null;
        }
    }

    /** H-FRAME-02: BEFORE the ordinal-0 {@code GlStateManager.clear(I)} in the pass. */
    public static void normalizeVanillaState() {
        FrameToken token = currentFrame;
        if (token != null) {
            driver().beforeFirstClear(token);
        }
    }

    /** H-FRAME-03: AFTER the ordinal-0 clear. */
    public static void afterFirstClear() {
        FrameToken token = currentFrame;
        if (token != null) {
            driver().afterFirstClear(token, McFrameState.mainDepthPreparation());
        }
    }

    /** H-FRAME-04: AFTER INVOKE {@code setupCameraTransform}. */
    public static void captureMainCamera() {
        FrameToken token = currentFrame;
        if (token == null) {
            return;
        }
        Matrix4Value modelView = readMatrix(0x1700); // GL_MODELVIEW_MATRIX
        Matrix4Value projection = readMatrix(0x1701); // GL_PROJECTION_MATRIX
        driver().captureMainCamera(token, new CameraSnapshot(modelView, projection));
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
        driver().afterTerrainSetup(token);
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
        return driver().enter(token, section);
    }

    /** Balanced scope pop; {@code opened} is the exact result this thread's enter produced. */
    public static void exitSection(RenderSection section, ScopeOpenResult opened) {
        if (!(opened instanceof ScopeOpenResult.Opened)) {
            return;
        }
        ScopeToken scope = ((ScopeOpenResult.Opened) opened).scope();
        FrameToken token = currentFrame;
        if (token != null) {
            driver().exit(token, scope);
        }
    }

    /** H-RESIZE-01/02: window or framebuffer extent changed off any open frame. */
    public static void onFramebufferExtentChanged() {
        McFrameState.noteExtentChanged();
    }

    private static FrameDriver driver() {
        return FrameRuntime.driver();
    }

    private static void finish(FrameExitKind kind) {
        FrameToken token = currentFrame;
        currentFrame = null;
        if (token != null) {
            driver().finish(token, kind);
        }
    }

    private static Matrix4Value readMatrix(int glMatrixEnum) {
        FloatBuffer buf = McFrameState.matrixScratch();
        McFrameState.getFloat(glMatrixEnum, buf);
        return new Matrix4Value(
                buf.get(0), buf.get(4), buf.get(8), buf.get(12),
                buf.get(1), buf.get(5), buf.get(9), buf.get(13),
                buf.get(2), buf.get(6), buf.get(10), buf.get(14),
                buf.get(3), buf.get(7), buf.get(11), buf.get(15));
    }
}
