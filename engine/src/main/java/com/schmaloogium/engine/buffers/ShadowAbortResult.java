// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of abortPass: abort that consumes the token without flips or a protocol rejection.
 */
public sealed interface ShadowAbortResult {

    record Aborted(long frameId, String diagnosticId, boolean fullClearRequired)
            implements ShadowAbortResult {

        public Aborted {
            java.util.Objects.requireNonNull(diagnosticId, "diagnosticId");
        }
    }

    record Rejected(ShadowProtocolRejection reason) implements ShadowAbortResult {

        public Rejected {
            java.util.Objects.requireNonNull(reason, "reason");
        }
    }
}
