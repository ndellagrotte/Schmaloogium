// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The legacy tag shim catalog (PHASE_9_DOC §4.7): tag keys map to named membership
 * providers whose expansion is frozen into {@link TagMembershipSnapshot} before any
 * resolution runs. This is the ore-dictionary-style shim RESEARCH requires — never a
 * claim that 1.13 datapack tags exist, and deliberately without automatic camel-case or
 * path conversion between modern tags and OreDictionary names.
 */
public record LegacyTagCatalog(int version, Map<String, List<Binding>> bindings) {

    public LegacyTagCatalog {
        if (version < 1) {
            throw new IllegalArgumentException("catalog version must be >= 1");
        }
        TreeMap<String, List<Binding>> ordered = new TreeMap<>();
        for (Map.Entry<String, List<Binding>> entry : java.util.Objects
                .requireNonNull(bindings, "bindings").entrySet()) {
            String key = java.util.Objects.requireNonNull(entry.getKey(), "tag key");
            if (key.isEmpty()) {
                throw new IllegalArgumentException("tag key must be non-empty");
            }
            List<Binding> copied = new ArrayList<>();
            for (Binding binding : entry.getValue()) {
                copied.add(java.util.Objects.requireNonNull(binding, "binding"));
            }
            ordered.put(key, List.copyOf(copied));
        }
        bindings = java.util.Collections.unmodifiableSortedMap(ordered);
    }

    /** The canonical empty catalog. */
    public static LegacyTagCatalog empty() {
        return new LegacyTagCatalog(1, Map.of());
    }

    /** One named membership provider binding. */
    public record Binding(String providerName, TagMembershipProvider provider) {

        public Binding {
            if (providerName == null || providerName.isEmpty()) {
                throw new IllegalArgumentException("providerName must be non-empty");
            }
            java.util.Objects.requireNonNull(provider, "provider");
        }
    }

    /** Returns only registry names present in the supplied snapshot; may be empty. */
    @FunctionalInterface
    public interface TagMembershipProvider {

        Collection<RegistryName> members(String tagKey, IdRegistrySnapshot registries);
    }
}
