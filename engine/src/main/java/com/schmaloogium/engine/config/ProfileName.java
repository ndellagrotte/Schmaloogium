// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.ProfileName;

/** Exact profile identity. */
public record ProfileName(String value) {

    public ProfileName {
        java.util.Objects.requireNonNull(value, "value");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("profile name must be non-empty");
        }
    }
}
