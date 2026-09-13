// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.Locale;

/** {@code -Dschmaloogium.debug.probeBuffers=true}: the raw GL truth at a shadow-pass point. */
final class ShadowGlProbe {

    private ShadowGlProbe() {
    }

    /** The draw FBO's depth/colour attachment names and its first two draw buffers. */
    static String attachments() {
        int fbo = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        if (fbo == 0) {
            return "fbo=0";
        }
        int depth = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
        int c0 = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
        int c1 = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1,
                GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
        int db0 = GL11.glGetInteger(GL20.GL_DRAW_BUFFER0);
        int db1 = GL11.glGetInteger(GL20.GL_DRAW_BUFFER0 + 1);
        return String.format(Locale.ROOT, "fbo=%d depth=%d color0=%d color1=%d drawBuffers=[%s %s] status=%d",
                fbo, depth, c0, c1, drawBuffer(db0), drawBuffer(db1),
                GL30.glCheckFramebufferStatus(GL30.GL_DRAW_FRAMEBUFFER));
    }

    private static String drawBuffer(int value) {
        return value == 0 ? "N" : value >= GL30.GL_COLOR_ATTACHMENT0 ? Integer.toString(value - GL30.GL_COLOR_ATTACHMENT0)
                : Integer.toString(value);
    }

    /** Depth statistics of the draw FBO's depth attachment (min, fraction of texels < 1). */
    static String depthStats() {
        int drawFbo = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        int readFbo = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        int w = Math.min(viewport[2], 2048);
        int h = Math.min(viewport[3], 2048);
        try {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, drawFbo);
            FloatBuffer px = BufferUtils.createFloatBuffer(w * h);
            GL11.glReadPixels(0, 0, w, h, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, px);
            float min = 1f;
            int drawn = 0;
            for (int i = 0; i < w * h; i++) {
                float d = px.get(i);
                if (d < min) {
                    min = d;
                }
                if (d < 1f) {
                    drawn++;
                }
            }
            return String.format(Locale.ROOT, "depth[min=%.4f drawn=%.1f%% of %dx%d]", min,
                    100.0 * drawn / (w * h), w, h);
        } finally {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readFbo);
        }
    }

    static String state() {
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        FloatBuffer proj = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, proj);
        FloatBuffer mv = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, mv);
        return String.format(Locale.ROOT,
                "drawFbo=%d readFbo=%d program=%d viewport=%dx%d@%d,%d matrixMode=%d depthTest=%b depthMask=%b cull=%b "
                        + "proj=[%.3f %.3f %.3f %.3f | %.3f %.3f %.3f %.3f] mv=[%.3f %.3f %.3f %.3f | t=%.2f %.2f %.2f]",
                GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING), GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING),
                GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM), viewport[2], viewport[3], viewport[0], viewport[1],
                GL11.glGetInteger(GL11.GL_MATRIX_MODE), GL11.glIsEnabled(GL11.GL_DEPTH_TEST),
                GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK), GL11.glIsEnabled(GL11.GL_CULL_FACE),
                proj.get(0), proj.get(5), proj.get(10), proj.get(14), proj.get(1), proj.get(4), proj.get(11), proj.get(15),
                mv.get(0), mv.get(5), mv.get(10), mv.get(15), mv.get(12), mv.get(13), mv.get(14));
    }
}
