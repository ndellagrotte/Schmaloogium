// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The activation/release outcome (PHASE_4_DOC §4.10). Never throws. {@code Activated}
 * guarantees program and provider locks active and the ordered participant sequence
 * completed; {@code FixedFunction} guarantees locks restored and fixed function bound;
 * {@code Skipped} is a mutation-free no-draw closed result; operational failure restores
 * fixed-function/vanilla-safe state; unprovable restoration is {@code FailedSafe};
 * {@code StalePublication} performs no GL work from a superseded view.
 */
public sealed interface BarrierResult {

    record Activated(
            ResolvedProgramDescriptor binding,
            java.util.List<BarrierParticipantResult.Degraded> degradations)
            implements BarrierResult {
    }

    record FixedFunction(java.util.List<ProgramSlotId> fallbackPath) implements BarrierResult {
    }

    record Skipped(ProgramSlotId requested) implements BarrierResult {
    }

    record ShadersOff(String diagnosticId) implements BarrierResult {
    }

    record FailedSafe(String diagnosticId) implements BarrierResult {
    }

    record StalePublication(long expectedGeneration, long currentGeneration)
            implements BarrierResult {
    }
}
