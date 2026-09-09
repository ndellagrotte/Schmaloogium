// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.pack.DimensionKey;

/** Root-only contextual source key. */
public record SourceKey(
        DimensionKey dimension,
        String programName,
        ShaderSourceStage stage,
        SourceId source) {

    public SourceKey {
        java.util.Objects.requireNonNull(dimension, "dimension");
        java.util.Objects.requireNonNull(programName, "programName");
        java.util.Objects.requireNonNull(stage, "stage");
        java.util.Objects.requireNonNull(source, "source");
    }
}
