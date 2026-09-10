// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Controller factory (§4.12): requires both sinks non-null, transfers sole lifecycle to the
 * caller, performs no evaluation. */
public interface CustomExpressionControllerFactory {
    CustomExpressionController create(ExpressionMetricsSink metricsSink,
                                      ExpressionDiagnosticSink diagnosticSink);
}
