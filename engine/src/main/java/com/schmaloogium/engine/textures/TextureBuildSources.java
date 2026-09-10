// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.List;
import java.util.Objects;

/** The build request's paired prepared sources (§2.3). */
public record TextureBuildSources(long resourceReloadEpoch,
                                  List<TexturePreparedSource> sources) {
    public TextureBuildSources {
        Objects.requireNonNull(sources, "sources");
        sources.forEach(Objects::requireNonNull);
        sources = List.copyOf(sources);
    }

    public static final TextureBuildSources EMPTY = new TextureBuildSources(0, List.of());
}
