// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Membership frozen from the {@link LegacyTagCatalog} before any resolution runs
 * (PHASE_9_DOC §4.7). Every entry name is present in the same {@link IdRegistrySnapshot};
 * expansions are sorted by registry name and deduplicated. Immutable and source-free.
 */
public record TagMembershipSnapshot(Map<String, List<RegistryName>> memberships) {

    private static final TagMembershipSnapshot EMPTY =
            new TagMembershipSnapshot(Map.of());

    public TagMembershipSnapshot {
        SortedMap<String, List<RegistryName>> ordered = new TreeMap<>();
        for (Map.Entry<String, List<RegistryName>> entry : java.util.Objects
                .requireNonNull(memberships, "memberships").entrySet()) {
            String key = java.util.Objects.requireNonNull(entry.getKey(), "tag key");
            if (key.isEmpty()) {
                throw new IllegalArgumentException("tag key must be non-empty");
            }
            List<RegistryName> sorted = List.copyOf(entry.getValue());
            RegistryName previous = null;
            for (RegistryName name : sorted) {
                if (previous != null && previous.compareTo(name) >= 0) {
                    throw new IllegalArgumentException("tag membership must be sorted and distinct");
                }
                previous = name;
            }
            ordered.put(key, sorted);
        }
        memberships = java.util.Collections.unmodifiableSortedMap(ordered);
    }

    /** The canonical empty snapshot (no frozen tags). */
    public static TagMembershipSnapshot empty() {
        return EMPTY;
    }

    /** The sorted expansion of one tag key, or empty when the key is unknown. */
    public Optional<List<RegistryName>> members(String tagKey) {
        return Optional.ofNullable(memberships.get(tagKey));
    }
}
