// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import java.util.Objects;

/**
 * Canonical SHA-256 hook-health fingerprint ({@code ShadowHookHealth/flattened-v3} domain).
 */
public record ShadowHookFingerprint(String canonicalSha256) {

    public ShadowHookFingerprint {
        Objects.requireNonNull(canonicalSha256, "canonicalSha256");
    }
}
