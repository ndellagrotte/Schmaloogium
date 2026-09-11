// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

/**
 * The sampleable main-depth replacement (PHASE_5_DOC §4.8 piece 2): vanilla's framebuffer
 * depth is a renderbuffer, which no shader can read as {@code depthtex0}. The
 * {@code Framebuffer} mixin suppresses the renderbuffer allocation and hands the framebuffer
 * here, where a NEAREST/CLAMP_TO_EDGE depth texture (packed depth-stencil when the Forge
 * stencil flag is on) is allocated and attached. Raw GL is permitted in {@code mod.glue}
 * only; the mixin itself never names LWJGL.
 */
public final class DepthTexture {

    private DepthTexture() {
    }

    /**
     * Allocates and attaches the depth texture to {@code framebufferObject}; returns the GL
     * texture name (a texture, never a renderbuffer). Framebuffer and texture bindings are
     * restored. Render thread.
     */
    public static int createAndAttach(int framebufferObject, int width, int height, boolean stencil) {
        int savedTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        int savedFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int texture = GL11.glGenTextures();
        try {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL11.GL_NONE);
            if (stencil) {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH24_STENCIL8, width, height, 0,
                        GL30.GL_DEPTH_STENCIL, GL30.GL_UNSIGNED_INT_24_8, (ByteBuffer) null);
            } else {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0,
                        GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
            }
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferObject);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                    GL11.GL_TEXTURE_2D, texture, 0);
            if (stencil) {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT,
                        GL11.GL_TEXTURE_2D, texture, 0);
            }
            int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                Logs.channel(LogChannels.BUFFERS).warn(
                        "H-FBO-03 main framebuffer {} incomplete after depth texture attach: 0x{}",
                        framebufferObject, Integer.toHexString(status));
            } else {
                Logs.channel(LogChannels.BUFFERS).info(
                        "H-FBO-03 main depth renderbuffer replaced by texture {} ({}x{}, stencil {})",
                        texture, width, height, stencil);
            }
            return texture;
        } finally {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, savedFramebuffer);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, savedTexture);
        }
    }

    /** Deletes a texture created by {@link #createAndAttach}; negative names are ignored. */
    public static void delete(int texture) {
        if (texture > 0) {
            GL11.glDeleteTextures(texture);
        }
    }
}
