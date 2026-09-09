// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.Optional;

/**
 * One canonical per-slot evidence row (PHASE_4_DOC §4.6): exactly one per catalog slot,
 * immutable, catalog-ordered (never maps or completion order). {@code sourcePresent} is the
 * requested slot's independent pre-build fact; {@code ownBuild} is its required
 * own-build classification; {@code driverLog} carries deterministic sanitized failure
 * detail for {@code FAILED} rows and is empty otherwise. Barrier/publication failures occur
 * after this snapshot and can never alter a row. Phase 2 goldens and Phase 7 runtime
 * manifests copy these values without reconstruction.
 */
public record ProgramResolutionProjection(
        ProgramSlotId slot,
        ProgramResolutionStatus status,
        Optional<ProgramSlotId> from,
        boolean sourcePresent,
        ProgramOwnBuildDisposition ownBuild,
        String driverLog) {

    public ProgramResolutionProjection {
        java.util.Objects.requireNonNull(slot, "slot");
        from = from == null ? Optional.empty() : from;
        java.util.Objects.requireNonNull(ownBuild, "ownBuild");
        java.util.Objects.requireNonNull(driverLog, "driverLog");
    }
}
