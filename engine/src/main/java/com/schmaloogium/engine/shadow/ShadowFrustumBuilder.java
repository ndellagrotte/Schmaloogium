// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import java.util.Objects;

/**
 * Pure frustum synthesis seam (PHASE_8_DOC §4.5.3): from the MVP matrix to at most ten
 * planes — six normalized if non-degenerate, empty + culling-disabled flag on
 * degeneracy. The default zero-origin builder delegates to the process-wide math
 * instance; the origin-carrying overload subtracts the given camera position before
 * testing so chunk AABBs may be supplied in world coordinates.
 */
public interface ShadowFrustumBuilder {

    ShadowFrustum build(ShadowCameraProjection camera);

    /** Identical build; named for the §4.5.3 synthesis step. */
    default ShadowFrustum frustum(ShadowCameraProjection camera) {
        return build(camera);
    }

    /**
     * Builds and returns a test view whose {@code intersects} evaluates the frustum
     * against world-space AABBs by subtracting the supplied camera origin first (§4.6).
     */
    default WorldSpace worldSpace(ShadowCameraProjection camera, double cameraX, double cameraY,
            double cameraZ) {
        Objects.requireNonNull(camera, "camera");
        ShadowFrustum frustum = build(camera);
        return aabb -> frustum.intersects(new ShadowAabb(
                aabb.minX() - cameraX, aabb.minY() - cameraY, aabb.minZ() - cameraZ,
                aabb.maxX() - cameraX, aabb.maxY() - cameraY, aabb.maxZ() - cameraZ));
    }

    /** World-coordinate AABB predicate over a built frustum. */
    interface WorldSpace {

        boolean intersects(ShadowAabb worldAabb);
    }

    /** The default zero-origin builder; never null. */
    static ShadowFrustumBuilder zeroOrigin() {
        return ShadowCameraMath.shared().frustumBuilder();
    }
}
