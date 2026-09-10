// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of committing or aborting the open frame (PHASE_5_DOC §2.2).
 */
public sealed interface FrameEndResult {

    record Committed(long frameId) implements FrameEndResult {
    }

    record Aborted(long frameId, String diagnosticId, boolean fullClearRequired)
            implements FrameEndResult {
    }

    record Rejected(FrameProtocolRejection reason) implements FrameEndResult {
    }

    record BackendFailed(BufferFailure failure, boolean fullClearRequired)
            implements FrameEndResult {
    }
}
