// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.List;

/** Provenance of a bound texture object with its explanatory diagnostics. */
public record BindingOrigin(BindingOriginKind kind, List<TextureBindingDiagnostic> diagnostics) {

    public BindingOrigin {
        java.util.Objects.requireNonNull(kind, "kind");
        diagnostics = List.copyOf(diagnostics);
    }
}
