// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.type;

import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;
import com.schmaloogium.engine.expr.api.SourceSpan;

/** One expression-local load error other than lex/parse/limit: unknown or duplicate names,
 * wrong arity or typing, non-finite literals, duplicate smooth ids (§4.9). */
public final class SemanticException extends Exception {

    private final ExpressionDiagnosticKind kind;
    private final transient SourceSpan span;

    public SemanticException(ExpressionDiagnosticKind kind, SourceSpan span, String message) {
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
