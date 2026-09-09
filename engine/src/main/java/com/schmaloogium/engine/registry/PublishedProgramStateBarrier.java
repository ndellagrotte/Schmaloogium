// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The non-owning published barrier view (PHASE_4_DOC §2.2). Render-thread-only; every method
 * first verifies that both its generation and identity still match the current ready
 * publication — a replaced view returns StalePublication without a GL call or barrier-state
 * change. Deliberately no close operation; snapshot consumers have no teardown capability.
 */
public interface PublishedProgramStateBarrier {

    long generation();

    ProgramSelectionResult select(ProgramSlotId requested, BarrierContext context);

    BarrierResult activate(UseProgramRequest request);

    BarrierResult releaseToFixedFunction(BarrierContext context);
}
