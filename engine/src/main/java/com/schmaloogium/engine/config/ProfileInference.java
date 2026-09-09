// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.ProfileName;

import java.util.Optional;

/** Preview-only profile inference outcome. */
public record ProfileInference(Optional<ProfileName> selected, boolean custom) {

    public ProfileInference {
        java.util.Objects.requireNonNull(selected, "selected");
    }
}
