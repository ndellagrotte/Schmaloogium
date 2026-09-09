// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.DimensionKey;

import java.util.Optional;

/** One profile program disable; absent dimension matches every dimension. */
public record ProgramDisable(Optional<DimensionKey> dimension, String programName) {

    public ProgramDisable {
        java.util.Objects.requireNonNull(dimension, "dimension");
        java.util.Objects.requireNonNull(programName, "programName");
    }
}
