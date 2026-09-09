// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.List;

public sealed interface MaterializationResult {
    record Available(MaterializedSource source) implements MaterializationResult {}
    record Unavailable(SourceKey root, List<EngineDiagnostic> diagnostics)
        implements MaterializationResult {

        public Unavailable {
            diagnostics = List.copyOf(diagnostics);
        }
    }
}
