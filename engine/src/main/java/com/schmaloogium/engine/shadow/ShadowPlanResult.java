// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import java.util.Objects;

/**
 * Closed planning result (PHASE_8_DOC §4.1): a ready plan, structurally-valid absence, or
 * a typed disable with a stable diagnostic id. Precedence is structural validation first
 * (malformed input disables, never {@code NotRequested}), then absence, then the listed
 * policy checks, then the required hook rows.
 */
public sealed interface ShadowPlanResult {

    record Ready(ShadowPlan plan) implements ShadowPlanResult {

        public Ready {
            Objects.requireNonNull(plan, "plan");
        }
    }

    record NotRequested() implements ShadowPlanResult {
    }

    record Disabled(ShadowDisableReason reason, String diagnosticId) implements ShadowPlanResult {

        public Disabled {
            Objects.requireNonNull(reason, "reason");
            Objects.requireNonNull(diagnosticId, "diagnosticId");
        }
    }
}
