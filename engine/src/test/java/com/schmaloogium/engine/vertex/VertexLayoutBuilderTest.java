// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T10-GROWTH (PHASE_10_DOC §4.10, §8): a test-only appended named field changes the
 * stride while the unchanged binder/copy/state algorithms keep delivering supplied
 * values — extensibility infrastructure, not modern pack support.
 */
class VertexLayoutBuilderTest {

    @Test
    void growthFollowsTheCanonicalAtMidBlockExample() {
        // §4.10: signed-byte xyz at offset 56 plus one reserved byte → 60-byte stride.
        VertexLayoutBuilder builder = Classic56Layout.grow();
        assertEquals(56, builder.strideBytes());
        builder.append("at_midBlock", 3, StorageType.INT8, Delivery.FLOAT_VALUE);
        assertEquals(59, builder.strideBytes());
        builder.reserve("reserved", 1);
        assertEquals(60, builder.strideBytes());
        VertexLayout grown = builder.build("CLASSIC_60_EXAMPLE");
        assertEquals(60, grown.strideBytes());
        assertEquals(56, Classic56Layout.field(grown, "at_midBlock").byteOffset());
        assertEquals(59, Classic56Layout.field(grown, "reserved").byteOffset());
        // The classic floor fields are untouched by the append.
        assertEquals(48, Classic56Layout.field(grown, "mc_Entity").byteOffset());
        assertNotEquals(Classic56Layout.layout().fingerprint(), grown.fingerprint());
    }

    @Test
    void appendedFloatFieldAlignsItself() {
        VertexLayoutBuilder builder = Classic56Layout.grow()
                .append("extraByte", 1, StorageType.UINT8, Delivery.PADDING);
        assertEquals(57, builder.strideBytes());
        VertexLayout layout = builder
                .append("extraFloat", 1, StorageType.FLOAT32, Delivery.FLOAT_VALUE)
                .build("ALIGNED");
        // The float field inserts an implicit 3-byte run so it lands on its alignment.
        assertEquals(60, Classic56Layout.field(layout, "extraFloat").byteOffset());
        assertEquals(64, layout.strideBytes());
        VertexField gap = Classic56Layout.field(layout, "padding@57");
        assertEquals(Delivery.PADDING, gap.delivery());
        assertEquals(3, gap.components());
    }

    @Test
    void identicalContentBuildsIdenticalFingerprints() {
        String first = Classic56Layout.grow()
                .append("at_midBlock", 3, StorageType.INT8, Delivery.FLOAT_VALUE)
                .build("A").fingerprint();
        String second = Classic56Layout.grow()
                .append("at_midBlock", 3, StorageType.INT8, Delivery.FLOAT_VALUE)
                .build("B").fingerprint();
        assertEquals(first, second);
    }

    @Test
    void invalidFieldsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Classic56Layout.grow().append("x", 0, StorageType.UINT8, Delivery.PADDING));
        assertThrows(IllegalArgumentException.class,
                () -> Classic56Layout.grow().append("x", 5, StorageType.UINT8, Delivery.PADDING));
        assertThrows(IllegalArgumentException.class,
                () -> Classic56Layout.grow().append("mc_Entity", 1, StorageType.UINT8,
                        Delivery.PADDING));
        assertThrows(IllegalArgumentException.class,
                () -> Classic56Layout.grow().append(null, 1, StorageType.UINT8, Delivery.PADDING));
        assertThrows(IllegalArgumentException.class, () -> Classic56Layout.grow().reserve("r", 0));
    }

    @Test
    void seededBuilderRejectsOverlappingSeeds() {
        assertThrows(IllegalArgumentException.class, () -> VertexLayoutBuilder.seeded(
                java.util.List.of(
                        new VertexField("a", 0, 4, StorageType.FLOAT32, Delivery.FLOAT_VALUE),
                        new VertexField("b", 2, 4, StorageType.FLOAT32, Delivery.FLOAT_VALUE)),
                8));
        // A gapless seed builds and continues from the larger of startOffset/seed end.
        VertexLayout seeded = VertexLayoutBuilder.seeded(
                java.util.List.of(new VertexField("a", 0, 4, StorageType.FLOAT32,
                        Delivery.FLOAT_VALUE)), 16)
                .append("b", 1, StorageType.UINT8, Delivery.PADDING)
                .build("SEEDED");
        assertEquals(17, seeded.strideBytes());
    }

    @Test
    void unchangedCopyAlgorithmsDeliverGrownRecords() {
        // The topology expander is stride-parametrized: the same binder code delivers a
        // grown layout without consumer edits (T10-GROWTH extensible-copy contract).
        int stride = 60;
        ByteBuffer source = ByteBuffer.allocateDirect(4 * stride)
                .order(java.nio.ByteOrder.LITTLE_ENDIAN);
        for (int record = 0; record < 4; record++) {
            source.putInt(record * stride, record);
        }
        var expansion = ConditionalTopology.expandQuads(source, 0, 4, stride);
        assertTrue(expansion instanceof ConditionalTopology.QuadExpansion.Expanded);
        ByteBuffer derived = ((ConditionalTopology.QuadExpansion.Expanded) expansion).derived();
        int[] expected = {0, 1, 3, 1, 2, 3};
        for (int record = 0; record < 6; record++) {
            assertEquals(expected[record], derived.getInt(record * stride));
        }
    }
}
