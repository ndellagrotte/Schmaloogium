// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.OptionKind;
import com.schmaloogium.engine.config.SourceAttribution;

import java.util.List;
import java.util.Optional;

/** One immutable discovered option definition in source order. */
public record OptionDefinition(
        String name,
        OptionKind kind,
        OptionValue defaultValue,
        List<OptionValue> allowedValues,
        OptionAvailability availability,
        Optional<String> tooltip,
        List<SourceAttribution> occurrences) {

    public OptionDefinition {
        java.util.Objects.requireNonNull(name, "name");
        java.util.Objects.requireNonNull(kind, "kind");
        java.util.Objects.requireNonNull(defaultValue, "defaultValue");
        allowedValues = List.copyOf(allowedValues);
        java.util.Objects.requireNonNull(availability, "availability");
        tooltip = tooltip == null ? Optional.empty() : tooltip;
        occurrences = List.copyOf(occurrences);
    }
}
