// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import java.util.Optional;

/**
 * The polled status of an issued reload token (PHASE_7_DOC §5.1). Issued tokens stay
 * queryable — no expiry, no supersession history; only unissued tokens answer Unknown.
 */
public sealed interface ReloadStatus {

    record Queued() implements ReloadStatus {
    }

    record Building() implements ReloadStatus {
    }

    record Active(PipelineIdentity identity, PipelineVersion version) implements ReloadStatus {
    }

    record Off(PipelineVersion version) implements ReloadStatus {
    }

    record Failed(FailureId failure) implements ReloadStatus {
    }

    /** The token was never issued by this controller; a mutation-free answer. */
    record Unknown() implements ReloadStatus {
    }

    /** Convenience view of an active status. */
    static Optional<PipelineVersion> activeVersion(ReloadStatus status) {
        return status instanceof Active active ? Optional.of(active.version()) : Optional.empty();
    }
}
