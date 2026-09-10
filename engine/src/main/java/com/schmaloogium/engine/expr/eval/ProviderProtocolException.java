// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.eval;

import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;

/** A provider-protocol or backend-invariant failure (§4.8): aborts the remaining refresh,
 * keeps the committed prefix, and never counts as an expression-local error. Carries the
 * stable diagnostic identity when one is prescribed (for example the context provider's
 * own supplied ID), plus the closed diagnostic kind for the structural report. */
public final class ProviderProtocolException extends Exception {

    private final ExpressionDiagnosticKind kind;
    private final String diagnosticId;

    public ProviderProtocolException(String reason) {
        this(reason, ExpressionDiagnosticKind.PROVIDER, null);
    }

    public ProviderProtocolException(String reason, ExpressionDiagnosticKind kind, String diagnosticId) {
        super(reason);
        this.kind = kind;
        this.diagnosticId = diagnosticId;
    }

    public ExpressionDiagnosticKind kind() {
        return kind;
    }

    public String diagnosticId() {
        return diagnosticId;
    }
}
