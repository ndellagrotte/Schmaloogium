// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.parse;

import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;
import com.schmaloogium.engine.expr.api.SourceSpan;

/** One declaration's load error from the lexer/parser (§4.2): kind is {@code LEX},
 * {@code PARSE}, or {@code LIMIT}, always attributed to a span inside the declaration. */
public final class ParseException extends Exception {

    private final ExpressionDiagnosticKind kind;
    private final transient SourceSpan span;

    public ParseException(ExpressionDiagnosticKind kind, SourceSpan span, String message) {
        super(message);
        this.kind = kind;
        this.span = span;
    }

    public ExpressionDiagnosticKind kind() {
        return kind;
    }

    public SourceSpan span() {
        return span;
    }
}
