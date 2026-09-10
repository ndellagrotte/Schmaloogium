// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow.internal;

import com.schmaloogium.engine.shadow.ShadowFrustum;
import com.schmaloogium.engine.shadow.ShadowPolicy;
import com.schmaloogium.engine.shadow.ShadowTraversalPlan;
import com.schmaloogium.engine.shadow.ShadowTraversalPlanner;
import com.schmaloogium.engine.uniforms.Float3;

import java.util.Objects;

/**
 * The sole §4.7 selection implementation. Pure arithmetic; frustum and light direction
 * arrive precomputed and are carried through unchanged.
 */
public final class ShadowTraversalPlannerImpl implements ShadowTraversalPlanner {

    private static final float BLOCKS_PER_CHUNK = 16.0f;

    @Override
    public ShadowTraversalPlan plan(ShadowFrustum frustum, ShadowPolicy policy,
            Float3 towardLight, int viewDistanceChunks) {
        Objects.requireNonNull(frustum, "frustum");
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(towardLight, "towardLight");
        if (viewDistanceChunks < 0) {
            throw new IllegalArgumentException("negative view distance");
        }
        float renderMul = policy.shadowDistanceRenderMul();
        if (renderMul <= 0f) {
            return new ShadowTraversalPlan.FullLoadedView(frustum);
        }
        double distanceBlocks = (double) policy.shadowDistance() * (double) renderMul;
        if (distanceBlocks < 0d) {
            distanceBlocks = 0d;
        }
        int d = (int) Math.ceil(distanceBlocks / BLOCKS_PER_CHUNK);
        if (d >= viewDistanceChunks) {
            return new ShadowTraversalPlan.FullLoadedView(frustum);
        }
        return new ShadowTraversalPlan.SunAlignedPrism(frustum, d, viewDistanceChunks,
                towardLight);
    }
}
