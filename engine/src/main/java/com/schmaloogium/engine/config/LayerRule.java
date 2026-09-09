// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.List;
import java.util.Optional;

/** One layer mapping rule. */
public record LayerRule(
        RequestedRenderLayer layer,
        SelectorKind selectorKind,
        String selectorToken,
        Optional<MetadataConstraint> legacyMetadata,
        List<PropertyPredicate> propertyPredicates,
        MappingEra era,
        MappingOrigin origin,
        int sourceLine,
        int selectorOrdinal) implements MappingRule {

    public LayerRule {
        java.util.Objects.requireNonNull(layer, "layer");
        java.util.Objects.requireNonNull(selectorKind, "selectorKind");
        java.util.Objects.requireNonNull(selectorToken, "selectorToken");
        legacyMetadata = legacyMetadata == null ? Optional.empty() : legacyMetadata;
        propertyPredicates = List.copyOf(propertyPredicates);
        java.util.Objects.requireNonNull(era, "era");
        java.util.Objects.requireNonNull(origin, "origin");
    }
}
