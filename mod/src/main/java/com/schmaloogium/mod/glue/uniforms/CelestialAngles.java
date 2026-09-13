// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.uniforms;

/**
 * The pack-facing sun angle derived from vanilla's celestial angle (RESEARCH App D.2,
 * PHASE_6_DOC §4.2): vanilla's {@code getCelestialAngle} is 0 at noon and 0.75 at dawn;
 * the pack's {@code sunAngle} is 0 at sunrise, 0.25 at noon, 0.5 at sunset, 0.75 at
 * midnight, and {@code shadowAngle} folds the night half back onto [0, 0.5). Pure.
 */
public final class CelestialAngles {

    private CelestialAngles() {
    }

    /** Vanilla's celestial angle reduced to [0, 1); non-finite input is noon (0). */
    public static float skyAngle(float celestialAngle) {
        if (!Float.isFinite(celestialAngle)) {
            return 0f;
        }
        float fraction = celestialAngle - (float) Math.floor(celestialAngle);
        return fraction >= 1f || fraction < 0f ? 0f : fraction;
    }

    /** {@code sunAngle = sky < 0.75 ? sky + 0.25 : sky - 0.75}, in [0, 1). */
    public static float sunAngle(float celestialAngle) {
        float sky = skyAngle(celestialAngle);
        float sun = sky < 0.75f ? sky + 0.25f : sky - 0.75f;
        return sun >= 1f || sun < 0f ? 0f : sun;
    }

    /** {@code shadowAngle = sunAngle <= 0.5 ? sunAngle : sunAngle - 0.5}. */
    public static float shadowAngle(float celestialAngle) {
        float sun = sunAngle(celestialAngle);
        return sun <= 0.5f ? sun : sun - 0.5f;
    }
}
