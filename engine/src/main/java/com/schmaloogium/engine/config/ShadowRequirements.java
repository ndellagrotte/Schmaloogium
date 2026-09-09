// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Set;
import java.util.Optional;

/** Shadow projection/filter/mipmap requirements. */
public record ShadowRequirements(int resolution, Optional<Float> fov, float distance,
        float distanceRenderMultiplier, float intervalSize,
        Set<ShadowTextureKey> mipmapped, Set<ShadowTextureKey> nearest,
        Set<ShadowDepthKey> hardwarePcf) {

    public ShadowRequirements {
        fov = fov == null ? Optional.empty() : fov;
        mipmapped = java.util.Collections.unmodifiableSet(
            java.util.EnumSet.copyOf(mipmapped));
        nearest = java.util.Collections.unmodifiableSet(java.util.EnumSet.copyOf(nearest));
        hardwarePcf = java.util.Collections.unmodifiableSet(java.util.EnumSet.copyOf(hardwarePcf));
    }
}
