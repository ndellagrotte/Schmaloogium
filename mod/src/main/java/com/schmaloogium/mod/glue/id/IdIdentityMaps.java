// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * The glue-side identity maps from canonical Minecraft objects to the snapshot's dense
 * ordinals (PHASE_9_DOC §2.3: "the glue retains separate identity maps ... they never
 * cross the seam"). Built once per snapshot, never mutated afterwards, so chunk workers
 * may read them concurrently. Unknown objects answer {@code -1}.
 */
public final class IdIdentityMaps {

    public static final IdIdentityMaps EMPTY = new IdIdentityMaps(
            new IdentityHashMap<>(), new IdentityHashMap<>(), new IdentityHashMap<>());

    private final Map<Object, Integer> states;
    private final Map<Object, Integer> items;
    private final Map<Object, Integer> entities;

    IdIdentityMaps(IdentityHashMap<Object, Integer> states, IdentityHashMap<Object, Integer> items,
                   IdentityHashMap<Object, Integer> entities) {
        this.states = Collections.unmodifiableMap(states);
        this.items = Collections.unmodifiableMap(items);
        this.entities = Collections.unmodifiableMap(entities);
    }

    /** The block-state ordinal of a canonical state object, or -1. */
    public int stateOrdinal(Object state) {
        Integer o = state == null ? null : states.get(state);
        return o == null ? -1 : o;
    }

    /** The item ordinal of an {@code Item} object, or -1. */
    public int itemOrdinal(Object item) {
        Integer o = item == null ? null : items.get(item);
        return o == null ? -1 : o;
    }

    /** The entity-type ordinal of an entity class, or -1. */
    public int entityOrdinal(Object entityClass) {
        Integer o = entityClass == null ? null : entities.get(entityClass);
        return o == null ? -1 : o;
    }

    public int stateCount() {
        return states.size();
    }
}
