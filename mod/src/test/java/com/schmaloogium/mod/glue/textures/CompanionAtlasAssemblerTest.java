// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.textures;

import com.schmaloogium.engine.gl.PixelFormat;
import com.schmaloogium.engine.gl.PixelLayout;
import com.schmaloogium.engine.gl.PixelType;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureData;
import com.schmaloogium.engine.gl.TextureRegion;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Companion layout/mip-chain law (§4.1.3, §8.2): extent and origins match the base, the
 * chain is one box-filter step per level from the previous level, missing regions equal
 * the kind's contract default, and unfitting sprite data is rejected.
 */
class CompanionAtlasAssemblerTest {

    private static final byte[] NORMAL_DEFAULT = {(byte) 0xFF, 0x7F, 0x7F, (byte) 0xFF};
    private static final byte[] SPECULAR_DEFAULT = {0, 0, 0, 0};

    private static byte[] bytes(TextureData data) {
        ByteBuffer cursor = data.texels();
        cursor.position(0);
        byte[] out = new byte[cursor.remaining()];
        cursor.get(out);
        return out;
    }

    private static byte texel(byte[] rgba, int width, int x, int y, int channel) {
        return rgba[(y * width + x) * 4 + channel];
    }

    @Test
    void layoutAndMipChainMatchTheBaseExtent() {
        List<TextureData> levels = CompanionAtlasAssembler.assemble(
            TextureAllocationTarget.TEXTURE_2D, 4, 6, 2, List.of(), NORMAL_DEFAULT);
        assertEquals(3, levels.size());
        for (int mip = 0; mip < 3; mip++) {
            TextureData level = levels.get(mip);
            assertEquals(TextureAllocationTarget.TEXTURE_2D, level.target());
            assertEquals(mip, level.mipLevel());
            PixelLayout.Color layout = assertInstanceOf(PixelLayout.Color.class, level.layout());
            assertEquals(PixelFormat.RGBA, layout.format());
            assertEquals(PixelType.UNSIGNED_BYTE, layout.type());
        }
        assertEquals(new TextureRegion(0, 0, 0, 4, 6, 1), levels.get(0).region());
        assertEquals(new TextureRegion(0, 0, 0, 2, 3, 1), levels.get(1).region());
        assertEquals(new TextureRegion(0, 0, 0, 1, 1, 1), levels.get(2).region());
        byte[] level0 = bytes(levels.get(0));
        byte[] level1 = bytes(levels.get(1));
        byte[] level2 = bytes(levels.get(2));
        assertEquals(4 * 6 * 4, level0.length);
        assertEquals(2 * 3 * 4, level1.length);
        assertEquals(4, level2.length);
        // The chain law: each level is exactly one box-filter step of the previous one.
        assertEquals(java.util.Arrays.hashCode(
                CompanionAtlasAssembler.boxDownsample(level0, 4, 6)),
            java.util.Arrays.hashCode(level1));
        assertEquals(java.util.Arrays.hashCode(
                CompanionAtlasAssembler.boxDownsample(level1, 2, 3)),
            java.util.Arrays.hashCode(level2));
    }

    @Test
    void spriteRegionMatchesFrameAtLevelZeroAndFilteredAbove() {
        byte[] frame = new byte[2 * 2 * 4];
        for (int i = 0; i < 4; i++) {
            frame[i * 4] = (byte) (0x10 * (i + 1));     // R per texel
            frame[i * 4 + 1] = 0;                        // G
            frame[i * 4 + 2] = 0;                        // B
            frame[i * 4 + 3] = (byte) 0xFF;              // A
        }
        var sprite = new CompanionAtlasAssembler.SpritePlacement(
            "block/stone", 1, 1, 2, 2, List.of(frame));
        List<TextureData> levels = CompanionAtlasAssembler.assemble(
            TextureAllocationTarget.TEXTURE_2D, 4, 6, 2, List.of(sprite), NORMAL_DEFAULT);
        byte[] level0 = bytes(levels.get(0));
        // Default fill everywhere except the sprite region.
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 4; x++) {
                boolean inSprite = x >= 1 && x < 3 && y >= 1 && y < 3;
                for (int c = 0; c < 4; c++) {
                    byte expected = inSprite
                        ? frame[((y - 1) * 2 + (x - 1)) * 4 + c]
                        : NORMAL_DEFAULT[c];
                    assertEquals(expected, texel(level0, 4, x, y, c),
                        "texel (" + x + "," + y + ") channel " + c);
                }
            }
        }
        // Levels 1..2 are the box-filtered chain of the composed level 0.
        byte[] level1 = bytes(levels.get(1));
        byte[] expected1 = CompanionAtlasAssembler.boxDownsample(level0, 4, 6);
        assertEquals(java.util.Arrays.hashCode(expected1), java.util.Arrays.hashCode(level1));
        byte[] level2 = bytes(levels.get(2));
        byte[] expected2 = CompanionAtlasAssembler.boxDownsample(expected1, 2, 3);
        assertEquals(java.util.Arrays.hashCode(expected2), java.util.Arrays.hashCode(level2));
    }

    @Test
    void missingNormalRegionsUseTheContractDefaultBytes() {
        byte[] frame = new byte[2 * 2 * 4];
        java.util.Arrays.fill(frame, (byte) 0x55);
        var sprite = new CompanionAtlasAssembler.SpritePlacement(
            "block/dirt", 0, 0, 2, 2, List.of(frame));
        List<TextureData> levels = CompanionAtlasAssembler.assemble(
            TextureAllocationTarget.TEXTURE_2D, 4, 6, 1, List.of(sprite), NORMAL_DEFAULT);
        byte[] level0 = bytes(levels.get(0));
        // (4,5) is outside both the sprite and any filtered special case.
        int p = (5 * 4 + 3) * 4;
        assertEquals(NORMAL_DEFAULT[0], level0[p]);
        assertEquals(NORMAL_DEFAULT[1], level0[p + 1]);
        assertEquals(NORMAL_DEFAULT[2], level0[p + 2]);
        assertEquals(NORMAL_DEFAULT[3], level0[p + 3]);
    }

    @Test
    void missingSpecularRegionsUseZeroFill() {
        List<TextureData> levels = CompanionAtlasAssembler.assemble(
            TextureAllocationTarget.TEXTURE_2D, 4, 6, 2, List.of(), SPECULAR_DEFAULT);
        for (TextureData level : levels) {
            for (byte value : bytes(level)) {
                assertEquals(0, value);
            }
        }
    }

    @Test
    void boxDownsampleIsThePlainTwoByTwoAverage() {
        byte[] block = new byte[] {
            10, 0, 0, (byte) 255,
            20, 4, 0, (byte) 255,
            30, 8, 0, (byte) 255,
            40, 12, 0, (byte) 255,
        };
        byte[] out = CompanionAtlasAssembler.boxDownsample(block, 2, 2);
        assertEquals(4, out.length);
        assertEquals(25, out[0] & 0xFF);   // (10+20+30+40)/4 floor
        assertEquals(6, out[1] & 0xFF);    // (0+4+8+12)/4
        assertEquals(0, out[2] & 0xFF);
        assertEquals((byte) 255, out[3]);
        // Integer floor, no rounding up: (1+1+1+2)/4 = 1 per channel.
        byte[] floors = new byte[] {
            1, 1, 1, 1,
            1, 1, 1, 1,
            1, 1, 1, 1,
            2, 2, 2, 2,
        };
        byte[] averaged = CompanionAtlasAssembler.boxDownsample(floors, 2, 2);
        assertEquals(1, averaged[0] & 0xFF);
        assertEquals(1, averaged[1] & 0xFF);
        assertEquals(1, averaged[2] & 0xFF);
        assertEquals(1, averaged[3] & 0xFF);
    }

    @Test
    void boxDownsampleRunsOncePerLevelOnOddExtents() {
        // 3x3 → 1x1 samples exactly the four top-left texels (edge clamp, floor average).
        byte[] grid = new byte[3 * 3 * 4];
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                grid[(y * 3 + x) * 4] = (byte) (10 * (x + 1) + y);
            }
        }
        byte[] out = CompanionAtlasAssembler.boxDownsample(grid, 3, 3);
        assertEquals(4, out.length);
        int expected = (10 + 20 + 11 + 21) / 4; // texels (0,0),(1,0),(0,1),(1,1)
        assertEquals(expected, out[0] & 0xFF);
    }

    @Test
    void unfittingSpriteDataIsRejected() {
        byte[] frame = new byte[16 * 16 * 4];
        var sprite = new CompanionAtlasAssembler.SpritePlacement(
            "block/big", 56, 0, 16, 16, List.of(frame));
        assertThrows(IllegalArgumentException.class, () -> CompanionAtlasAssembler.assemble(
            TextureAllocationTarget.TEXTURE_2D, 64, 32, 0, List.of(sprite), NORMAL_DEFAULT));
        assertThrows(IllegalArgumentException.class, () -> CompanionAtlasAssembler.assemble(
            TextureAllocationTarget.TEXTURE_2D, 64, 32, -1, List.of(), NORMAL_DEFAULT));
        assertThrows(IllegalArgumentException.class, () -> CompanionAtlasAssembler.assemble(
            TextureAllocationTarget.TEXTURE_1D, 64, 32, 0, List.of(), NORMAL_DEFAULT));
        assertThrows(IllegalArgumentException.class, () -> CompanionAtlasAssembler.assemble(
            TextureAllocationTarget.TEXTURE_2D, 64, 32, 0, List.of(), new byte[3]));
        // Wrong frame byte count is rejected at construction.
        assertThrows(IllegalArgumentException.class,
            () -> new CompanionAtlasAssembler.SpritePlacement(
                "block/bad", 0, 0, 4, 4, List.of(new byte[10])));
    }
}
