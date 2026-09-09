// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed engine wrap vocabulary (PHASE_1_DOC §4.7.7): REPEAT and CLAMP_TO_EDGE only —
 * neither granted wrap mode samples a border, which is why the border value is pinned to
 * zero. Applied S, ST or STR for 1D, 2D/RECT or 3D respectively; unused wrap axes must be
 * CLAMP_TO_EDGE in canonical values.
 */
public enum TextureWrap {
    REPEAT,
    CLAMP_TO_EDGE
}
