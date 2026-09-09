// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * One driver-level error, attributable to the DRAIN WINDOW that produced it — and
 * therefore to one call when the window held exactly one MUTATING FACADE call ([D-P1-32],
 * PHASE_1_DOC §4.7.4). The GL error flag is per-context and this architecture guarantees
 * GL traffic that never reaches the facade, so a window may hold an error no facade call
 * caused; {@link #op()}/{@link #subjectLabel()} can then name the wrong call, which is why
 * attribution rests on the replay ({@link ReplayAwareGLError}, [D-P1-42]), not on this
 * record.
 *
 * <p>{@code op} is the facade verb ("uniforms.upload", "textures.allocate");
 * {@code subjectLabel} is the debug label of the handle or the uniform name involved when
 * the window held one call, and {@code "(batched, N calls)"} when it held several.
 * {@code kind} is an engine enum — never a GL constant.
 */
public record GLError(String op, String subjectLabel, GLErrorKind kind, String detail) {
}
