// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import java.util.List;
import java.util.Map;

public record PackDecisionSnapshot(int projectionVersion, int schemaVersion,
        ConfigurationFingerprint configurationFingerprint, List<DecisionSource> sources,
        Map<String, DecisionValue> sections, List<DecisionDiagnostic> diagnostics) {

    public PackDecisionSnapshot {
        sources = List.copyOf(sources);
        sections = Map.copyOf(sections);
        diagnostics = List.copyOf(diagnostics);
    }
}
