// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.Objects;

/**
 * The registry-wide fingerprint (PHASE_4_DOC §4.11): an opaque SHA-256 digest over the
 * configuration/profile/dimension identity, evaluated option snapshot, materialization and
 * layout fingerprints, policy identity, geometry requests, capability fields and the
 * resolution projection. Equality — never ordering — is the cache protocol.
 */
public record RegistryFingerprint(String value) {

    public RegistryFingerprint {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("fingerprint value must be non-blank");
        }
    }

    @Override
    public String toString() {
        return "RegistryFingerprint[" + value + "]";
    }
}
