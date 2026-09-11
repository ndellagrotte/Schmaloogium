// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.config.CenterDepthRequirements;
import com.schmaloogium.engine.config.SmoothingConstants;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.uniforms.UniformConfiguration;

import org.junit.jupiter.api.Test;

class UniformConfigurationsTest {

    @Test
    void derivation_carriesFingerprintHalfLivesAndCenterDepth() {
        UniformConfiguration configuration = UniformConfigurations.derive("fp-1",
                new SmoothingConstants(600f, 200f, 10f, 1f), new CenterDepthRequirements(true));
        assertEquals("fp-1", configuration.packFingerprint());
        assertEquals(600d, configuration.wetnessHalflifeTicks());
        assertEquals(200d, configuration.drynessHalflifeTicks());
        assertEquals(10d, configuration.eyeBrightnessHalflifeTicks());
        assertEquals(1d, configuration.centerDepthHalflifeTicks());
        assertTrue(configuration.centerDepthRequired());
        assertEquals(PackFrontEnd.CURRENT_SCHEMA_VERSION, configuration.catalogVersion());
    }
}
