// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.OptionalInt;

/** One texture-binding diagnostic with its optional texture unit. */
public record TextureBindingDiagnostic(TextureBindingDiagnosticCode code, String exactName,
                                       OptionalInt unit) {

    public TextureBindingDiagnostic {
        java.util.Objects.requireNonNull(code, "code");
        java.util.Objects.requireNonNull(exactName, "exactName");
        java.util.Objects.requireNonNull(unit, "unit");
    }
}
