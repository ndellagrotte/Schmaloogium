// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.ProfileName;

import java.util.Map;

/** One locale's complete decoration catalog (D-P3-63). */
public record LangDecorations(
        Map<String, String> optionLabels,
        Map<String, String> optionComments,
        Map<ValueDecorationKey, String> valueLabels,
        Map<String, String> prefixes,
        Map<String, String> suffixes,
        Map<ProfileName, String> profileLabels,
        Map<ProfileName, String> profileComments,
        Map<String, String> screenLabels,
        Map<String, String> screenComments) {

    public LangDecorations {
        optionLabels = Map.copyOf(optionLabels);
        optionComments = Map.copyOf(optionComments);
        valueLabels = Map.copyOf(valueLabels);
        prefixes = Map.copyOf(prefixes);
        suffixes = Map.copyOf(suffixes);
        profileLabels = Map.copyOf(profileLabels);
        profileComments = Map.copyOf(profileComments);
        screenLabels = Map.copyOf(screenLabels);
        screenComments = Map.copyOf(screenComments);
    }

    /** A decoration catalog with nine empty maps. */
    public static LangDecorations empty() {
        return new LangDecorations(Map.of(), Map.of(), Map.of(), Map.of(), Map.of(),
                Map.of(), Map.of(), Map.of(), Map.of());
    }
}
