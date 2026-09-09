// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** One named subscreen entry. */
public record ScreenSubscreenEntry(String screenName) implements ScreenEntry {

    public ScreenSubscreenEntry {
        java.util.Objects.requireNonNull(screenName, "screenName");
    }
}
