// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed facade allocation-target vocabulary (PHASE_1_DOC §4.7.7a, D-P1-63) — a facade
 * value, not an alternative pack target parser. RECT dispatch is native
 * TEXTURE_RECTANGLE, not TEXTURE_2D; the other targets map by identical names. Depth
 * specs admit only {@link #TEXTURE_2D}; color admits all four.
 */
public enum TextureAllocationTarget {
    TEXTURE_1D,
    TEXTURE_2D,
    TEXTURE_3D,
    RECTANGLE
}
