// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.List;

/** One refresh observation (§5.6): commands in submission order, runtime diagnostics in
 * callback order (kind/id/location lists same length), the closed refresh result, and the
 * exact provider/random sample counts. */
public record ExpressionConformanceObservation(
        List<CustomUploadCommand> commands,
        List<ExpressionDiagnosticKind> diagnosticKinds,
        List<String> stableDiagnosticIds,
        List<ExpressionDiagnosticLocation> diagnosticLocations,
        CustomRefreshResult refreshResult,
        int contextSamples,
        int randomSamples) {

    public ExpressionConformanceObservation {
        commands = List.copyOf(commands);
        diagnosticKinds = List.copyOf(diagnosticKinds);
        stableDiagnosticIds = List.copyOf(stableDiagnosticIds);
        diagnosticLocations = List.copyOf(diagnosticLocations);
    }
}
