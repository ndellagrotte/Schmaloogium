// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * An allocation extent (PHASE_1_DOC §4.7.7a). All dimensions positive. Canonical unused
 * axes: {@code height = depth = 1} for 1D; {@code depth = 1} for 2D/RECT; 3D retains all
 * three axes. Allocation checks width for 1D and width/height for 2D against
 * {@code maxTextureSize}; all three 3D dimensions use {@code max3DTextureSize}; rectangle
 * width/height use {@code maxRectangleTextureSize} ([D-P1-66]).
 */
public record TextureExtent(int width, int height, int depth) {

    public TextureExtent {
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("extent dimensions must be positive: "
                    + width + "x" + height + "x" + depth);
        }
    }
}
