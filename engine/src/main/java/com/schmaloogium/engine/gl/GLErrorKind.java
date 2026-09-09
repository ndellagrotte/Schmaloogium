// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed engine vocabulary for driver-level error kinds (PHASE_1_DOC §4.7.4) — never a GL
 * constant; the backend maps driver values into this set, with {@link #UNKNOWN} absorbing
 * anything unrecognized.
 */
public enum GLErrorKind {
    INVALID_ENUM,
    INVALID_VALUE,
    INVALID_OPERATION,
    OUT_OF_MEMORY,
    INVALID_FRAMEBUFFER_OPERATION,
    UNKNOWN
}
