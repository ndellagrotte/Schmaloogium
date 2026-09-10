// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** Closed runtime value algebra crossing the Phase 6 bridge and memo slots (§2.3).
 * Every numeric component is finite: no non-finite scalar enters a plan or leaves an
 * evaluator (§2.4 invariant 6). */
public sealed interface ExpressionValue {

    record Bool(boolean value) implements ExpressionValue {}

    record Int(int value) implements ExpressionValue {}

    record Float(float value) implements ExpressionValue {
        public Float {
            if (!java.lang.Float.isFinite(value)) {
                throw new IllegalArgumentException("float value must be finite");
            }
        }
    }

    record Vec2(float x, float y) implements ExpressionValue {
        public Vec2 {
            requireFinite("x", x);
            requireFinite("y", y);
        }
    }

    record Vec3(float x, float y, float z) implements ExpressionValue {
        public Vec3 {
            requireFinite("x", x);
            requireFinite("y", y);
            requireFinite("z", z);
        }
    }

    record Vec4(float x, float y, float z, float w) implements ExpressionValue {
        public Vec4 {
            requireFinite("x", x);
            requireFinite("y", y);
            requireFinite("z", z);
            requireFinite("w", w);
        }
    }

    private static void requireFinite(String name, float value) {
        if (!java.lang.Float.isFinite(value)) {
            throw new IllegalArgumentException(name + " must be finite");
        }
    }

    static ExpressionType typeOf(ExpressionValue value) {
        return switch (value) {
            case Bool ignored -> ExpressionType.BOOL;
            case Int ignored -> ExpressionType.INT;
            case Float ignored -> ExpressionType.FLOAT;
            case Vec2 ignored -> ExpressionType.VEC2;
            case Vec3 ignored -> ExpressionType.VEC3;
            case Vec4 ignored -> ExpressionType.VEC4;
        };
    }

    static boolean isNumeric(ExpressionValue value) {
        return !(value instanceof Bool);
    }

    static void requireNonNullDeep(ExpressionValue value) {
        Objects.requireNonNull(value, "value");
    }
}
