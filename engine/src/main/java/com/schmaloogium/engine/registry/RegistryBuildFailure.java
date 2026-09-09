// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.List;

/**
 * The closed, sanitized registry-wide build failure (PHASE_4_DOC §4.12). The program list
 * is immutable and ordered by requested slot; it may be empty for pack-wide capability,
 * policy, program-state or unsafe-state failures. Driver logs and source text never enter
 * {@code userMessage}. Immutable.
 */
public record RegistryBuildFailure(
        RegistryFailureKind kind,
        List<ProgramBuildFailure> programFailures,
        String diagnosticId,
        String userMessage) {

    public RegistryBuildFailure {
        java.util.Objects.requireNonNull(kind, "kind");
        programFailures = List.copyOf(programFailures == null ? List.of() : programFailures);
        java.util.Objects.requireNonNull(diagnosticId, "diagnosticId");
        if (diagnosticId.isBlank()) {
            throw new IllegalArgumentException("diagnosticId must be non-blank");
        }
        java.util.Objects.requireNonNull(userMessage, "userMessage");
    }
}
