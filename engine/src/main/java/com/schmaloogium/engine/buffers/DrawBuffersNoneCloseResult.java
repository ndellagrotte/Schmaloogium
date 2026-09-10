// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Closed draw-buffers-none lease close outcomes (PHASE_5_DOC §2.2).
 */
public sealed interface DrawBuffersNoneCloseResult {

    record Restored(long frameId) implements DrawBuffersNoneCloseResult {
    }

    record AlreadyClosed(long frameId) implements DrawBuffersNoneCloseResult {
    }

    record Rejected(FrameProtocolRejection reason) implements DrawBuffersNoneCloseResult {
    }

    record BackendFailed(BufferFailure failure, boolean fullClearRequired)
            implements DrawBuffersNoneCloseResult {
    }
}
