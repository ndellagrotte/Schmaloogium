// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.SourceAttribution;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.UserChannel;

import java.util.Optional;

public record DecisionDiagnostic(String code, DiagnosticSeverity severity, UserChannel channel,
        Optional<SourceAttribution> location) {

    public DecisionDiagnostic {
        java.util.Objects.requireNonNull(code, "code");
        java.util.Objects.requireNonNull(severity, "severity");
        java.util.Objects.requireNonNull(channel, "channel");
        location = location == null ? Optional.empty() : location;
    }
}
