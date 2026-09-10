// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.buffers.CompanionKind;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureBorderColor;
import com.schmaloogium.engine.gl.TextureCompareFunction;
import com.schmaloogium.engine.gl.TextureCompareMode;
import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureParameters;
import com.schmaloogium.engine.gl.TextureSwizzle;
import com.schmaloogium.engine.gl.TextureWrap;

import java.util.Objects;
import java.util.Set;

/**
 * Effective-parameter law (§4.3.5/§4.3.3/§4.1.4/§5.5): target/format legality rejection
 * before allocation, the D-P13-42 companion/default baselines, and the D-P13-29 complete
 * synchronous P1 parameter mapping (neutral remaining fields, per-target wrap axes, object
 * mip baseline).
 */
public final class ParameterPolicy {

    private static final Set<ColorInternalFormat> INTEGER_FORMATS = Set.of(
        ColorInternalFormat.R32I, ColorInternalFormat.RG32I,
        ColorInternalFormat.RGB32I, ColorInternalFormat.RGBA32I,
        ColorInternalFormat.R32UI, ColorInternalFormat.RG32UI,
        ColorInternalFormat.RGB32UI, ColorInternalFormat.RGBA32UI);

    private static final Set<TextureMinFilter> MIPMAP_MIN_FILTERS = Set.of(
        TextureMinFilter.NEAREST_MIPMAP_NEAREST, TextureMinFilter.LINEAR_MIPMAP_NEAREST,
        TextureMinFilter.NEAREST_MIPMAP_LINEAR, TextureMinFilter.LINEAR_MIPMAP_LINEAR);

    private ParameterPolicy() {
    }

    /**
     * Target/format legality (§4.3.5): RECTANGLE rejects REPEAT and mipmap minification;
     * integer storage requires nearest-only filtering. A default is not an exemption.
     */
    public static boolean isLegal(TextureAllocationTarget target, ColorInternalFormat format,
                                  TextureParameterSpec parameters) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(format, "format");
        Objects.requireNonNull(parameters, "parameters");
        if (target == TextureAllocationTarget.RECTANGLE) {
            if (parameters.wrap() == TextureWrap.REPEAT) {
                return false;
            }
            if (MIPMAP_MIN_FILTERS.contains(parameters.minFilter())) {
                return false;
            }
        }
        if (INTEGER_FORMATS.contains(format)) {
            return parameters.magFilter() == TextureMagFilter.NEAREST
                && (parameters.minFilter() == TextureMinFilter.NEAREST
                    || parameters.minFilter() == TextureMinFilter.NEAREST_MIPMAP_NEAREST);
        }
        return true;
    }

    /** The D-P13-42 generated-noise baseline: LINEAR/LINEAR/REPEAT. */
    public static TextureParameterSpec generatedNoisePolicy() {
        return new TextureParameterSpec(TextureMinFilter.LINEAR,
            TextureMagFilter.LINEAR, TextureWrap.REPEAT);
    }

    /** The D-P13-42 full-companion-atlas policy derived from the accepted mip count. */
    public static TextureParameterSpec companionAtlasPolicy(int mipmapLevels) {
        if (mipmapLevels < 0) {
            throw new IllegalArgumentException("mipmapLevels must be nonnegative");
        }
        return mipmapLevels > 0
            ? new TextureParameterSpec(TextureMinFilter.NEAREST_MIPMAP_LINEAR,
                TextureMagFilter.NEAREST, TextureWrap.REPEAT)
            : new TextureParameterSpec(TextureMinFilter.NEAREST,
                TextureMagFilter.NEAREST, TextureWrap.REPEAT);
    }

    /** The D-P13-42 standalone 1×1 level-zero kind-default policy. */
    public static TextureParameterSpec standaloneDefaultPolicy() {
        return new TextureParameterSpec(TextureMinFilter.NEAREST,
            TextureMagFilter.NEAREST, TextureWrap.REPEAT);
    }

    /**
     * The missing-sprite fill inside a full atlas inherits the atlas object's mipped or
     * nonmipped policy, never the standalone policy (D-P13-42).
     */
    public static TextureParameterSpec filledSpritePolicy(int atlasMipmapLevels) {
        return companionAtlasPolicy(atlasMipmapLevels);
    }

    /** The D-P13-42 parameter-fingerprint policy role for a companion kind. */
    public static SidecarPolicy.Role companionRole(CompanionKind kind, boolean standalone) {
        return standalone ? SidecarPolicy.Role.DEFAULT_FILL : SidecarPolicy.Role.COMPANION;
    }

    /**
     * The complete D-P13-29 synchronous P1 parameter value: direct min/mag, the single wrap
     * applied to target-applicable axes with unused axes CLAMP_TO_EDGE, compare NONE/LEQUAL,
     * zero border, LOD −1000/+1000, bias 0, anisotropy 1, base 0, max = admitted contiguous
     * last mip (0 when nonmipped), IDENTITY swizzle.
     */
    public static TextureParameters facadeParameters(TextureAllocationTarget target,
                                                     TextureParameterSpec spec,
                                                     int mipLevels) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(spec, "spec");
        if (mipLevels < 1) {
            throw new IllegalArgumentException("mipLevels must be at least 1: " + mipLevels);
        }
        TextureWrap wrapS;
        TextureWrap wrapT;
        TextureWrap wrapR;
        switch (target) {
            case TEXTURE_1D -> {
                wrapS = spec.wrap();
                wrapT = TextureWrap.CLAMP_TO_EDGE;
                wrapR = TextureWrap.CLAMP_TO_EDGE;
            }
            case TEXTURE_2D, RECTANGLE -> {
                wrapS = spec.wrap();
                wrapT = spec.wrap();
                wrapR = TextureWrap.CLAMP_TO_EDGE;
            }
            case TEXTURE_3D -> {
                wrapS = spec.wrap();
                wrapT = spec.wrap();
                wrapR = spec.wrap();
            }
            default -> throw new IllegalStateException("uncovered target: " + target);
        }
        boolean mipped = MIPMAP_MIN_FILTERS.contains(spec.minFilter());
        int maxLevel = mipped ? mipLevels - 1 : 0;
        return new TextureParameters(spec.minFilter(), spec.magFilter(),
            wrapS, wrapT, wrapR,
            TextureCompareMode.NONE, TextureCompareFunction.LEQUAL,
            new TextureBorderColor(0f, 0f, 0f, 0f),
            -1000f, 1000f, 0f, 1f,
            0, maxLevel, TextureSwizzle.IDENTITY);
    }
}
