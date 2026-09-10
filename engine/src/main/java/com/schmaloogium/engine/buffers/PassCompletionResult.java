// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of completing the one open pass snapshot, applying its recorded flip set
 * (PHASE_5_DOC §2.2).
 */
public sealed interface PassCompletionResult {

    record Completed(long frameId) implements PassCompletionResult {
    }

    record Rejected(FrameProtocolRejection reason) implements PassCompletionResult {
    }
}
