// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** Closed built-in runtime value algebra for the exact-name input view (§5.3/§5.4):
 * scalars, float/integer vectors, and mat4 containers. Matrix values are input containers,
 * not expression results (§2.3). */
public sealed interface BuiltInValue {

    record Float1(float value) implements BuiltInValue {
        public Float1 {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException("value must be finite");
            }
        }
    }

    record Int1(int value) implements BuiltInValue {}

    record Float2(float x, float y) implements BuiltInValue {
        public Float2 {
            requireFinite(x, y);
        }
    }

    record Float3(float x, float y, float z) implements BuiltInValue {
        public Float3 {
            requireFinite(x, y, z);
        }
    }

    record Float4(float x, float y, float z, float w) implements BuiltInValue {
        public Float4 {
            requireFinite(x, y, z, w);
        }
    }

    record Int2(int x, int y) implements BuiltInValue {}

    record Int3(int x, int y, int z) implements BuiltInValue {}

    record Int4(int x, int y, int z, int w) implements BuiltInValue {}

    /** Row-major 4x4 container; {@code m[row * 4 + col]}. */
    record Mat4(float[] rowMajor) implements BuiltInValue {
        public Mat4 {
            Objects.requireNonNull(rowMajor, "rowMajor");
            if (rowMajor.length != 16) {
                throw new IllegalArgumentException("mat4 needs exactly 16 components");
            }
            for (float v : rowMajor) {
                if (!Float.isFinite(v)) {
                    throw new IllegalArgumentException("mat4 components must be finite");
                }
            }
            rowMajor = rowMajor.clone();
        }
    }

    private static void requireFinite(float... values) {
        for (float v : values) {
            if (!Float.isFinite(v)) {
                throw new IllegalArgumentException("components must be finite");
            }
        }
    }
}
