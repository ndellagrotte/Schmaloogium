// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import java.util.Objects;

/**
 * The canonical SHA-256 fingerprint of a ready plan (PHASE_8_DOC §2.2): every policy
 * value, the hook fingerprint and canonical {@code requested=true}. Never a registry
 * fingerprint or generation; absent/disabled results have no fabricated hash.
 */
public record ShadowPlanFingerprint(String canonicalSha256) {

    public ShadowPlanFingerprint {
        Objects.requireNonNull(canonicalSha256, "canonicalSha256");
    }
}
