// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** One {@code version.<mcver>=<edition>} rule with exact decoded accessor values (D-P3-52/53). */
public record MinimumEditionRule(String minecraftVersion, String minimumEdition) {

    public MinimumEditionRule {
        java.util.Objects.requireNonNull(minecraftVersion, "minecraftVersion");
        java.util.Objects.requireNonNull(minimumEdition, "minimumEdition");
    }
}
