// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Derived value: the sampler-object state of a texture (R-P14→P1-2, PHASE_1_DOC §4.7.8;
 * PHASE_14_DOC §4.1.2). D-P14-1: a sampler is DERIVED, never authored — Phase 5 (and, at
 * v0.5, Phase 13) remain the sole authors of what filter and wrap a texture has; Phase 14
 * computes the key. The key is exactly the sampler-state prefix of
 * {@link TextureParameters} (§4.7.7): minFilter, magFilter, wrapS/T/R, compareMode,
 * compareFunction, borderColor, minLod, maxLod, lodBias and maxAnisotropy — copied
 * losslessly by {@link #of}; baseLevel/maxLevel/swizzle stay on the object. The record is
 * a pure value, so equality is structural and interning is deterministic.
 */
public record SamplerKey(TextureMinFilter minFilter,
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
                         float maxAnisotropy) {

    /** Copies the sampler-state prefix of {@code parameters} losslessly (D-P14-1): no
     *  reduced filter vocabulary, no guessed field. */
    public static SamplerKey of(TextureParameters parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("parameters must not be null");
        }
        return new SamplerKey(
                parameters.minFilter(),
                parameters.magFilter(),
                parameters.wrapS(),
                parameters.wrapT(),
                parameters.wrapR(),
                parameters.compareMode(),
                parameters.compareFunction(),
                parameters.borderColor(),
                parameters.minLod(),
                parameters.maxLod(),
                parameters.lodBias(),
                parameters.maxAnisotropy());
    }
}
