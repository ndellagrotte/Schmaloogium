// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import com.schmaloogium.engine.buffers.FixedSamplerLookup;
import com.schmaloogium.engine.buffers.FixedSamplerName;
import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.buffers.TextureParameterFingerprint;
import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.config.CustomTextureSpec;
import com.schmaloogium.engine.config.TextureBindingKey;
import com.schmaloogium.engine.config.TexturePropertyStage;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.PixelFormat;
import com.schmaloogium.engine.gl.PixelType;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.textures.CustomTexturePlanEntry;
import com.schmaloogium.engine.textures.TextureSourceAsset;
import com.schmaloogium.engine.textures.TextureUploadSpec;
import com.schmaloogium.engine.textures.UnsupportedBinding;
import com.schmaloogium.engine.textures.UnsupportedReason;
import com.schmaloogium.engine.textures.RequestedTextureTarget;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The pure custom-texture planner (PHASE_13_DOC §4.3.1/§4.3.3): walks the Phase3 canonical
 * typed spec list in order, expands stages, resolves exact fixed names, and emits one
 * plan entry per executable spec whose expanded stages are all name-legal and whose
 * preparation produced a ready asset. Every unresolved key stays a closed diagnostic —
 * an unknown spelling keeps its exact text, a known name out of a stage column keeps its
 * typed enum. No sampler name, unit or source is ever fabricated.
 */
public final class CustomTexturePlanner {

    private static final DeclaredGlslType.Sampler SAMPLER_2D =
        new DeclaredGlslType.Sampler(SampledKind.FLOAT, TextureDimension.D2, false, false, false);

    private CustomTexturePlanner() {
    }

    /** Preparer-computed effective parameterization, used verbatim by planning. */
    public record PreparedParameterization(TextureParameterSpec parameters, String fingerprint) {
        public PreparedParameterization {
            Objects.requireNonNull(parameters, "parameters");
            Objects.requireNonNull(fingerprint, "fingerprint");
        }
    }

    /** Plan output: entries in input list order, diagnostics in walk order. */
    public record Result(List<CustomTexturePlanEntry> entries,
                         List<UnsupportedBinding> unsupported) {
        public Result {
            Objects.requireNonNull(entries, "entries");
            Objects.requireNonNull(unsupported, "unsupported");
            entries = List.copyOf(entries);
            unsupported = List.copyOf(unsupported);
        }
    }

    /**
     * Plans every executable custom spec of the configuration. The input list is already
     * Phase3-canonical (property-stage order, unsigned-UTF8 sampler, absent discriminator
     * then 0..9, source kind); planning preserves list order and assigns each original
     * entry its zero-based {@code phase3Ordinal}.
     *
     * <p>A spec yields no entry — without a key-domain diagnostic — when its preparation
     * failed or is missing: that is source evidence, carried by the catalog, not a
     * key-domain failure.
     */
    public static Result plan(PackConfiguration configuration,
            Map<CustomTextureSpec, TextureSourceAsset> preparedAssets,
            Map<CustomTextureSpec, PreparedParameterization> parameterizations) {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(preparedAssets, "preparedAssets");
        Objects.requireNonNull(parameterizations, "parameterizations");

        List<CustomTextureSpec> specs = configuration.properties().textures();
        List<CustomTexturePlanEntry> entries = new ArrayList<>();
        List<UnsupportedBinding> unsupported = new ArrayList<>();

        int phase3Ordinal = 0;
        for (CustomTextureSpec spec : specs) {
            TextureBindingKey key = keyOf(spec);
            StageId declared = StageId.valueOf(key.stage().name());
            Set<StageId> expansion = StageColumnPolicy.expand(declared);

            FixedSamplerLookup lookup = FixedSamplerPolicies.lookup(key.sampler());
            if (lookup instanceof FixedSamplerLookup.Unknown unknown) {
                unsupported.add(new UnsupportedBinding(key, declared,
                    new RequestedTextureTarget.UnknownSampler(unknown.exactName()),
                    UnsupportedReason.KEY_DOMAIN));
                phase3Ordinal++;
                continue;
            }
            FixedSamplerName name = ((FixedSamplerLookup.Known) lookup).name();

            Set<StageId> legal = StageColumnPolicy.legalStages(name);
            EnumSet<StageId> supported = EnumSet.noneOf(StageId.class);
            List<StageId> illegalStages = new ArrayList<>();
            for (StageId stage : expansion) {
                if (legal.contains(stage)) {
                    supported.add(stage);
                } else {
                    illegalStages.add(stage);
                }
            }
            for (StageId stage : illegalStages) {
                unsupported.add(new UnsupportedBinding(key, stage,
                    new RequestedTextureTarget.KnownSampler(name),
                    UnsupportedReason.STAGE_COLUMN));
            }
            if (supported.isEmpty()) {
                phase3Ordinal++;
                continue;
            }

            if (!(preparedAssets.get(spec) instanceof TextureSourceAsset.ReadyAsset ready)) {
                phase3Ordinal++;
                continue;
            }
            PreparedParameterization parameterization = parameterizations.get(spec);
            if (parameterization == null) {
                throw new IllegalArgumentException(
                    "missing parameterization for prepared spec " + key);
            }

            entries.add(new CustomTexturePlanEntry(key, phase3Ordinal, name, supported,
                shapeOf(spec), targetOf(spec), ready.identity(), uploadOf(spec, ready),
                parameterization.parameters(),
                new TextureParameterFingerprint(parameterization.fingerprint())));
            phase3Ordinal++;
        }
        return new Result(entries, unsupported);
    }

    private static TextureBindingKey keyOf(CustomTextureSpec spec) {
        return switch (spec) {
            case CustomTextureSpec.PackPath packPath -> packPath.key();
            case CustomTextureSpec.MinecraftResource resource -> resource.key();
            case CustomTextureSpec.Raw raw -> raw.key();
        };
    }

    private static TextureAllocationTarget targetOf(CustomTextureSpec spec) {
        return switch (spec) {
            case CustomTextureSpec.PackPath packPath -> TextureAllocationTarget.TEXTURE_2D;
            case CustomTextureSpec.MinecraftResource resource ->
                TextureAllocationTarget.TEXTURE_2D;
            case CustomTextureSpec.Raw raw ->
                TextureAllocationTarget.valueOf(raw.target().name());
        };
    }

    private static DeclaredGlslType.Sampler shapeOf(CustomTextureSpec spec) {
        if (!(spec instanceof CustomTextureSpec.Raw raw)) {
            return SAMPLER_2D;
        }
        TextureAllocationTarget target = TextureAllocationTarget.valueOf(raw.target().name());
        return new DeclaredGlslType.Sampler(signednessOf(raw.internalFormat().name()),
            dimensionOf(target), false, false, false);
    }

    /** P3 internal-format spelling decides signedness: *I signed, *UI unsigned, else float. */
    private static SampledKind signednessOf(String internalFormatName) {
        if (internalFormatName.endsWith("UI")) {
            return SampledKind.UNSIGNED_INT;
        }
        if (internalFormatName.endsWith("I")) {
            return SampledKind.SIGNED_INT;
        }
        return SampledKind.FLOAT;
    }

    private static TextureDimension dimensionOf(TextureAllocationTarget target) {
        return switch (target) {
            case TEXTURE_1D -> TextureDimension.D1;
            case TEXTURE_2D -> TextureDimension.D2;
            case TEXTURE_3D -> TextureDimension.D3;
            case RECTANGLE -> TextureDimension.RECTANGLE;
        };
    }

    /**
     * Raw specs carry their declared upload one-for-one converted to the shared
     * engine enumerants; PNG and Minecraft sources carry the decoded-extent RGBA8 2D
     * descriptor built from the ready asset (§2.3: an object-capability descriptor even
     * for a foreign source). Raw dimensions arity is P3-validated; a mismatch here is a
     * broken invariant, not a plannable condition.
     */
    private static TextureUploadSpec uploadOf(CustomTextureSpec spec,
            TextureSourceAsset.ReadyAsset ready) {
        return switch (spec) {
            case CustomTextureSpec.Raw raw -> rawUpload(raw);
            case CustomTextureSpec.PackPath packPath -> decodedTwoDUpload(ready);
            case CustomTextureSpec.MinecraftResource resource -> decodedTwoDUpload(ready);
        };
    }

    private static TextureUploadSpec rawUpload(CustomTextureSpec.Raw raw) {
        List<Integer> dimensions = raw.dimensions();
        return switch (raw.target()) {
            case TEXTURE_1D -> {
                requireArity(dimensions, 1, raw);
                yield new TextureUploadSpec.OneD(dimensions.get(0),
                    ColorInternalFormat.valueOf(raw.internalFormat().name()),
                    PixelFormat.valueOf(raw.pixelFormat().name()),
                    PixelType.valueOf(raw.pixelType().name()));
            }
            case TEXTURE_2D -> {
                requireArity(dimensions, 2, raw);
                yield new TextureUploadSpec.TwoD(dimensions.get(0), dimensions.get(1),
                    ColorInternalFormat.valueOf(raw.internalFormat().name()),
                    PixelFormat.valueOf(raw.pixelFormat().name()),
                    PixelType.valueOf(raw.pixelType().name()));
            }
            case TEXTURE_3D -> {
                requireArity(dimensions, 3, raw);
                yield new TextureUploadSpec.ThreeD(dimensions.get(0), dimensions.get(1),
                    dimensions.get(2),
                    ColorInternalFormat.valueOf(raw.internalFormat().name()),
                    PixelFormat.valueOf(raw.pixelFormat().name()),
                    PixelType.valueOf(raw.pixelType().name()));
            }
            case RECTANGLE -> {
                requireArity(dimensions, 2, raw);
                yield new TextureUploadSpec.Rectangle(dimensions.get(0), dimensions.get(1),
                    ColorInternalFormat.valueOf(raw.internalFormat().name()),
                    PixelFormat.valueOf(raw.pixelFormat().name()),
                    PixelType.valueOf(raw.pixelType().name()));
            }
        };
    }

    private static void requireArity(List<Integer> dimensions, int expected,
            CustomTextureSpec.Raw raw) {
        if (dimensions.size() != expected) {
            throw new IllegalArgumentException("raw " + raw.target()
                + " declaration requires " + expected + " dimensions, got "
                + dimensions.size() + ": " + raw.bytes().canonicalString());
        }
    }

    private static TextureUploadSpec decodedTwoDUpload(TextureSourceAsset.ReadyAsset ready) {
        List<Integer> dimensions = ready.dimensions();
        if (dimensions.size() < 2) {
            throw new IllegalArgumentException(
                "decoded 2D asset needs two extent axes, got " + dimensions.size());
        }
        return new TextureUploadSpec.TwoD(dimensions.get(0), dimensions.get(1),
            ColorInternalFormat.RGBA8, PixelFormat.RGBA, PixelType.UNSIGNED_BYTE);
    }
}
