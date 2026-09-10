// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of pass snapshot acquisition (PHASE_5_DOC §2.2).
 */
public sealed interface PassSnapshotResult {

    record Acquired(PassBufferSnapshot snapshot) implements PassSnapshotResult {
    }

    record Rejected(FrameProtocolRejection reason) implements PassSnapshotResult {
    }

    /**
     * Mutation-bearing preparation failure: {@code frameAborted} is always true — no snapshot is
     * exposed, the frame token is already consumed, and all frame pass/binding snapshots are
     * invalidated.
     */
    record Failed(BufferFailure failure, String diagnosticId, boolean frameAborted)
            implements PassSnapshotResult {
    }
}
