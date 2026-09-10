// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.List;
import java.util.Objects;

/** Copied sprite animation sequence metadata: sequence order, never source-frame order. */
public record SpriteAnimationMetadata(List<AnimationFrameDescriptor> frames, boolean interpolate) {
    public SpriteAnimationMetadata {
        frames = AnimationFrameDescriptor.copyOf(frames);
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("animation metadata requires at least one frame");
        }
    }

    /** The nonanimated shape: one frame, no interpolation (§2.3 ordering law). */
    public static SpriteAnimationMetadata singleFrame(int sourceFrameIndex) {
        return new SpriteAnimationMetadata(
                List.of(new AnimationFrameDescriptor(sourceFrameIndex, 1)), false);
    }
}
