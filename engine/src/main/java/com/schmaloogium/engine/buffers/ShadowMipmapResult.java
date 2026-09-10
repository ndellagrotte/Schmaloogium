// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.List;

/**
 * Result of generateShadowMipmaps: per-buffer outcomes, result-level neutralization,
 * or a protocol rejection.
 */
public sealed interface ShadowMipmapResult {

    /**
     * One outcome per requested buffer, in the policy's canonical order.
     */
    record Generated(List<ShadowMipmapOutcome> outcomes) implements ShadowMipmapResult {

        public Generated {
            outcomes = List.copyOf(java.util.Objects.requireNonNull(outcomes, "outcomes"));
        }
    }

    record Neutralized(
            LogicalBuffer buffer, BufferFailure failure, String diagnosticId,
            boolean openSnapshotAborted) implements ShadowMipmapResult {

        public Neutralized {
            java.util.Objects.requireNonNull(buffer, "buffer");
            java.util.Objects.requireNonNull(failure, "failure");
            java.util.Objects.requireNonNull(diagnosticId, "diagnosticId");
        }
    }

    record Rejected(ShadowProtocolRejection reason) implements ShadowMipmapResult {

        public Rejected {
            java.util.Objects.requireNonNull(reason, "reason");
        }
    }
}
