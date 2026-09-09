// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The effective provider's actual linked GEOMETRY input primitive (PHASE_4_DOC §2.2, §4.8).
 * Checked against Phase 3's expected input before candidate publication; {@link #NONE} means
 * no linked geometry stage (including the fixed-function sentinel) — never unknown,
 * unavailable or failed geometry. {@code legacyGeometry} in the state bundle is
 * provenance/configuration, not a geometry predicate. Detached views retain this enum
 * safely after close; it is not a draw capability.
 */
public enum GeometryInputRequirement {
    NONE,
    POINTS,
    LINES,
    LINES_ADJACENCY,
    TRIANGLES,
    TRIANGLES_ADJACENCY
}
