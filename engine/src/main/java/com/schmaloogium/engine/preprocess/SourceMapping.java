// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import java.util.List;

public record SourceMapping(
        int startOffset, int endOffset, AttributedSourceLocation location,
        List<AttributedSourceLocation> expansionTrace) {

    public SourceMapping {
        java.util.Objects.requireNonNull(location, "location");
        expansionTrace = List.copyOf(expansionTrace);
    }
}
