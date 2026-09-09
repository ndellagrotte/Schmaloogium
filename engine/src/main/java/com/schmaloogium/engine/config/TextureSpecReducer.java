// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.NormalizedPackPath;
import java.util.List;
import java.util.Optional;

/**
 * Reduces one decoded texture.* value into an executable CustomTextureSpec;
 * null means the executable effect is omitted (declaration is retained separately).
 */
public final class TextureSpecReducer {

    private TextureSpecReducer() {
    }

    public static CustomTextureSpec reduce(TextureBindingKey key, String[] tokens) {
        if (tokens.length == 0) {
            return null;
        }
        if (tokens[0].startsWith("minecraft:")) {
            if (tokens.length != 1) {
                return null;
            }
            return new CustomTextureSpec.MinecraftResource(key, tokens[0]);
        }
        if (tokens.length == 1) {
            String path = tokens[0];
            if (!path.endsWith(".png") || path.length() <= ".png".length()
                    || path.endsWith("/.png")) {
                return null; // case-sensitive lowercase .png with non-empty stem
            }
            NormalizedPackPath image;
            try {
                image = new NormalizedPackPath(path);
            } catch (IllegalArgumentException e) {
                return null;
            }
            return new CustomTextureSpec.PackPath(key, image, Optional.empty());
        }
        // raw form: <path> <target> <internalFormat> <dimensions...> <pixelFormat> <pixelType>
        return reduceRaw(key, tokens);
    }

    private static CustomTextureSpec reduceRaw(TextureBindingKey key, String[] tokens) {
        if (tokens.length < 6) {
            return null;
        }
        String path = tokens[0];
        TextureTarget target;
        int expected;
        switch (tokens[1]) {
            case "TEXTURE_1D" -> {
                target = TextureTarget.TEXTURE_1D;
                expected = 6;
            }
            case "TEXTURE_2D" -> {
                target = TextureTarget.TEXTURE_2D;
                expected = 7;
            }
            case "TEXTURE_3D" -> {
                target = TextureTarget.TEXTURE_3D;
                expected = 8;
            }
            case "TEXTURE_RECTANGLE" -> {
                target = TextureTarget.RECTANGLE;
                expected = 7;
            }
            default -> {
                return null;
            }
        }
        if (tokens.length != expected) {
            return null;
        }
        ColorInternalFormat internalFormat;
        try {
            internalFormat = ColorInternalFormat.valueOf(tokens[2]);
        } catch (IllegalArgumentException e) {
            return null;
        }
        int dimensionCount = expected - 5;
        List<Integer> dimensions = new java.util.ArrayList<>(dimensionCount);
        for (int i = 0; i < dimensionCount; i++) {
            int d;
            try {
                d = Integer.parseInt(tokens[3 + i]);
            } catch (NumberFormatException e) {
                return null;
            }
            if (d <= 0) {
                return null;
            }
            dimensions.add(d);
        }
        PixelFormat pixelFormat;
        PixelType pixelType;
        try {
            pixelFormat = PixelFormat.valueOf(tokens[3 + dimensionCount]);
            pixelType = PixelType.valueOf(tokens[4 + dimensionCount]);
        } catch (IllegalArgumentException e) {
            return null;
        }
        boolean integerInternal = internalFormat.name().endsWith("I");
        boolean integerTransfer = pixelFormat.name().endsWith("_INTEGER");
        if (integerInternal != integerTransfer) {
            return null;
        }
        NormalizedPackPath bytes;
        try {
            bytes = new NormalizedPackPath(path);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return new CustomTextureSpec.Raw(key, bytes, target, internalFormat, dimensions,
            pixelFormat, pixelType, Optional.empty());
    }
}
