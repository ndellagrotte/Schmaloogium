// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.buffers.CompanionKind;

import java.util.List;
import java.util.Objects;

/**
 * One full companion atlas plan: the base atlas's exact extent and mip chain, one source per
 * base sprite for the kind, and the kind's missing-sprite default fill (§4.1.3/§4.1.4).
 */
public record CompanionAtlasPlan(
        AtlasId base, CompanionKind kind, int width, int height, int mipmapLevels,
        List<CompanionSpriteSource> sprites, int defaultFill) {
    public CompanionAtlasPlan {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(kind, "kind");
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("companion extent must be positive: "
                + width + "x" + height);
        }
        if (mipmapLevels < 0) {
            throw new IllegalArgumentException("mipmapLevels must be nonnegative: "
                + mipmapLevels);
        }
        Objects.requireNonNull(sprites, "sprites");
        sprites.forEach(Objects::requireNonNull);
        sprites = List.copyOf(sprites);
    }
}
