// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed pack-facing transfer (pixel) format vocabulary — exactly P5 §4.2's complete list
 * (RESEARCH.md App B.4, D-P1-63): the six base formats plus their {@code *_INTEGER}
 * variants. Integer internal formats require the integer transfer path.
 */
public enum PixelFormat {
    RED,
    RG,
    RGB,
    BGR,
    RGBA,
    BGRA,
    RED_INTEGER,
    RG_INTEGER,
    RGB_INTEGER,
    BGR_INTEGER,
    RGBA_INTEGER,
    BGRA_INTEGER
}
