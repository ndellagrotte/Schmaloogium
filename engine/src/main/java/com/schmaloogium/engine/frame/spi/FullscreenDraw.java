// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

import com.schmaloogium.engine.registry.PassDescriptor;

import java.util.Objects;

/**
 * One immutable fullscreen draw request (PHASE_7_DOC §5.1): the raster descriptor, the
 * mipmaps generated before the pass, the viewport scale, and the instance row. The engine
 * executes instances 0…N−1 by drawing this exact request once per index.
 */
public record FullscreenDraw(
        PassDescriptor pass,
        MipmapSet mipmaps,
        ViewportScale viewport,
        int instanceIndex,
        int instanceCount,
        FullscreenPrimitive primitive) {

    public FullscreenDraw {
        Objects.requireNonNull(pass, "pass");
        Objects.requireNonNull(mipmaps, "mipmaps");
        Objects.requireNonNull(viewport, "viewport");
        Objects.requireNonNull(primitive, "primitive");
        if (instanceCount <= 0) {
            throw new IllegalArgumentException("instanceCount must be positive: " + instanceCount);
        }
        if (instanceIndex < 0 || instanceIndex >= instanceCount) {
            throw new IllegalArgumentException(
                    "instanceIndex must be in 0..instanceCount-1: " + instanceIndex);
        }
    }
}
