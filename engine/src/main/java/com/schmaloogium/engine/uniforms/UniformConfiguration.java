// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/**
 * The immutable derivation of one Phase 3 {@code PackConfiguration} (PHASE_6_DOC §4.1):
 * the pack schema/configuration fingerprint, the four smoothing half-lives in ticks,
 * the center-depth requirement and the fixed catalog version. Never holds a source
 * string, parser, Minecraft object, program handle, framebuffer handle or mutable
 * provider. Half-lives are validated finite and non-negative here — a malformed
 * directive is a Phase 3 diagnostic/default matter; an invariant breach rejects the
 * uniform candidate without GL work.
 */
public record UniformConfiguration(
        String packFingerprint,
        double wetnessHalflifeTicks,
        double drynessHalflifeTicks,
        double eyeBrightnessHalflifeTicks,
        double centerDepthHalflifeTicks,
        boolean centerDepthRequired,
        int catalogVersion) {

    public UniformConfiguration {
        if (packFingerprint == null || packFingerprint.isEmpty()) {
            throw new IllegalArgumentException("packFingerprint must be non-empty");
        }
        requireHalfLife(wetnessHalflifeTicks, "wetnessHalflifeTicks");
        requireHalfLife(drynessHalflifeTicks, "drynessHalflifeTicks");
        requireHalfLife(eyeBrightnessHalflifeTicks, "eyeBrightnessHalflifeTicks");
        requireHalfLife(centerDepthHalflifeTicks, "centerDepthHalflifeTicks");
    }

    private static void requireHalfLife(double value, String name) {
        if (!Double.isFinite(value) || value < 0d) {
            throw new IllegalArgumentException(name + " must be finite and non-negative: " + value);
        }
    }
}
