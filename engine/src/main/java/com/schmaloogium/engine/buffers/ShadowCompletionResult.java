// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of completePass: completion that consumes the token or a protocol rejection.
 */
public sealed interface ShadowCompletionResult {

    record Completed(long frameId) implements ShadowCompletionResult {
    }

    record Rejected(ShadowProtocolRejection reason) implements ShadowCompletionResult {

        public Rejected {
            java.util.Objects.requireNonNull(reason, "reason");
        }
    }
}
