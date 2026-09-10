// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.buffers.TextureOverlayLease;

/**
 * The texture-system surface (§2.2). Planning is pure/total. Build re-runs planning,
 * validates the prepared-source/catalog pairing before allocation, and returns Ready or
 * Failed without a partial publication. {@code atlasSize(AtlasId)} queries any exact
 * current-epoch catalogued atlas; the parameterless form is the convenience query for the
 * catalogued block/item atlas. {@code close()} retires first and defers owned deletion until
 * all leases drain; it never blocks the render thread waiting for itself.
 */
public interface TextureSystem extends TextureLeaseSource {

    TexturePlanResult plan(TexturePlanRequest request);

    TextureBuildResult build(TextureBuildRequest request);

    AtlasSizeResult atlasSize(AtlasId atlas);

    AtlasSizeResult atlasSize();

    void close();
}
