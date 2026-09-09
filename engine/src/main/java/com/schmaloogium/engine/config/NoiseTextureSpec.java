// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.NormalizedPackPath;

import java.util.Optional;

/** Noise texture source: generated or overridden. */
public sealed interface NoiseTextureSpec
        permits NoiseTextureSpec.Generated, NoiseTextureSpec.Override {

    /** Generated noise (no {@code texture.noise} declaration). */
    record Generated() implements NoiseTextureSpec {
    }

    /** Pack image override. */
    record Override(NormalizedPackPath image, Optional<TextureSidecarRef> sidecar)
        implements NoiseTextureSpec {

        public Override {
            java.util.Objects.requireNonNull(image, "image");
            sidecar = sidecar == null ? Optional.empty() : sidecar;
        }
    }
}
