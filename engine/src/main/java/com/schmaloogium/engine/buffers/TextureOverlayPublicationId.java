// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Identity of a texture overlay publication. {@code generation} is the accepted
 * estateGeneration, not the registry generation.
 */
public record TextureOverlayPublicationId(long generation,
                                          TextureOverlayFingerprint contentFingerprint) {

    public TextureOverlayPublicationId {
        java.util.Objects.requireNonNull(contentFingerprint, "contentFingerprint");
    }
}
