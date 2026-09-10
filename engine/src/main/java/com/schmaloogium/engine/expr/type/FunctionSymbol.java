// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.type;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** The exact Appendix F.6 function surface (§3.2); nothing extra becomes pack-visible.
 * Arity metadata drives static ARITY checks; special forms ({@code if}, {@code smooth},
 * {@code in}) are typed by the checker. */
public enum FunctionSymbol {
    SIN("sin", 1, 1), COS("cos", 1, 1), ASIN("asin", 1, 1), ACOS("acos", 1, 1),
    TAN("tan", 1, 1), ATAN("atan", 1, 1), ATAN2("atan2", 2, 2),
    TORAD("torad", 1, 1), TODEG("todeg", 1, 1),
    MIN("min", 2, Integer.MAX_VALUE), MAX("max", 2, Integer.MAX_VALUE),
    CLAMP("clamp", 3, 3), ABS("abs", 1, 1), FLOOR("floor", 1, 1), CEIL("ceil", 1, 1),
    EXP("exp", 1, 1), FRAC("frac", 1, 1), LOG("log", 1, 1), POW("pow", 2, 2),
    RANDOM("random", 0, 0), ROUND("round", 1, 1), SIGNUM("signum", 1, 1),
    SQRT("sqrt", 1, 1), FMOD("fmod", 2, 2),
    IF("if", 3, Integer.MAX_VALUE), SMOOTH("smooth", 1, 4),
    BETWEEN("between", 3, 3), EQUALS("equals", 3, 3),
    IN("in", 2, Integer.MAX_VALUE),
    VEC2("vec2", 2, 2), VEC3("vec3", 3, 3), VEC4("vec4", 4, 4);

    public static final Map<String, FunctionSymbol> BY_NAME = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(FunctionSymbol::spelling, Function.identity()));

    private final String spelling;
    private final int minArity;
    private final int maxArity;

    FunctionSymbol(String spelling, int minArity, int maxArity) {
        this.spelling = spelling;
        this.minArity = minArity;
        this.maxArity = maxArity;
    }

    public String spelling() {
        return spelling;
    }

    public int minArity() {
        return minArity;
    }

    public int maxArity() {
        return maxArity;
    }

    public boolean accepts(int arity) {
        if (this == IF) {
            return arity >= 3 && arity % 2 == 1;
        }
        return arity >= minArity && arity <= maxArity;
    }

    static List<String> reservedNames() {
        return Stream.of(values()).map(FunctionSymbol::spelling).toList();
    }
}
