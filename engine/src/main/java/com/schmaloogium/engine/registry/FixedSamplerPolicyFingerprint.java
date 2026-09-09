// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * Exact identity of the Phase-5-owned fixed sampler policy (PHASE_4_DOC §4.7): stable,
 * non-null, and identifying the same schema/table used by Phase 5's resolver.
 */
public record FixedSamplerPolicyFingerprint(String value) {

    public FixedSamplerPolicyFingerprint {
        java.util.Objects.requireNonNull(value, "value");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("policy fingerprint must be non-empty");
        }
    }
}
