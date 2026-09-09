// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Closed Appendix F.1 engine flags; every absent flag is {@code DEFAULT}. */
public record EngineFlags(
        CloudMode clouds,
        TriState oldHandLight,
        TriState dynamicHandLight,
        TriState oldLighting,
        TriState shadowTranslucent,
        TriState underwaterOverlay,
        TriState sun,
        TriState moon,
        TriState vignette,
        TriState backFaceSolid,
        TriState backFaceCutout,
        TriState backFaceCutoutMipped,
        TriState backFaceTranslucent,
        TriState rainDepth,
        TriState beaconBeamDepth,
        TriState separateAo,
        TriState frustumCulling) {

    /** The all-{@code DEFAULT}/{@code DEFAULT} baseline used when no flags are declared. */
    public static EngineFlags allDefault() {
        return new EngineFlags(CloudMode.DEFAULT, TriState.DEFAULT, TriState.DEFAULT,
                TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT,
                TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT,
                TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT,
                TriState.DEFAULT, TriState.DEFAULT);
    }
}
