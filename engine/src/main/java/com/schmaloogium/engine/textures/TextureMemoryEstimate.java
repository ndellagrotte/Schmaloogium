// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

/** Planning-side memory estimate (§4.8). Estimates are data, not limits. */
public record TextureMemoryEstimate(long companionBytes, long noiseBytes, long customBytes) {
    public TextureMemoryEstimate {
        if (companionBytes < 0 || noiseBytes < 0 || customBytes < 0) {
            throw new IllegalArgumentException("memory estimate components must be nonnegative");
        }
    }
}
