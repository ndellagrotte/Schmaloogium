// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.MacroOverride;

import com.schmaloogium.engine.config.EngineOptionData;

import java.util.Map;

/** Immutable runtime identity; MC tuple must be exactly (1,12,2). */
public record RuntimeIdentityData(
        int mcMajor,
        int mcMinor,
        int mcPatch,
        String engineEdition,
        String engineVersion,
        OsFamily osFamily,
        Map<String, MacroOverride> perPackIdentityOverrides) {

    public RuntimeIdentityData {
        java.util.Objects.requireNonNull(engineEdition, "engineEdition");
        java.util.Objects.requireNonNull(engineVersion, "engineVersion");
        java.util.Objects.requireNonNull(osFamily, "osFamily");
        java.util.SortedMap<String, MacroOverride> ordered =
            new java.util.TreeMap<>(EngineOptionData::compareUnsignedUtf8);
        ordered.putAll(perPackIdentityOverrides == null
            ? Map.of() : perPackIdentityOverrides);
        perPackIdentityOverrides = java.util.Collections.unmodifiableSortedMap(ordered);
    }
}
