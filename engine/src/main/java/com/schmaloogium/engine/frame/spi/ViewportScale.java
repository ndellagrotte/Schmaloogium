// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

import java.util.Objects;

/**
 * The viewport request for one fullscreen draw (PHASE_7_DOC §5.1): normalized origin and
 * extent; the engine's viewport math applies floor for nonnegative values, a minimum of
 * one pixel for positive scale, and bounds by the target.
 */
public record ViewportScale(float x, float y, float width, float height) {

    public ViewportScale {
        requireFinite(x, "x");
        requireFinite(y, "y");
        requireFinite(width, "width");
        requireFinite(height, "height");
    }

    /** The full-target viewport. */
    public static ViewportScale full() {
        return new ViewportScale(0f, 0f, 1f, 1f);
    }

    private static void requireFinite(float value, String name) {
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException(name + " must be finite: " + value);
        }
    }
}
