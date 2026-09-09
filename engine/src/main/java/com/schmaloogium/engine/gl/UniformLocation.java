// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * A uniform location lookup result — deliberately NOT a {@link GLHandle} (a location is a
 * lookup result, not an object) and not sealed either, because [D-P1-34] obliges every
 * backend to implement it (PHASE_1_DOC §4.7.3).
 *
 * <p>{@link #isAbsent()} is load-bearing: GLSL compilers routinely optimize out unused
 * uniforms ({@code glGetUniformLocation} returns -1), and the reference implementation's
 * per-program location caching (RESEARCH.md §4.2) depends on distinguishing "not looked up
 * yet" from "looked up, not present". Exposing that as a boolean instead of a sentinel
 * integer is exactly the kind of leak the opaque-handle decision is meant to prevent.
 */
public interface UniformLocation {

    /** True when the uniform was optimized out; uploads through it are no-ops. */
    boolean isAbsent();
}
