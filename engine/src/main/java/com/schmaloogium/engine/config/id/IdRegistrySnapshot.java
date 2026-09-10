// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The deep-immutable, schema-versioned loader-neutral registry/state/tag projection
 * (PHASE_9_DOC §2.3). Lists are sorted by {@code (namespace,path)} and then canonical
 * state-property tuple; ordinals are dense and snapshot-local, so a list index always
 * equals its ordinal. The snapshot rejects duplicate names/ordinals, non-dense ordinals,
 * invalid property tuples, light outside 0–15 and missing fingerprints before any
 * resolution runs. Minecraft objects never appear here; glue keeps the identity maps.
 */
public record IdRegistrySnapshot(
        long registryGeneration,
        IdRegistryFingerprint fingerprint,
        List<BlockTypeRecord> blocks,
        List<BlockStateRecord> blockStates,
        List<ItemTypeRecord> items,
        List<EntityTypeRecord> entities,
        TagMembershipSnapshot tags) {

    public IdRegistrySnapshot {
        if (registryGeneration < 0) {
            throw new IllegalArgumentException("registryGeneration must be >= 0");
        }
        java.util.Objects.requireNonNull(fingerprint, "fingerprint");
        java.util.Objects.requireNonNull(tags, "tags");
        // Sort defensive mutable copies; freeze the canonical order afterwards.
        List<BlockTypeRecord> mutableBlocks = new ArrayList<>(blocks);
        List<BlockStateRecord> mutableStates = new ArrayList<>(blockStates);
        List<ItemTypeRecord> mutableItems = new ArrayList<>(items);
        List<EntityTypeRecord> mutableEntities = new ArrayList<>(entities);
        sortAndValidate(mutableBlocks, mutableStates, mutableItems, mutableEntities);
        blocks = List.copyOf(mutableBlocks);
        blockStates = List.copyOf(mutableStates);
        items = List.copyOf(mutableItems);
        entities = List.copyOf(mutableEntities);
    }

    private static void sortAndValidate(List<BlockTypeRecord> blocks,
            List<BlockStateRecord> blockStates, List<ItemTypeRecord> items,
            List<EntityTypeRecord> entities) {
        Set<RegistryName> blockNames = new HashSet<>();
        Map<Integer, Integer> stateCounts = new HashMap<>();
        for (BlockTypeRecord block : blocks) {
            if (!blockNames.add(block.name())) {
                throw new IllegalArgumentException("duplicate block name: " + block.name());
            }
            for (int stateOrdinal : block.stateOrdinals()) {
                Integer previous = stateCounts.put(stateOrdinal, block.blockOrdinal());
                if (previous != null) {
                    throw new IllegalArgumentException(
                            "state ordinal " + stateOrdinal + " owned by two blocks");
                }
            }
        }
        // States sort by owning block (namespace,path), then canonical property tuple.
        Map<Integer, RegistryName> blockNamesByOrdinal = new HashMap<>();
        for (BlockTypeRecord block : blocks) {
            blockNamesByOrdinal.put(block.blockOrdinal(), block.name());
        }
        List<BlockStateRecord> orderedStates = new ArrayList<>(blockStates);
        orderedStates.sort(Comparator
                .comparing((BlockStateRecord s) -> blockNamesByOrdinal.get(s.blockOrdinal()))
                .thenComparing(BlockStateRecord::canonicalPropertyTuple));
        for (int i = 0; i < orderedStates.size(); i++) {
            BlockStateRecord state = orderedStates.get(i);
            if (state.stateOrdinal() != i) {
                throw new IllegalArgumentException("block-state ordinals must be dense at index "
                        + i + " but found " + state.stateOrdinal());
            }
            Integer owner = stateCounts.get(state.stateOrdinal());
            if (owner == null) {
                throw new IllegalArgumentException("state ordinal "
                        + state.stateOrdinal() + " is not owned by any block");
            }
            if (owner != state.blockOrdinal()) {
                throw new IllegalArgumentException("state ordinal "
                        + state.stateOrdinal() + " disagrees about its owning block");
            }
        }
        blockStates = orderedStates;
        blocks.sort(Comparator.comparing(BlockTypeRecord::name));
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).blockOrdinal() != i) {
                throw new IllegalArgumentException("block ordinals must be dense at index " + i);
            }
        }
        validateNamed(items, ItemTypeRecord::itemOrdinal, ItemTypeRecord::name, "item");
        validateNamed(entities, EntityTypeRecord::entityTypeOrdinal, EntityTypeRecord::name,
                "entity");
        for (ItemTypeRecord item : items) {
            if (item.placedBlockDefaultStateOrdinal().isPresent()
                    && item.placedBlockDefaultStateOrdinal().getAsInt() >= orderedStates.size()) {
                throw new IllegalArgumentException("item placed state ordinal out of range");
            }
        }
    }

    private static <T> void validateNamed(List<T> records, java.util.function.ToIntFunction<T> ordinal,
            java.util.function.Function<T, RegistryName> name, String universe) {
        List<T> ordered = new ArrayList<>(records);
        ordered.sort(Comparator.comparing(name));
        Set<RegistryName> seen = new HashSet<>();
        for (int i = 0; i < ordered.size(); i++) {
            T record = ordered.get(i);
            if (ordinal.applyAsInt(record) != i) {
                throw new IllegalArgumentException(universe + " ordinals must be dense at index " + i);
            }
            if (!seen.add(name.apply(record))) {
                throw new IllegalArgumentException("duplicate " + universe + " name: "
                        + name.apply(record));
            }
        }
        records.sort(Comparator.comparing(name));
    }

    /** Canonical sorted-map view used by builders and tests. */
    public TreeMap<RegistryName, BlockTypeRecord> blocksByName() {
        TreeMap<RegistryName, BlockTypeRecord> index = new TreeMap<>();
        for (BlockTypeRecord block : blocks) {
            index.put(block.name(), block);
        }
        return index;
    }
}
