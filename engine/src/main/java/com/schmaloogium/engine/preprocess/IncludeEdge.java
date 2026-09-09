// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.pack.NormalizedPackPath;

import java.util.Optional;

/** One physical include location; missing targets keep an empty {@code included}. */
public record IncludeEdge(
        SourceId including,
        NormalizedPackPath requested,
        Optional<SourceId> included,
        int logicalLine) {

    public IncludeEdge {
        java.util.Objects.requireNonNull(including, "including");
        java.util.Objects.requireNonNull(requested, "requested");
        included = included == null ? Optional.empty() : included;
    }
}
