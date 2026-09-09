// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Set;

/** Extended vertex attribute opt-ins, enum-ordered. */
public record VertexRequirements(Set<VertexAttribute> attributes) {

    public VertexRequirements {
        java.util.SortedSet<VertexAttribute> ordered =
            java.util.Collections.unmodifiableSortedSet(new java.util.TreeSet<>(attributes));
        attributes = ordered;
    }
}
