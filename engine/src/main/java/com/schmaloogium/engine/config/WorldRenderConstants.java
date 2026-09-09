// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** World-render constants; no superSamplingLevel field (D-P3-66). */
public record WorldRenderConstants(float sunPathRotation, float ambientOcclusionLevel) {

    public WorldRenderConstants {
        sunPathRotation = ViewportScale.canonical(sunPathRotation);
        ambientOcclusionLevel = ViewportScale.canonical(ambientOcclusionLevel);
        if (!Float.isFinite(sunPathRotation)) {
            throw new IllegalArgumentException("sunPathRotation must be finite");
        }
        if (!Float.isFinite(ambientOcclusionLevel)
                || ambientOcclusionLevel < 0.0f || ambientOcclusionLevel > 1.0f) {
            throw new IllegalArgumentException("ambientOcclusionLevel outside 0..1");
        }
    }
}
