// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.uniforms.Float3;

/**
 * Pure §4.7 strategy selection (PHASE_8_DOC): {@code renderMul <= 0 → FullLoadedView};
 * otherwise {@code D = ceil(max(0, shadowDistance * renderMul) / 16)} and
 * {@code D >= V → FullLoadedView}; otherwise the sun-aligned prism from
 * {@code camera - L*D} through {@code camera + L*V} with perpendicular half-width
 * {@code D}. The frustum and light direction arrive from the §4.5 camera math and are
 * carried through unchanged; nothing here recomputes geometry.
 */
public interface ShadowTraversalPlanner {

    ShadowTraversalPlan plan(ShadowFrustum frustum, ShadowPolicy policy,
            Float3 towardLight, int viewDistanceChunks);

    /** The stateless default implementation. */
    static ShadowTraversalPlanner standard() {
        return Standard.INSTANCE;
    }

    /** Holder for the shared planner. */
    final class Standard {
        private Standard() {
        }

        private static final ShadowTraversalPlanner INSTANCE =
                new com.schmaloogium.engine.shadow.internal.ShadowTraversalPlannerImpl();
    }
}
