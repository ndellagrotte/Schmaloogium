// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed legacy-geometry output domain (PHASE_1_DOC §4.7.4, §0.25): exactly
 * {@code TRIANGLE_STRIP}. {@code maxVerticesOut} is a positive Java int, copied exactly
 * from the Phase-4-validated Phase 3 declaration (D-P1-44).
 */
public enum LegacyGeometryOutputPrimitive {
    TRIANGLE_STRIP
}
