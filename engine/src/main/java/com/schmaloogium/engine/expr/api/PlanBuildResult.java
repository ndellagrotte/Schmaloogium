// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.List;

/** Closed plan-build result (§4.1): {@code Failure} carries a non-empty diagnostic list and
 * no plan; {@code Success} may carry none; {@code Partial} carries at least one. */
public sealed interface PlanBuildResult {

    record Success(CustomExpressionPlan plan, List<ExpressionDiagnostic> diagnostics)
            implements PlanBuildResult {
        public Success {
            if (plan == null) {
                throw new IllegalArgumentException("plan");
            }
            diagnostics = List.copyOf(diagnostics);
        }
    }

    record Partial(CustomExpressionPlan plan, List<ExpressionDiagnostic> diagnostics)
            implements PlanBuildResult {
        public Partial {
            if (plan == null) {
                throw new IllegalArgumentException("plan");
            }
            if (diagnostics.isEmpty()) {
                throw new IllegalArgumentException("partial build needs at least one diagnostic");
            }
            diagnostics = List.copyOf(diagnostics);
        }
    }

    record Failure(List<ExpressionDiagnostic> diagnostics) implements PlanBuildResult {
        public Failure {
            if (diagnostics.isEmpty()) {
                throw new IllegalArgumentException("failure needs a non-empty diagnostic list");
            }
            diagnostics = List.copyOf(diagnostics);
        }
    }
}
