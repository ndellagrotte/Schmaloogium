// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.buffers.Extent2i;

/**
 * The frame-begin payload (PHASE_7_DOC §5.1). Field order is binding; the driver assigns
 * {@code frameId} and never accepts a caller-made one. {@code mainTerrainFrameToken} is the
 * integer token vanilla will pass to setupTerrain, read at H-FRAME-01 HEAD before the
 * incrementing argument is evaluated. Immutable; invalid extents or non-finite time values
 * are rejected by the driver before any mutation.
 */
public record FrameBeginSignal(
        long worldEpoch,
        long logicalTick,
        double smoothingTimeTicks,
        float frameTimeSeconds,
        DimensionKey dimension,
        int vanillaPass,
        int mainTerrainFrameToken,
        float partialTicks,
        Extent2i targetView,
        Extent2i priorCompletedFramebuffer,
        AnaglyphEye eye) {

    public FrameBeginSignal {
        java.util.Objects.requireNonNull(dimension, "dimension");
        java.util.Objects.requireNonNull(targetView, "targetView");
        java.util.Objects.requireNonNull(priorCompletedFramebuffer, "priorCompletedFramebuffer");
        java.util.Objects.requireNonNull(eye, "eye");
        if (!Double.isFinite(smoothingTimeTicks)) {
            throw new IllegalArgumentException("smoothingTimeTicks must be finite");
        }
        if (!Float.isFinite(frameTimeSeconds) || frameTimeSeconds < 0f) {
            throw new IllegalArgumentException("frameTimeSeconds must be finite and >= 0");
        }
        if (!Float.isFinite(partialTicks) || partialTicks < 0f || partialTicks >= 1f) {
            throw new IllegalArgumentException("partialTicks must be finite in [0,1)");
        }
    }
}
