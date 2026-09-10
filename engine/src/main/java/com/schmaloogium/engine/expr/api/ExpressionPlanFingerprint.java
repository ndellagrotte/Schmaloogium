// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Opaque deterministic plan identity (§2.3). */
public record ExpressionPlanFingerprint(String value) {

    public ExpressionPlanFingerprint {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("fingerprint must be non-empty");
        }
    }
}
