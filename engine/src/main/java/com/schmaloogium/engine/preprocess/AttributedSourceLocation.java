// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

public record AttributedSourceLocation(SourceId source, int logicalLine, int column) {

    public AttributedSourceLocation {
        java.util.Objects.requireNonNull(source, "source");
        if (logicalLine < 1) {
            throw new IllegalArgumentException("logicalLine must be >= 1");
        }
        if (column < 1) {
            throw new IllegalArgumentException("column must be >= 1");
        }
    }
}
