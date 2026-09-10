// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.uniforms.Double3;

import java.util.Objects;

/**
 * The immutable shadow frame identity the driver lends with the exact driver frame id
 * (PHASE_7_DOC §5.1, Phase-8 R8-1): the main setupTerrain token, copied camera position and
 * the same-sample sky/sun angles — all from one accepted frame-begin sample. A mismatch
 * prevents the bridge from opening.
 */
public record ShadowFrameView(
        long worldEpoch,
        long frameId,
        float partialTicks,
        int mainTerrainFrameToken,
        Double3 cameraPosition,
        float skyAngle,
        float sunAngle) {

    public ShadowFrameView {
        Objects.requireNonNull(cameraPosition, "cameraPosition");
        if (!Float.isFinite(partialTicks) || partialTicks < 0f || partialTicks >= 1f) {
            throw new IllegalArgumentException("partialTicks must be finite in [0,1)");
        }
        if (!Float.isFinite(skyAngle) || skyAngle < 0f || skyAngle >= 1f) {
            throw new IllegalArgumentException("skyAngle must be finite in [0,1)");
        }
        if (!Float.isFinite(sunAngle) || sunAngle < 0f || sunAngle >= 1f) {
            throw new IllegalArgumentException("sunAngle must be finite in [0,1)");
        }
    }
}
