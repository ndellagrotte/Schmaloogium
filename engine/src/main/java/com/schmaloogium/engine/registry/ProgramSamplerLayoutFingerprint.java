// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/** Exact identity of one canonical sampler projection (PHASE_4_DOC §4.7). */
public record ProgramSamplerLayoutFingerprint(String value) {

    public ProgramSamplerLayoutFingerprint {
        java.util.Objects.requireNonNull(value, "value");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("sampler layout fingerprint must be non-empty");
        }
    }
}
