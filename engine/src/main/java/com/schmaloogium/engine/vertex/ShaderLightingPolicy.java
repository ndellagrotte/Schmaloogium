// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

/**
 * Immutable effective shader lighting/AO pair (PHASE_10_DOC §5.1, §4.8.1): the
 * shader-visible {@code oldLighting} fixed directional shading and {@code separateAo}
 * color-channel policy. Phase 7 resolves the pair before vertex preparation; bake and
 * build tasks capture it in transition/bake identity and never sample it per vertex
 * from mutable settings.
 *
 * <p>Local missing-value fallbacks (D-P10-11): {@code oldLighting} defaults
 * {@code true}; {@code separateAo} defaults {@code false}. {@code default} and
 * {@code false} user wire states remain fingerprint-distinct upstream — this record
 * carries only the resolved effective pair.
 */
public record ShaderLightingPolicy(boolean oldLighting, boolean separateAo) {

    /** The missing-value fallback pair: fixed directional shade on, AO kept in RGB. */
    public static final ShaderLightingPolicy DEFAULT = new ShaderLightingPolicy(true, false);
}
