// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.SourceAttribution;

import com.schmaloogium.engine.pack.NormalizedPackPath;

/** Source-attributed coordinates; one-based physical line/column of a key's first code point. */
public record SourceAttribution(NormalizedPackPath source, int physicalLine, int physicalColumn) {

    public SourceAttribution {
        java.util.Objects.requireNonNull(source, "source");
        if (physicalLine < 1) {
            throw new IllegalArgumentException("physicalLine must be >= 1");
        }
        if (physicalColumn < 1) {
            throw new IllegalArgumentException("physicalColumn must be >= 1");
        }
    }
}
