// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Engine-issued Boolean switch expression with its captured bytes. */
final class ProgramEnabledExpressionValue implements ProgramEnabledExpression {

    private final BooleanExpression ast;
    private final byte[] expressionBytes;

    ProgramEnabledExpressionValue(BooleanExpression ast, byte[] expressionBytes) {
        this.ast = java.util.Objects.requireNonNull(ast, "ast");
        this.expressionBytes = expressionBytes.clone();
    }

    @Override
    public byte[] capturedExpressionBytes() {
        return expressionBytes.clone();
    }

    java.util.Set<String> referencedSwitches() {
        return ast.switches();
    }

    boolean evaluate(java.util.function.Predicate<String> switches) {
        return ast.evaluate(switches);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ProgramEnabledExpressionValue other
            && java.util.Arrays.equals(expressionBytes, other.expressionBytes)
            && ast.equals(other.ast);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(expressionBytes);
    }
}
