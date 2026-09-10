// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/**
 * The frame-begin input (PHASE_6_DOC §2.2/§4.6). Phase 7 supplies the accepted frame's
 * authoritative identity and clocks; Phase 6 never reads a replacement clock. Seconds are
 * finite and non-negative; dimensions are non-negative. Immutable.
 */
public record FrameBeginInput(
        long registryGeneration,
        long frameId,
        long worldEpoch,
        long logicalTick,
        double smoothingTimeTicks,
        float frameTimeSeconds,
        int targetViewWidth,
        int targetViewHeight,
        int priorFramebufferWidth,
        int priorFramebufferHeight) {

    public FrameBeginInput {
        if (!Double.isFinite(smoothingTimeTicks)) {
            throw new IllegalArgumentException("smoothingTimeTicks must be finite");
        }
        if (!Float.isFinite(frameTimeSeconds) || frameTimeSeconds < 0f) {
            throw new IllegalArgumentException("frameTimeSeconds must be finite and >= 0");
        }
        if (targetViewWidth < 0 || targetViewHeight < 0) {
            throw new IllegalArgumentException("target view dimensions must be non-negative");
        }
        if (priorFramebufferWidth < 0 || priorFramebufferHeight < 0) {
            throw new IllegalArgumentException("prior framebuffer dimensions must be non-negative");
        }
    }
}
