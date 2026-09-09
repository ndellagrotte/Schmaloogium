// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** One profile constraint: option name and required value. */
public record ProfileConstraint(String optionName, OptionValue requiredValue) {

    public ProfileConstraint {
        java.util.Objects.requireNonNull(optionName, "optionName");
        java.util.Objects.requireNonNull(requiredValue, "requiredValue");
    }
}
