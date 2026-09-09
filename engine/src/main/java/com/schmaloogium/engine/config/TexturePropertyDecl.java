// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.SourceAttribution;

/** One lossless decoded {@code texture.*} declaration (D-P3-59). */
public record TexturePropertyDecl(
        String key, String value, int sourceOrdinal, SourceAttribution attribution,
        TexturePropertyDisposition disposition) {

    public TexturePropertyDecl {
        java.util.Objects.requireNonNull(key, "key");
        java.util.Objects.requireNonNull(value, "value");
        if (sourceOrdinal < 0) {
            throw new IllegalArgumentException("sourceOrdinal must be >= 0");
        }
        java.util.Objects.requireNonNull(attribution, "attribution");
        java.util.Objects.requireNonNull(disposition, "disposition");
        if (!key.startsWith("texture.")) {
            throw new IllegalArgumentException("key outside the texture. prefix");
        }
    }
}
