// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The publication outcome (PHASE_4_DOC §2.2). Accepted and RecoveredOff both return a fresh
 * atomic snapshot with the generation incremented exactly once; Rejected returns the
 * unchanged prior snapshot with the cause.
 */
public sealed interface PublicationResult {

    record Accepted(PublishedRegistry published) implements PublicationResult {
    }

    record Rejected(PublishedRegistry unchanged, PublicationFailure cause)
            implements PublicationResult {
    }

    record RecoveredOff(PublishedRegistry published, PublicationFailure cause)
            implements PublicationResult {
    }
}
