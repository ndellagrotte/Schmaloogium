// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import java.util.List;

/** One decoded physical document with original logical lines. */
public record SourceDocument(SourceId id, List<String> originalLogicalLines) {

    public SourceDocument {
        java.util.Objects.requireNonNull(id, "id");
        originalLogicalLines = List.copyOf(originalLogicalLines);
    }
}
