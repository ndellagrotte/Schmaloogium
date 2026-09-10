// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The shadow open outcome (PHASE_7_DOC §5.1). WRONG_THREAD is checked before
 * ALREADY_ACTIVE; open is non-nestable.
 */
public sealed interface ShadowExecutionOpenResult {

    record Opened(ShadowExecutionView view) implements ShadowExecutionOpenResult {
    }

    record Rejected(ShadowExecutionOpenRejection reason) implements ShadowExecutionOpenResult {
    }
}
