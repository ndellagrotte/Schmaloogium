// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Half-life constants in ticks; {@code dryness} stays distinct from {@code wetness} (B1). */
public record SmoothingConstants(float wetnessHalfLifeTicks, float drynessHalfLifeTicks,
        float eyeBrightnessHalfLifeTicks, float centerDepthHalfLifeTicks) {
}
