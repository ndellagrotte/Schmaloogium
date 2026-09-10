// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.schmaloogium.engine.pack.DiscoveryGeneration;
import com.schmaloogium.engine.pack.PackCandidateId;

/**
 * The pack-selection view-model (PHASE_12_DOC §4.6.1): rows in Phase 3's discovery
 * order, never re-sorted; the {@code (off)} sentinel always first and always
 * selectable. Display names are Phase 3-sanitized and never accepted back as paths;
 * candidate ids are opaque and valid only against their discovery generation.
 */
public record PackSelectionModel(
        DiscoveryGeneration generation,
        List<PackSelectionRow> rows,
        PackCandidateId selected,
        Optional<String> lastActionSummary) {

    public PackSelectionModel {
        Objects.requireNonNull(generation, "generation");
        rows = List.copyOf(rows);
        lastActionSummary = lastActionSummary == null ? Optional.empty() : lastActionSummary;
    }
}
