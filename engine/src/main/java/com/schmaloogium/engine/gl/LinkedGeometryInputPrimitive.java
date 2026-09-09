// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed linked-input primitive vocabulary (PHASE_1_DOC §4.7.4a, D-P1-48) — metadata and
 * log vocabulary, not new configurable primitive parameters.
 * {@link ShaderService#linkedGeometryInput} exposes exactly one of these for a geometry
 * program's immutable successful-link input requirement; {@code Optional.empty()} means
 * no geometry stage, never unknown.
 */
public enum LinkedGeometryInputPrimitive {
    POINTS,
    LINES,
    LINES_ADJACENCY,
    TRIANGLES,
    TRIANGLES_ADJACENCY
}
