// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** One option entry. */
public record ScreenOptionEntry(String optionName) implements ScreenEntry {

    public ScreenOptionEntry {
        java.util.Objects.requireNonNull(optionName, "optionName");
    }
}
