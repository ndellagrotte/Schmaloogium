// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Optional;

/**
 * Leased view over a texture overlay publication. {@link #isCurrent()} means open AND its owner
 * is the currently READY publication; retiring or closed owners return {@code false}.
 */
public interface TextureOverlayLease extends TextureOverlaySnapshot, AutoCloseable {
    BaseAtlasContext baseAtlasContext();

    Optional<TextureHandleRef> baseTexture();

    BaseAtlasContext atlasContext(TextureHandleRef base);

    boolean isCurrent();

    @Override
    void close();
}
