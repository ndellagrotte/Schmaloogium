// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The shadow close outcome (PHASE_7_DOC §5.1). Close invalidates the view before it
 * returns, in every branch.
 */
public sealed interface ShadowExecutionCloseResult {

    record Closed() implements ShadowExecutionCloseResult {
    }

    record Rejected(ShadowExecutionCloseRejection reason) implements ShadowExecutionCloseResult {
    }
}
