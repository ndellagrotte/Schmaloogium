// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The shadow slot invocation seam consumed by the driver at H-FRAME-05 (PHASE_7_DOC §5.1).
 * Absent until Phase 8 constructs a pipeline; every real invocation is one of the exact
 * closed results.
 */
public interface ShadowInvocationSlot {

    /** The epoch this slot instance was published with. */
    ShadowSlotEpoch slotEpoch();

    ShadowInvocationResult invoke(ShadowInvocationContext context);
}
