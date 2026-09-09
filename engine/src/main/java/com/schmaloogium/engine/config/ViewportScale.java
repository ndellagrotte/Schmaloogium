// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** {@code scale.<prog>} state; every component finite in 0..1. */
public record ViewportScale(float scale, float offsetX, float offsetY) {

    public ViewportScale {
        scale = ViewportScale.canonical(scale);
        offsetX = ViewportScale.canonical(offsetX);
        offsetY = ViewportScale.canonical(offsetY);
        if (!Float.isFinite(scale) || scale < 0.0f || scale > 1.0f) {
            throw new IllegalArgumentException("scale outside 0..1");
        }
        if (!Float.isFinite(offsetX) || offsetX < 0.0f || offsetX > 1.0f) {
            throw new IllegalArgumentException("offsetX outside 0..1");
        }
        if (!Float.isFinite(offsetY) || offsetY < 0.0f || offsetY > 1.0f) {
            throw new IllegalArgumentException("offsetY outside 0..1");
        }
    }

    static float canonical(float v) {
        return v == 0.0f ? 0.0f : v;
    }
}
