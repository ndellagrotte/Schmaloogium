// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * The complete owned-texture parameter value (PHASE_1_DOC §4.7.7, D-P1-52) — closed
 * validation, no dependency on P5/P13 types. Sampler state is exactly the prefix through
 * {@code maxAnisotropy} (what {@link SamplerKey#of} copies); object-only state is
 * {@code baseLevel}/{@code maxLevel}/{@code swizzle}. Storage immutability belongs to
 * allocation, never to {@link TextureService#setParameters}.
 *
 * <p>Closed value rules (§4.7.7): fields nonnull; floats finite with negative zero
 * canonicalized to positive zero; {@code minLod <= maxLod}; {@code 0 <= baseLevel <=
 * maxLevel}; this scope permits {@code maxAnisotropy = 1} only; border is exactly
 * (0,0,0,0) — neither granted wrap mode samples a border. Violations throw
 * {@link IllegalArgumentException} at construction.
 */
public record TextureParameters(
        TextureMinFilter minFilter,
        TextureMagFilter magFilter,
        TextureWrap wrapS,
        TextureWrap wrapT,
        TextureWrap wrapR,
        TextureCompareMode compareMode,
        TextureCompareFunction compareFunction,
        TextureBorderColor borderColor,
        float minLod,
        float maxLod,
        float lodBias,
        float maxAnisotropy,
        int baseLevel,
        int maxLevel,
        TextureSwizzle swizzle) {

    public TextureParameters {
        if (minFilter == null) {
            throw new IllegalArgumentException("minFilter must not be null");
        }
        if (magFilter == null) {
            throw new IllegalArgumentException("magFilter must not be null");
        }
        if (wrapS == null) {
            throw new IllegalArgumentException("wrapS must not be null");
        }
        if (wrapT == null) {
            throw new IllegalArgumentException("wrapT must not be null");
        }
        if (wrapR == null) {
            throw new IllegalArgumentException("wrapR must not be null");
        }
        if (compareMode == null) {
            throw new IllegalArgumentException("compareMode must not be null");
        }
        if (compareFunction == null) {
            throw new IllegalArgumentException("compareFunction must not be null");
        }
        if (borderColor == null) {
            throw new IllegalArgumentException("borderColor must not be null");
        }
        if (swizzle == null) {
            throw new IllegalArgumentException("swizzle must not be null");
        }
        minLod = canonical(minLod, "minLod");
        maxLod = canonical(maxLod, "maxLod");
        lodBias = canonical(lodBias, "lodBias");
        maxAnisotropy = canonical(maxAnisotropy, "maxAnisotropy");
        if (minLod > maxLod) {
            throw new IllegalArgumentException("minLod must be <= maxLod: " + minLod + " > " + maxLod);
        }
        if (baseLevel < 0) {
            throw new IllegalArgumentException("baseLevel must be >= 0: " + baseLevel);
        }
        if (maxLevel < baseLevel) {
            throw new IllegalArgumentException("maxLevel must be >= baseLevel: " + maxLevel + " < " + baseLevel);
        }
        if (maxAnisotropy != 1.0f) {
            throw new IllegalArgumentException("this scope permits maxAnisotropy = 1 only: " + maxAnisotropy);
        }
    }

    private static float canonical(float v, String name) {
        if (!Float.isFinite(v)) {
            throw new IllegalArgumentException(name + " must be finite: " + v);
        }
        return v + 0.0f; // canonicalizes -0.0 to +0.0
    }
}
