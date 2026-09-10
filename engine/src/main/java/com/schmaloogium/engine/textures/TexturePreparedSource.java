// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.gl.TextureHandle;

import java.util.Objects;

/**
 * The build-time prepared source paired with its exact catalog asset (§2.3): owned sources
 * carry their upload payload; foreign sources carry the borrowed handle and never a payload.
 */
public sealed interface TexturePreparedSource {

    record Owned(TextureSourceAsset.ReadyAsset asset, TextureUploadPayload payload)
            implements TexturePreparedSource {
        public Owned {
            Objects.requireNonNull(asset, "asset");
            Objects.requireNonNull(payload, "payload");
        }
    }

    record Foreign(TextureSourceAsset.ReadyAsset asset, TextureHandle handle)
            implements TexturePreparedSource {
        public Foreign {
            Objects.requireNonNull(asset, "asset");
            Objects.requireNonNull(handle, "handle");
        }
    }
}
