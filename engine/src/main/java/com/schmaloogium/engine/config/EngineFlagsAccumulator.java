// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.LinkedHashMap;
import java.util.Map;

/** Last-valid-wins accumulator for the one CloudMode and sixteen TriState flags. */
public final class EngineFlagsAccumulator {

    static final Map<String, Integer> TRI_STATES = new LinkedHashMap<>();
    private static final String[] ORDER = {
        "oldHandLight", "dynamicHandLight", "oldLighting", "shadowTranslucent",
        "underwaterOverlay", "sun", "moon", "vignette",
        "backFace.solid", "backFace.cutout", "backFace.cutoutMipped", "backFace.translucent",
        "rain.depth", "beacon.beam.depth", "separateAo", "frustum.culling"
    };

    static {
        for (int i = 0; i < ORDER.length; i++) {
            TRI_STATES.put(ORDER[i], i);
        }
    }

    CloudMode clouds = CloudMode.DEFAULT;
    final Map<String, TriState> triStates = new LinkedHashMap<>();

    EngineFlags build() {
        return new EngineFlags(
            clouds,
            tri(ORDER[0]), tri(ORDER[1]), tri(ORDER[2]), tri(ORDER[3]),
            tri(ORDER[4]), tri(ORDER[5]), tri(ORDER[6]), tri(ORDER[7]),
            tri(ORDER[8]), tri(ORDER[9]), tri(ORDER[10]), tri(ORDER[11]),
            tri(ORDER[12]), tri(ORDER[13]), tri(ORDER[14]), tri(ORDER[15]));
    }

    private TriState tri(String key) {
        return triStates.getOrDefault(key, TriState.DEFAULT);
    }
}
