// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed result of {@link ShaderService#initializeSamplerUnits} (PHASE_1_DOC §4.7.4,
 * D-P1-59). {@link Completed} requires every active assignment initialized and the exact
 * previous selection restored. {@link Failed} with {@code selectionRestored=true} permits
 * Phase 4's candidate cleanup/fallback; a failed or unprovable restoration returns
 * {@code Failed(detail, false)}, invalidates remembered selection and poisons shader
 * admission until safe backend recovery — it can never become a local successful
 * fallback. Backend exceptions are contained by this result.
 */
public sealed interface SamplerInitializationResult {

    /** Every active assignment initialized; exact previous selection restored. */
    record Completed() implements SamplerInitializationResult {
    }

    /** Failed; {@code selectionRestored} says whether the previous selection provably
     *  came back. {@code detail} preserves sanitized error detail. */
    record Failed(String detail, boolean selectionRestored) implements SamplerInitializationResult {
    }
}
