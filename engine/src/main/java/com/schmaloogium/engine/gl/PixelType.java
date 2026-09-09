// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed pack-facing transfer (pixel) type vocabulary — exactly P5 §4.2's complete list
 * (RESEARCH.md App B.4, D-P1-63): scalar types plus the packed variants. Packed types
 * occupy one 1/2/4-byte word per pixel as named, never multiplied by component count.
 */
public enum PixelType {
    BYTE,
    SHORT,
    INT,
    HALF_FLOAT,
    FLOAT,
    UNSIGNED_BYTE,
    UNSIGNED_BYTE_3_3_2,
    UNSIGNED_BYTE_2_3_3_REV,
    UNSIGNED_SHORT,
    UNSIGNED_SHORT_5_6_5,
    UNSIGNED_SHORT_5_6_5_REV,
    UNSIGNED_SHORT_4_4_4_4,
    UNSIGNED_SHORT_4_4_4_4_REV,
    UNSIGNED_SHORT_5_5_5_1,
    UNSIGNED_SHORT_1_5_5_5_REV,
    UNSIGNED_INT,
    UNSIGNED_INT_8_8_8_8,
    UNSIGNED_INT_8_8_8_8_REV,
    UNSIGNED_INT_10_10_10_2,
    UNSIGNED_INT_2_10_10_10_REV
}
