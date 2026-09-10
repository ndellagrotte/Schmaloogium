// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.List;
import java.util.Optional;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;

/**
 * The shared issues indicator (PHASE_12_DOC §4.9): count plus worst severity of the
 * current pack's SHADER_GUI store; absent when empty. Views render it from this value
 * only — they never rank severities themselves.
 */
public record IssuesIndicator(int count, DiagnosticSeverity worst) {

    public static Optional<IssuesIndicator> of(List<EngineDiagnostic> diagnostics) {
        if (diagnostics == null || diagnostics.isEmpty()) {
            return Optional.empty();
        }
        DiagnosticSeverity worst = DiagnosticSeverity.INFO;
        for (EngineDiagnostic diagnostic : diagnostics) {
            if (diagnostic.severity().ordinal() > worst.ordinal()) {
                worst = diagnostic.severity();
            }
        }
        return Optional.of(new IssuesIndicator(diagnostics.size(), worst));
    }
}
