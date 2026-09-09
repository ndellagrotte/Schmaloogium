// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * The transfer layout of one synchronous upload (PHASE_1_DOC §4.7.7a): a {@link Color}
 * layout drawn from the same engine-level format vocabulary {@link TextureSpec} uses, or
 * a {@link Depth} layout. Describes tightly packed pixels, not internal channel sizes —
 * every upload uses unpack alignment 1, no row length/image height/skips, no byte swap,
 * and no unpack PBO binding; rows run x fastest, then y, then z.
 */
public sealed interface PixelLayout {

    /** Color transfer: one {@link PixelFormat} / {@link PixelType} pair. */
    record Color(PixelFormat format, PixelType type) implements PixelLayout {

        public Color {
            if (format == null) {
                throw new IllegalArgumentException("format must not be null");
            }
            if (type == null) {
                throw new IllegalArgumentException("type must not be null");
            }
        }
    }

    /** Depth transfer: float depth or packed 24/8 words. */
    record Depth(DepthTransferLayout value) implements PixelLayout {

        public Depth {
            if (value == null) {
                throw new IllegalArgumentException("value must not be null");
            }
        }
    }
}
