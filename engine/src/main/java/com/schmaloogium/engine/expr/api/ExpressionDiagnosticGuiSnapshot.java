// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.List;
import java.util.Objects;

/** Immutable final-attempt GUI snapshot (§4.9.1). P7 owns publication; P12 consumes. */
public record ExpressionDiagnosticGuiSnapshot(
        String packFingerprint,
        String configurationFingerprint,
        long attemptSerial,
        ExpressionDiagnosticAttemptOutcome outcome,
        List<ExpressionDiagnosticGuiEntry> entries) {

    public ExpressionDiagnosticGuiSnapshot {
        Objects.requireNonNull(packFingerprint, "packFingerprint");
        Objects.requireNonNull(configurationFingerprint, "configurationFingerprint");
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(entries, "entries");
        entries = List.copyOf(entries);
    }
}
