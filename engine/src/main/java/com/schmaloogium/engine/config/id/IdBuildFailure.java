// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * Closed build-failure value carried by {@code IdBuildResult.Failed} (PHASE_9_DOC §2.2).
 * A pure builder's failure never mutates a prior table and never touches the local
 * publisher; Phase 7's coordinated rebuild owns the recovered-off consequence.
 */
public record IdBuildFailure(Kind kind, String detail) {

    public IdBuildFailure {
        java.util.Objects.requireNonNull(kind, "kind");
        if (detail == null || detail.isEmpty()) {
            throw new IllegalArgumentException("detail must be non-empty");
        }
    }

    /** Closed failure classes. */
    public enum Kind {
        /** Request component missing or self-inconsistent. */
        INVALID_REQUEST,
        /** Mapping input schema is not the current {@code PackFrontEnd} schema. */
        SCHEMA_MISMATCH,
        /** Registry projection rejected before resolution. */
        SNAPSHOT_INCONSISTENT,
        /** Unexpected internal failure; never thrown out of {@code build}. */
        INTERNAL
    }
}
