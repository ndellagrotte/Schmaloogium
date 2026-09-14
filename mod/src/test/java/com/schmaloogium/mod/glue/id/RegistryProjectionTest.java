// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.schmaloogium.engine.config.id.BlockStateRecord;
import com.schmaloogium.engine.config.id.IdRegistrySnapshot;
import com.schmaloogium.engine.config.id.RegistryName;
import com.schmaloogium.engine.config.id.TagMembershipSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

/** The glue reproduces the engine's canonical ordering and keeps identities out of the seam. */
class RegistryProjectionTest {

    private static RegistryProjection.StateInput state(Object id, int meta, String... kv) {
        TreeMap<String, String> props = new TreeMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            props.put(kv[i], kv[i + 1]);
        }
        return new RegistryProjection.StateInput(id, meta, props, 3, true, 0);
    }

    @Test
    void blocksStatesItemsAndEntitiesAreDenseAndCanonicallyOrdered() {
        Object stoneState = new Object();
        Object grassSnowy = new Object();
        Object grassPlain = new Object();
        Object stickItem = new Object();
        Object stoneItem = new Object();
        Object cowClass = new Object();
        Object batClass = new Object();
        var blocks = List.of(
            new RegistryProjection.BlockInput("minecraft", "stone", 1, List.of(state(stoneState, 0))),
            new RegistryProjection.BlockInput("minecraft", "grass", 2, List.of(
                state(grassSnowy, 0, "snowy", "true"), state(grassPlain, 0, "snowy", "false"))));
        var items = List.of(
            new RegistryProjection.ItemInput(stickItem, "minecraft", "stick", Optional.empty()),
            new RegistryProjection.ItemInput(stoneItem, "minecraft", "stone", Optional.of(stoneState)));
        var entities = List.of(
            new RegistryProjection.EntityInput(cowClass, "minecraft", "cow"),
            new RegistryProjection.EntityInput(batClass, "minecraft", "bat"));

        RegistryProjection.Projection p = RegistryProjection.project(7, blocks, items, entities,
            TagMembershipSnapshot.empty());
        IdRegistrySnapshot s = p.snapshot();

        assertEquals(7, s.registryGeneration());
        assertEquals(List.of(new RegistryName("minecraft", "grass"), new RegistryName("minecraft", "stone")),
            s.blocks().stream().map(b -> b.name()).toList());
        // grass states sort by property tuple: snowy=false before snowy=true; stone follows.
        List<BlockStateRecord> states = s.blockStates();
        assertEquals("snowy=false", states.get(0).canonicalPropertyTuple());
        assertEquals("snowy=true", states.get(1).canonicalPropertyTuple());
        assertEquals(1, states.get(2).blockOrdinal());
        assertEquals(0, p.maps().stateOrdinal(grassPlain));
        assertEquals(1, p.maps().stateOrdinal(grassSnowy));
        assertEquals(2, p.maps().stateOrdinal(stoneState));
        assertEquals(-1, p.maps().stateOrdinal(new Object()));
        // items by name; the ItemBlock relation resolves to the placed default state ordinal.
        assertEquals(0, p.maps().itemOrdinal(stickItem));
        assertEquals(1, p.maps().itemOrdinal(stoneItem));
        assertEquals(OptionalInt.of(2), s.items().get(1).placedBlockDefaultStateOrdinal());
        assertEquals(0, p.maps().entityOrdinal(batClass));
        assertEquals(1, p.maps().entityOrdinal(cowClass));
        // the fingerprint is content-derived: the same inputs in another order agree.
        RegistryProjection.Projection again = RegistryProjection.project(7,
            List.of(blocks.get(1), blocks.get(0)), items, entities, TagMembershipSnapshot.empty());
        assertEquals(s.fingerprint(), again.snapshot().fingerprint());
    }

    @Test
    void invalidPropertyNamesAreRejectedByTheEngineValidation() {
        var blocks = List.of(new RegistryProjection.BlockInput("minecraft", "odd", 9,
            List.of(state(new Object(), 0, "Bad Name", "x"))));
        assertThrows(IllegalArgumentException.class, () -> RegistryProjection.project(1, blocks,
            List.of(), List.of(), TagMembershipSnapshot.empty()));
    }
}
