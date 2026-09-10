// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import java.util.Objects;

/**
 * The registry-independent immutable plan (PHASE_8_DOC §2.2): complete policy, the
 * stateless delegating celestial policy, borrowed hook health and the canonical
 * fingerprint. No hidden registry identity; identical content across compilations is
 * legal and never revives a closed publication.
 */
public record ShadowPlan(
        ShadowPolicy policy,
        ShadowCelestialPolicy celestialPolicy,
        ShadowHookHealth hookHealth,
        ShadowPlanFingerprint fingerprint) {

    public ShadowPlan {
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(celestialPolicy, "celestialPolicy");
        Objects.requireNonNull(hookHealth, "hookHealth");
        Objects.requireNonNull(fingerprint, "fingerprint");
    }
}
