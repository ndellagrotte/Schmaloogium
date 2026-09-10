// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** Closed upload sink outcomes (§5.4): accepted, normal active-program-absent skip, or a
 * Phase 6-owned rejection carrying its own stable diagnostic id. */
public sealed interface CustomSubmitResult {

    record Accepted() implements CustomSubmitResult {}

    record SkippedAbsent() implements CustomSubmitResult {}

    record Rejected(String stableDiagnosticId) implements CustomSubmitResult {
        public Rejected {
            Objects.requireNonNull(stableDiagnosticId, "stableDiagnosticId");
        }
    }
}
