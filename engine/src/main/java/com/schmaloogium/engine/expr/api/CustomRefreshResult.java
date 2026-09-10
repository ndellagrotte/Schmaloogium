// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** Closed refresh result (§4.8). Expression-local errors never produce {@code Aborted};
 * {@code Aborted} is reserved for structural failures and always reports the authoritative
 * accepted/skippedAbsent/rejected prefix counters. */
public sealed interface CustomRefreshResult {

    record NoCustoms() implements CustomRefreshResult {}

    record Completed(long accepted, long skippedAbsent, long rejected) implements CustomRefreshResult {}

    record Aborted(String diagnosticId, long accepted, long skippedAbsent, long rejected)
            implements CustomRefreshResult {
        public Aborted {
            Objects.requireNonNull(diagnosticId, "diagnosticId");
        }
    }
}
