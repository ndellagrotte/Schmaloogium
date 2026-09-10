// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.List;
import java.util.Objects;

/** The prepared-source metadata catalog delivered to planning (§2.3). */
public record TextureSourceCatalog(long resourceReloadEpoch, List<TextureSourceAsset> assets) {
    public TextureSourceCatalog {
        Objects.requireNonNull(assets, "assets");
        assets.forEach(Objects::requireNonNull);
        assets = List.copyOf(assets);
    }

    public static final TextureSourceCatalog EMPTY =
        new TextureSourceCatalog(0, List.of());
}
