// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of opening the draw-buffers-none scope (PHASE_5_DOC §2.2).
 */
public sealed interface DrawBuffersNoneOpenResult {

    record Opened(DrawBuffersNoneLease lease) implements DrawBuffersNoneOpenResult {
    }

    record Rejected(FrameProtocolRejection reason) implements DrawBuffersNoneOpenResult {
    }

    record BackendFailed(BufferFailure failure) implements DrawBuffersNoneOpenResult {
    }
}
