// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.buffers.BaseAtlasContext;
import com.schmaloogium.engine.gl.TextureHandle;

import java.util.Objects;

/**
 * The capture acceptor the stitch/animation adapters drive (§4.4/§4.6). Post-only acceptance:
 * a stitch capture lands here only after the glue state machine's complete outer-attempt
 * acceptance, and reload/close invalidate everything at the new epoch before vanilla
 * replacement.
 */
public interface TextureCaptureSink {

    /**
     * Publishes one accepted stitch capture at the current epoch. Post acceptance is the only
     * path here; the descriptor carries the actual captured extent and mip count.
     *
     * @param designatedBlockItemAtlas whether this map is the catalogued block/item atlas
     *        backing the parameterless {@link TextureSystem#atlasSize()} convenience query
     */
    void onStitchAccepted(AtlasDescriptor descriptor, boolean designatedBlockItemAtlas);

    /** Invalidates catalog/animation/size/base associations before vanilla replacement. */
    void invalidateStitch(long newResourceReloadEpoch);

    /**
     * Records the accepted base-object association (exact handle incarnation + resource epoch
     * → Atlas or known NonAtlas) after matched allocation/Post acceptance. Replacement or
     * failure invalidates; equal resource strings or sizes never revive.
     */
    void associateBase(TextureHandle base, BaseAtlasContext association,
                       long resourceReloadEpoch);

    /**
     * Applies one post-vanilla animation snapshot: validation, matching companion frame
     * uploads, frame0 fallback with one diagnostic on invalid/failed state. Render thread.
     */
    void applyAnimationSnapshot(AtlasAnimationSnapshot snapshot);

    /** The designated block/item atlas identity, if captured. */
    AtlasId designatedBlockItemAtlas();
}
