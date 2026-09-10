// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.List;
import java.util.Objects;

/** One copied animation source frame: its index and tick duration. */
public record AnimationFrameDescriptor(int sourceFrameIndex, int durationTicks) {
    public AnimationFrameDescriptor {
        if (sourceFrameIndex < 0) {
            throw new IllegalArgumentException("sourceFrameIndex must be nonnegative: "
                + sourceFrameIndex);
        }
        if (durationTicks <= 0) {
            throw new IllegalArgumentException("durationTicks must be positive: "
                + durationTicks);
        }
    }

    public static List<AnimationFrameDescriptor> copyOf(List<AnimationFrameDescriptor> frames) {
        Objects.requireNonNull(frames, "frames");
        frames.forEach(Objects::requireNonNull);
        return List.copyOf(frames);
    }
}
