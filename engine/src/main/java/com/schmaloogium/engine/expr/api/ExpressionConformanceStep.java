// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.List;

/** Closed conformance step sequence (§5.6): scripted activation/reset/close plus refreshes
 * driving the real bridge with P6-conforming scripted views and sinks. Close is terminal
 * and last. */
public sealed interface ExpressionConformanceStep {

    record Activate(List<ExpressionContextResult> contexts, List<Float> randomValues)
            implements ExpressionConformanceStep {}

    record Refresh(BuiltInExpressionView builtIns, List<CustomSubmitResult> sinkResults)
            implements ExpressionConformanceStep {}

    record Reset(ExpressionResetReason reason) implements ExpressionConformanceStep {}

    record Close() implements ExpressionConformanceStep {}
}
