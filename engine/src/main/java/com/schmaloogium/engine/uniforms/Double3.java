// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/** Immutable by-value double-precision 3-vector (PHASE_6_DOC §4.2). Finite components. */
public record Double3(double x, double y, double z) {

    public Double3 {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("components must be finite");
        }
    }
}
