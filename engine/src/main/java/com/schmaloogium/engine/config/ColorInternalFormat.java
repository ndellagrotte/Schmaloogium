// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** The 37 closed internal color formats (Appendix B.4). */
public enum ColorInternalFormat {
    R8, RG8, RGB8, RGBA8, R8_SNORM, RG8_SNORM, RGB8_SNORM, RGBA8_SNORM,
    R16, RG16, RGB16, RGBA16, R16_SNORM, RG16_SNORM, RGB16_SNORM, RGBA16_SNORM,
    R16F, RG16F, RGB16F, RGBA16F, R32F, RG32F, RGB32F, RGBA32F,
    R32I, RG32I, RGB32I, RGBA32I, R32UI, RG32UI, RGB32UI, RGBA32UI,
    R3_G3_B2, RGB5_A1, RGB10_A2, R11F_G11F_B10F, RGB9_E5
}
