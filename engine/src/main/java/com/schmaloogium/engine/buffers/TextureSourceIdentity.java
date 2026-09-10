// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Opaque Phase-13-owned source identity. Phase 5 never parses it.
 */
public record TextureSourceIdentity(String value) {

    public TextureSourceIdentity {
        java.util.Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must be non-blank");
        }
    }
}
