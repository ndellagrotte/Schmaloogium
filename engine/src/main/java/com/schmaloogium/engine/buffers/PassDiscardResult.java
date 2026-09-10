// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of discarding the open pass snapshot before a committed draw, without flips
 * (PHASE_5_DOC §2.2).
 */
public sealed interface PassDiscardResult {

    record Discarded(long frameId) implements PassDiscardResult {
    }

    record Rejected(FrameProtocolRejection reason) implements PassDiscardResult {
    }
}
