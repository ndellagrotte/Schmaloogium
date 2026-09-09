// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed allocation specification for one owned texture (PHASE_1_DOC §4.7.7a, D-P1-63).
 * Defines storage, not initialized/sampleable contents; formats are Phase 5's.
 *
 * <p>{@code mipLevels} is a COUNT — initially defined contiguous levels
 * {@code 0..mipLevels-1}; valid range is {@code 1..1+floor(log2(max used dimension))},
 * RECTANGLE exactly 1. At level {@code l} each used dimension is
 * {@code max(1, baseDimension >> l)}; unused dimensions remain 1. The first successful
 * allocation fixes an owned object's target; reallocation may change extent/format/levels
 * only within that target — changing target requires a new owned object.
 */
public sealed interface TextureSpec {

    /** A color allocation; admits all four targets. */
    record ColorTextureSpec(TextureAllocationTarget target, ColorInternalFormat format,
                            PixelLayout.Color allocationLayout, TextureExtent extent, int mipLevels)
            implements TextureSpec {

        public ColorTextureSpec {
            validateShape(target, extent, mipLevels);
            if (format == null) {
                throw new IllegalArgumentException("format must not be null");
            }
            if (allocationLayout == null) {
                throw new IllegalArgumentException("allocationLayout must not be null");
            }
        }
    }

    /** A depth/depth-stencil allocation; admits only {@link TextureAllocationTarget#TEXTURE_2D}. */
    record DepthTextureSpec(TextureAllocationTarget target, DepthAttachmentFormat format,
                            PixelLayout.Depth allocationLayout, TextureExtent extent, int mipLevels)
            implements TextureSpec {

        public DepthTextureSpec {
            if (target != TextureAllocationTarget.TEXTURE_2D) {
                throw new IllegalArgumentException(
                        "depth specs admit only TEXTURE_2D (PHASE_1_DOC §4.7.7a): " + target);
            }
            validateShape(target, extent, mipLevels);
            if (format == null) {
                throw new IllegalArgumentException("format must not be null");
            }
            if (allocationLayout == null) {
                throw new IllegalArgumentException("allocationLayout must not be null");
            }
        }
    }

    private static void validateShape(TextureAllocationTarget target, TextureExtent extent, int mipLevels) {
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }
        if (extent == null) {
            throw new IllegalArgumentException("extent must not be null");
        }
        int maxUsedDimension = switch (target) {
            case TEXTURE_1D -> extent.width();
            case TEXTURE_2D, RECTANGLE -> Math.max(extent.width(), extent.height());
            case TEXTURE_3D -> Math.max(extent.width(), Math.max(extent.height(), extent.depth()));
        };
        int maxMipLevels = 1 + (Integer.SIZE - Integer.numberOfLeadingZeros(maxUsedDimension) - 1);
        if (mipLevels < 1 || mipLevels > maxMipLevels) {
            throw new IllegalArgumentException("mipLevels must be in 1.." + maxMipLevels
                    + " for extent " + extent.width() + "x" + extent.height() + "x" + extent.depth()
                    + " on " + target + ": " + mipLevels);
        }
        if (target == TextureAllocationTarget.RECTANGLE && mipLevels != 1) {
            throw new IllegalArgumentException("RECTANGLE admits exactly one mip level: " + mipLevels);
        }
    }
}
