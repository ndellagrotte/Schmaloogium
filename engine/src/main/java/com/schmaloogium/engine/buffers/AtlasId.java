// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Atlas identifier. Phase-13-owned content semantics: this immutable identity value participates
 * in Phase 13's texture digest.
 */
public record AtlasId(String value) {

    public AtlasId {
        java.util.Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must be non-blank");
        }
    }
}
