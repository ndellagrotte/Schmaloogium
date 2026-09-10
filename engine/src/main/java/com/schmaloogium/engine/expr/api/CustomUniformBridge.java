// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** The one custom-participant bridge Phase 6 calls after built-ins on every successful
 * activation (§4.8, §5.3). Implementations never throw through the render callback and
 * never touch GL. */
public interface CustomUniformBridge {
    CustomRefreshResult refresh(BuiltInExpressionView values, CustomUniformUploadSink uploads);
}
