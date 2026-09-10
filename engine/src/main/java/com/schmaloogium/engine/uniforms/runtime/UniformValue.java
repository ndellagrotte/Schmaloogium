// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.runtime;

import java.util.Arrays;

/**
 * Canonical typed uniform value of a cell or an upload command (PHASE_6_DOC §4.3).
 * Floats are canonicalized at construction ({@code -0.0f} becomes {@code 0.0f}); NaNs
 * never enter a valid cell. Equality is exact: {@code floatToIntBits} per component for
 * floats, exact ints for integer vectors, and matrices deliberately never compare equal
 * (they are always uploaded). Immutable; array-bearing variants copy on entry.
 */
public sealed interface UniformValue {

    record I(int x) implements UniformValue {
    }

    record F(float x) implements UniformValue {
        public F {
            if (x == 0.0f) {
                x = 0.0f; // normalize -0.0f
            }
            if (!Float.isFinite(x)) {
                throw new IllegalArgumentException("value must be finite");
            }
        }
    }

    record I2(int x, int y) implements UniformValue {
    }

    record I4(int x, int y, int z, int w) implements UniformValue {
    }

    record F2(float x, float y) implements UniformValue {
        public F2 {
            x = normalize(x);
            y = normalize(y);
        }
    }

    record F3(float x, float y, float z) implements UniformValue {
        public F3 {
            x = normalize(x);
            y = normalize(y);
            z = normalize(z);
        }
    }

    record F4(float x, float y, float z, float w) implements UniformValue {
        public F4 {
            x = normalize(x);
            y = normalize(y);
            z = normalize(z);
            w = normalize(w);
        }
    }

    /** Column-major mat4 container in facade upload order. */
    record M4(float[] columnMajor) implements UniformValue {
        public M4 {
            if (columnMajor == null || columnMajor.length != 16) {
                throw new IllegalArgumentException("matrix needs exactly 16 column-major floats");
            }
            for (float v : columnMajor) {
                if (!Float.isFinite(v)) {
                    throw new IllegalArgumentException("matrix components must be finite");
                }
            }
            columnMajor = columnMajor.clone();
        }

        /** A fresh column-major copy for the facade upload. */
        public float[] columnMajorCopy() {
            return columnMajor.clone();
        }
    }

    /**
     * Exact canonical comparison (PHASE_6_DOC §4.3): floats compare by
     * {@code floatToIntBits} after construction-time {@code -0.0f} normalization, ints
     * compare exactly, vectors compare component-wise, matrices never compare equal.
     */
    default boolean sameAs(UniformValue other) {
        if (this instanceof M4 || other instanceof M4) {
            return false;
        }
        return equals(other);
    }

    private static float normalize(float v) {
        if (!Float.isFinite(v)) {
            throw new IllegalArgumentException("value must be finite");
        }
        return v == 0.0f ? 0.0f : v;
    }

    /** Human-stable digest used by replay diagnostics only (never in the hot path). */
    default String describe() {
        if (this instanceof I i) {
            return "int(" + i.x() + ")";
        }
        if (this instanceof F f) {
            return "float(" + f.x() + ")";
        }
        if (this instanceof I2 v) {
            return "ivec2(" + v.x() + "," + v.y() + ")";
        }
        if (this instanceof I4 v) {
            return "ivec4(" + v.x() + "," + v.y() + "," + v.z() + "," + v.w() + ")";
        }
        if (this instanceof F2 v) {
            return "vec2(" + v.x() + "," + v.y() + ")";
        }
        if (this instanceof F3 v) {
            return "vec3(" + v.x() + "," + v.y() + "," + v.z() + ")";
        }
        if (this instanceof F4 v) {
            return "vec4(" + v.x() + "," + v.y() + "," + v.z() + "," + v.w() + ")";
        }
        if (this instanceof M4 m) {
            return "mat4(crc32=0x" + Integer.toHexString(crc32(m.columnMajor())) + ")";
        }
        throw new IllegalStateException("unknown variant");
    }

    private static int crc32(float[] values) {
        java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        for (float v : values) {
            crc.update(Float.floatToRawIntBits(v));
        }
        return (int) crc.getValue();
    }

    /** Whether two arrays carry identical float bits (verification helper). */
    static boolean bitsEqual(float[] a, float[] b) {
        return Arrays.equals(a, b);
    }
}
