// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.config.CloudMode;
import com.schmaloogium.engine.frame.ShadowFrameView;

/**
 * The world snapshot captured by the glue adapter (PHASE_8_DOC §4.3): the exact shadow
 * frame, its camera presence and loaded-chunk bounds, vanilla view distance, and the
 * current cloud render mode. The sample is bit-equality tested against the invocation's
 * shadow frame before use; a missing or mismatched sample is a stale-frame rejection,
 * never a synthesized default.
 */
public record ShadowWorldSample(
        ShadowFrameView frame,
        boolean cameraPresent,
        int viewDistanceChunks,
        int worldMinSection,
        int worldMaxSection,
        CloudMode cloudMode) {

    public ShadowWorldSample {
        if (frame == null) {
            throw new IllegalArgumentException("frame");
        }
        if (viewDistanceChunks < 0) {
            throw new IllegalArgumentException("negative view distance");
        }
        if (worldMinSection > worldMaxSection) {
            throw new IllegalArgumentException("inverted section range");
        }
    }
}
