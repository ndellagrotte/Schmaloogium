// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * One valid canonical block state of the live registry projection (PHASE_9_DOC §2.3).
 * {@code legacyMetadata} is the live {@code getMetaFromState} capture, {@code renderType}
 * the live {@code EnumBlockRenderType} ordinal, and {@code emittedLight} the zero-context
 * light after glue range validation. Property tuples are canonical {@code name -> value}
 * text in canonical property-name order.
 */
public record BlockStateRecord(
        int stateOrdinal,
        int blockOrdinal,
        int legacyMetadata,
        SortedMap<String, String> properties,
        int renderType,
        boolean solidOpaqueCube,
        int emittedLight) {

    public BlockStateRecord {
        if (stateOrdinal < 0) {
            throw new IllegalArgumentException("stateOrdinal must be >= 0");
        }
        if (blockOrdinal < 0) {
            throw new IllegalArgumentException("blockOrdinal must be >= 0");
        }
        if (legacyMetadata < 0 || legacyMetadata > 15) {
            throw new IllegalArgumentException("legacyMetadata must be in 0..15: " + legacyMetadata);
        }
        if (renderType < 0 || renderType > 0xffff) {
            throw new IllegalArgumentException("renderType must fit the 16-bit vertex field");
        }
        if (emittedLight < 0 || emittedLight > 15) {
            throw new IllegalArgumentException("emittedLight must be in 0..15: " + emittedLight);
        }
        SortedMap<String, String> ordered = new TreeMap<>();
        for (SortedMap.Entry<String, String> entry : java.util.Objects
                .requireNonNull(properties, "properties").entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (name == null || !name.matches("[a-z0-9_]+")) {
                throw new IllegalArgumentException("invalid property name: " + name);
            }
            if (value == null || value.isEmpty() || value.stripTrailing().length() != value.length()
                    || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
                throw new IllegalArgumentException("invalid property value for " + name);
            }
            ordered.put(name, value);
        }
        properties = java.util.Collections.unmodifiableSortedMap(ordered);
    }

    /** The canonical property tuple text used for deterministic state ordering. */
    public String canonicalPropertyTuple() {
        StringBuilder text = new StringBuilder();
        for (SortedMap.Entry<String, String> entry : properties.entrySet()) {
            if (text.length() > 0) {
                text.append('|');
            }
            text.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return text.toString();
    }
}
