// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * One closed, sanitized publication failure (PHASE_4_DOC §4.12). Neither field changes a
 * candidate build disposition or any resolution projection. Immutable.
 */
public record PublicationFailure(
        PublicationFailureKind kind,
        String diagnosticId,
        String userMessage) {

    public PublicationFailure {
        java.util.Objects.requireNonNull(kind, "kind");
        java.util.Objects.requireNonNull(diagnosticId, "diagnosticId");
        if (diagnosticId.isBlank()) {
            throw new IllegalArgumentException("diagnosticId must be non-blank");
        }
        java.util.Objects.requireNonNull(userMessage, "userMessage");
    }
}
