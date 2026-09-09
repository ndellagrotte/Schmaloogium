// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed color internal-format vocabulary (PHASE_1_DOC §4.7.7a; RESEARCH.md App B.4's 37
 * pack-facing values). Legality and defaults are Phase 5's; this is the facade's
 * representation. {@link #RGBA_COMPAT} is the private 38th value — the plain unsized RGBA
 * allocation representation Phase 5 uses for DefaultRgba and whole-estate fallback; no
 * P3 pack grammar admits it.
 */
public enum ColorInternalFormat {

    // 8-bit normalized (App B.4)
    R8,
    RG8,
    RGB8,
    RGBA8,

    // 8-bit signed normalized
    R8_SNORM,
    RG8_SNORM,
    RGB8_SNORM,
    RGBA8_SNORM,

    // 16-bit normalized
    R16,
    RG16,
    RGB16,
    RGBA16,

    // 16-bit signed normalized
    R16_SNORM,
    RG16_SNORM,
    RGB16_SNORM,
    RGBA16_SNORM,

    // 16-bit float
    R16F,
    RG16F,
    RGB16F,
    RGBA16F,

    // 32-bit float
    R32F,
    RG32F,
    RGB32F,
    RGBA32F,

    // 32-bit integer
    R32I,
    RG32I,
    RGB32I,
    RGBA32I,

    // 32-bit unsigned integer
    R32UI,
    RG32UI,
    RGB32UI,
    RGBA32UI,

    // Mixed
    R3_G3_B2,
    RGB5_A1,
    RGB10_A2,
    R11F_G11F_B10F,
    RGB9_E5,

    /** Private unsized-RGBA fallback representation (P5 §4.2); never a pack directive. */
    RGBA_COMPAT
}
