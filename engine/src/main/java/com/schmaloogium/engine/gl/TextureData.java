// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.nio.ByteBuffer;

/**
 * One synchronous upload payload (PHASE_1_DOC §4.7.7a): target, region, mip level, a
 * {@link PixelLayout} and the texels. The facade borrows the bytes for this synchronous
 * call only — it never retains or mutates content, position, limit or byte order; the
 * caller keeps them stable through return. All fields nonnull; the mip index nonnegative.
 */
public record TextureData(TextureAllocationTarget target, TextureRegion region,
                          int mipLevel, PixelLayout layout, ByteBuffer texels) {

    public TextureData {
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }
        if (region == null) {
            throw new IllegalArgumentException("region must not be null");
        }
        if (layout == null) {
            throw new IllegalArgumentException("layout must not be null");
        }
        if (texels == null) {
            throw new IllegalArgumentException("texels must not be null");
        }
        if (mipLevel < 0) {
            throw new IllegalArgumentException("mipLevel must be nonnegative: " + mipLevel);
        }
    }
}
