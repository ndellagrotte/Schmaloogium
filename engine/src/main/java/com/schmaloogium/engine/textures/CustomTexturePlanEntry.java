// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.FixedSamplerName;
import com.schmaloogium.engine.buffers.TextureParameterFingerprint;
import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.config.TextureBindingKey;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.registry.StageId;

import java.util.Objects;
import java.util.Set;

/**
 * One executable custom texture's plan entry (§2.3): handle-free original key and content
 * retained, canonical phase3Ordinal, resolved exact fixed name, expanded stage set, typed
 * upload shape and effective parameterization. Only typed executable specs create entries.
 */
public record CustomTexturePlanEntry(
        TextureBindingKey key, int phase3Ordinal, FixedSamplerName name, Set<StageId> stages,
        DeclaredGlslType.Sampler shape, TextureAllocationTarget target,
        TextureSourceIdentity source, TextureUploadSpec upload, TextureParameterSpec parameters,
        TextureParameterFingerprint parameterizationFingerprint) {
    public CustomTexturePlanEntry {
        Objects.requireNonNull(key, "key");
        if (phase3Ordinal < 0) {
            throw new IllegalArgumentException("phase3Ordinal must be nonnegative: "
                + phase3Ordinal);
        }
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(stages, "stages");
        if (stages.isEmpty()) {
            throw new IllegalArgumentException("stages must be non-empty");
        }
        stages = java.util.Collections.unmodifiableSet(
            new java.util.TreeSet<>(stages));
        Objects.requireNonNull(shape, "shape");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(upload, "upload");
        Objects.requireNonNull(parameters, "parameters");
        Objects.requireNonNull(parameterizationFingerprint, "parameterizationFingerprint");
    }
}
