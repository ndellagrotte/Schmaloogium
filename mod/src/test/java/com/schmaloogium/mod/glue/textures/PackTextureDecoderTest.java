// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.textures;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Headless PNG decode: exact RGBA extraction and entry-local failure. */
class PackTextureDecoderTest {

    private static byte[] png(int[][] argbPixels) {
        int height = argbPixels.length;
        int width = argbPixels[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, argbPixels[y][x]);
            }
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new AssertionError("in-memory png encode failed", e);
        }
    }

    @Test
    void decodesExactRowMajorRgba() throws Exception {
        byte[] png = png(new int[][] {
            {0xFF112233, 0x80445566},
            {0x00000000, 0xFFFFFFFF},
        });
        PackTextureDecoder.Decoded decoded = PackTextureDecoder.decode(png);
        assertEquals(2, decoded.width());
        assertEquals(2, decoded.height());
        assertArrayEquals(new byte[] {
            (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0xFF,
            (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x80,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        }, decoded.rgba());
    }

    @Test
    void decodeIsDeterministic() throws Exception {
        byte[] png = png(new int[][] {{0xFF010203, 0xFF040506}});
        PackTextureDecoder.Decoded first = PackTextureDecoder.decode(png);
        PackTextureDecoder.Decoded second = PackTextureDecoder.decode(png);
        assertArrayEquals(first.rgba(), second.rgba());
        assertEquals(first.width(), second.width());
    }

    @Test
    void garbageBytesThrowDecodeException() {
        assertThrows(PackTextureDecoder.DecodeException.class,
            () -> PackTextureDecoder.decode("definitely not a png".getBytes()));
        assertThrows(PackTextureDecoder.DecodeException.class,
            () -> PackTextureDecoder.decode(new byte[0]));
    }

    @Test
    void truncatedPngThrowsDecodeException() {
        byte[] png = png(new int[][] {{0xFF112233}});
        byte[] truncated = new byte[png.length / 2];
        System.arraycopy(png, 0, truncated, 0, truncated.length);
        assertThrows(PackTextureDecoder.DecodeException.class,
            () -> PackTextureDecoder.decode(truncated));
    }
}
