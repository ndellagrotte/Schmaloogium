// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * A non-blank diagnostic identifier crossing every closed failure result (PHASE_7_DOC §5.1).
 * Pure value; it authenticates nothing and authorizes nothing.
 */
public record FailureId(String diagnosticId) {

    public FailureId {
        if (diagnosticId == null || diagnosticId.isBlank()) {
            throw new IllegalArgumentException("diagnosticId must be non-blank");
        }
    }

    @Override
    public String toString() {
        return diagnosticId;
    }
}
