// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.ProgramBindingSelection;
import java.util.List;

/** Degraded texture-binding result: the selection, its diagnostics and the mandated action. */
public record TextureBindingDegradation(ProgramBindingSelection selection,
                                        List<TextureBindingDiagnostic> diagnostics,
                                        TextureBindingAction action) {

    public TextureBindingDegradation {
        java.util.Objects.requireNonNull(selection, "selection");
        java.util.Objects.requireNonNull(action, "action");
        diagnostics = List.copyOf(diagnostics);
    }
}
