// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.List;

/** Immutable loader-neutral A-G environment with typed integer MC_VERSION. */
public record IdMappingMacroEnvironment(
        int mcVersion,
        List<MacroDefinition> standardMacros) {

    public IdMappingMacroEnvironment {
        standardMacros = List.copyOf(java.util.Objects.requireNonNull(standardMacros, "standardMacros"));
    }
}
