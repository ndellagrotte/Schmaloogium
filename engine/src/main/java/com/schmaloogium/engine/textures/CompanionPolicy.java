// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.Objects;

/** The plan's companion enablement (PHASE_13_DOC §4.1.1). Disabled kinds allocate zero. */
public record CompanionPolicy(boolean normalsEnabled, boolean specularEnabled,
                              CompanionDemandSource source) {
    public CompanionPolicy {
        Objects.requireNonNull(source, "source");
    }
}
