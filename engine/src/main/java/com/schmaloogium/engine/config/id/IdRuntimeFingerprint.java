// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * Opaque complete identity of one derived ID runtime (PHASE_9_DOC §2.4, §4.14): exactly
 * one Phase 3 schema and mapping identity, one selected-list discriminator per
 * contribution, one registry fingerprint and generation, one mod-source fingerprint, one
 * alias/tag catalog version, and the resolved hand-light policy. A consumer never
 * combines components from different identities; equal fingerprints never authorize
 * pairing an old ordinal map with a new lookup.
 */
public record IdRuntimeFingerprint(String value) {

    public IdRuntimeFingerprint {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("runtime fingerprint must be non-empty");
        }
    }
}
