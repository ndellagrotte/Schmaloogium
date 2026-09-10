// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.frame.ShadowInvocationSlot;

import java.util.Objects;

/**
 * Closed pass-factory result (PHASE_8_DOC §2.2): a ready invocation slot with its
 * minted lifecycle epoch, a runtime disable, or the already-closed marker for a
 * re-request of a closed publication.
 */
public sealed interface ShadowPassBuildResult {

    record Ready(ShadowInvocationSlot slot, com.schmaloogium.engine.frame.ShadowSlotEpoch epoch)
            implements ShadowPassBuildResult {

        public Ready {
            Objects.requireNonNull(slot, "slot");
            Objects.requireNonNull(epoch, "epoch");
        }
    }

    record DisabledRuntime(ShadowDisableReason reason, String diagnosticId)
            implements ShadowPassBuildResult {

        public DisabledRuntime {
            Objects.requireNonNull(reason, "reason");
            Objects.requireNonNull(diagnosticId, "diagnosticId");
        }
    }

    record Closed() implements ShadowPassBuildResult {
    }
}
