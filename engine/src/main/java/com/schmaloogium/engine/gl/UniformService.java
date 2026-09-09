// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Uniform lookup and upload (PHASE_1_DOC §4.7.4). {@code locate} never throws on a
 * missing uniform — the result says {@link UniformLocation#isAbsent()} and uploads
 * through it are no-ops. The {@code int, int} overload serves App D.3 {@code atlasSize}
 * and App D.1 {@code eyeBrightness}; the {@code int, int, int, int} overload serves App
 * D.4 {@code blendFunc}.
 *
 * <p>NO uniform-block / UBO entry point — the pack contract forbids it (RESEARCH.md
 * §6.1, D-9). Likewise deliberately absent: {@code ivec3} and {@code mat3} uploads (no
 * contract consumer; §4.7.4's absent-verbs table).
 */
public interface UniformService {

    UniformLocation locate(ProgramHandle p, String name);

    void upload(UniformLocation loc, int v);

    void upload(UniformLocation loc, int x, int y);                // ivec2

    void upload(UniformLocation loc, int x, int y, int z, int w);  // ivec4

    void upload(UniformLocation loc, float v);

    void upload(UniformLocation loc, float x, float y);

    void upload(UniformLocation loc, float x, float y, float z);

    void upload(UniformLocation loc, float x, float y, float z, float w);

    void uploadMatrix4(UniformLocation loc, float[] m16, boolean transpose);
}
