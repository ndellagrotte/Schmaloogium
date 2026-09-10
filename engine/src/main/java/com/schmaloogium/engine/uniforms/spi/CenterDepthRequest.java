// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.spi;

/**
 * The center-depth read request (PHASE_6_DOC §4.2): names the completed prior
 * framebuffer's dimensions and the center pixel. Positive dimensions imply
 * {@code pixelX = floor(width/2)} and {@code pixelY = floor(height/2)} in bottom-left
 * pixel coordinates; zero dimensions produce {@code Unavailable} without calling the
 * source. Immutable.
 */
public record CenterDepthRequest(
        long registryGeneration,
        long worldEpoch,
        long frameId,
        int framebufferWidth,
        int framebufferHeight,
        int pixelX,
        int pixelY) {

    public CenterDepthRequest {
        if (framebufferWidth < 0 || framebufferHeight < 0) {
            throw new IllegalArgumentException("framebuffer dimensions must be non-negative");
        }
    }
}
