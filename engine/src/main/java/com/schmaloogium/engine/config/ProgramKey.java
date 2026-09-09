// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;


import com.schmaloogium.engine.pack.DimensionKey;

/** Dimension-qualified program identity ordered by dimension then name. */
public record ProgramKey(DimensionKey dimension, String programName)
        implements Comparable<ProgramKey> {

    public ProgramKey {
        java.util.Objects.requireNonNull(dimension, "dimension");
        java.util.Objects.requireNonNull(programName, "programName");
    }

    /** Dimension-major order, then unsigned-UTF-8 program name order. */
    @Override
    public int compareTo(ProgramKey other) {
        int byDimension = dimension.compareTo(other.dimension);
        return byDimension != 0 ? byDimension
            : EngineOptionData.compareUnsignedUtf8(programName, other.programName);
    }
}
