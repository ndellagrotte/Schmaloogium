// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.List;
import java.util.Objects;

/**
 * One sprite's post-vanilla animation state row (PHASE_13_DOC §4.1.7): the sequence
 * position, the current and next source-frame indices, elapsed ticks in the current
 * frame's duration, and the exact next-frame weight the glue interpolated. Row values are
 * copied verbatim from the hook — they are not validated here; {@code AtlasAnimationSupport.validate}
 * is the arbiter that turns corrupt rows into discard/fallback verdicts.
 */
public record SpriteAnimationState(
        String iconName, int sequencePosition, int currentSourceFrameIndex,
        int nextSourceFrameIndex, int elapsedTicks, int currentDuration,
        double nextFrameWeight) {

    public SpriteAnimationState {
        Objects.requireNonNull(iconName, "iconName");
    }
}
