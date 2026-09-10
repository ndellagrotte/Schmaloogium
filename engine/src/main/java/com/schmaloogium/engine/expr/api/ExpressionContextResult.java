// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** Closed context availability result (§4.10). The provider's supplied {@code diagnosticId}
 * is authoritative for an Unavailable result. */
public sealed interface ExpressionContextResult {

    record Available(ExpressionContextSnapshot snapshot) implements ExpressionContextResult {
        public Available {
            Objects.requireNonNull(snapshot, "snapshot");
        }
    }

    record Unavailable(String diagnosticId) implements ExpressionContextResult {
        public Unavailable {
            Objects.requireNonNull(diagnosticId, "diagnosticId");
        }
    }
}
