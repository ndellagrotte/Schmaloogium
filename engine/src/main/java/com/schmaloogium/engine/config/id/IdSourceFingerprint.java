// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * Opaque identity of the mapping-side inputs (PHASE_9_DOC §4.7): the Phase 3 schema and
 * mapping-file fingerprints, each contribution's selected-list discriminator, the
 * mod-source corpus fingerprint, and the alias/tag catalog versions. Warn-once keys are
 * scoped by this identity plus the registry fingerprint, so a new generation with
 * unchanged inputs does not repeat warnings while a genuinely changed input may.
 */
public record IdSourceFingerprint(String value) {

    public IdSourceFingerprint {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("source fingerprint must be non-empty");
        }
    }
}
