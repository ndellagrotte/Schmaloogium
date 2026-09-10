// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of degradeToNeutral: neutralization, idempotent repetition, or a protocol rejection.
 */
public sealed interface ShadowNeutralizationResult {

    record Neutralized(long generation, String diagnosticId, boolean openSnapshotAborted)
            implements ShadowNeutralizationResult {

        public Neutralized {
            java.util.Objects.requireNonNull(diagnosticId, "diagnosticId");
        }
    }

    record AlreadyNeutral(long generation, String diagnosticId)
            implements ShadowNeutralizationResult {

        public AlreadyNeutral {
            java.util.Objects.requireNonNull(diagnosticId, "diagnosticId");
        }
    }

    record Rejected(ShadowProtocolRejection reason)
            implements ShadowNeutralizationResult {

        public Rejected {
            java.util.Objects.requireNonNull(reason, "reason");
        }
    }
}
