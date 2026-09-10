// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.parse;

import com.schmaloogium.engine.expr.api.SourceSpan;

import java.util.List;

/** Untyped clean-room syntax tree (§4.2). Numeric signs are unary operators, not literal
 * characters; member selectors carry letter/numeric provenance so the type checker can
 * enforce matrix-access rules (§4.2/§4.3). */
public sealed interface Ast {

    SourceSpan span();

    record Num(float value, SourceSpan span) implements Ast {}

    record Ident(String name, SourceSpan span) implements Ast {}

    enum UnaryOp { POS, NEG, NOT }

    record Unary(UnaryOp op, Ast operand, SourceSpan span) implements Ast {}

    enum BinaryOp { OR, AND, EQ, NE, GT, GE, LT, LE, ADD, SUB, MUL, DIV, MOD }

    record Bin(BinaryOp op, Ast left, Ast right, SourceSpan span) implements Ast {}

    /** {@code named} selectors are the letters x/y/z/r/g/b (component 0..2); numeric
     * selectors are the literal index tokens 0..3. */
    record Member(Ast base, int index, boolean named, SourceSpan span) implements Ast {}

    record Call(String name, List<Ast> args, SourceSpan nameSpan, SourceSpan span) implements Ast {}
}
