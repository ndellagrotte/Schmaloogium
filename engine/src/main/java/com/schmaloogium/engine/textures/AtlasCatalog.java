// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.List;
import java.util.Objects;

/** The handle-free atlas catalog delivered to planning (§2.3). */
public record AtlasCatalog(List<AtlasDescriptor> atlases) {
    public AtlasCatalog {
        Objects.requireNonNull(atlases, "atlases");
        atlases.forEach(Objects::requireNonNull);
        atlases = List.copyOf(atlases);
    }

    public static final AtlasCatalog EMPTY = new AtlasCatalog(List.of());
}
