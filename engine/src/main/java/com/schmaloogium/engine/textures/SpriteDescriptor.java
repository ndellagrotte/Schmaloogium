// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.Objects;

/**
 * One copied base-atlas sprite: atlas-local placement, frame count and immutable animation
 * metadata. No Minecraft object crosses this boundary.
 */
public record SpriteDescriptor(
        String iconName, int originX, int originY, int width, int height,
        int frameCount, boolean animated, SpriteAnimationMetadata animation) {
    public SpriteDescriptor {
        Objects.requireNonNull(iconName, "iconName");
        if (iconName.isEmpty()) {
            throw new IllegalArgumentException("iconName must be non-empty");
        }
        if (originX < 0 || originY < 0) {
            throw new IllegalArgumentException("sprite origins must be nonnegative: "
                + originX + ", " + originY);
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("sprite extent must be positive: "
                + width + "x" + height);
        }
        if (frameCount <= 0) {
            throw new IllegalArgumentException("frameCount must be positive: " + frameCount);
        }
        Objects.requireNonNull(animation, "animation");
    }

    public static SpriteDescriptor staticSprite(
            String iconName, int originX, int originY, int width, int height) {
        return new SpriteDescriptor(iconName, originX, originY, width, height,
            1, false, SpriteAnimationMetadata.singleFrame(0));
    }
}
