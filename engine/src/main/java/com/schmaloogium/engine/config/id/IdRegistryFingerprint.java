// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * Opaque fingerprint of one live registry/tag projection (PHASE_9_DOC §2.3, §4.14).
 * This is Phase 9's own live ID-registry identity, deliberately distinct from Phase 4's
 * program-registry {@code RegistryFingerprint}, which stays opaque upstream identity in
 * normal composition (D-P9-18). A registry remap changes the value and therefore forces
 * a new snapshot and publication.
 */
public record IdRegistryFingerprint(String value) {

    public IdRegistryFingerprint {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("registry fingerprint must be non-empty");
        }
    }
}
