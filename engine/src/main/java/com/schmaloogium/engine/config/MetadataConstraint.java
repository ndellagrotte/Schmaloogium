// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.List;

/** Nonempty union of metadata intervals; endpoints 0..15. */
public record MetadataConstraint(List<IntegerRange> alternatives) {

    private static final int MAX_METADATA = 15;

    public MetadataConstraint {
        alternatives = List.copyOf(java.util.Objects.requireNonNull(alternatives, "alternatives"));
        if (alternatives.isEmpty()) {
            throw new IllegalArgumentException("alternatives must be non-empty");
        }
        for (IntegerRange r : alternatives) {
            if (r.upperInclusive() > MAX_METADATA) {
                throw new IllegalArgumentException("metadata endpoint above 15");
            }
        }
    }
}
