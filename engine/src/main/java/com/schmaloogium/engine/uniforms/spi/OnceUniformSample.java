// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.spi;

/**
 * The once-acquisition sample (PHASE_6_DOC §4.2). {@code nearPlane} must be exactly
 * {@code 0.05f} — the record exists to keep the value behind the provider seam, not to
 * make it configurable.
 */
public record OnceUniformSample(float nearPlane) {

    public OnceUniformSample {
        if (nearPlane != 0.05f) {
            throw new IllegalArgumentException("nearPlane must be exactly 0.05: " + nearPlane);
        }
    }
}
