// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.pack.ImmutableBytes;

import java.util.Optional;

public record IdMappingParseRequest(
        MappingKind kind,
        Optional<ImmutableBytes> source,
        MappingOrigin origin,
        IdMappingMacroEnvironment environment,
        DiagnosticReporter diagnostics) {

    public IdMappingParseRequest {
        source = source == null ? Optional.empty() : source;
        java.util.Objects.requireNonNull(origin, "origin");
        java.util.Objects.requireNonNull(environment, "environment");
    }
}
