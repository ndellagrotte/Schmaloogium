// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.MappingKind;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The bounded, origin-preserving active-mod source corpus in deterministic mod order
 * (PHASE_9_DOC §4.3, D-P9-9): Unicode code-point {@code modid}, then mapping kind. The
 * canonical order is enforced here, so permuted loader enumeration still yields identical
 * tables and fingerprints. Duplicate {@code (modId, kind)} pairs are rejected; duplicate
 * active containers with the same mod id are the provider's rejection, never re-derived
 * here.
 */
public record ModIdSourceSnapshot(List<ModSourceEntry> sources) {

    /** Engine-side defense-in-depth re-validation of the glue's corpus bounds. */
    public static final int MAX_ENTRIES = 4096;
    public static final int MAX_TOTAL_BYTES = 32 * 1024 * 1024;

    public ModIdSourceSnapshot {
        sources = new ArrayList<>(java.util.Objects.requireNonNull(sources, "sources"));
        if (sources.size() > MAX_ENTRIES) {
            throw new IllegalArgumentException("mod-source corpus exceeds the entry bound");
        }
        sources.sort(Comparator.comparing(ModSourceEntry::modId)
                .thenComparing(entry -> entry.kind().ordinal()));
        Set<String> seen = new HashSet<>();
        long total = 0;
        for (ModSourceEntry entry : sources) {
            if (!seen.add(entry.modId() + ":" + entry.kind())) {
                throw new IllegalArgumentException("duplicate mod source: "
                        + entry.modId() + ":" + entry.kind());
            }
            total += entry.bytes().size();
            if (total > MAX_TOTAL_BYTES) {
                throw new IllegalArgumentException("mod-source corpus exceeds the byte bound");
            }
        }
        sources = List.copyOf(sources);
    }

    /** The canonical empty snapshot. */
    public static ModIdSourceSnapshot empty() {
        return new ModIdSourceSnapshot(List.of());
    }

    /** Deterministic corpus fingerprint over the canonical order. */
    public String fingerprint() {
        StringBuilder canonical = new StringBuilder();
        for (ModSourceEntry entry : sources) {
            canonical.append(entry.modId()).append(':').append(entry.kind())
                    .append(':').append(entry.sourceName())
                    .append(':').append(entry.fingerprint()).append('\n');
        }
        return Fingerprints.sha256Hex(canonical.toString());
    }

}
