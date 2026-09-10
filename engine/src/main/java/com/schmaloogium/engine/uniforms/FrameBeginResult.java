// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/** Closed frame-begin outcome (PHASE_6_DOC §4.6). Only {@code ACCEPTED} mutates state;
 *  {@code DUPLICATE} is a safe no-op for a live runtime; both rejections forbid the shader
 *  draw. A retired runtime returns {@code REJECTED_GENERATION} before duplicate/identity
 *  handling. */
public enum FrameBeginResult {
    ACCEPTED,
    DUPLICATE,
    REJECTED_STALE_FRAME,
    REJECTED_GENERATION
}
