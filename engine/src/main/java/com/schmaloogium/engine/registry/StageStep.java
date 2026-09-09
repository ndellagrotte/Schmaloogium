// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * One schedule occurrence: stage identity, band, and population (PHASE_4_DOC §2.2).
 * Every returned {@link PassDescriptor} carries the identical contained {@code StageStep}
 * value; lookup and traversal never synthesize new records.
 */
public record StageStep(StageId stage, StageBand band, PassPopulation population) {

    public StageStep {
        java.util.Objects.requireNonNull(stage, "stage");
        java.util.Objects.requireNonNull(band, "band");
        java.util.Objects.requireNonNull(population, "population");
    }
}
