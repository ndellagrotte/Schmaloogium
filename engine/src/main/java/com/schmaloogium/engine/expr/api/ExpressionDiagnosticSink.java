// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Typed synchronous diagnostic destination owned by composition (§4.9). */
public interface ExpressionDiagnosticSink {
    void report(ExpressionDiagnostic diagnostic);
}
