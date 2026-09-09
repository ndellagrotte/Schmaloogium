// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Optional;

/** One per-pack macro override. */
public record MacroOverride(MacroOverrideAction action, Optional<String> replacement) {

    public MacroOverride {
        java.util.Objects.requireNonNull(action, "action");
        replacement = replacement == null ? Optional.empty() : replacement;
    }
}
