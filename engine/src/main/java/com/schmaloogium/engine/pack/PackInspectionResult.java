// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import java.util.List;
import java.util.Optional;

import com.schmaloogium.engine.config.InternalOptionSnapshot;

public sealed interface PackInspectionResult {
    record Off() implements PackInspectionResult {}
    record Failed(PackLoadFailure failure, List<DecisionDiagnostic> diagnostics)
        implements PackInspectionResult {

        public Failed {
            java.util.Objects.requireNonNull(failure, "failure");
            diagnostics = List.copyOf(diagnostics);
        }
    }

    record Inspected(PackConfiguration configuration, PackDecisionSnapshot snapshot,
        Optional<String> archiveSha512) implements PackInspectionResult {

        public Inspected {
            java.util.Objects.requireNonNull(configuration, "configuration");
            java.util.Objects.requireNonNull(snapshot, "snapshot");
            archiveSha512 = archiveSha512 == null ? Optional.empty() : archiveSha512;
        }
    }
}
