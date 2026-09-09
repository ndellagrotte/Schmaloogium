// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Closed uniform/variable custom-expression type domain. */
public final class CustomExpressionTypes {

    private CustomExpressionTypes() {
    }

    public static CustomExpressionType of(String token) {
        return switch (token) {
            case "float" -> CustomExpressionType.FLOAT;
            case "int" -> CustomExpressionType.INT;
            case "bool" -> CustomExpressionType.BOOL;
            case "vec2" -> CustomExpressionType.VEC2;
            case "vec3" -> CustomExpressionType.VEC3;
            case "vec4" -> CustomExpressionType.VEC4;
            default -> null;
        };
    }
}
