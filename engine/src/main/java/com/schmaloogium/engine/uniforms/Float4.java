// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/** Immutable by-value float 4-vector (PHASE_6_DOC §4.2). Finite components. */
public record Float4(float x, float y, float z, float w) {

    public Float4 {
        if (!Float.isFinite(x) || !Float.isFinite(y)
                || !Float.isFinite(z) || !Float.isFinite(w)) {
            throw new IllegalArgumentException("components must be finite");
        }
    }

    /** The zero vector — the neutral outside a scoped {@code entityColor} producer. */
    public static Float4 zero() {
        return new Float4(0f, 0f, 0f, 0f);
    }
}
