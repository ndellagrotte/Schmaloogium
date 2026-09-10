// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import java.util.Objects;

/**
 * A synthesized shadow culling frustum (PHASE_8_DOC §4.5.3, §2.2). Immutable. Degenerate
 * plane pairs return an empty list and the flagged culling-disabled form, whose
 * {@link #intersects(ShadowAabb)} is unconditionally {@code true} and whose
 * {@link #planes()} is empty — FullLoadedView fallback selection happens during
 * traversal planning, never by substituting geometry here. Non-degenerate capacity is
 * fixed at ten: at most six light-facing base planes plus synthesized silhouettes,
 * never six silhouettes.
 */
public record ShadowFrustum(double[][] planes, boolean cullingDisabled) {

    /** Fixed non-degenerate capacity: six base + up to four synthesized silhouettes. */
    public static final int CAPACITY = 10;

    public ShadowFrustum {
        planes = planes == null ? new double[0][] : planes;
        for (double[] plane : planes) {
            Objects.requireNonNull(plane, "planes");
            if (plane.length != 4) {
                throw new IllegalArgumentException("plane must carry four coefficients");
            }
        }
        if (!cullingDisabled && planes.length > CAPACITY) {
            throw new IllegalArgumentException("frustum exceeds ten-plane capacity");
        }
        if (cullingDisabled && planes.length != 0) {
            throw new IllegalArgumentException("culling-disabled frustum must be empty");
        }
        planes = deepCopy(planes);
    }

    /** The culling-disabled always-intersecting fallback. */
    public static ShadowFrustum disabledCulling() {
        return new ShadowFrustum(new double[0][], true);
    }

    private static double[][] deepCopy(double[][] planes) {
        double[][] copy = new double[planes.length][];
        for (int i = 0; i < planes.length; i++) {
            copy[i] = planes[i].clone();
        }
        return copy;
    }

    /**
     * AABB intersection in the frustum's own coordinate space (§4.6: camera/world
     * coordinates are subtracted before testing by the builder, so the test input is
     * caster-relative). A degenerate culling-disabled frustum intersects everything; a
     * NaN corner never means outside.
     */
    public boolean intersects(ShadowAabb aabb) {
        Objects.requireNonNull(aabb, "aabb");
        if (cullingDisabled) {
            return true;
        }
        for (double[] plane : planes) {
            double nx = plane[0];
            double ny = plane[1];
            double nz = plane[2];
            double d = plane[3];
            double x = nx >= 0 ? aabb.maxX() : aabb.minX();
            double y = ny >= 0 ? aabb.maxY() : aabb.minY();
            double z = nz >= 0 ? aabb.maxZ() : aabb.minZ();
            if (nx * x + ny * y + nz * z + d < 0) {
                return false;
            }
        }
        return true;
    }
}
