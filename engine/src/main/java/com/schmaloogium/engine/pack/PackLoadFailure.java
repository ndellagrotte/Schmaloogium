// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.diag.EngineDiagnostic;

public record PackLoadFailure(
        PackLoadFailureCode code,
        EngineDiagnostic primaryDiagnostic) {

    public PackLoadFailure {
        java.util.Objects.requireNonNull(code, "code");
        java.util.Objects.requireNonNull(primaryDiagnostic, "primaryDiagnostic");
    }
}
