// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.frame.spi.ViewportScale;

/**
 * The fullscreen-pass viewport math (PHASE_7_DOC §4.6 step 7): normalized origin and extent
 * over the bound target, floor for nonnegative values, a minimum of one pixel for a
 * positive scale, bounded by the target. Pure so the glue tests pin it without GL.
 */
public final class FullscreenViewport {

    private FullscreenViewport() {
    }

    /** Returns {x, y, width, height} in pixels for the given target extent and scale. */
    public static int[] compute(Extent2i target, ViewportScale scale) {
        int targetW = Math.max(1, target.width());
        int targetH = Math.max(1, target.height());
        int x = clamp((int) Math.floor(Math.max(0f, scale.x()) * targetW), 0, targetW - 1);
        int y = clamp((int) Math.floor(Math.max(0f, scale.y()) * targetH), 0, targetH - 1);
        int w = scale.width() > 0f
            ? Math.max(1, (int) Math.floor(scale.width() * targetW))
            : 0;
        int h = scale.height() > 0f
            ? Math.max(1, (int) Math.floor(scale.height() * targetH))
            : 0;
        w = Math.min(w, targetW - x);
        h = Math.min(h, targetH - y);
        return new int[] {x, y, w, h};
    }

    private static int clamp(int value, int low, int high) {
        return Math.max(low, Math.min(high, value));
    }
}
