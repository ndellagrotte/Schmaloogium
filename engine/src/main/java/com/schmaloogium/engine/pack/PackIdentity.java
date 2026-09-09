// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.pack.PackIdentity;

import java.util.Map;

/** Selected validated root plus canonical-path-ordered opaque content hashes. */
public record PackIdentity(
        NormalizedPackPath selectedRoot,
        Map<NormalizedPackPath, String> contentHashes) {

    public PackIdentity {
        java.util.Objects.requireNonNull(selectedRoot, "selectedRoot");
        java.util.SortedMap<NormalizedPackPath, String> ordered =
            new java.util.TreeMap<>(NormalizedPackPath.ORDER);
        ordered.putAll(java.util.Objects.requireNonNull(contentHashes, "contentHashes"));
        contentHashes = java.util.Collections.unmodifiableSortedMap(ordered);
    }
}
