// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.spi;

/**
 * The per-frame acquisition request (PHASE_6_DOC §4.2), constructed by the runtime from
 * the accepted {@code FrameBeginInput}. Dimensions are non-negative; seconds are finite
 * and non-negative. Immutable.
 */
public record FrameSampleRequest(
        long registryGeneration,
        long worldEpoch,
        long frameId,
        long logicalTick,
        float frameTimeSeconds,
        int targetViewWidth,
        int targetViewHeight) {
}
