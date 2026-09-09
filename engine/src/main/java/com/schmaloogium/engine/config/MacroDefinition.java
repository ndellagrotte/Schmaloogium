// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** One object-like macro definition; replacement is non-null and newline-free. */
public record MacroDefinition(String name, String replacement) {

    public MacroDefinition {
        java.util.Objects.requireNonNull(name, "name");
        java.util.Objects.requireNonNull(replacement, "replacement");
        if (replacement.indexOf('\n') >= 0 || replacement.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("macro replacement must be newline-free");
        }
    }
}
