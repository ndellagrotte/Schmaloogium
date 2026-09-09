// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.NormalizedPackPath;

import java.util.List;
import java.util.Optional;

/** Sealed custom texture spec sum (Appendix F.5). */
public sealed interface CustomTextureSpec
        permits CustomTextureSpec.PackPath, CustomTextureSpec.MinecraftResource,
                CustomTextureSpec.Raw {

    /** Pack-relative PNG image source. */
    record PackPath(TextureBindingKey key, NormalizedPackPath image,
        Optional<TextureSidecarRef> sidecar) implements CustomTextureSpec {

        public PackPath {
            java.util.Objects.requireNonNull(key, "key");
            java.util.Objects.requireNonNull(image, "image");
            sidecar = sidecar == null ? Optional.empty() : sidecar;
        }
    }

    /** Exact non-empty {@code minecraft:} resource identity, kept as text. */
    record MinecraftResource(TextureBindingKey key, String resourceIdentity)
        implements CustomTextureSpec {

        public MinecraftResource {
            java.util.Objects.requireNonNull(key, "key");
            java.util.Objects.requireNonNull(resourceIdentity, "resourceIdentity");
            if (resourceIdentity.isEmpty()) {
                throw new IllegalArgumentException("resourceIdentity must be non-empty");
            }
        }
    }

    /** Raw external byte source with typed format algebra. */
    record Raw(TextureBindingKey key, NormalizedPackPath bytes, TextureTarget target,
        ColorInternalFormat internalFormat, List<Integer> dimensions, PixelFormat pixelFormat,
        PixelType pixelType, Optional<TextureSidecarRef> sidecar) implements CustomTextureSpec {

        public Raw {
            java.util.Objects.requireNonNull(key, "key");
            java.util.Objects.requireNonNull(bytes, "bytes");
            java.util.Objects.requireNonNull(target, "target");
            java.util.Objects.requireNonNull(internalFormat, "internalFormat");
            dimensions = List.copyOf(dimensions);
            java.util.Objects.requireNonNull(pixelFormat, "pixelFormat");
            java.util.Objects.requireNonNull(pixelType, "pixelType");
            sidecar = sidecar == null ? Optional.empty() : sidecar;
        }
    }
}
