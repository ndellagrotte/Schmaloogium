// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Per-refresh aggregate metrics sink (§4.11): synchronous, no-op by default; sink failure
 * is caught, reported once, and disables metrics without affecting evaluation. */
public interface ExpressionMetricsSink {
    void record(ExpressionMetrics metrics);
}
