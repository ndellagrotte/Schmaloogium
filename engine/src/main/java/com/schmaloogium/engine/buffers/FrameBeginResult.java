// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of beginning a frame on the estate (PHASE_5_DOC §2.2).
 */
public sealed interface FrameBeginResult {

    record Begun(long estateGeneration, long depthAttachmentEpoch, long frameId)
            implements FrameBeginResult {
    }

    record Rejected(FrameProtocolRejection reason) implements FrameBeginResult {
    }

    record BackendFailed(BufferFailure failure) implements FrameBeginResult {
    }
}
