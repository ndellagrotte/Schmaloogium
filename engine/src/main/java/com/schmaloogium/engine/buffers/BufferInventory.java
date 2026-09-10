// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.BufferDomain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Duplicate-free inventory of buffers in canonical order: {@link BufferDomain} declaration order, then
 * {@link BufferIndex} ascending (PHASE_5_DOC §2.2).
 */
public record BufferInventory(List<BufferInventoryEntry> entries) {

    public BufferInventory {
        Objects.requireNonNull(entries, "entries");
        List<BufferInventoryEntry> sorted = new ArrayList<>(entries);
        for (BufferInventoryEntry entry : sorted) {
            Objects.requireNonNull(entry, "entry");
        }
        sorted.sort(Comparator.comparing((BufferInventoryEntry entry) -> entry.buffer().domain())
                .thenComparingInt(entry -> entry.buffer().index().value()));
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).buffer().equals(sorted.get(i - 1).buffer())) {
                throw new IllegalArgumentException("duplicate buffer inventory entry: " + sorted.get(i).buffer());
            }
        }
        entries = List.copyOf(sorted);
    }

    /** Number of inventory rows in the given domain. */
    public int count(BufferDomain domain) {
        Objects.requireNonNull(domain, "domain");
        int n = 0;
        for (BufferInventoryEntry entry : entries) {
            if (entry.buffer().domain() == domain) {
                n++;
            }
        }
        return n;
    }
}
