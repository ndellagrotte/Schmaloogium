// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Opaque effective-parameterization fingerprint; identity uses the phase13.parameters/v2 domain.
 * Phase 13 owns its content.
 */
public record TextureParameterFingerprint(String value) {

    public TextureParameterFingerprint {
        java.util.Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must be non-blank");
        }
    }
}
