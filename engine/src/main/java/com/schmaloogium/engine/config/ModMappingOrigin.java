// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Phase-9 mod contribution origin. */
public record ModMappingOrigin(
        String modId, int contributionOrdinal, String sourceName) implements MappingOrigin {

    public ModMappingOrigin {
        java.util.Objects.requireNonNull(modId, "modId");
        if (modId.isEmpty()) {
            throw new IllegalArgumentException("modId must be non-empty");
        }
        if (contributionOrdinal < 0) {
            throw new IllegalArgumentException("contributionOrdinal must be >= 0");
        }
        java.util.Objects.requireNonNull(sourceName, "sourceName");
        if (sourceName.isEmpty()) {
            throw new IllegalArgumentException("sourceName must be non-empty");
        }
    }
}
