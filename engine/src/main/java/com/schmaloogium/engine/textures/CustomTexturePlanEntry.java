// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.FixedSamplerName;
import com.schmaloogium.engine.buffers.TextureParameterFingerprint;
import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.config.TextureBindingKey;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
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
        TextureAllocationTarget uploadTarget = switch (upload) {
            case TextureUploadSpec.OneD ignored -> TextureAllocationTarget.TEXTURE_1D;
            case TextureUploadSpec.TwoD ignored -> TextureAllocationTarget.TEXTURE_2D;
            case TextureUploadSpec.ThreeD ignored -> TextureAllocationTarget.TEXTURE_3D;
            case TextureUploadSpec.Rectangle ignored -> TextureAllocationTarget.RECTANGLE;
        };
        TextureDimension dimension = switch (uploadTarget) {
            case TEXTURE_1D -> TextureDimension.D1;
            case TEXTURE_2D -> TextureDimension.D2;
            case TEXTURE_3D -> TextureDimension.D3;
            case RECTANGLE -> TextureDimension.RECTANGLE;
        };
        ColorInternalFormat format = switch (upload) {
            case TextureUploadSpec.OneD spec -> spec.internalFormat();
            case TextureUploadSpec.TwoD spec -> spec.internalFormat();
            case TextureUploadSpec.ThreeD spec -> spec.internalFormat();
            case TextureUploadSpec.Rectangle spec -> spec.internalFormat();
        };
        SampledKind sampledKind = switch (format) {
            case R32I, RG32I, RGB32I, RGBA32I -> SampledKind.SIGNED_INT;
            case R32UI, RG32UI, RGB32UI, RGBA32UI -> SampledKind.UNSIGNED_INT;
            default -> SampledKind.FLOAT;
        };
        if (target != uploadTarget || shape.dimension() != dimension
                || shape.sample() != sampledKind || shape.arrayed()
                || shape.shadow() || shape.multisample()) {
            throw new IllegalArgumentException(
                "custom sampler shape and target must match the non-comparing color upload");
        }
    }
}
