// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * One engine enum constant per {@link StateService} verb above {@code snapshot}
 * (PHASE_1_DOC §4.7.4) — the enumeration §4.7.4 derives from RESEARCH.md §4.3/§4.4 and
 * App F.7. Names the aspects {@link StateService#snapshot} captures and
 * {@link StateService#restore} puts back; programs are NOT among them (§4.7.4a).
 */
public enum StateAspect {
    VIEWPORT,
    CLEAR_COLOR,
    CLEAR,
    DEPTH_MASK,
    DEPTH_TEST,
    BLEND,
    ALPHA_TEST,
    FOG
}
