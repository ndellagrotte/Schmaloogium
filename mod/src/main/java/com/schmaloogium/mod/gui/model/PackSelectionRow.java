// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.pack.CompatibilityStatus;
import com.schmaloogium.engine.pack.DiscoveryGeneration;
import com.schmaloogium.engine.pack.PackCandidateId;
import com.schmaloogium.engine.pack.PackCandidateKind;
import com.schmaloogium.engine.pack.PackCandidateStatus;

/** One selectable row: kind/status/compatibility badges plus attributed diagnostics. */
public record PackSelectionRow(
        PackCandidateId id,
        PackCandidateKind kind,
        String displayName,
        PackCandidateStatus status,
        Optional<CompatibilityStatus> compatibility,
        List<EngineDiagnostic> diagnostics,
        boolean interactive) {

    public PackSelectionRow {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(status, "status");
        compatibility = compatibility == null ? Optional.empty() : compatibility;
        diagnostics = List.copyOf(diagnostics);
    }
}
