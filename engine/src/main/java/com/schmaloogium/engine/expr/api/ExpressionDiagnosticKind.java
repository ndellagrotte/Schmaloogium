// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Closed diagnostic kind domain (§4.9). */
public enum ExpressionDiagnosticKind {
    LEX, PARSE, LIMIT, UNKNOWN_NAME, DUPLICATE_NAME, DUPLICATE_SMOOTH_ID,
    TYPE, ARITY, CYCLE, INVALID_DEPENDENCY, INPUT_ABSENT, INPUT_SCHEMA_MISMATCH,
    DIVIDE_BY_ZERO, DOMAIN, NON_FINITE, INT_RANGE, PROVIDER, UNSUPPORTED_BACKEND, BACKEND_INVARIANT
}
