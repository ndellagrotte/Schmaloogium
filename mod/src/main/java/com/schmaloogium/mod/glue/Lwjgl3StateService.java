// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.AlphaBlendOverride;
import com.schmaloogium.engine.gl.AlphaTestState;
import com.schmaloogium.engine.gl.BlendState;
import com.schmaloogium.engine.gl.ClearTarget;
import com.schmaloogium.engine.gl.FogState;
import com.schmaloogium.engine.gl.StateAspect;
import com.schmaloogium.engine.gl.StateService;
import com.schmaloogium.engine.gl.StateSnapshot;

import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.nio.FloatBuffer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The LWJGL3 deliberate-narrow state service (PHASE_1_DOC §4.7.4, D-P1-29). Every verb
 * whose GL state {@code GlStateManager} caches is issued through it; viewport and clear
 * masks are the documented exceptions. Snapshot/restore ([D-P1-52]) reads actual state
 * through native queries - {@code glGet*} does not stale the cache - and writes each
 * changed field through GLSM with the two-step poke so the cache can never be left
 * believing a stale value ([D-P1-57]).
 */
final class Lwjgl3StateService implements StateService {

    private final Lwjgl3GLDevice device;

    Lwjgl3StateService(Lwjgl3GLDevice device) {
        this.device = device;
    }

    // ------------------------------------------------------------- verbs

    @Override
    public void viewport(int x, int y, int w, int h) {
        device.requireRenderThread("state.viewport");
        GL11.glViewport(x, y, w, h); // excepted: not GLSM-cached
        device.noteMutation("state.viewport", "(viewport)");
    }

    @Override
    public void clearColor(float r, float g, float b, float a) {
        device.requireRenderThread("state.clearColor");
        GlStateManager.clearColor(r, g, b, a); // [D-P1-29]: cached clear color
        device.noteMutation("state.clearColor", "(clear color)");
    }

    @Override
    public void clear(EnumSet<ClearTarget> targets) {
        device.requireRenderThread("state.clear");
        if (targets == null || targets.isEmpty()) {
            throw new IllegalArgumentException("state.clear: targets must be nonempty");
        }
        GlStateManager.clear(GlNames.glClearMask(targets)); // "any clear" through GLSM
        device.noteMutation("state.clear", "(clear)");
    }

    @Override
    public void depthMask(boolean enabled) {
        device.requireRenderThread("state.depthMask");
        GlStateManager.depthMask(enabled);
        device.noteMutation("state.depthMask", "(depth mask)");
    }

    @Override
    public void depthTest(boolean enabled) {
        device.requireRenderThread("state.depthTest");
        issueDepthTest(enabled);
        device.noteMutation("state.depthTest", "(depth test)");
    }

    private static void issueDepthTest(boolean enabled) {
        if (enabled) {
            GlStateManager.enableDepth();
        } else {
            GlStateManager.disableDepth();
        }
    }

    @Override
    public void blend(BlendState state) {
        device.requireRenderThread("state.blend");
        if (device.suppressesBlendMutation()) {
            return; // the lock holds blend: this write would destroy the locked lease
        }
        issueBlend(state);
        device.noteMutation("state.blend", "(blend)");
    }

    private static void issueBlend(BlendState state) {
        if (state == null) {
            GlStateManager.disableBlend();
            return;
        }
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlNames.glBlendFactor(state.srcRgb()),
                GlNames.glBlendFactor(state.dstRgb()),
                GlNames.glBlendFactor(state.srcAlpha()),
                GlNames.glBlendFactor(state.dstAlpha()));
    }

    // ------------------------------------------------------------- lock plumbing

    /** Native-read capture for the lock ([D-P1-57]): present aspects only. */
    AlphaTestState captureAlpha() {
        float[] ref = new float[1];
        GL11.glGetFloatv(GL11.GL_ALPHA_TEST_REF, ref);
        return new AlphaTestState(alphaFunc(GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC)), ref[0]);
    }

    /** Native-read capture of the effective blend factors including disabled ones. */
    BlendState captureBlend() {
        return effectiveBlend();
    }

    /** The lock's own writes bypass the suppression gate they create. */
    void issueAlphaTestForLock(AlphaTestState value) {
        issueAlphaTest(value);
    }

    void issueBlendForLock(BlendState value) {
        issueBlend(value);
    }

    @Override
    public void alphaTest(AlphaTestState state) {
        device.requireRenderThread("state.alphaTest");
        if (device.suppressesAlphaMutation()) {
            return;
        }
        issueAlphaTest(state);
        device.noteMutation("state.alphaTest", "(alpha test)");
    }

    private static void issueAlphaTest(AlphaTestState state) {
        if (state == null) {
            GlStateManager.disableAlpha();
            return;
        }
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GlNames.glAlphaFunc(state.function()), state.reference());
    }

    @Override
    public void fog(FogState state) {
        device.requireRenderThread("state.fog");
        if (state == null) {
            GlStateManager.disableFog();
        } else {
            issueFog(state);
        }
        device.noteMutation("state.fog", "(fog)");
    }

    private static void issueFog(FogState state) {
        GlStateManager.enableFog();
        GlStateManager.setFog(switch (state.mode()) {
            case LINEAR -> GlStateManager.FogMode.LINEAR;
            case EXP -> GlStateManager.FogMode.EXP;
            case EXP2 -> GlStateManager.FogMode.EXP2;
        });
        GlStateManager.setFogStart(state.start());
        GlStateManager.setFogEnd(state.end());
        GlStateManager.setFogDensity(state.density());
        // Fog color is not GLSM-cached in 1.12.2 - the raw setter is the sanctioned path.
        GL11.glFogfv(GL11.GL_FOG_COLOR, fogColor(state.red(), state.green(), state.blue(), state.alpha()));
    }

    private static FloatBuffer fogColor(float r, float g, float b, float a) {
        FloatBuffer buf = org.lwjgl.BufferUtils.createFloatBuffer(4);
        buf.put(r).put(g).put(b).put(a).flip();
        return buf;
    }

    // ------------------------------------------------------------- alpha/blend lock

    @Override
    public AlphaBlendOverride lockAlphaBlend(
            Optional<AlphaTestState> alpha, Optional<BlendState> blend) {
        device.requireRenderThread("state.lockAlphaBlend");
        // Both optionals empty is the common case (a program with no alphaTest/blend
        // override): Phase 4 §4 step 6 acquires the lease for every shader activation, so
        // the answer is a lease that holds and restores nothing, not a rejection.
        if (device.alphaLeaseHeld()) {
            throw new IllegalStateException("state.lockAlphaBlend: a lease is already active");
        }
        Lwjgl3AlphaBlendOverride lease = new Lwjgl3AlphaBlendOverride(device, alpha, blend);
        device.attachAlphaLease(lease);
        return lease;
    }

    @Override
    public BlendState effectiveBlend() {
        device.requireRenderThread("state.effectiveBlend");
        return new BlendState(
                blendFactor(GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB)),
                blendFactor(GL11.glGetInteger(GL14.GL_BLEND_DST_RGB)),
                blendFactor(GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA)),
                blendFactor(GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA)));
    }

    private static BlendState.BlendFactor blendFactor(int gl) {
        for (BlendState.BlendFactor f : BlendState.BlendFactor.values()) {
            if (GlNames.glBlendFactor(f) == gl) {
                return f;
            }
        }
        throw new IllegalStateException("state.effectiveBlend: unknown blend factor 0x"
                + Integer.toHexString(gl));
    }

    // ------------------------------------------------------------- snapshot/restore

    @Override
    public StateSnapshot snapshot(EnumSet<StateAspect> aspects) {
        device.requireRenderThread("state.snapshot");
        if (aspects == null || aspects.isEmpty()) {
            throw new IllegalArgumentException("state.snapshot: aspects must be nonempty");
        }
        Map<StateAspect, Object> captured = new HashMap<>();
        for (StateAspect aspect : aspects) {
            captured.put(aspect, readAspect(aspect));
        }
        return new Lwjgl3StateSnapshot(device, captured);
    }

    @Override
    public void restore(StateSnapshot snapshot) {
        device.requireRenderThread("state.restore");
        if (!(snapshot instanceof Lwjgl3StateSnapshot snap) || snap.device() != device) {
            throw new IllegalArgumentException("state.restore: foreign snapshot (wrong device or backend)");
        }
        snap.restore();
    }

    /** Reads actual native state (D-P1-57: reads do not stale the GLSM cache). */
    private Object readAspect(StateAspect aspect) {
        return switch (aspect) {
            case VIEWPORT -> {
                int[] v = new int[4];
                GL11.glGetIntegerv(GL11.GL_VIEWPORT, v);
                yield v;
            }
            case CLEAR_COLOR -> {
                float[] c = new float[4];
                GL11.glGetFloatv(GL11.GL_COLOR_CLEAR_VALUE, c);
                yield c;
            }
            case CLEAR -> {
                double[] d = new double[1];
                GL11.glGetDoublev(GL11.GL_DEPTH_CLEAR_VALUE, d);
                yield new Object[]{d[0], GL11.glGetInteger(GL11.GL_STENCIL_CLEAR_VALUE)};
            }
            case DEPTH_MASK -> GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
            case DEPTH_TEST -> GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            case BLEND -> new BlendState(
                    blendFactor(GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB)),
                    blendFactor(GL11.glGetInteger(GL14.GL_BLEND_DST_RGB)),
                    blendFactor(GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA)),
                    blendFactor(GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA)));
            case ALPHA_TEST -> {
                float[] ref = new float[1];
                GL11.glGetFloatv(GL11.GL_ALPHA_TEST_REF, ref);
                yield new AlphaTestState(
                        alphaFunc(GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC)), ref[0]);
            }
            case FOG -> {
                float[] color = new float[4];
                GL11.glGetFloatv(GL11.GL_FOG_COLOR, color);
                yield new FogState(
                        fogMode(GL11.glGetInteger(GL11.GL_FOG_MODE)),
                        GL11.glGetFloat(GL11.GL_FOG_START),
                        GL11.glGetFloat(GL11.GL_FOG_END),
                        GL11.glGetFloat(GL11.GL_FOG_DENSITY),
                        color[0], color[1], color[2], color[3]);
            }
        };
    }

    private static AlphaTestState.AlphaTestFunction alphaFunc(int gl) {
        for (AlphaTestState.AlphaTestFunction f : AlphaTestState.AlphaTestFunction.values()) {
            if (GlNames.glAlphaFunc(f) == gl) {
                return f;
            }
        }
        throw new IllegalStateException("state.snapshot: unknown alpha func 0x" + Integer.toHexString(gl));
    }

    private static FogState.FogMode fogMode(int gl) {
        for (FogState.FogMode m : FogState.FogMode.values()) {
            if (GlNames.glFogMode(m) == gl) {
                return m;
            }
        }
        throw new IllegalStateException("state.snapshot: unknown fog mode 0x" + Integer.toHexString(gl));
    }

    /** Writes through GLSM with the two-step poke (D-P1-57): poke, then target. */
    void writeAspect(StateAspect aspect, Object value) {
        switch (aspect) {
            case VIEWPORT -> {
                int[] v = (int[]) value;
                GL11.glViewport(0, 0, 1, 1);
                GL11.glViewport(v[0], v[1], v[2], v[3]);
            }
            case CLEAR_COLOR -> {
                float[] c = (float[]) value;
                GlStateManager.clearColor(1f, 1f, 1f, 1f);
                GlStateManager.clearColor(c[0], c[1], c[2], c[3]);
            }
            case CLEAR -> {
                Object[] ds = (Object[]) value;
                GlStateManager.clearDepth(1.0);
                GlStateManager.clearDepth((Double) ds[0]);
                int stencil = (Integer) ds[1];
                GL11.glClearStencil(stencil == 0 ? 1 : 0);
                GL11.glClearStencil(stencil);
            }
            case DEPTH_MASK -> {
                GlStateManager.depthMask(!(Boolean) value);
                GlStateManager.depthMask((Boolean) value);
            }
            case DEPTH_TEST -> {
                issueDepthTest(!(Boolean) value);
                issueDepthTest((Boolean) value);
            }
            case BLEND -> {
                issueBlend(null);
                issueBlend((BlendState) value);
            }
            case ALPHA_TEST -> {
                issueAlphaTest(null);
                issueAlphaTest((AlphaTestState) value);
            }
            case FOG -> {
                GlStateManager.disableFog();
                issueFog((FogState) value);
            }
        }
    }
}
