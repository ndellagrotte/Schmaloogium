// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed legacy-geometry input domain (PHASE_1_DOC §4.7.4, §0.25): exactly
 * {@code TRIANGLES}. The exact engine domains for
 * {@link ShaderService#configureLegacyGeometry} are TRIANGLES and TRIANGLE_STRIP; the
 * operation does not parse GLSL or choose topology (D-P1-44).
 */
public enum LegacyGeometryInputPrimitive {
    TRIANGLES
}
