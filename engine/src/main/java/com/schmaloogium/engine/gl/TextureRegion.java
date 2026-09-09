// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * A sub-image region (PHASE_1_DOC §4.7.7a): origins nonnegative, sizes positive. The
 * framebuffer depth verbs take a 2D region {@code (srcX, srcY, 0, width, height, 1)}
 * copying to destination origin (0,0) at level 0. Upload region end coordinates must fit
 * the exact allocated level.
 */
public record TextureRegion(int x, int y, int z, int width, int height, int depth) {

    public TextureRegion {
        if (x < 0 || y < 0 || z < 0) {
            throw new IllegalArgumentException("region origins must be nonnegative: "
                    + x + ", " + y + ", " + z);
        }
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("region sizes must be positive: "
                    + width + "x" + height + "x" + depth);
        }
    }
}
