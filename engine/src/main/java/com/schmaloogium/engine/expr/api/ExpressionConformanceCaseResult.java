// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.List;

/** Adapter-side per-case outcome (§5.6); FAIL reports stable source-free mismatch paths. */
public record ExpressionConformanceCaseResult(
        String caseId,
        String backendSemanticId,
        String fixedSchemaVersion,
        String contextSchemaVersion,
        ExpressionConformanceVerdict verdict,
        List<String> mismatchPaths) {

    public ExpressionConformanceCaseResult {
        mismatchPaths = List.copyOf(mismatchPaths);
    }
}
