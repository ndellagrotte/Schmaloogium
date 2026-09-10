// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** A texture unit that had no binding, with the classified reason. */
public record MissingTextureBinding(TextureBindingDiagnosticCode reason, String exactName,
                                    int unit) {

    public MissingTextureBinding {
        java.util.Objects.requireNonNull(reason, "reason");
        java.util.Objects.requireNonNull(exactName, "exactName");
    }
}
