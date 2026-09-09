// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.ProgramKey;

import com.schmaloogium.engine.pack.DimensionKey;

/** Dimension-qualified program identity ordered by dimension then name. */
public record ProgramKey(DimensionKey dimension, String programName) {

    public ProgramKey {
        java.util.Objects.requireNonNull(dimension, "dimension");
        java.util.Objects.requireNonNull(programName, "programName");
    }
}
