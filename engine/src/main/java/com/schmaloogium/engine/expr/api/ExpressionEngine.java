// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import com.schmaloogium.engine.expr.eval.ControllerFactory;
import com.schmaloogium.engine.expr.plan.PlanCompiler;

/** Published construction point for the Phase 11 expression engine (§5.1). Implementation
 * classes outside {@code engine.expr.api} are not contract-visible; composition wires
 * everything through this facade. */
public final class ExpressionEngine {

    private ExpressionEngine() {}

    public static CustomExpressionCompiler compiler() {
        return new PlanCompiler();
    }

    public static CustomExpressionControllerFactory controllerFactory() {
        return ControllerFactory.INSTANCE;
    }
}
