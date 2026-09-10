// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.gl.TextureData;

import java.util.List;
import java.util.Objects;

/**
 * One sprite's animation-frame transfer rows: ascending mip, exact atlas mip origins and
 * extents preserved (§2.3 payload law).
 */
public record TextureFramePayload(String iconName, int sourceFrameIndex,
                                  List<TextureData> uploads) {
    public TextureFramePayload {
        Objects.requireNonNull(iconName, "iconName");
        if (iconName.isEmpty()) {
            throw new IllegalArgumentException("iconName must be non-empty");
        }
        if (sourceFrameIndex < 0) {
            throw new IllegalArgumentException("sourceFrameIndex must be nonnegative: "
                + sourceFrameIndex);
        }
        Objects.requireNonNull(uploads, "uploads");
        uploads.forEach(Objects::requireNonNull);
        uploads = List.copyOf(uploads);
    }
}
