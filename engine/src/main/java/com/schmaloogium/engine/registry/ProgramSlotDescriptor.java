// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.Optional;
import java.util.Set;

/**
 * One immutable catalog slot row (PHASE_4_DOC §2.2). Virtual slots have empty source stem
 * and fallback; fixed-terminal raster roots (for example {@code shadow}) have an empty
 * fallback. {@code permittedBands} is the closed set of bands the slot may execute in.
 * Immutable.
 */
public record ProgramSlotDescriptor(
        ProgramSlotId id,
        StageId stage,
        ProgramSlotKind kind,
        Optional<String> sourceStem,
        Optional<ProgramSlotId> fallback,
        Set<StageBand> permittedBands) {

    public ProgramSlotDescriptor {
        java.util.Objects.requireNonNull(id, "id");
        java.util.Objects.requireNonNull(stage, "stage");
        java.util.Objects.requireNonNull(kind, "kind");
        sourceStem = sourceStem == null ? Optional.empty() : sourceStem;
        fallback = fallback == null ? Optional.empty() : fallback;
        permittedBands = Set.copyOf(permittedBands);
    }
}
