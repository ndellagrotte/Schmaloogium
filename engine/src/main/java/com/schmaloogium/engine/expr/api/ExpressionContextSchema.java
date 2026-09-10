// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/** Immutable biome-constant catalog plus the fixed fourteen view-boolean name set (§4.10).
 * Biome names are unique {@code BIOME_*} identifiers and IDs are exactly float-representable. */
public record ExpressionContextSchema(String version, Map<String, Integer> biomeConstants) {

    public ExpressionContextSchema {
        Objects.requireNonNull(version, "version");
        if (version.isEmpty()) {
            throw new IllegalArgumentException("version must be non-empty");
        }
        Objects.requireNonNull(biomeConstants, "biomeConstants");
        TreeMap<String, Integer> sorted = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : biomeConstants.entrySet()) {
            String name = Objects.requireNonNull(entry.getKey(), "biome name");
            Integer id = Objects.requireNonNull(entry.getValue(), "biome id");
            if (!name.matches("BIOME_[A-Za-z0-9_]+")) {
                throw new IllegalArgumentException("invalid biome constant name: " + name);
            }
            if (id != (int) (float) id) {
                throw new IllegalArgumentException("biome id not float-representable: " + id);
            }
            sorted.put(name, id);
        }
        biomeConstants = Map.copyOf(sorted);
    }
}
