// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** Per-refresh aggregate metrics reported through {@link ExpressionMetricsSink} (§4.11).
 * Never records expression text or per-node values. */
public record ExpressionMetrics(
        String planFingerprint,
        long refreshCount,
        long nodeEvaluations,
        long variableMemoHits,
        long variableMemoMisses,
        long uniformSuccesses,
        long uniformErrors,
        long uniformSkips,
        long elapsedNanos,
        String profilerCorrelationId) {

    public ExpressionMetrics {
        Objects.requireNonNull(planFingerprint, "planFingerprint");
        Objects.requireNonNull(profilerCorrelationId, "profilerCorrelationId");
    }
}
