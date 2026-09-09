// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.EngineOptionData;

import com.schmaloogium.engine.pack.CompanionOptionMacros;

import java.util.List;
import java.util.Map;

/** Immutable macro graph; companion projection must agree with the option family. */
public record MacroConfiguration(
        MacroIdentityPolicy identityPolicy,
        List<MacroDefinition> baseCompatibilityMacros,
        List<MacroDefinition> optionMacros,
        CompanionOptionMacros companionOptionMacros,
        List<MacroDefinition> capabilityFeatureMacros,
        List<MacroDefinition> engineIdentityMacros,
        Map<String, MacroOverride> perPackOverrides,
        List<String> reservedContributors) {

    public MacroConfiguration {
        java.util.Objects.requireNonNull(identityPolicy, "identityPolicy");
        baseCompatibilityMacros = List.copyOf(baseCompatibilityMacros);
        optionMacros = List.copyOf(optionMacros);
        java.util.Objects.requireNonNull(companionOptionMacros, "companionOptionMacros");
        capabilityFeatureMacros = List.copyOf(capabilityFeatureMacros);
        engineIdentityMacros = List.copyOf(engineIdentityMacros);
        java.util.SortedMap<String, MacroOverride> ordered =
            new java.util.TreeMap<>(EngineOptionData::compareUnsignedUtf8);
        ordered.putAll(java.util.Objects.requireNonNull(perPackOverrides, "perPackOverrides"));
        perPackOverrides = java.util.Collections.unmodifiableSortedMap(ordered);
        reservedContributors = List.copyOf(reservedContributors);
        if (!companionProjectionAgrees()) {
            throw new IllegalArgumentException("companion pair disagrees with optionMacros projection");
        }
    }

    private boolean companionProjectionAgrees() {
        boolean normal = optionMacros.stream().anyMatch(m -> m.name().equals("MC_NORMAL_MAP"));
        boolean specular = optionMacros.stream().anyMatch(m -> m.name().equals("MC_SPECULAR_MAP"));
        return normal == companionOptionMacros.normalMap()
            && specular == companionOptionMacros.specularMap();
    }
}
