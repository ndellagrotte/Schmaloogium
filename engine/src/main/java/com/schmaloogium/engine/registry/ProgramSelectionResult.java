// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The selection outcome (PHASE_4_DOC §2.2). {@code Selected} privately retains the
 * already-resolved binding and exact context identity; fixed terminals return Selected with
 * the explicit FixedFunctionEmpty layout. Invalid request/issuer/context and unavailable
 * required selection return ShadersOff, mutation-free; a stale wrapper returns
 * StalePublication.
 */
public sealed interface ProgramSelectionResult {

    record Selected(ProgramBindingSelection selection) implements ProgramSelectionResult {
    }

    record Skipped(ProgramSlotId requested) implements ProgramSelectionResult {
    }

    record StalePublication(long expectedGeneration, long currentGeneration)
            implements ProgramSelectionResult {
    }

    record ShadersOff(String diagnosticId) implements ProgramSelectionResult {
    }
}
