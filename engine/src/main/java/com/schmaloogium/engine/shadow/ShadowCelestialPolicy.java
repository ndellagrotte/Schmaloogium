// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

/**
 * The stateless pure angular policy retained by a ready plan (PHASE_8_DOC §2.2): a
 * delegate to {@link CelestialMath#angles}, never an availability gate and never a
 * second formula.
 */
public interface ShadowCelestialPolicy {

    ShadowCelestialAngles sample(float sunAngle);
}
