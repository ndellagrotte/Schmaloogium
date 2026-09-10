// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.spi;

/**
 * The tick-acquisition sample (PHASE_6_DOC §4.2). Identities must equal the call
 * arguments; {@code moonPhase} is 0…7 and {@code rainStrength} is finite in [0,1].
 * Immutable.
 */
public record TickUniformSample(
        long worldEpoch,
        long logicalTick,
        long worldTicks,
        int moonPhase,
        float rainStrength) {

    public TickUniformSample {
        if (moonPhase < 0 || moonPhase > 7) {
            throw new IllegalArgumentException("moonPhase must be in 0..7: " + moonPhase);
        }
        if (!Float.isFinite(rainStrength) || rainStrength < 0f || rainStrength > 1f) {
            throw new IllegalArgumentException("rainStrength must be finite in [0,1]: "
                    + rainStrength);
        }
    }
}
