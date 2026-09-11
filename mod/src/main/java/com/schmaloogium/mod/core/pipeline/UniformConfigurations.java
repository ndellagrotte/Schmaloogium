// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.engine.config.CenterDepthRequirements;
import com.schmaloogium.engine.config.SmoothingConstants;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.uniforms.UniformConfiguration;

/**
 * The pure derivation of the P6 {@link UniformConfiguration} from an accepted P3
 * configuration: the pack fingerprint, the four §4.7 smoothing half-lives and the
 * center-depth requirement. {@code catalogVersion} only stamps P6's fixed expression
 * schema; the front end's current schema version is the one catalog constant it exposes.
 */
public final class UniformConfigurations {

    private UniformConfigurations() {
    }

    public static UniformConfiguration derive(PackConfiguration configuration) {
        return derive(configuration.fingerprint().value(),
                configuration.resources().smoothing(),
                configuration.resources().centerDepth());
    }

    public static UniformConfiguration derive(String packFingerprint, SmoothingConstants smoothing,
                                              CenterDepthRequirements centerDepth) {
        return new UniformConfiguration(
                packFingerprint,
                smoothing.wetnessHalfLifeTicks(),
                smoothing.drynessHalfLifeTicks(),
                smoothing.eyeBrightnessHalfLifeTicks(),
                smoothing.centerDepthHalfLifeTicks(),
                centerDepth.required(),
                PackFrontEnd.CURRENT_SCHEMA_VERSION);
    }
}
