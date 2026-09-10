// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.List;

/** The complete current-schema ID-mapping input. */
public record IdMappingInput(
        int schemaVersion,
        IdMappingMacroEnvironment parserEnvironment,
        IdMappingFileInput blocks,
        IdMappingFileInput items,
        IdMappingFileInput entities,
        IdMappingFileInput layers) {

    public IdMappingInput {
        java.util.Objects.requireNonNull(parserEnvironment, "parserEnvironment");
        java.util.Objects.requireNonNull(blocks, "blocks");
        java.util.Objects.requireNonNull(items, "items");
        java.util.Objects.requireNonNull(entities, "entities");
        java.util.Objects.requireNonNull(layers, "layers");
        if (blocks.kind() != MappingKind.BLOCK || items.kind() != MappingKind.ITEM
                || entities.kind() != MappingKind.ENTITY || layers.kind() != MappingKind.LAYER) {
            throw new IllegalArgumentException("kind/file mismatch in IdMappingInput");
        }
        // D-P3-72: BLOCK/ENTITY may publish forced11300Rules; ITEM/LAYER never do
        if (!items.forced11300Rules().isEmpty() || !layers.forced11300Rules().isEmpty()) {
            throw new IllegalArgumentException("forced rules are legal only on BLOCK and ENTITY");
        }
        for (IdMappingFileInput f : List.of(blocks, items, entities, layers)) {
            for (MappingRule r : f.ordinaryRules()) {
                if (r.era() != MappingEra.CLASSIC) {
                    throw new IllegalArgumentException("ordinary rules are CLASSIC");
                }
            }
            for (MappingRule r : f.forced11300Rules()) {
                if (r.era() != MappingEra.MODERN) {
                    throw new IllegalArgumentException("forced rules are MODERN");
                }
            }
        }
    }
}
