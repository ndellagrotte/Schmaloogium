// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

/**
 * The pure plan factory (PHASE_8_DOC §4.1). Accepts only the resolved projection; no
 * registry fingerprint/generation, source strings, property maps, Minecraft objects, GL
 * handles or mutable collections.
 */
public interface ShadowPlanFactory {

    ShadowPlanResult plan(ShadowPlanInput input);
}
