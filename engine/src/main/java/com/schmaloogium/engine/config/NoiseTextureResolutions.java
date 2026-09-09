// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** noiseTextureResolution value grammar: 32..4096 powers of two; null outside. */
public final class NoiseTextureResolutions {

    private NoiseTextureResolutions() {
    }

    public static Integer parse(String value) {
        try {
            int v = Integer.parseInt(value.trim());
            if (v < 32 || v > 4096 || Integer.bitCount(v) != 1) {
                return null;
            }
            return v;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
