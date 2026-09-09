// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance;

import com.schmaloogium.engine.gl.GLCapabilityProfile;

import java.util.Set;

/**
 * Synthetic capability profiles for hermetic harness runs (§4.12's synthetic minimum,
 * built programmatically: the committed profile *files* live in {@code :engine}'s
 * {@code testFixtures}, which this wave cannot create — named deferral).
 */
public final class SyntheticProfiles {

    /** A realistic desktop GL 3.3 profile, fully synthetic. */
    public static GLCapabilityProfile syntheticGl33() {
        return new GLCapabilityProfile(3, 3, "3.30 NVIDIA synthetic", "Schmaloogium",
            "conformance-synthetic", 8, 8, 16, 16, 16384, 0, 0,
            Set.of("GL_ARB_texture_rectangle"));
    }

    /** The GL 2.1 minimum: no 3D/rectangle targets, no mipmap generation. */
    public static GLCapabilityProfile minimumGl21() {
        return new GLCapabilityProfile(2, 1, "1.20 synthetic", "Schmaloogium",
            "conformance-synthetic-minimum", 4, 4, 12, 8, 4096, 0, 0,
            Set.of());
    }

    private SyntheticProfiles() {
    }
}
