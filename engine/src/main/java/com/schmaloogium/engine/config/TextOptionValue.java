// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** A variable/constant replacement-token value. */
public record TextOptionValue(String value) implements OptionValue {

    public TextOptionValue {
        java.util.Objects.requireNonNull(value, "value");
    }
}
