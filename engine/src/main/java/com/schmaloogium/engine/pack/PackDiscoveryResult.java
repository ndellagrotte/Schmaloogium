// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.List;

public record PackDiscoveryResult(
        DiscoveryGeneration generation,
        List<PackCandidate> candidates,
        List<EngineDiagnostic> diagnostics) {

    public PackDiscoveryResult {
        candidates = List.copyOf(candidates);
        diagnostics = List.copyOf(diagnostics);
    }
}
