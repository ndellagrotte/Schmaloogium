// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.List;
import java.util.Optional;

public record GlobalShaderOptionsReadResult(
        EngineOptionData values,
        PersistenceReadStatus status,
        Optional<PersistenceFailure> failure,
        List<EngineDiagnostic> diagnostics) {

    public GlobalShaderOptionsReadResult {
        java.util.Objects.requireNonNull(values, "values");
        failure = failure == null ? Optional.empty() : failure;
        diagnostics = List.copyOf(diagnostics);
    }
}
