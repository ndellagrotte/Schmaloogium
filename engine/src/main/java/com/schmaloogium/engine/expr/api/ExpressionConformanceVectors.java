// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.List;

/** P11-owned original vector catalog consumed by Phase 2's RUN-EXPRESSION-CONFORMANCE
 * adapter (§5.6). No {@code :engine} dependency on any harness. */
public interface ExpressionConformanceVectors {
    List<ExpressionConformanceCase> cases();
}
