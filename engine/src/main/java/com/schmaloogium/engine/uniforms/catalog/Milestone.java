// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.catalog;

/**
 * Governing milestone of a built-in's VALUE producer (PHASE_6_DOC §4.4/§9). The
 * interface exists at v0.1 for everything; a program declaring a value whose producer
 * milestone has not landed receives the documented neutral plus a once-per-pack warning.
 * Neutrals are never claimed as feature support.
 */
public enum Milestone {
    V0_1,
    V0_2,
    V0_3,
    V0_5
}
