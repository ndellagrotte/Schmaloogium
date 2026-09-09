// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * Exact linked-declaration identity of a merged uniform layout (PHASE_4_DOC §4.7): hashes a
 * schema tag plus canonical exact-name order, Phase 3 structural type values, ordered sites
 * and their materialization fingerprints. Contains no GL handle, location, post-link
 * activity, generation or object identity; equal merged inputs produce equal fingerprints.
 */
public record ProgramUniformLayoutFingerprint(String value) {

    public ProgramUniformLayoutFingerprint {
        java.util.Objects.requireNonNull(value, "value");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("uniform layout fingerprint must be non-empty");
        }
    }
}
