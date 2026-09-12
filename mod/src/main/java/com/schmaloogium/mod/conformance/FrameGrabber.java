// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ScreenShotHelper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

/**
 * §4.5.3 frame grab: reads the finished frame from Minecraft's own framebuffer through the
 * vanilla {@code ScreenShotHelper.createScreenshot} (C-3 clean — no LWJGL here), hashes the
 * ARGB raster in row-major big-endian order ([D-P2-16]) and writes a metadata-free PNG.
 * Render thread only, inside the H-CAPTURE-01 callback.
 */
final class FrameGrabber {

    record Grab(int width, int height, String pixelSha256) {
    }

    private FrameGrabber() {
    }

    static Grab grab(Minecraft mc, Path target) throws IOException {
        BufferedImage image = ScreenShotHelper.createScreenshot(mc.displayWidth, mc.displayHeight,
                mc.getFramebuffer());
        int w = image.getWidth();
        int h = image.getHeight();
        int[] argb = image.getRGB(0, 0, w, h, null, 0, w);
        String sha = pixelSha256(w, h, argb);
        BufferedImage canonical = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        canonical.setRGB(0, 0, w, h, argb, 0, w);
        Files.createDirectories(target.getParent());
        Path temp = target.resolveSibling(target.getFileName() + ".part");
        if (!ImageIO.write(canonical, "png", temp.toFile())) {
            throw new IOException("no PNG writer available");
        }
        Files.move(temp, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return new Grab(w, h, sha);
    }

    static String pixelSha256(int width, int height, int[] argb) {
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
        return TreeHash.hex(md.digest());
    }
}
