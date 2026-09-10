// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.buffers.ShadowMipmapPolicy;
import com.schmaloogium.engine.uniforms.OptionalFloat;

import java.util.Objects;

/**
 * The complete effective configuration-derived policy projection (PHASE_8_DOC §2.2).
 * Every field is the resolved Phase-3 value, frozen before provider construction; the
 * plan factory validates the feature-relevant fields and never clamps them into a
 * different pack contract. {@code mipmaps} is Phase-5-owned and consumed unchanged;
 * {@code pcf} is the Phase-8-local compare-depth policy.
 */
public record ShadowPolicy(
        OptionalFloat shadowMapFov,
        float shadowDistance,
        float shadowDistanceRenderMul,
        float shadowIntervalSize,
        float sunPathRotationDegrees,
        boolean shadowTranslucent,
        boolean cloudsInShadow,
        ShadowMipmapPolicy mipmaps,
        ShadowPcfPolicy pcf) {

    public ShadowPolicy {
        Objects.requireNonNull(shadowMapFov, "shadowMapFov");
        Objects.requireNonNull(mipmaps, "mipmaps");
        Objects.requireNonNull(pcf, "pcf");
    }
}
