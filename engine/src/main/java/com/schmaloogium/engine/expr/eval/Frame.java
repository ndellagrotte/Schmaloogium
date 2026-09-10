// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.eval;

import com.schmaloogium.engine.expr.api.BuiltInExpressionView;
import com.schmaloogium.engine.expr.api.ExpressionContextSnapshot;
import com.schmaloogium.engine.expr.api.RandomSource;
import com.schmaloogium.engine.expr.plan.Program;
import com.schmaloogium.engine.expr.state.SmoothState;

/** One evaluation traversal: scratch cells, the epoch's memo/state, injected providers,
 * and the slot currently being evaluated (for smooth site lookup). Render-thread confined. */
final class Frame {

    /** Evaluates one definition slot through the controller's transaction (§4.5). */
    interface SlotEvaluator {
        void evaluate(int slot, Frame frame) throws ExprEvalException, ProviderProtocolException;
    }

    final Program program;
    final MemoTable memo;
    final Cells cells = new Cells();
    final SmoothState smooth;
    final RandomSource random;
    final BuiltInExpressionView view;
    final ExpressionContextSnapshot context;
    final SlotEvaluator evaluator;
    final int epoch;
    double nowSeconds;
    int ownerSlot;
    long nodeEvaluations;
    int randomSamples;
    long variableMemoHits;
    long variableMemoMisses;

    Frame(Program program, MemoTable memo, SmoothState smooth, int epoch, double nowSeconds,
          RandomSource random, BuiltInExpressionView view, ExpressionContextSnapshot context,
          SlotEvaluator evaluator) {
        this.program = program;
        this.memo = memo;
        this.smooth = smooth;
        this.epoch = epoch;
        this.nowSeconds = nowSeconds;
        this.random = random;
        this.view = view;
        this.context = context;
        this.evaluator = evaluator;
    }
}
