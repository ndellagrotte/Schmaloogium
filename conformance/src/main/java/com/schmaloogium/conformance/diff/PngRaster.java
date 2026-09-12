// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.diff;

import com.schmaloogium.conformance.wire.Hashes;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.imageio.ImageIO;

/**
 * A decoded image as one {@code int[]} ARGB raster (§7: never per-pixel {@code getRGB})
 * plus its [D-P2-16] identity: SHA-256 over the raster in row-major order, four big-endian
 * bytes (A, R, G, B) per pixel. An encoder change can therefore never look like a rendering
 * change. The same definition is implemented by the client-side grabber.
 */
public record PngRaster(int width, int height, int[] argb) {

    public PngRaster {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("raster dimensions must be positive");
        }
        Objects.requireNonNull(argb, "argb");
        if (argb.length != width * height) {
            throw new IllegalArgumentException("raster length " + argb.length + " != " + width + "x" + height);
        }
    }

    public static PngRaster of(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] argb = image.getRGB(0, 0, w, h, null, 0, w);
        return new PngRaster(w, h, argb);
    }

    public static PngRaster read(Path png) throws IOException {
        try (InputStream in = Files.newInputStream(png)) {
            BufferedImage image = ImageIO.read(in);
            if (image == null) {
                throw new IOException("not a decodable image: " + png);
            }
            return of(image);
        }
    }

    /** The [D-P2-16] pixel identity. */
    public String pixelSha256() {
        return pixelSha256(width, height, argb);
    }

    public static String pixelSha256(int width, int height, int[] argb) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        ByteBuffer row = ByteBuffer.allocate(width * 4);
        for (int y = 0; y < height; y++) {
            row.clear();
            int base = y * width;
            for (int x = 0; x < width; x++) {
                row.putInt(argb[base + x]);
            }
            md.update(row.array(), 0, width * 4);
        }
        return Hashes.hex(md.digest());
    }

    public int pixel(int x, int y) {
        return argb[y * width + x];
    }
}
