// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/** Closed publication outcome (PHASE_9_DOC §2.2). */
public sealed interface IdPublishResult {

    /** The candidate transferred exactly once; Phase 7 owns the returned publication. */
    record Published(PublishedIdRuntime runtime) implements IdPublishResult {
    }

    /** The candidate is untouched and caller-owned; the current runtime is unchanged. */
    record Rejected(Rejection reason) implements IdPublishResult {
    }

    /** Closed rejection classes. */
    enum Rejection {
        /** The caller already closed the candidate without publishing it. */
        CANDIDATE_CLOSED,
        /** A previous publication already transferred this candidate. */
        CANDIDATE_ALREADY_PUBLISHED,
        /** The publisher is permanently inactive (off recovery or overflow). */
        PUBLISHER_TERMINAL,
        /** The positive generation space is exhausted: terminal shaders-off. */
        GENERATION_OVERFLOW
    }
}
