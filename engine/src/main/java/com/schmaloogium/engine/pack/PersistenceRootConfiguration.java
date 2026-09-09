// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import java.nio.file.Path;

public record PersistenceRootConfiguration(Path shaderpacksDirectory, Path gameDirectory) {

    public PersistenceRootConfiguration {
        java.util.Objects.requireNonNull(shaderpacksDirectory, "shaderpacksDirectory");
        java.util.Objects.requireNonNull(gameDirectory, "gameDirectory");
    }
}
