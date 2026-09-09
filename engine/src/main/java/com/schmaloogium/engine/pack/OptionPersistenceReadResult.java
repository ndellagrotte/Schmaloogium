// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.OptionState;
import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.List;
import java.util.Optional;

public sealed interface OptionPersistenceReadResult {
    record Completed(
        OptionState state,
        PersistenceReadStatus status,
        Optional<PersistenceFailure> failure,
        List<EngineDiagnostic> diagnostics) implements OptionPersistenceReadResult {

        public Completed {
            failure = failure == null ? Optional.empty() : failure;
            diagnostics = List.copyOf(diagnostics);
        }
    }

    record InvalidRequest(
        PersistenceFailure failure,
        List<EngineDiagnostic> diagnostics) implements OptionPersistenceReadResult {

        public InvalidRequest {
            java.util.Objects.requireNonNull(failure, "failure");
            diagnostics = List.copyOf(diagnostics);
        }
    }
}
