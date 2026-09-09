// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.DrawService;
import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive;
import com.schmaloogium.engine.gl.ProgramHandle;

import org.lwjgl.opengl.GL11;

/**
 * The LWJGL3 composite draw (PHASE_1_DOC §4.7.4, D-P1-33): a caller-side loop over this
 * primitive with an uploaded backend copy; the verb only establishes the draw - no draw
 * state, and the caller is responsible for the composite. The active linked program's
 * geometry input is the established requirement: QUADS only with NO_GEOMETRY when
 * supported, TRIANGLE_STRIP for TRIANGLES-family inputs; other inputs reject with a
 * queued INVALID_OPERATION and no native draw.
 */
final class Lwjgl3DrawService implements DrawService {

    private final Lwjgl3GLDevice device;

    Lwjgl3DrawService(Lwjgl3GLDevice device) {
        this.device = device;
    }

    @Override
    public void fullscreenQuad() {
        device.requireRenderThread("draw.fullscreenQuad");
        ProgramHandle active = device.activeProgram();
        LinkedGeometryInputPrimitive required = null;
        if (active != null) {
            required = device.shaders().linkedGeometryInput(active).orElse(null);
        }
        if (required != null && required != LinkedGeometryInputPrimitive.TRIANGLES
                && required != LinkedGeometryInputPrimitive.TRIANGLES_ADJACENCY) {
            device.noteMutation("draw.fullscreenQuad", "(composite draw)");
            device.queueError("draw.fullscreenQuad", "(composite draw)", GLErrorKind.INVALID_OPERATION,
                    "active program's geometry input " + required
                            + " cannot be served by a fullscreen quad without a converter");
            return;
        }
        // 1.12.2 compatibility contexts serve QUADS; TRIANGLES-family programs get the strip.
        device.noteMutation("draw.fullscreenQuad", "(composite draw)");
        float[] savedTex = new float[4];
        GL11.glGetFloatv(GL11.GL_CURRENT_TEXTURE_COORDS, savedTex);
        try {
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            vertex(-1f, -1f, 0f, 0f);
            vertex(1f, -1f, 1f, 0f);
            vertex(-1f, 1f, 0f, 1f);
            vertex(1f, 1f, 1f, 1f);
            GL11.glEnd();
        } finally {
            GL11.glTexCoord2f(savedTex[0], savedTex[1]); // restore current-value state
        }
    }

    private static void vertex(float x, float y, float u, float v) {
        GL11.glTexCoord2f(u, v);
        GL11.glVertex2f(x, y);
    }
}
