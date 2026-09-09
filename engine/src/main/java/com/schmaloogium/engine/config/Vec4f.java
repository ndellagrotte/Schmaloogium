// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Four finite float components; signed zero canonicalized to +0.0f. */
public record Vec4f(float red, float green, float blue, float alpha) {

    public Vec4f {
        red = ViewportScale.canonical(red);
        green = ViewportScale.canonical(green);
        blue = ViewportScale.canonical(blue);
        alpha = ViewportScale.canonical(alpha);
        if (!Float.isFinite(red) || !Float.isFinite(green)
                || !Float.isFinite(blue) || !Float.isFinite(alpha)) {
            throw new IllegalArgumentException("Vec4f components must be finite");
        }
    }
}
