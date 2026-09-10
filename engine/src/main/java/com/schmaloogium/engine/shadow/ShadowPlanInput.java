// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import java.util.Objects;

/**
 * The complete pure planning input (PHASE_8_DOC §4.1, R7-13): policy, hook health and the
 * accepted-minima-derived {@code requested} boolean. Registry identity is never an input;
 * the structural triple is the whole planning identity, demand included even when policy
 * and health are equal.
 */
public record ShadowPlanInput(ShadowPolicy policy, ShadowHookHealth hookHealth, boolean requested) {

    public ShadowPlanInput {
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(hookHealth, "hookHealth");
    }
}
