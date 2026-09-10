// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.List;
import java.util.Objects;

/** Shadow framebuffer resource summary with per-texture parameter sets (PHASE_5_DOC §2.2). */
public record ShadowResourceProjection(int depthTextures, int colorTextures, int resolution,
        List<ShadowTextureResource> depth, List<ShadowTextureResource> color) {

    public ShadowResourceProjection {
        if (depthTextures < 0) {
            throw new IllegalArgumentException("negative shadow depth texture count: " + depthTextures);
        }
        if (colorTextures < 0) {
            throw new IllegalArgumentException("negative shadow color texture count: " + colorTextures);
        }
        depth = List.copyOf(Objects.requireNonNull(depth, "depth"));
        color = List.copyOf(Objects.requireNonNull(color, "color"));
    }
}
