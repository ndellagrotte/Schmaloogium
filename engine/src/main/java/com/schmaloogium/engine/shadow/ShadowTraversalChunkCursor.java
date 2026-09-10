// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.uniforms.Float3;

/**
 * The lazy §4.7 chunk-coordinate iterator (PHASE_8_DOC). Fixed primitive storage, no
 * per-step allocation: {@link #advance} writes the next chunk coordinate into the
 * caller's three-slot array and reports whether one was emitted. Creation precomputes
 * the walk; advancement is pure index arithmetic.
 *
 * <p>Order: the longitudinal axis is the largest absolute {@code towardLight} component
 * (ties resolve X, then Y, then Z). Longitudinal offsets from the camera's chunk are
 * emitted in ascending distance (near-to-far, toward-light direction first at each
 * distance), clipped to {@code [-shadowRadiusChunks, +viewRadiusChunks]}. Within one
 * longitudinal slab, the perpendicular plane is enumerated as a deterministic square —
 * near-vertical lights use an X-major horizontal square over X then Z instead of
 * dividing by a near-zero horizontal projection; horizontal lights walk the two
 * remaining axes the same X-before-Z way. Vertical sections ascend over every loaded
 * section. A {@code FullLoadedView} plan has no cursor; vanilla traversal enumerates.
 */
public interface ShadowTraversalChunkCursor {

    /** Writes the next chunk coordinate into {@code chunkOut[0..2]}; false when done. */
    boolean advance(int[] chunkOut);

    /** Total coordinates this cursor will still emit. */
    int remaining();

    /** Creates the cursor for a prism plan; never null. */
    static ShadowTraversalChunkCursor create(ShadowTraversalPlan.SunAlignedPrism plan,
            Float3 cameraPosition, int minSection, int maxSection) {
        return new com.schmaloogium.engine.shadow.internal.ShadowTraversalChunkCursorImpl(
                plan, cameraPosition, minSection, maxSection);
    }
}
