// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

public record PackInputLimits(
        int maxEntries,
        long maxTotalBytes,
        int maxPathLength,
        int maxNestingDepth) {

    public PackInputLimits {
        if (maxEntries <= 0 || maxTotalBytes <= 0 || maxPathLength <= 0 || maxNestingDepth <= 0) {
            throw new IllegalArgumentException("PackInputLimits fields must be strictly positive");
        }
    }
}
