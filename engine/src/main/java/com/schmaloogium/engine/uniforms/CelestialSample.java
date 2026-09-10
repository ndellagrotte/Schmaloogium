// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import java.util.Objects;

/**
 * The celestial-rotation signal (PHASE_6_DOC §4.2/§4.12). Eye-space sun/moon/shadow-light
 * and world-up vectors captured inside the sky rotation. Values are Phase 8's at v0.2;
 * the interface and cells exist at v0.1 with zero-vector neutrals. Immutable.
 */
public record CelestialSample(
        long worldEpoch,
        long frameId,
        Float3 sunPosition,
        Float3 moonPosition,
        Float3 shadowLightPosition,
        Float3 upPosition) {

    public CelestialSample {
        Objects.requireNonNull(sunPosition, "sunPosition");
        Objects.requireNonNull(moonPosition, "moonPosition");
        Objects.requireNonNull(shadowLightPosition, "shadowLightPosition");
        Objects.requireNonNull(upPosition, "upPosition");
    }
}
