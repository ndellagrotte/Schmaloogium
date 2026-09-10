// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** Source-free GUI projection row (§4.9.1): no raw expression, path, span, attribution,
 * dependency chain, or exception text ever enters this record. */
public record ExpressionDiagnosticGuiEntry(
        String stableId,
        ExpressionDiagnosticKind kind,
        DiagnosticSeverity severity,
        String declarationName,
        String summary) {

    public ExpressionDiagnosticGuiEntry {
        Objects.requireNonNull(stableId, "stableId");
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(declarationName, "declarationName");
        Objects.requireNonNull(summary, "summary");
    }
}
