// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Closed {@code copyDepth} outcomes (PHASE_5_DOC §2.2).
 */
public sealed interface DepthCopyResult {

    record Copied(DepthCopyPoint point, boolean initialized) implements DepthCopyResult {
    }

    record DuplicateIgnored(DepthCopyPoint point, String diagnosticId)
            implements DepthCopyResult {
    }

    record Rejected(FrameProtocolRejection reason) implements DepthCopyResult {
    }

    record BackendDegraded(DepthCopyPoint point, BufferFailure failure, String diagnosticId)
            implements DepthCopyResult {
    }
}
