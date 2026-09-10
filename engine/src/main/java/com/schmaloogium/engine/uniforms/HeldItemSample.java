// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/**
 * The held-item signal (PHASE_6_DOC §4.2/§4.12). Phase 9 constructs the pair: with
 * old-hand-light disabled {@code heldBlockLightValue} is the main-hand light; enabled it
 * is {@code max(main, off)}. {@code heldBlockLightValue2} is always the actual off-hand
 * value. Light values are 0…15; item IDs retain the full {@code int} alias domain. The
 * sample replaces the value for its logical tick. Immutable.
 */
public record HeldItemSample(
        long worldEpoch,
        long logicalTick,
        int heldItemId,
        int heldBlockLightValue,
        int heldItemId2,
        int heldBlockLightValue2) {

    public HeldItemSample {
        requireLight(heldBlockLightValue, "heldBlockLightValue");
        requireLight(heldBlockLightValue2, "heldBlockLightValue2");
    }

    private static void requireLight(int value, String name) {
        if (value < 0 || value > 15) {
            throw new IllegalArgumentException(name + " must be in 0..15: " + value);
        }
    }
}
