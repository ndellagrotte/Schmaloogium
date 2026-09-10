// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.textures;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Deterministic headless PNG decode for pack texture preparation (§4.3.2): any supported
 * image type is normalized to INT_ARGB and extracted as row-major RGBA bytes. No caching,
 * no host paths, no Minecraft/LWJGL types; decode failures are entry-local so the caller
 * maps them to the closed failure vocabulary.
 */
public final class PackTextureDecoder {

    private PackTextureDecoder() {
    }

    /** One decoded image: extent and exact row-major RGBA channel bytes. */
    public record Decoded(int width, int height, byte[] rgba) {
        public Decoded {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("decoded extent must be positive: "
                    + width + "x" + height);
            }
            java.util.Objects.requireNonNull(rgba, "rgba");
            if (rgba.length != (long) width * height * 4) {
                throw new IllegalArgumentException("rgba length must be width*height*4");
            }
        }
    }

    /** Local checked decode failure; the caller maps this to SOURCE_DECODE_FAILED. */
    public static final class DecodeException extends Exception {

        private static final long serialVersionUID = 1L;

        public DecodeException(String reason) {
            super(reason);
        }
    }

    /** Decodes one PNG byte sequence to RGBA. Deterministic; throws on any failure. */
    public static Decoded decode(byte[] png) throws DecodeException {
        if (png == null) {
            throw new DecodeException("null image bytes");
        }
        BufferedImage read;
        try {
            read = ImageIO.read(new ByteArrayInputStream(png));
        } catch (IOException e) {
            throw new DecodeException("stream read failed");
        } catch (RuntimeException e) {
            throw new DecodeException("image reader failed");
        }
        if (read == null) {
            throw new DecodeException("no reader claimed these bytes");
        }
        int width = read.getWidth();
        int height = read.getHeight();
        if (width <= 0 || height <= 0) {
            throw new DecodeException("nonpositive decoded extent " + width + "x" + height);
        }
        if ((long) width * height > Integer.MAX_VALUE / 4L - 3L) {
            throw new DecodeException("decoded extent too large to buffer");
        }
        int[] packed = new int[width * height];
        read.getRGB(0, 0, width, height, packed, 0, width);
        byte[] rgba = new byte[packed.length * 4];
        for (int i = 0; i < packed.length; i++) {
            int pixel = packed[i];
            rgba[i * 4] = (byte) (pixel >>> 16);
            rgba[i * 4 + 1] = (byte) (pixel >>> 8);
            rgba[i * 4 + 2] = (byte) pixel;
            rgba[i * 4 + 3] = (byte) (pixel >>> 24);
        }
        return new Decoded(width, height, rgba);
    }
}
