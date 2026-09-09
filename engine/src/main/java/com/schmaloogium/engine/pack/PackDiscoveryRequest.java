// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import java.nio.file.Path;

import com.schmaloogium.engine.diag.DiagnosticReporter;

public record PackDiscoveryRequest(
        Path shaderpacksDirectory,
        DiagnosticReporter diagnostics) {

    public PackDiscoveryRequest {
        java.util.Objects.requireNonNull(shaderpacksDirectory, "shaderpacksDirectory");
    }
}
