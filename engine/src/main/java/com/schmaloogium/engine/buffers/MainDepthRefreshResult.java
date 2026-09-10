// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Closed {@code refreshMainDepth} outcomes (PHASE_5_DOC §2.2): {@code ResizeRequired} carries
 * exactly MAIN_DEPTH_RESIZE_REQUIRED and {@code Failed} rejects it, so that transition has one
 * unambiguous carrier.
 */
public sealed interface MainDepthRefreshResult {

    record Unchanged(long version) implements MainDepthRefreshResult {
    }

    record Reattached(long version, long depthAttachmentEpoch)
            implements MainDepthRefreshResult {
    }

    record ResizeRequired(BufferFailure failure) implements MainDepthRefreshResult {
        public ResizeRequired {
            if (failure.code() != BufferFailureCode.MAIN_DEPTH_RESIZE_REQUIRED) {
                throw new IllegalArgumentException(
                        "ResizeRequired requires MAIN_DEPTH_RESIZE_REQUIRED, got "
                                + failure.code());
            }
        }
    }

    record Failed(BufferFailure failure) implements MainDepthRefreshResult {
        public Failed {
            if (failure.code() == BufferFailureCode.MAIN_DEPTH_RESIZE_REQUIRED) {
                throw new IllegalArgumentException(
                        "Failed rejects MAIN_DEPTH_RESIZE_REQUIRED; use ResizeRequired");
            }
        }
    }
}
