// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.NormalizedPackPath;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Reduces one decoded texture.* value into an executable CustomTextureSpec;
 * null means the executable effect is omitted (declaration is retained separately).
 *
 * <p>Declared image/byte tokens are pack-relative to the selected shaders root — the
 * directory holding {@code shaders.properties} — so they are resolved against that root
 * before becoming the {@link NormalizedPackPath} Phase 13 acquires from the same-load
 * asset snapshot. An adjacent {@code <path>.mcmeta} that the pack actually declares is
 * retained as the source's {@link TextureSidecarRef}; a reference is never issued for a
 * file outside the declared domain, because Phase 13 acquires it unconditionally.
 */
public final class TextureSpecReducer {

    private TextureSpecReducer() {
    }

    public static CustomTextureSpec reduce(TextureBindingKey key, String[] tokens,
            String rootPrefix, Set<NormalizedPackPath> declaredAssets) {
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
            NormalizedPackPath image = imagePath(tokens[0], rootPrefix);
            if (image == null) {
                return null;
            }
            return new CustomTextureSpec.PackPath(key, image, sidecar(image, declaredAssets));
        }
        // raw form: <path> <target> <internalFormat> <dimensions...> <pixelFormat> <pixelType>
        return reduceRaw(key, tokens, rootPrefix, declaredAssets);
    }

    /**
     * Reduces the {@code texture.noise=<path>} override value; null means the declaration
     * is not executable and the generated texture stands.
     */
    public static NoiseTextureSpec reduceNoise(String[] tokens, String rootPrefix,
            Set<NormalizedPackPath> declaredAssets) {
        if (tokens.length != 1) {
            return null;
        }
        NormalizedPackPath image = imagePath(tokens[0], rootPrefix);
        if (image == null) {
            return null;
        }
        return new NoiseTextureSpec.Override(image, sidecar(image, declaredAssets));
    }

    /** The case-sensitive lowercase {@code .png} rule, then root resolution. */
    private static NormalizedPackPath imagePath(String token, String rootPrefix) {
        if (!token.endsWith(".png") || token.length() <= ".png".length()
                || token.endsWith("/.png")) {
            return null;
        }
        return packPath(token, rootPrefix);
    }

    private static NormalizedPackPath packPath(String token, String rootPrefix) {
        try {
            return new NormalizedPackPath(
                rootPrefix.isEmpty() ? token : rootPrefix + "/" + token);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Optional<TextureSidecarRef> sidecar(NormalizedPackPath image,
            Set<NormalizedPackPath> declaredAssets) {
        NormalizedPackPath meta = new NormalizedPackPath(image.canonicalString() + ".mcmeta");
        return declaredAssets.contains(meta) ? Optional.of(new TextureSidecarRef(meta))
            : Optional.empty();
    }

    private static CustomTextureSpec reduceRaw(TextureBindingKey key, String[] tokens,
            String rootPrefix, Set<NormalizedPackPath> declaredAssets) {
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
        NormalizedPackPath bytes = packPath(path, rootPrefix);
        if (bytes == null) {
            return null;
        }
        return new CustomTextureSpec.Raw(key, bytes, target, internalFormat, dimensions,
            pixelFormat, pixelType, sidecar(bytes, declaredAssets));
    }
}
