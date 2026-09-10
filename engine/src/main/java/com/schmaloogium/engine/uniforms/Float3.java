// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/** Immutable by-value float 3-vector (PHASE_6_DOC §4.2). Finite components. */
public record Float3(float x, float y, float z) {

    public Float3 {
        if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) {
            throw new IllegalArgumentException("components must be finite");
        }
    }

    /** The zero vector — the pending-value neutral for celestial vectors. */
    public static Float3 zero() {
        return new Float3(0f, 0f, 0f);
    }
}
