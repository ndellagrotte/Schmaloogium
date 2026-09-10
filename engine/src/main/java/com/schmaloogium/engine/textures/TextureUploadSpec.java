// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.PixelFormat;
import com.schmaloogium.engine.gl.PixelType;

import java.util.Objects;

/**
 * Handle-free planning-side upload shape (§4.3.3/D-P13-36): the exact P5/P1 closed shared
 * allocation/transfer values after one-for-one conversion from Phase 3's typed enumerants.
 * Formats here are never the private RGBA_COMPAT allocation value from a raw declaration.
 */
public sealed interface TextureUploadSpec {

    record OneD(int width, ColorInternalFormat internalFormat, PixelFormat pixelFormat,
                PixelType pixelType) implements TextureUploadSpec {
        public OneD {
            if (width <= 0) {
                throw new IllegalArgumentException("width must be positive: " + width);
            }
        }
    }

    record TwoD(int width, int height, ColorInternalFormat internalFormat,
                PixelFormat pixelFormat, PixelType pixelType) implements TextureUploadSpec {
        public TwoD {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("extent must be positive: "
                    + width + "x" + height);
            }
        }
    }

    record ThreeD(int width, int height, int depth, ColorInternalFormat internalFormat,
                  PixelFormat pixelFormat, PixelType pixelType) implements TextureUploadSpec {
        public ThreeD {
            if (width <= 0 || height <= 0 || depth <= 0) {
                throw new IllegalArgumentException("extent must be positive: "
                    + width + "x" + height + "x" + depth);
            }
        }
    }

    record Rectangle(int width, int height, ColorInternalFormat internalFormat,
                     PixelFormat pixelFormat, PixelType pixelType) implements TextureUploadSpec {
        public Rectangle {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("extent must be positive: "
                    + width + "x" + height);
            }
        }
    }

    /** Exact level-0 byte length for this spec: per-component math or packed scalar words. */
    default long level0ByteSize() {
        return switch (this) {
            case OneD s -> level0Bytes(s.pixelFormat(), s.pixelType(), s.width());
            case TwoD s -> level0Bytes(s.pixelFormat(), s.pixelType(),
                (long) s.width() * s.height());
            case ThreeD s -> level0Bytes(s.pixelFormat(), s.pixelType(),
                (long) s.width() * s.height() * s.depth());
            case Rectangle s -> level0Bytes(s.pixelFormat(), s.pixelType(),
                (long) s.width() * s.height());
        };
    }

    private static long transferWords(PixelFormat format, PixelType type) {
        return componentsOf(format) * bytesPerComponent(type);
    }

    private static int componentsOf(PixelFormat format) {
        return switch (format) {
            case RED, RED_INTEGER -> 1;
            case RG, RG_INTEGER -> 2;
            case RGB, BGR, RGB_INTEGER, BGR_INTEGER -> 3;
            case RGBA, BGRA, RGBA_INTEGER, BGRA_INTEGER -> 4;
        };
    }

    private static int bytesPerComponent(PixelType type) {
        return switch (type) {
            case BYTE, UNSIGNED_BYTE -> 1;
            case SHORT, UNSIGNED_SHORT, HALF_FLOAT -> 2;
            case INT, UNSIGNED_INT, FLOAT -> 4;
            // Packed scalar words: one word covers the whole pixel, not per-component.
            case UNSIGNED_BYTE_3_3_2, UNSIGNED_BYTE_2_3_3_REV -> 0;
            case UNSIGNED_SHORT_5_6_5, UNSIGNED_SHORT_5_6_5_REV,
                 UNSIGNED_SHORT_4_4_4_4, UNSIGNED_SHORT_4_4_4_4_REV,
                 UNSIGNED_SHORT_5_5_5_1, UNSIGNED_SHORT_1_5_5_5_REV -> 0;
            case UNSIGNED_INT_8_8_8_8, UNSIGNED_INT_8_8_8_8_REV,
                 UNSIGNED_INT_10_10_10_2, UNSIGNED_INT_2_10_10_10_REV -> 0;
        };
    }

    /** Byte size of a packed scalar-word pixel for the packed types. */
    static long packedPixelBytes(PixelType type) {
        return switch (type) {
            case UNSIGNED_BYTE_3_3_2, UNSIGNED_BYTE_2_3_3_REV -> 1L;
            case UNSIGNED_SHORT_5_6_5, UNSIGNED_SHORT_5_6_5_REV,
                 UNSIGNED_SHORT_4_4_4_4, UNSIGNED_SHORT_4_4_4_4_REV,
                 UNSIGNED_SHORT_5_5_5_1, UNSIGNED_SHORT_1_5_5_5_REV -> 2L;
            case UNSIGNED_INT_8_8_8_8, UNSIGNED_INT_8_8_8_8_REV,
                 UNSIGNED_INT_10_10_10_2, UNSIGNED_INT_2_10_10_10_REV -> 4L;
            default -> 0L;
        };
    }

    /** Exact level-0 byte length: per-component math or packed scalar words. */
    static long level0Bytes(PixelFormat format, PixelType type, long texelCount) {
        long perComponent = bytesPerComponent(type);
        if (perComponent == 0) {
            return packedPixelBytes(type) * texelCount;
        }
        return componentsOf(format) * perComponent * texelCount;
    }
}
