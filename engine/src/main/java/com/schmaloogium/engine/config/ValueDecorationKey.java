// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** {@code value.<NAME>.<VALUE>} lang key projection. */
public record ValueDecorationKey(String optionName, String value) {

    public ValueDecorationKey {
        java.util.Objects.requireNonNull(optionName, "optionName");
        java.util.Objects.requireNonNull(value, "value");
    }
}
