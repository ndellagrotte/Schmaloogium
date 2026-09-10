// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import java.util.Objects;

/**
 * The fog signal (PHASE_6_DOC §4.2/§4.12): {@code fogMode} encoded by {@code mod.glue}
 * into the legacy numeric domain, the clamped density and the current linear RGB color.
 * {@code fogMode} and blend factors retain their full {@code int} domains. Immutable.
 */
public record FogSample(
        long worldEpoch,
        long frameId,
        int fogMode,
        float density,
        Float3 color) {

    public FogSample {
        Objects.requireNonNull(color, "color");
        if (!Float.isFinite(density) || density < 0f || density > 1f) {
            throw new IllegalArgumentException("density must be finite in [0,1]: " + density);
        }
    }
}
