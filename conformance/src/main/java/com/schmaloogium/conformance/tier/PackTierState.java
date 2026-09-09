// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.tier;

import java.util.Objects;

/**
 * One pack's recorded state for one tier: the outcome plus the note that says where
 * its evidence lives or why it is absent. This is the in-memory shape the future
 * {@code TierLedger} (§4.2.5, [D-P2-47]) will persist per run — the ledger's
 * evidence-domain freezing and index machinery land with the run-root/cache
 * infrastructure, which the live-capture leg needs first; until then states are
 * reported from the harness run directly and never imply ledger completeness.
 */
public record PackTierState(String packId, String packVersion, Tier tier, TierOutcome outcome,
        String note) {

    public PackTierState {
        Objects.requireNonNull(packId, "packId");
        Objects.requireNonNull(packVersion, "packVersion");
        Objects.requireNonNull(tier, "tier");
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(note, "note");
    }
}
