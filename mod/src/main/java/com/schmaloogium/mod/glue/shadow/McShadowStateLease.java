// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import com.schmaloogium.engine.shadow.ShadowCameraProjection;
import com.schmaloogium.engine.shadow.ShadowStateLease;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

/**
 * The reversible §4.4 state lease over vanilla/LWJGL state (PHASE_8_DOC §4.4): captured at
 * open in the documented order and restored exactly once in reverse, on every terminal path,
 * never throwing past its own step.
 *
 * <p>Opening forces third-person view {@code 1} (so the view entity renders as a caster),
 * records the Forge render pass and the render manager's blob toggle (set false for the
 * offscreen entity traversals), pushes both fixed-function stacks and loads the supplied
 * shadow projection / model-view (column-major, PHASE_8_DOC §4.5), captures the framebuffer,
 * viewport, masks, active texture unit and the cull/blend/alpha/depth/shade state through
 * {@code GlStateManager} so its cache stays coherent. The main {@code renderInfos} reference
 * captured by the world port's forced setup is put back here too (§4.7 step 6).
 */
public final class McShadowStateLease implements ShadowStateLease {

    private final McShadowInvocationState invocation;
    private final int savedThirdPerson;
    private final int savedForgePass;
    private final boolean savedRenderShadow;
    private final int savedFramebuffer;
    private final int[] savedViewport;
    private final boolean[] savedColorMask;
    private final boolean savedDepthMask;
    private final int savedActiveTexture;
    private final boolean savedCull;
    private final boolean savedBlend;
    private final boolean savedAlpha;
    private final boolean savedDepthTest;
    private final int savedDepthFunc;
    private final int savedShadeModel;
    private final int savedMatrixMode;
    private boolean restored;

    private McShadowStateLease(McShadowInvocationState invocation, int savedThirdPerson,
            int savedForgePass, boolean savedRenderShadow, int savedFramebuffer, int[] savedViewport,
            boolean[] savedColorMask, boolean savedDepthMask, int savedActiveTexture, boolean savedCull,
            boolean savedBlend, boolean savedAlpha, boolean savedDepthTest, int savedDepthFunc,
            int savedShadeModel, int savedMatrixMode) {
        this.invocation = invocation;
        this.savedThirdPerson = savedThirdPerson;
        this.savedForgePass = savedForgePass;
        this.savedRenderShadow = savedRenderShadow;
        this.savedFramebuffer = savedFramebuffer;
        this.savedViewport = savedViewport;
        this.savedColorMask = savedColorMask;
        this.savedDepthMask = savedDepthMask;
        this.savedActiveTexture = savedActiveTexture;
        this.savedCull = savedCull;
        this.savedBlend = savedBlend;
        this.savedAlpha = savedAlpha;
        this.savedDepthTest = savedDepthTest;
        this.savedDepthFunc = savedDepthFunc;
        this.savedShadeModel = savedShadeModel;
        this.savedMatrixMode = savedMatrixMode;
    }

    /** Captures and installs; any throw here leaves nothing to restore (the caller reports). */
    public static McShadowStateLease open(ShadowCameraProjection camera, int shadowExtentWidth,
            int shadowExtentHeight, McShadowInvocationState invocation) {
        Minecraft mc = Minecraft.getMinecraft();
        // 1. camera option
        int thirdPerson = mc.gameSettings.thirdPersonView;
        // 2. Forge render pass
        int forgePass = MinecraftForgeClient.getRenderPass();
        // 3. blob toggle
        boolean renderShadow = mc.getRenderManager().isRenderShadow();
        // 5. framebuffer, viewport, masks, active unit
        int framebuffer = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        int readFramebuffer = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        java.nio.ByteBuffer mask = BufferUtils.createByteBuffer(16);
        GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, mask);
        boolean[] colorMask = {mask.get(0) != 0, mask.get(1) != 0, mask.get(2) != 0, mask.get(3) != 0};
        boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        // 6. cull/blend/alpha/depth/shade
        boolean cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean alpha = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
        boolean depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        int depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        int shadeModel = GL11.glGetInteger(GL11.GL_SHADE_MODEL);
        // 4. matrix stacks
        int matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        // 5b/6b. atlas + lightmap bindings and the cached fixed-function toggles the entity
        // traversals touch (vanilla bound the atlas before setupTerrain and draws SOLID right
        // after it, so every one of these must come back exactly).
        FixedFunction ff = FixedFunction.capture(activeTexture);

        String before = PROBE && !probed ? ShadowGlProbe.state() : null;
        // Install the shadow state.
        mc.gameSettings.thirdPersonView = 1;
        mc.getRenderManager().setRenderShadow(false);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.multMatrix(columnMajor(camera.projection().toColumnMajorArray()));
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.multMatrix(columnMajor(camera.modelView().toColumnMajorArray()));
        GL11.glViewport(0, 0, shadowExtentWidth, shadowExtentHeight);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        if (before != null) {
            probed = true;
            com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.SHADOW)
                    .info("H8-PROBE lease open: before [{}] after [{}] shadowProjection {} shadowModelView {}",
                            before, ShadowGlProbe.state(),
                            java.util.Arrays.toString(camera.projection().toColumnMajorArray()),
                            java.util.Arrays.toString(camera.modelView().toColumnMajorArray()));
        }
        McShadowStateLease lease = new McShadowStateLease(invocation, thirdPerson, forgePass, renderShadow,
                framebuffer, viewport, colorMask, depthMask, activeTexture, cull, blend, alpha, depthTest,
                depthFunc, shadeModel, matrixMode);
        lease.fixedFunction = ff;
        lease.savedReadFramebuffer = readFramebuffer;
        return lease;
    }

    private FixedFunction fixedFunction;
    private int savedReadFramebuffer;

    /** The unit-0/1 bindings and the enable bits vanilla's entity renderers flip. */
    private record FixedFunction(int texture0, int texture1, boolean lighting, boolean fog, boolean texture2d,
            boolean rescaleNormal, boolean colorMaterial, int blendSrc, int blendDst, int alphaFunc,
            float alphaRef, float[] color) {

        static FixedFunction capture(int activeTexture) {
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
            int texture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            boolean texture2d = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
            int texture1 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            restoreActive(activeTexture);
            FloatBuffer color = BufferUtils.createFloatBuffer(16);
            GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, color);
            return new FixedFunction(texture0, texture1,
                    GL11.glIsEnabled(GL11.GL_LIGHTING), GL11.glIsEnabled(GL11.GL_FOG), texture2d,
                    GL11.glIsEnabled(org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL),
                    GL11.glIsEnabled(GL11.GL_COLOR_MATERIAL),
                    GL11.glGetInteger(GL11.GL_BLEND_SRC), GL11.glGetInteger(GL11.GL_BLEND_DST),
                    GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC), GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF),
                    new float[] {color.get(0), color.get(1), color.get(2), color.get(3)});
        }

        void restore(int activeTexture) {
            // Raw and cached, so the real binding and GlStateManager's belief agree again
            // whatever diverged inside the pass (a cached bind is skipped when the cache
            // already claims the name, which is exactly how a stale real binding survives).
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture1);
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
            GlStateManager.bindTexture(texture1);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture0);
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
            GlStateManager.bindTexture(texture0);
            if (texture2d) {
                GlStateManager.enableTexture2D();
            } else {
                GlStateManager.disableTexture2D();
            }
            restoreActive(activeTexture);
            if (lighting) {
                GlStateManager.enableLighting();
            } else {
                GlStateManager.disableLighting();
            }
            if (fog) {
                GlStateManager.enableFog();
            } else {
                GlStateManager.disableFog();
            }
            if (rescaleNormal) {
                GlStateManager.enableRescaleNormal();
            } else {
                GlStateManager.disableRescaleNormal();
            }
            if (colorMaterial) {
                GlStateManager.enableColorMaterial();
            } else {
                GlStateManager.disableColorMaterial();
            }
            GlStateManager.blendFunc(blendSrc, blendDst);
            GlStateManager.alphaFunc(alphaFunc, alphaRef);
            GlStateManager.color(color[0], color[1], color[2], color[3]);
        }

        /** Units 0-7 are cached by GlStateManager; anything higher must stay raw. */
        static void restoreActive(int activeTexture) {
            int unit = activeTexture - GL13.GL_TEXTURE0;
            GL13.glActiveTexture(activeTexture);
            if (unit >= 0 && unit < 8) {
                GlStateManager.setActiveTexture(activeTexture);
            }
        }
    }

    private static FloatBuffer columnMajor(float[] sixteen) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(sixteen).flip();
        return buffer;
    }

    @Override
    public void restore() {
        if (restored) {
            return;
        }
        restored = true;
        // Reverse acquisition order; every step is independent.
        step(() -> {
            if (invocation != null) {
                invocation.restoreMainList();
            }
        });
        step(() -> {
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(savedMatrixMode);
        });
        step(() -> {
            GlStateManager.shadeModel(savedShadeModel);
            GlStateManager.depthFunc(savedDepthFunc);
            if (savedDepthTest) {
                GlStateManager.enableDepth();
            } else {
                GlStateManager.disableDepth();
            }
            if (savedAlpha) {
                GlStateManager.enableAlpha();
            } else {
                GlStateManager.disableAlpha();
            }
            if (savedBlend) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
            }
            if (savedCull) {
                GlStateManager.enableCull();
            } else {
                GlStateManager.disableCull();
            }
        });
        step(() -> {
            if (fixedFunction != null) {
                fixedFunction.restore(savedActiveTexture);
            }
            FixedFunction.restoreActive(savedActiveTexture);
            GlStateManager.depthMask(savedDepthMask);
            GlStateManager.colorMask(savedColorMask[0], savedColorMask[1], savedColorMask[2],
                    savedColorMask[3]);
            GL11.glViewport(savedViewport[0], savedViewport[1], savedViewport[2], savedViewport[3]);
            OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, savedFramebuffer);
            OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, savedReadFramebuffer);
        });
        step(() -> Minecraft.getMinecraft().getRenderManager().setRenderShadow(savedRenderShadow));
        step(() -> ForgeHooksClient.setRenderPass(savedForgePass));
        step(() -> Minecraft.getMinecraft().gameSettings.thirdPersonView = savedThirdPerson);
        if (PROBE && !restoreProbed) {
            restoreProbed = true;
            com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.SHADOW)
                    .info("H8-PROBE lease restored: [{}]", ShadowGlProbe.state());
        }
    }

    private static final boolean PROBE = Boolean.getBoolean("schmaloogium.debug.probeBuffers");
    private static volatile boolean probed;
    private static volatile boolean restoreProbed;

    private static void step(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException e) {
            com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.SHADOW)
                    .warn("shadow state lease: restore step threw {}", String.valueOf(e));
        }
    }
}
