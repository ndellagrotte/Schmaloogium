// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * O(1) full-int alias queries and the exact two-word {@code mc_Entity} result, stamped
 * with the publication generation (PHASE_9_DOC §2.2/§4.10). Phase 10 borrows one lookup,
 * records {@link #generation()}, and discards work whose generation changed. Lookups are
 * bounds checks plus primitive-array reads: no registry, property map, string parsing or
 * allocation on the hot path. Out-of-range ordinals are programmer/protocol rejections
 * and never index an array.
 */
public interface AliasLookup {

    /** The publication generation this lookup was issued for. */
    long generation();

    /** The full-int block alias for one state ordinal, with explicit presence. */
    AliasValue blockId(int blockStateOrdinal);

    /** The full-int item alias for one item ordinal, with explicit presence. */
    AliasValue itemId(int itemOrdinal);

    /** The full-int entity alias for one entity type ordinal, with explicit presence. */
    AliasValue entityId(int entityTypeOrdinal);

    /** The two exact {@code mc_Entity} payload words for one state ordinal. */
    BlockStampResult mcEntity(int blockStateOrdinal);
}
