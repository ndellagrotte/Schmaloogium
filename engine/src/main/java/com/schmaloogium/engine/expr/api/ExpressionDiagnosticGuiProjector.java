// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.List;

/** Pure source-free projection of typed diagnostics into the display vocabulary (§4.9.1).
 * Invalid caller arguments throw {@link IllegalArgumentException} before publication. */
public interface ExpressionDiagnosticGuiProjector {

    ExpressionDiagnosticGuiSnapshot project(
            String packFingerprint,
            String configurationFingerprint,
            long attemptSerial,
            ExpressionDiagnosticAttemptOutcome outcome,
            List<ExpressionDiagnostic> diagnostics);
}
