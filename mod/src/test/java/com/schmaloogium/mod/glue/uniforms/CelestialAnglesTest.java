// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.uniforms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** App D.2: vanilla's celestial angle (0 = noon, 0.75 = dawn) to the pack's sunrise-based angles. */
class CelestialAnglesTest {

    @Test
    void sunAngleIsSunriseBased() {
        assertEquals(0.25f, CelestialAngles.sunAngle(0.0f), 1e-6f, "noon");
        assertEquals(0.0f, CelestialAngles.sunAngle(0.75f), 1e-6f, "dawn");
        assertEquals(0.5f, CelestialAngles.sunAngle(0.25f), 1e-6f, "dusk");
        assertEquals(0.75f, CelestialAngles.sunAngle(0.5f), 1e-6f, "midnight");
        assertEquals(0.25f, CelestialAngles.sunAngle(Float.NaN), 0f, "non-finite is noon");
    }

    @Test
    void shadowAngleFoldsTheNightHalf() {
        assertEquals(0.25f, CelestialAngles.shadowAngle(0.0f), 1e-6f);
        assertEquals(0.25f, CelestialAngles.shadowAngle(0.5f), 1e-6f, "midnight folds onto noon");
        assertEquals(0.5f, CelestialAngles.shadowAngle(0.25f), 1e-6f, "dusk stays at the boundary");
    }

    @Test
    void skyAngleReducesToTheUnitInterval() {
        assertEquals(0.5f, CelestialAngles.skyAngle(2.5f), 1e-6f);
        assertEquals(0.0f, CelestialAngles.skyAngle(1.0f), 0f);
    }
}
