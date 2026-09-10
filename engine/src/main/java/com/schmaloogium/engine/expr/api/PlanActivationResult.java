// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** Closed activation result (§4.12): atomic install, or rejection that keeps the prior
 * tuple until the next refresh. */
public sealed interface PlanActivationResult {

    record Activated(String planFingerprint) implements PlanActivationResult {
        public Activated {
            if (planFingerprint == null || planFingerprint.isEmpty()) {
                throw new IllegalArgumentException("planFingerprint");
            }
        }
    }

    record Rejected(String diagnosticId) implements PlanActivationResult {
        public Rejected {
            Objects.requireNonNull(diagnosticId, "diagnosticId");
        }
    }
}
