// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.List;
import java.util.Optional;

public record PackCandidate(
        PackCandidateId id,
        Optional<FilesystemCandidateReference> filesystemReference,
        PackCandidateKind kind,
        String displayName,
        PackCandidateStatus status,
        List<EngineDiagnostic> diagnostics) {

    public PackCandidate {
        filesystemReference = filesystemReference == null ? Optional.empty() : filesystemReference;
        diagnostics = List.copyOf(diagnostics);
    }
}
