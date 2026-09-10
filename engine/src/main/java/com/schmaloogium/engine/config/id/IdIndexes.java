// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Name, numeric, owning-block and property-domain indexes built once per build over the
 * immutable snapshot (PHASE_9_DOC §7). Resolution is O(rules × matched candidates); the
 * property indexes bound it to the affected block's states.
 */
final class IdIndexes {

    private final IdRegistrySnapshot registries;
    private final BlockStateRecord[] states;
    private final Map<RegistryName, BlockTypeRecord> blockByName;
    private final Map<Integer, BlockTypeRecord> blockByNumericId;
    private final Map<RegistryName, ItemTypeRecord> itemByName;
    private final Map<RegistryName, EntityTypeRecord> entityByName;
    private final BlockTypeRecord[] ownerByState;
    private final Map<String, Set<RegistryName>> universeNames;
    private final Map<String, Map<String, PropertyDomain>> propertyDomains = new HashMap<>();

    IdIndexes(IdRegistrySnapshot registries) {
        this.registries = registries;
        this.states = registries.blockStates().toArray(new BlockStateRecord[0]);
        this.blockByName = new HashMap<>();
        this.blockByNumericId = new HashMap<>();
        for (BlockTypeRecord block : registries.blocks()) {
            blockByName.put(block.name(), block);
            blockByNumericId.putIfAbsent(block.liveLegacyNumericId(), block);
        }
        this.ownerByState = new BlockTypeRecord[registries.blockStates().size()];
        for (BlockTypeRecord block : registries.blocks()) {
            for (int stateOrdinal : block.stateOrdinals()) {
                ownerByState[stateOrdinal] = block;
            }
        }
        this.itemByName = new HashMap<>();
        for (ItemTypeRecord item : registries.items()) {
            itemByName.put(item.name(), item);
        }
        this.entityByName = new HashMap<>();
        for (EntityTypeRecord entity : registries.entities()) {
            entityByName.put(entity.name(), entity);
        }
        this.universeNames = new HashMap<>();
        universeNames.put(universe("block"), blockByName.keySet());
        universeNames.put(universe("item"), itemByName.keySet());
        universeNames.put(universe("entity"), entityByName.keySet());
    }

    private static String universe(String name) {
        return name;
    }

    IdRegistrySnapshot registries() {
        return registries;
    }

    int stateCount() {
        return states.length;
    }

    BlockStateRecord state(int ordinal) {
        return states[ordinal];
    }

    BlockTypeRecord block(RegistryName name) {
        return blockByName.get(name);
    }

    BlockTypeRecord blockByNumericId(int numericId) {
        return blockByNumericId.get(numericId);
    }

    /** The block owning one state ordinal (dense-checked by the snapshot). */
    BlockTypeRecord owningBlock(int stateOrdinal) {
        return ownerByState[stateOrdinal];
    }

    ItemTypeRecord item(RegistryName name) {
        return itemByName.get(name);
    }

    EntityTypeRecord entity(RegistryName name) {
        return entityByName.get(name);
    }

    /** Whether the name exists in the named universe: "block", "item" or "entity". */
    boolean universeContains(String universe, RegistryName name) {
        Set<RegistryName> names = universeNames.get(universe);
        return names != null && names.contains(name);
    }

    /** The finite value domain of one property across the block's states, or null. */
    PropertyDomain propertyDomain(BlockTypeRecord block, String property) {
        String blockKey = block.name().canonical();
        Map<String, PropertyDomain> byProperty =
                propertyDomains.computeIfAbsent(blockKey, k -> new HashMap<>());
        PropertyDomain domain = byProperty.get(property);
        if (domain == null) {
            Set<String> values = new HashSet<>();
            for (int stateOrdinal : block.stateOrdinals()) {
                String value = states[stateOrdinal].properties().get(property);
                if (value != null) {
                    values.add(value);
                }
            }
            domain = new PropertyDomain(values);
            byProperty.put(property, domain);
        }
        return domain;
    }

    /** The finite property value domain captured from the block's states. */
    static final class PropertyDomain {
        private final Set<String> values;

        PropertyDomain(Set<String> values) {
            this.values = values;
        }

        boolean contains(String value) {
            return values.contains(value);
        }

        /** Whether any canonical decimal domain value lies within the closed range. */
        boolean intersects(int lowerInclusive, int upperInclusive) {
            for (String value : values) {
                Integer parsed = canonicalDecimal(value);
                if (parsed != null && parsed >= lowerInclusive && parsed <= upperInclusive) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Parses canonical nonnegative decimal text ({@code 0|[1-9][0-9]*}), else null. */
    static Integer canonicalDecimal(String text) {
        if (!text.matches("0|[1-9][0-9]*")) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
