// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.AtlasId;

import java.util.List;
import java.util.Objects;

/**
 * One atlas's post-vanilla animation snapshot (PHASE_13_DOC §4.1.7, H13-ATLAS-04): the
 * resource-reload epoch the rows belong to, the engine-wide tick sequence the snapshot was
 * taken at, and one {@link SpriteAnimationState} per animated sprite in the atlas's
 * canonical sprite order. Rows are copied, never retained by reference.
 */
public record AtlasAnimationSnapshot(
        AtlasId atlas, long resourceReloadEpoch, long tickSequence,
        List<SpriteAnimationState> sprites) {

    public AtlasAnimationSnapshot {
        Objects.requireNonNull(atlas, "atlas");
        Objects.requireNonNull(sprites, "sprites");
        sprites.forEach(Objects::requireNonNull);
        sprites = List.copyOf(sprites);
    }
}
