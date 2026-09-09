// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.GLHandle;
import com.schmaloogium.engine.gl.DebugService;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.KHRDebug;

/**
 * The v0.1 debug service (PHASE_1_DOC §4.7.4, [D-P1-73]): KHR_debug object labels and
 * groups. Group push/pop is a v0.5 activity - v0.1 implementations are no-ops but
 * {@link #isActive()} is still honest about the v0.5 contract gate. Labels apply when
 * the flag is on and KHR_debug is active; label updates on unmaterialized owned textures
 * remain retained (D-P1-71) and apply at materialization.
 */

final class Lwjgl3DebugService implements DebugService {
    private final Lwjgl3GLDevice device;

    Lwjgl3DebugService(Lwjgl3GLDevice device) {
        this.device = device;
    }

    @Override
    public void pushGroup(String labelGroup) {
        device.requireRenderThread("debug.pushGroup");
        if (!isActive() || labelGroup == null) {
            return;
        }
        KHRDebug.glPushDebugGroup(GL43.GL_DEBUG_SOURCE_APPLICATION, 0, labelGroup);
    }

    @Override
    public void popGroup() {
        device.requireRenderThread("debug.popGroup");
        if (!isActive()) {
            return;
        }
        KHRDebug.glPopDebugGroup();
    }

    @Override
    public void label(GLHandle handle, String label) {
        device.requireRenderThread("debug.label");
        if (!isActive() || label == null) {
            return;
        }
        applyLabel(device, handle, label);
    }

    @Override
    public boolean isActive() {
        return device.recordsLabels()
                && (device.capabilities().atLeast(4, 3)
                || device.capabilities().hasExtension("GL_KHR_debug"));
    }

    // ------------------------------------------------------------------ application

    static void applyLabel(Lwjgl3GLDevice device, GLHandle handle, String label) {
        if (handle instanceof Lwjgl3OwnedTexture texture) {
            texture.relabel(label);
            if (texture.materialized()) {
                applyLabelIfPossible(device, GL11.GL_TEXTURE, texture.glName(), label);
            }
            return;
        }
        if (handle instanceof Lwjgl3ProgramHandle program) {
            program.relabel(label);
            applyLabelIfPossible(device, GL43.GL_PROGRAM, program.glName(), label);
            return;
        }
        if (handle instanceof Lwjgl3ShaderHandle shader) {
            shader.relabel(label);
            applyLabelIfPossible(device, GL43.GL_SHADER, shader.glName(), label);
            return;
        }
        if (handle instanceof Lwjgl3FramebufferHandle framebuffer) {
            framebuffer.relabel(label);
            applyLabelIfPossible(device, GL43.GL_FRAMEBUFFER, framebuffer.glName(), label);
        }
    }

    /** Applies a KHR object label only when the v0.5-style gate says the device can. */
    static void applyLabelIfPossible(Lwjgl3GLDevice device, int identifier, int name, String label) {
        if (!device.recordsLabels() || name < 0) {
            return;
        }
        KHRDebug.glObjectLabel(identifier, name, label);
    }
}
