// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of bind, clear, or copyDepth on an open shadow pass snapshot.
 */
public sealed interface ShadowOperationResult {

    record Applied() implements ShadowOperationResult {
    }

    record Rejected(ShadowProtocolRejection reason) implements ShadowOperationResult {

        public Rejected {
            java.util.Objects.requireNonNull(reason, "reason");
        }
    }

    /**
     * Backend failure: leaves the pass token open and flip state unchanged for abort.
     */
    record BackendFailed(BufferFailure failure) implements ShadowOperationResult {

        public BackendFailed {
            java.util.Objects.requireNonNull(failure, "failure");
        }
    }
}
