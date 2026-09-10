// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.catalog;

/**
 * Closed uniform value-type domain of the built-in catalog (PHASE_6_DOC §4.4). Exact
 * GLSL pack-facing types; no vec2/vec3-int hybrids exist in Appendix D.
 */
public enum BuiltInUniformType {
    INT,
    FLOAT,
    IVEC2,
    VEC3,
    VEC4,
    IVEC4,
    MAT4
}
