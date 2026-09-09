// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.Map;
import java.util.Set;

/**
 * Stage-wide readable permission and write intent for one pass (PHASE_4_DOC §4.4) — not
 * the program's declaration set; the effective {@link ProgramSamplerLayout} is that exact
 * declaration contract. {@code explicitFlips} holds only the Phase 3 tri-state override
 * entries; absence is not serialized as {@code false}. Symbolic ranges stay symbolic until
 * Phase 5 supplies the estate size. Never stores a main/alt texture side. Immutable.
 */
public record PassResourceAccess(
        Set<BufferRef> readable,
        Set<BufferRef> writes,
        Map<BufferRef, Boolean> explicitFlips,
        Set<BufferRef> mipmappedBeforeRead) {

    private static final PassResourceAccess EMPTY =
        new PassResourceAccess(Set.of(), Set.of(), Map.of(), Set.of());

    public PassResourceAccess {
        readable = Set.copyOf(readable);
        writes = Set.copyOf(writes);
        explicitFlips = Map.copyOf(explicitFlips);
        mipmappedBeforeRead = Set.copyOf(mipmappedBeforeRead);
    }

    /** The all-empty access: used by virtual prelude descriptors. */
    public static PassResourceAccess empty() {
        return EMPTY;
    }
}
