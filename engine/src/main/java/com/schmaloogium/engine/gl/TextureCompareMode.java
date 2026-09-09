// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed engine shadow-comparison mode vocabulary (PHASE_1_DOC §4.7.7);
 * {@code REF_TO_TEXTURE} maps to the native compare-ref-to-texture constant. Comparison
 * requires depth/depth-stencil storage; no color/integer texture may acquire comparison
 * as a workaround.
 */
public enum TextureCompareMode {
    NONE,
    REF_TO_TEXTURE
}
