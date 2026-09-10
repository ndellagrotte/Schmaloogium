// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The observable per-frame machine states (PHASE_7_DOC §4.2). This is the public face of
 * the driver's internal phase; IDLE, the abort states and shaders-off recovery are internal
 * because no hook ever observes them as an {@code Advanced} value.
 */
public enum FrameState {
    SAMPLED,
    BUFFER_OPEN,
    MATRICES_CAPTURED,
    ESTATE_CLEARED,
    SHADOW_DONE,
    GBUFFERS,
    DEFERRED_DONE,
    GBUFFERS_TRANS,
    FINALIZING,
    COMMITTED
}
