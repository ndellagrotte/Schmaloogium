// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.UniformLocation;
import com.schmaloogium.engine.gl.UniformService;

import org.lwjgl.opengl.GL20;

/**
 * The LWJGL3 uniform service (PHASE_1_DOC §4.7.4). Uniform state exists ONLY in
 * programs: uploads through an absent {@link UniformLocation} are no-ops with no GL
 * entry. An upload whose owning program is not the current one is refused with a queued
 * INVALID_OPERATION carrying the uniform name - the program-selection barrier is
 * {@code shaders.use}, never an implicit re-selection.
 */
final class Lwjgl3UniformService implements UniformService {

    private final Lwjgl3GLDevice device;

    Lwjgl3UniformService(Lwjgl3GLDevice device) {
        this.device = device;
    }

    @Override
    public UniformLocation locate(ProgramHandle p, String name) {
        device.requireRenderThread("uniforms.locate");
        Lwjgl3ProgramHandle program = device.programOf(p, "uniforms.locate");
        if (name == null) {
            throw new IllegalArgumentException("uniforms.locate: name must not be null");
        }
        int location = GL20.glGetUniformLocation(program.glName(), name);
        if (location < 0) {
            return Lwjgl3UniformLocation.absent(device, program, name);
        }
        return Lwjgl3UniformLocation.present(device, program, name, location);
    }

    /** Absent locations upload nothing; otherwise the program barrier must hold. */
    private Lwjgl3UniformLocation uploadable(UniformLocation loc) {
        if (!(loc instanceof Lwjgl3UniformLocation l)
                || l.owner != device
                || l.programHandle().owner() != device) {
            throw new IllegalArgumentException(
                    "uniforms.upload: foreign handle value (not issued by this backend)");
        }
        if (l.isAbsent()) {
            return null;
        }
        if (!device.programSelectionMirrors(l.programHandle())) {
            device.noteMutation("uniforms.upload", l.name);
            device.queueError("uniforms.upload", l.name, GLErrorKind.INVALID_OPERATION,
                    "owning program is not the current program - select it with shaders.use first");
            return null;
        }
        device.noteMutation("uniforms.upload", l.name);
        return l;
    }

    @Override
    public void upload(UniformLocation loc, int v) {
        Lwjgl3UniformLocation l = uploadable(loc);
        if (l != null) {
            GL20.glUniform1i(l.glLocation(), v);
        }
    }

    @Override
    public void upload(UniformLocation loc, int x, int y) {
        Lwjgl3UniformLocation l = uploadable(loc);
        if (l != null) {
            GL20.glUniform2i(l.glLocation(), x, y);
        }
    }

    @Override
    public void upload(UniformLocation loc, int x, int y, int z, int w) {
        Lwjgl3UniformLocation l = uploadable(loc);
        if (l != null) {
            GL20.glUniform4i(l.glLocation(), x, y, z, w);
        }
    }

    @Override
    public void upload(UniformLocation loc, float v) {
        Lwjgl3UniformLocation l = uploadable(loc);
        if (l != null) {
            GL20.glUniform1f(l.glLocation(), v);
        }
    }

    @Override
    public void upload(UniformLocation loc, float x, float y) {
        Lwjgl3UniformLocation l = uploadable(loc);
        if (l != null) {
            GL20.glUniform2f(l.glLocation(), x, y);
        }
    }

    @Override
    public void upload(UniformLocation loc, float x, float y, float z) {
        Lwjgl3UniformLocation l = uploadable(loc);
        if (l != null) {
            GL20.glUniform3f(l.glLocation(), x, y, z);
        }
    }

    @Override
    public void upload(UniformLocation loc, float x, float y, float z, float w) {
        Lwjgl3UniformLocation l = uploadable(loc);
        if (l != null) {
            GL20.glUniform4f(l.glLocation(), x, y, z, w);
        }
    }

    @Override
    public void uploadMatrix4(UniformLocation loc, float[] m16, boolean transpose) {
        if (m16 == null || m16.length < 16) {
            throw new IllegalArgumentException("uniforms.uploadMatrix4: m16 must carry 16 floats");
        }
        Lwjgl3UniformLocation l = uploadable(loc);
        if (l != null) {
            GL20.glUniformMatrix4fv(l.glLocation(), transpose, m16);
        }
    }
}
