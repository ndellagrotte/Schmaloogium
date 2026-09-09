// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The use-program state barrier (PHASE_4_DOC §4.10). Selection and activation are distinct
 * normative operations; every method is render-thread-only and never throws.
 */
public interface ProgramStateBarrier {

    ProgramSelectionResult select(ProgramSlotId requested, BarrierContext context);

    BarrierResult activate(UseProgramRequest request);

    BarrierResult releaseToFixedFunction(BarrierContext context);
}
