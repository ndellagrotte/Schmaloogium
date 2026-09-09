// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Texture border color (PHASE_1_DOC §4.7.7, D-P1-52). In this closed scope the border is
 * exactly (0,0,0,0): neither granted wrap mode samples a border, and no unknown
 * integer-border state enters the claimed complete mapping — so the constructor rejects
 * any other value.
 */
public record TextureBorderColor(float red, float green, float blue, float alpha) {

    public TextureBorderColor {
        red = canonical(red, "red");
        green = canonical(green, "green");
        blue = canonical(blue, "blue");
        alpha = canonical(alpha, "alpha");
    }

    private static float canonical(float v, String name) {
        if (!Float.isFinite(v)) {
            throw new IllegalArgumentException("border color component " + name + " must be finite: " + v);
        }
        v = v + 0.0f; // canonicalizes -0.0 to +0.0
        if (v != 0.0f) {
            throw new IllegalArgumentException("the border is exactly (0,0,0,0) in this scope (PHASE_1_DOC §4.7.7): "
                    + name + " = " + v);
        }
        return v;
    }
}
