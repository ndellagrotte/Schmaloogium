// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.eval;

import com.schmaloogium.engine.expr.api.CustomExpressionController;
import com.schmaloogium.engine.expr.api.CustomExpressionControllerFactory;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticSink;
import com.schmaloogium.engine.expr.api.ExpressionMetricsSink;
import java.util.Objects;

/** Controller factory (§4.12); transfers sole lifecycle to the caller, performs no
 * evaluation. */
public final class ControllerFactory implements CustomExpressionControllerFactory {

    public static final ControllerFactory INSTANCE = new ControllerFactory();

    private ControllerFactory() {}

    @Override
    public CustomExpressionController create(ExpressionMetricsSink metricsSink,
                                             ExpressionDiagnosticSink diagnosticSink) {
        Objects.requireNonNull(metricsSink, "metricsSink");
        Objects.requireNonNull(diagnosticSink, "diagnosticSink");
        return new Controller(metricsSink, diagnosticSink);
    }
}
