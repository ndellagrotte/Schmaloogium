// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/** Closed per-draw scope rejection reasons (PHASE_9_DOC §4.12). */
public enum IdScopeRejection {
    /** Admission missing, revoked, or its producer is disabled for the frame. */
    STALE_ADMISSION,
    /** Admission generation does not echo the borrowed publication generation. */
    STALE_GENERATION,
    /** Current thread is not the admission's authenticated owner. */
    WRONG_THREAD,
    /** The fixed-capacity stack is full; the producer is disabled for the frame. */
    STACK_LIMIT
}
