// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.parse;

/** One lexed token with its half-open character range in the raw expression. */
public record Token(TokenType type, String text, int start, int end) {

    public Token {
        if (type == null || text == null) {
            throw new IllegalArgumentException("token type and text are required");
        }
        if (start < 0 || end < start) {
            throw new IllegalArgumentException("invalid token range");
        }
    }
}
