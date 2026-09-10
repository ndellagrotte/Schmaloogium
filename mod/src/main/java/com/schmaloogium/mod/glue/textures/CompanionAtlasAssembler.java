// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.textures;

import com.schmaloogium.engine.gl.PixelFormat;
import com.schmaloogium.engine.gl.PixelLayout;
import com.schmaloogium.engine.gl.PixelType;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureData;
import com.schmaloogium.engine.gl.TextureRegion;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Pure byte assembly of one full companion atlas (§4.1.3): base extent and mip count
 * copied from the accepted base descriptor, each sprite pasted at its base placement,
 * everything else the kind's default fill, and each level {@code l} generated from level
 * {@code l-1} by the box filter — plain 2x2 per-channel average, edges clamped, no
 * renormalization (the [A]-tagged T2-checkable choice). Sprite frame data is authoritative
 * and validated to fit; assembly never invents texels.
 */
public final class CompanionAtlasAssembler {

    /** One sprite's placement and full-resolution RGBA frames; frame 0 is primary. */
    public record SpritePlacement(String iconName, int originX, int originY, int width,
                                  int height, List<byte[]> rgbaFrames) {
        public SpritePlacement {
            Objects.requireNonNull(iconName, "iconName");
            Objects.requireNonNull(rgbaFrames, "rgbaFrames");
            rgbaFrames = List.copyOf(rgbaFrames);
            if (rgbaFrames.isEmpty()) {
                throw new IllegalArgumentException("at least one frame is required: " + iconName);
            }
            if (originX < 0 || originY < 0) {
                throw new IllegalArgumentException("sprite origins must be nonnegative: "
                    + iconName);
            }
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("sprite extent must be positive: " + iconName);
            }
            for (byte[] frame : rgbaFrames) {
                if (frame == null || frame.length != (long) width * height * 4) {
                    throw new IllegalArgumentException("frame bytes must be width*height*4: "
                        + iconName);
                }
            }
        }
    }

    private static final PixelLayout.Color RGBA = new PixelLayout.Color(
        PixelFormat.RGBA, PixelType.UNSIGNED_BYTE);

    private CompanionAtlasAssembler() {
    }

    /**
     * Assembles one full companion atlas: level 0 composed from the default fill plus
     * every sprite's frame 0, then levels {@code 1..mipmapLevels} box-filtered from the
     * previous level. Output is one TextureData per level, ascending mip order, target
     * TEXTURE_2D, full-region views over exact owned heap bytes.
     */
    public static List<TextureData> assemble(TextureAllocationTarget target, int width,
                                             int height, int mipmapLevels,
                                             List<SpritePlacement> sprites,
                                             byte[] defaultFillRgba) {
        if (target != TextureAllocationTarget.TEXTURE_2D) {
            throw new IllegalArgumentException("companion atlases are TEXTURE_2D: " + target);
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("atlas extent must be positive: "
                + width + "x" + height);
        }
        if (mipmapLevels < 0) {
            throw new IllegalArgumentException("mipmapLevels must be nonnegative: "
                + mipmapLevels);
        }
        Objects.requireNonNull(defaultFillRgba, "defaultFillRgba");
        if (defaultFillRgba.length != 4) {
            throw new IllegalArgumentException("default fill must be exactly four bytes");
        }
        List<SpritePlacement> placements = List.copyOf(Objects.requireNonNull(sprites, "sprites"));
        for (SpritePlacement placement : placements) {
            if (placement.originX() + placement.width() > width
                    || placement.originY() + placement.height() > height) {
                throw new IllegalArgumentException("sprite does not fit the atlas extent: "
                    + placement.iconName());
            }
        }
        byte[] level = new byte[width * height * 4];
        for (int i = 0; i < level.length; i += 4) {
            level[i] = defaultFillRgba[0];
            level[i + 1] = defaultFillRgba[1];
            level[i + 2] = defaultFillRgba[2];
            level[i + 3] = defaultFillRgba[3];
        }
        for (SpritePlacement placement : placements) {
            paste(level, width, height, placement.originX(), placement.originY(),
                placement.width(), placement.height(), placement.rgbaFrames().get(0));
        }
        List<TextureData> levels = new ArrayList<>(mipmapLevels + 1);
        int levelWidth = width;
        int levelHeight = height;
        for (int mip = 0; ; mip++) {
            levels.add(new TextureData(TextureAllocationTarget.TEXTURE_2D,
                new TextureRegion(0, 0, 0, levelWidth, levelHeight, 1), mip, RGBA,
                ByteBuffer.wrap(level)));
            if (mip == mipmapLevels) {
                break;
            }
            level = boxDownsample(level, levelWidth, levelHeight);
            levelWidth = Math.max(1, levelWidth >> 1);
            levelHeight = Math.max(1, levelHeight >> 1);
        }
        return List.copyOf(levels);
    }

    /**
     * One 2x2 box-filter step: each output texel averages four source texels per channel
     * (integer floor division by four); right/bottom edges clamp by repeating the last
     * source texel, so odd extents shrink to {@code max(1, extent >> 1)}.
     */
    static byte[] boxDownsample(byte[] rgba, int width, int height) {
        int outWidth = Math.max(1, width >> 1);
        int outHeight = Math.max(1, height >> 1);
        byte[] out = new byte[outWidth * outHeight * 4];
        for (int y = 0; y < outHeight; y++) {
            int sy0 = 2 * y;
            int sy1 = Math.min(sy0 + 1, height - 1);
            for (int x = 0; x < outWidth; x++) {
                int sx0 = 2 * x;
                int sx1 = Math.min(sx0 + 1, width - 1);
                int outBase = (y * outWidth + x) * 4;
                int s00 = (sy0 * width + sx0) * 4;
                int s01 = (sy0 * width + sx1) * 4;
                int s10 = (sy1 * width + sx0) * 4;
                int s11 = (sy1 * width + sx1) * 4;
                for (int c = 0; c < 4; c++) {
                    int sum = (rgba[s00 + c] & 0xFF) + (rgba[s01 + c] & 0xFF)
                        + (rgba[s10 + c] & 0xFF) + (rgba[s11 + c] & 0xFF);
                    out[outBase + c] = (byte) (sum / 4);
                }
            }
        }
        return out;
    }

    private static void paste(byte[] atlas, int atlasWidth, int atlasHeight, int originX,
                              int originY, int spriteWidth, int spriteHeight, byte[] frame) {
        for (int y = 0; y < spriteHeight; y++) {
            if (originY + y >= atlasHeight) {
                break;
            }
            int run = Math.min(spriteWidth, atlasWidth - originX);
            int source = y * spriteWidth * 4;
            int target = ((originY + y) * atlasWidth + originX) * 4;
            System.arraycopy(frame, source, atlas, target, run * 4);
        }
    }
}
