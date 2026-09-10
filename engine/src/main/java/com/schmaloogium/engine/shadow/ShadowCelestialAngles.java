// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

/**
 * The pure angular celestial result (PHASE_8_DOC §4.5.1): day/night selection, the Phase 6
 * {@code shadowAngle} uniform value in [0,1), and the model-view rotation {@code theta} in
 * radians. No world, frame, or camera state.
 */
public record ShadowCelestialAngles(boolean day, float shadowAngle, double thetaRadians) {
}
