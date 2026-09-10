// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.AtlasId;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * The copied base-atlas descriptor: exact accepted extent and mip count, sprite order
 * ascending iconName by unsigned UTF-8 bytes (§2.3 ordering law).
 */
public record AtlasDescriptor(
        AtlasId id, int width, int height, int mipmapLevels, List<SpriteDescriptor> sprites) {

    /** Canonical unsigned UTF-8 sprite order by iconName. */
    public static final Comparator<SpriteDescriptor> SPRITE_ORDER =
        (a, b) -> unsignedUtf8Compare(a.iconName(), b.iconName());

    public AtlasDescriptor {
        Objects.requireNonNull(id, "id");
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("atlas extent must be positive: "
                + width + "x" + height);
        }
        if (mipmapLevels < 0) {
            throw new IllegalArgumentException("mipmapLevels must be nonnegative: "
                + mipmapLevels);
        }
        Objects.requireNonNull(sprites, "sprites");
        sprites.forEach(Objects::requireNonNull);
        List<SpriteDescriptor> ordered = List.copyOf(sprites);
        for (int i = 1; i < ordered.size(); i++) {
            if (SPRITE_ORDER.compare(ordered.get(i - 1), ordered.get(i)) > 0) {
                throw new IllegalArgumentException("sprites must be in canonical order");
            }
        }
        sprites = ordered;
    }

    static int unsignedUtf8Compare(String a, String b) {
        byte[] ab = a.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bb = b.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        int shared = Math.min(ab.length, bb.length);
        for (int i = 0; i < shared; i++) {
            int cmp = (ab[i] & 0xFF) - (bb[i] & 0xFF);
            if (cmp != 0) {
                return cmp;
            }
        }
        return ab.length - bb.length;
    }
}
