// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;

import java.util.List;
import java.util.Objects;

/**
 * Closed per-source catalog entry (§2.3): a ready asset with its exact identity, target,
 * dimensions, shared allocation format, sample shape, effective parameters and digests — or
 * an entry-local failure. The format is the exact P5/P1 shared allocation value, never the
 * private RGBA_COMPAT from a raw declaration.
 */
public sealed interface TextureSourceAsset {

    record ReadyAsset(TextureSourceIdentity identity, TextureAllocationTarget target,
                      List<Integer> dimensions, ColorInternalFormat format,
                      DeclaredGlslType.Sampler shape, TextureParameterSpec parameters,
                      String contentDigest, String sidecarDigest) implements TextureSourceAsset {
        public ReadyAsset {
            Objects.requireNonNull(identity, "identity");
            Objects.requireNonNull(target, "target");
            Objects.requireNonNull(dimensions, "dimensions");
            if (dimensions.isEmpty() || dimensions.size() > 3) {
                throw new IllegalArgumentException("dimensions must name one to three axes");
            }
            dimensions.forEach(d -> {
                if (d == null || d <= 0) {
                    throw new IllegalArgumentException("dimensions must be positive");
                }
            });
            dimensions = List.copyOf(dimensions);
            Objects.requireNonNull(format, "format");
            Objects.requireNonNull(shape, "shape");
            Objects.requireNonNull(parameters, "parameters");
            Objects.requireNonNull(contentDigest, "contentDigest");
            Objects.requireNonNull(sidecarDigest, "sidecarDigest");
        }
    }

    record FailedAsset(TextureFailure failure) implements TextureSourceAsset {
        public FailedAsset {
            Objects.requireNonNull(failure, "failure");
        }
    }
}
