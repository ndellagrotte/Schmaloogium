// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.parse;

/** Closed token domain of the clean-room grammar (§4.2). */
public enum TokenType {
    NUMBER, IDENT,
    OR, AND, EQ, NE, GT, GE, LT, LE,
    PLUS, MINUS, STAR, SLASH, PERCENT, BANG,
    DOT, LPAREN, RPAREN, COMMA,
    EOF
}
