// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.eval;

import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;

/** One expression-local runtime failure (§4.6/§4.9): disables the owning definition and
 * its reverse-dependent readers; never aborts the refresh. */
public final class ExprEvalException extends Exception {

    private final ExpressionDiagnosticKind kind;

    public ExprEvalException(ExpressionDiagnosticKind kind, String message) {
        super(message);
        this.kind = kind;
    }

    public ExpressionDiagnosticKind kind() {
        return kind;
    }
}
