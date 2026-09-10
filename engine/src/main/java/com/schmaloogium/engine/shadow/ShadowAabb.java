// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

/**
 * Immutable axis-aligned bounds for §4.6 shadow culling. Phase 8 owns this carrier
 * because no engine-wide AABB exists; the mod glue converts chunk/entity bounds into it
 * before testing against a {@link ShadowFrustum}. Coordinates are frustum-space: the
 * builder already subtracted the camera origin, so callers test caster-relative bounds
 * (or world bounds against a zero-origin build).
 */
public record ShadowAabb(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {

    /** The smallest corner; finite component checks happen at the test, never here. */
    public ShadowAabb {
        if (minX > maxX || minY > maxY || minZ > maxZ) {
            throw new IllegalArgumentException("inverted aabb");
        }
    }
}
