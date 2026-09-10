// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import com.schmaloogium.engine.shadow.ShadowStateLease;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.opengl.GL11;

/**
 * The reversible §4.4 state lease over vanilla/LWJGL state (PHASE_8_DOC §4.4): forced
 * third-person, the shadow viewport and the fixed-function matrix stacks are captured
 * at open and restored exactly once in reverse acquisition order. Restoration never
 * throws past its own report; idempotent re-restore is a no-op.
 */
public final class McShadowStateLease implements ShadowStateLease {

    private final int savedThirdPerson;
    private final int savedViewportX;
    private final int savedViewportY;
    private final int savedViewportW;
    private final int savedViewportH;
    private final boolean savedCullEnabled;
    private final int savedMatrixMode;
    private final boolean savedModelViewPushed;
    private final boolean savedProjectionPushed;

    private boolean restored;

    private McShadowStateLease(int savedThirdPerson,
            int savedViewportX, int savedViewportY, int savedViewportW, int savedViewportH,
            boolean savedCullEnabled, int savedMatrixMode,
            boolean savedModelViewPushed, boolean savedProjectionPushed) {
        this.savedThirdPerson = savedThirdPerson;
        this.savedViewportX = savedViewportX;
        this.savedViewportY = savedViewportY;
        this.savedViewportW = savedViewportW;
        this.savedViewportH = savedViewportH;
        this.savedCullEnabled = savedCullEnabled;
        this.savedMatrixMode = savedMatrixMode;
        this.savedModelViewPushed = savedModelViewPushed;
        this.savedProjectionPushed = savedProjectionPushed;
    }

    /**
     * Captures and installs: forced third person, the shadow viewport, culling kept
     * running, and the model-view/projection stacks pushed for the shadow camera.
     */
    public static McShadowStateLease open(int shadowExtentWidth, int shadowExtentHeight) {
        Minecraft mc = Minecraft.getMinecraft();
        int thirdPerson = mc.gameSettings.thirdPersonView;
        mc.gameSettings.thirdPersonView = 1;

        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        GL11.glViewport(0, 0, shadowExtentWidth, shadowExtentHeight);

        boolean cullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        GlStateManager.enableCull();

        int matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(1.0d, 1.0d, 1.0d, 1.0d, 0.05d, 256.0d);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0d, 0.0d, -100.0d);

        return new McShadowStateLease(thirdPerson,
                viewport[0], viewport[1], viewport[2], viewport[3],
                cullEnabled, matrixMode, true, true);
    }

    @Override
    public void restore() {
        if (restored) {
            return;
        }
        restored = true;
        // Reverse acquisition order: model-view pop, projection pop, matrix mode,
        // culling, viewport, third person.
        if (savedModelViewPushed) {
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.popMatrix();
        }
        if (savedProjectionPushed) {
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.popMatrix();
        }
        GlStateManager.matrixMode(savedMatrixMode);
        if (!savedCullEnabled) {
            GlStateManager.disableCull();
        }
        GL11.glViewport(savedViewportX, savedViewportY, savedViewportW, savedViewportH);
        Minecraft.getMinecraft().gameSettings.thirdPersonView = savedThirdPerson;
    }
}
