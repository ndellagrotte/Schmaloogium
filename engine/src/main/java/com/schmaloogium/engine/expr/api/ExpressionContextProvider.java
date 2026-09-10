// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** One context snapshot per refresh, on the render thread, returned by copy (§4.10). */
public interface ExpressionContextProvider {
    ExpressionContextResult snapshot(ExpressionContextRequest request);
}
