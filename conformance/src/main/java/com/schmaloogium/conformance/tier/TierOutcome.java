// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.tier;

import java.util.List;

/**
 * The closed outcome vocabulary (§4.13): exactly these, no "n/a", nothing omitted.
 * {@code SKIPPED} and {@code NO_BASELINE} are image-tier outcomes; a tier is recorded
 * only with evidence ([D-P2-19]), and evidence that is absent or broken yields
 * {@code NOT_ATTEMPTED} — never a remembered pass.
 */
public enum TierOutcome {
    PASS,
    FAIL,
    SKIPPED,
    NO_BASELINE,
    NOT_ATTEMPTED
}
