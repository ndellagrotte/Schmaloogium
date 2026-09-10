// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.uniforms.Float3;

/**
 * The two immutable §4.7 traversal strategies. {@code FullLoadedView} carries only the
 * shadow frustum — no allowed-set restriction exists. {@code SunAlignedPrism} carries
 * the prism frustum, both radius credentials in chunks, and the unit
 * toward-light direction used to enumerate it.
 */
public sealed interface ShadowTraversalPlan {

    /** Every loaded chunk; the shadow frustum alone filters vanilla traversal. */
    record FullLoadedView(ShadowFrustum frustum) implements ShadowTraversalPlan {

        public FullLoadedView {
            if (frustum == null) {
                throw new IllegalArgumentException("frustum");
            }
        }
    }

    /**
     * The sun-aligned optimization prism: {@code shadowRadiusChunks = D},
     * {@code viewRadiusChunks = V}, {@code towardLight} unit.
     */
    record SunAlignedPrism(
            ShadowFrustum frustum,
            int shadowRadiusChunks,
            int viewRadiusChunks,
            Float3 towardLight) implements ShadowTraversalPlan {

        public SunAlignedPrism {
            if (frustum == null) {
                throw new IllegalArgumentException("frustum");
            }
            if (towardLight == null) {
                throw new IllegalArgumentException("towardLight");
            }
            if (shadowRadiusChunks < 0 || viewRadiusChunks < 0) {
                throw new IllegalArgumentException("negative radius");
            }
        }
    }
}
