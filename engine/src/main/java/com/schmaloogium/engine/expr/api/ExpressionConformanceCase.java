// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.List;

/** One original conformance fixture (§5.6): a compile request, its scripted step sequence,
 * and independently authored expectations. These are conformance fixtures, not new
 * production evaluator entry points. */
public record ExpressionConformanceCase(
        String caseId,
        CustomExpressionCompileRequest request,
        List<ExpressionConformanceStep> steps,
        ExpressionConformanceExpected expected) {

    public ExpressionConformanceCase {
        if (caseId == null || caseId.isEmpty()) {
            throw new IllegalArgumentException("caseId");
        }
        if (request == null || expected == null) {
            throw new IllegalArgumentException("request and expected are required");
        }
        steps = List.copyOf(steps);
    }
}
