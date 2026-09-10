// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * Resolved alias/layer tables plus first-writer attribution for conflict diagnostics
 * (PHASE_9_DOC §4.4). Primitive arrays only; presence is a boolean lane so explicit
 * shader ID zero stays distinguishable from absence. The live captured per-state facts
 * back the {@code mc_Entity} high word and metadata without touching the snapshot.
 */
final class IdResolutionTables {

    final int stateCount;
    final int itemCount;
    final int entityCount;
    final int[] blockIds;
    final boolean[] blockAssigned;
    final int[] itemIds;
    final boolean[] itemAssigned;
    final int[] entityIds;
    final boolean[] entityAssigned;
    final ResolvedRenderLayer[] layers;
    /** The rule (or fallback marker) that first wrote each cell, for conflict evidence. */
    final Object[] winnerByState;
    final Object[] winnerByItem;
    final Object[] winnerByEntity;
    /** Live captured per-state facts backing the mc_Entity high word and metadata. */
    final int[] renderType;
    final int[] legacyMetadata;

    int blockAssignments;
    int itemAssignments;
    int entityAssignments;
    int layerAssignments;

    IdResolutionTables(int stateCount, int itemCount, int entityCount) {
        this.stateCount = stateCount;
        this.itemCount = itemCount;
        this.entityCount = entityCount;
        this.blockIds = new int[stateCount];
        this.blockAssigned = new boolean[stateCount];
        this.itemIds = new int[itemCount];
        this.winnerByItem = new Object[itemCount];
        this.winnerByEntity = new Object[entityCount];
        this.itemAssigned = new boolean[itemCount];
        this.entityIds = new int[entityCount];
        this.entityAssigned = new boolean[entityCount];
        this.layers = new ResolvedRenderLayer[stateCount];
        this.winnerByState = new Object[stateCount];
        this.renderType = new int[stateCount];
        this.legacyMetadata = new int[stateCount];
    }

    /** Marker for tier-4 legacy numeric fallback attribution. */
    static final Object FALLBACK = new Object();
}
