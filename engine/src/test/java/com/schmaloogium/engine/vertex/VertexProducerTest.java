// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The item→block mapping table (PHASE_10_DOC §4.2 D-P10-30, §4.6 D-P10-29): ITEM is
 * partial ingress that maps onto the BLOCK builder by semantic — never a draw mask —
 * BLOCK's final mask keeps UV1 until the real brightness writer completes it, and
 * OLDMODEL never admits COLOR/UV1.
 */
class VertexProducerTest {

    @Test
    void blockFinalParticipationIncludesUv1AndNormal() {
        assertEquals(java.util.Set.of(
                        ConventionalInput.POSITION, ConventionalInput.COLOR,
                        ConventionalInput.UV0, ConventionalInput.UV1,
                        ConventionalInput.NORMAL),
                VertexProducer.BLOCK.participation());
    }

    @Test
    void oldmodelNeverAdmitsColorOrUv1() {
        assertEquals(java.util.Set.of(
                        ConventionalInput.POSITION, ConventionalInput.UV0,
                        ConventionalInput.NORMAL),
                VertexProducer.OLDMODEL.participation());
        assertFalse(VertexProducer.OLDMODEL.participation().contains(ConventionalInput.COLOR));
        assertFalse(VertexProducer.OLDMODEL.participation().contains(ConventionalInput.UV1));
    }

    @Test
    void itemIsPartialIngressWithoutUv1() {
        assertEquals(java.util.Set.of(
                        ConventionalInput.POSITION, ConventionalInput.COLOR,
                        ConventionalInput.UV0, ConventionalInput.NORMAL),
                VertexProducer.ITEM.participation());
        assertFalse(VertexProducer.ITEM.participation().contains(ConventionalInput.UV1));
    }

    @Test
    void itemToBlockIngressTableMapsBySemanticAndDefersUv1() {
        Map<ClassicSemantic, ClassicSemantic> table = VertexProducer.itemToBlockIngress();
        assertEquals(4, table.size());
        assertEquals(ClassicSemantic.POSITION, table.get(ClassicSemantic.POSITION));
        assertEquals(ClassicSemantic.COLOR, table.get(ClassicSemantic.COLOR));
        assertEquals(ClassicSemantic.UV0, table.get(ClassicSemantic.UV0));
        assertEquals(ClassicSemantic.NORMAL, table.get(ClassicSemantic.NORMAL));
        // The BLOCK builder's UV1 comes only from the real brightness writer.
        assertFalse(table.containsKey(ClassicSemantic.UV1));
        assertEquals(4, VertexProducer.UV1_COMPLETION_VERTICES);
    }

    @Test
    void tableDestinationSemanticsCarryClassicOffsets() {
        Map<ClassicSemantic, ClassicSemantic> table = VertexProducer.itemToBlockIngress();
        for (ClassicSemantic destination : table.values()) {
            assertTrue(destination.byteOffset() >= 0);
            assertTrue(destination.byteOffset() + destination.byteSize() <= 32,
                    "ingress destinations stay inside the ordinary prefix");
        }
        assertEquals(28, table.get(ClassicSemantic.NORMAL).byteOffset());
    }

    @Test
    void participationSetsAreImmutable() {
        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> VertexProducer.BLOCK.participation().add(ConventionalInput.UV1));
        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> VertexProducer.ITEM.participation().remove(ConventionalInput.NORMAL));
    }
}
