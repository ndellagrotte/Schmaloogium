// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** The custom-expression controller lifecycle (§4.12). The composition thread installs
 * exactly one controller before first use; the render thread owns refresh and mutable
 * evaluation state. */
public interface CustomExpressionController extends CustomUniformBridge, AutoCloseable {

    PlanActivationResult activate(CustomExpressionPlan plan,
                                  ExpressionContextProvider contexts,
                                  RandomSource random);

    void reset(ExpressionResetReason reason);

    /** Terminal; idempotent; no callbacks after close. */
    @Override
    void close();
}
