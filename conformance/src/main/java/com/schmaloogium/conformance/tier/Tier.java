// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.tier;

/**
 * The four harness tiers (§4.2, RESEARCH.md §8.2). T0 is implemented here and decided
 * manifest-only ({@link T0Evaluator}). T1 (renders plausibly, approved-baseline
 * comparison per §4.2.2/§4.7), T2 (classic-only OptiFine G6 parity, dual-spec packs are
 * refused per [D-P2-12], §4.8) and T3 (feature-complete, §4.2.4) are forward
 * references: their evaluators land with their evidence legs — live captures, baseline
 * approval, the feature protocol — none of which exist yet. They are named deferrals,
 * not implemented stubs.
 */
public enum Tier {
    T0,
    T1,
    T2,
    T3
}
