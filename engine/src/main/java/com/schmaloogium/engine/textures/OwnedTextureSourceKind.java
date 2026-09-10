// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

/** Closed owned-source-kind domain (§2.3). */
public enum OwnedTextureSourceKind {
    PACK_PNG, MINECRAFT_DECODED_ASSET, RAW_BYTES,
    GENERATED_NOISE, COMPANION_RESOURCE, DEFAULT_FILL
}
