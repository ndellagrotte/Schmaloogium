// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

/**
 * Closed expected-geometry vocabulary (PHASE_10_DOC §2.2; PHASE_1_DOC §4.7.6): the
 * nonnull {@code expectedGeometryInput()} of every {@link VertexInputPlan}; LIVE_DRAW and
 * LIST_REPLAY_GUARD compare it to the actual private linked requirement before input
 * mutation.
 */
public enum VertexGeometryInput {
    NONE,
    POINTS,
    LINES,
    LINES_ADJACENCY,
    TRIANGLES,
    TRIANGLES_ADJACENCY
}
