// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.Objects;

/** The build request: the frozen plan inputs plus the paired prepared sources (§2.3). */
public record TextureBuildRequest(TexturePlanRequest planRequest,
                                  TextureBuildSources sources) {
    public TextureBuildRequest {
        Objects.requireNonNull(planRequest, "planRequest");
        Objects.requireNonNull(sources, "sources");
    }
}
