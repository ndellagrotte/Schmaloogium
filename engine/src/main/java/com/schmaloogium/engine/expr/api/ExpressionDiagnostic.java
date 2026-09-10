// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** One typed diagnostic record; {@code stableId} never hashes prose (§4.9). */
public record ExpressionDiagnostic(
        String stableId,
        ExpressionDiagnosticKind kind,
        DiagnosticSeverity severity,
        DiagnosticChannel channel,
        ExpressionDiagnosticLocation location,
        String summary) {

    public ExpressionDiagnostic {
        Objects.requireNonNull(stableId, "stableId");
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(summary, "summary");
    }
}
