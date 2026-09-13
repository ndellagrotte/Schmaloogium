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
        ShadowPcfPolicy pcf,
        ShadowContent content) {

    /**
     * The v0.2 content switches ({@code shadowTerrain}, {@code shadowEntities},
     * {@code shadowBlockEntities}, {@code shadowPlayer} in shaders.properties; every one
     * defaults to true). Terrain/entities gate the slot's draw calls; block entities and
     * the player are filtered inside vanilla's entity traversal by the mod-side guards.
     */
    public record ShadowContent(boolean terrain, boolean entities, boolean blockEntities,
            boolean player) {
        public static final ShadowContent ALL = new ShadowContent(true, true, true, true);
    }

    public ShadowPolicy {
        Objects.requireNonNull(shadowMapFov, "shadowMapFov");
        Objects.requireNonNull(mipmaps, "mipmaps");
        Objects.requireNonNull(pcf, "pcf");
        Objects.requireNonNull(content, "content");
    }

    /** The pre-Task-E shape: every content switch on. */
    public ShadowPolicy(OptionalFloat shadowMapFov, float shadowDistance,
            float shadowDistanceRenderMul, float shadowIntervalSize, float sunPathRotationDegrees,
            boolean shadowTranslucent, boolean cloudsInShadow, ShadowMipmapPolicy mipmaps,
            ShadowPcfPolicy pcf) {
        this(shadowMapFov, shadowDistance, shadowDistanceRenderMul, shadowIntervalSize,
                sunPathRotationDegrees, shadowTranslucent, cloudsInShadow, mipmaps, pcf,
                ShadowContent.ALL);
    }
}
