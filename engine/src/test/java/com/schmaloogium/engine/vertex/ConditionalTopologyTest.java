// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T10-TOPOLOGY (PHASE_10_DOC §4.6, §8): the category matrix — NONE preserves, TRIANGLES
 * preserves triangles/strips/fans and converts QUADS only, adjacency families accept
 * only their own sources, no points/lines synthesis exists — plus the checked
 * (0,1,3),(1,2,3) whole-record expansion with bit-for-bit copies and source bytes left
 * untouched.
 */
class ConditionalTopologyTest {

    private static final int STRIDE = Classic56Layout.STRIDE_BYTES;

    @Test
    void nonePreservesEverySourcePrimitive() {
        for (ConditionalTopology.SourcePrimitive source
                : ConditionalTopology.SourcePrimitive.values()) {
            var decision = ConditionalTopology.plan(source, VertexGeometryInput.NONE);
            assertTrue(decision instanceof ConditionalTopology.TopologyDecision.Preserve,
                    source + " under NONE must preserve");
        }
    }

    @Test
    void trianglesInputConvertsQuadsOnly() {
        assertPreserves(VertexGeometryInput.TRIANGLES,
                ConditionalTopology.SourcePrimitive.TRIANGLES,
                ConditionalTopology.SourcePrimitive.TRIANGLE_STRIP,
                ConditionalTopology.SourcePrimitive.TRIANGLE_FAN);
        assertTrue(ConditionalTopology.plan(ConditionalTopology.SourcePrimitive.QUADS,
                VertexGeometryInput.TRIANGLES)
                instanceof ConditionalTopology.TopologyDecision.ExpandQuads);
        // QUAD_STRIP and POLYGON match no geometry input primitive (ARB_gs4 Issue 30).
        assertRejected(VertexGeometryInput.TRIANGLES,
                ConditionalTopology.SourcePrimitive.QUAD_STRIP,
                ConditionalTopology.SourcePrimitive.POLYGON,
                ConditionalTopology.SourcePrimitive.LINES,
                ConditionalTopology.SourcePrimitive.POINTS,
                ConditionalTopology.SourcePrimitive.LINES_ADJACENCY);
    }

    @Test
    void otherInputsAcceptOnlyTheirOwnFamilies() {
        assertTrue(ConditionalTopology.plan(ConditionalTopology.SourcePrimitive.POINTS,
                VertexGeometryInput.POINTS)
                instanceof ConditionalTopology.TopologyDecision.Preserve);
        assertRejected(VertexGeometryInput.POINTS, ConditionalTopology.SourcePrimitive.QUADS,
                ConditionalTopology.SourcePrimitive.LINES);

        assertPreserves(VertexGeometryInput.LINES,
                ConditionalTopology.SourcePrimitive.LINES,
                ConditionalTopology.SourcePrimitive.LINE_STRIP,
                ConditionalTopology.SourcePrimitive.LINE_LOOP);
        assertRejected(VertexGeometryInput.LINES, ConditionalTopology.SourcePrimitive.QUADS,
                ConditionalTopology.SourcePrimitive.TRIANGLES);

        assertPreserves(VertexGeometryInput.LINES_ADJACENCY,
                ConditionalTopology.SourcePrimitive.LINES_ADJACENCY,
                ConditionalTopology.SourcePrimitive.LINE_STRIP_ADJACENCY);
        assertRejected(VertexGeometryInput.LINES_ADJACENCY,
                ConditionalTopology.SourcePrimitive.QUADS);

        assertPreserves(VertexGeometryInput.TRIANGLES_ADJACENCY,
                ConditionalTopology.SourcePrimitive.TRIANGLES_ADJACENCY,
                ConditionalTopology.SourcePrimitive.TRIANGLE_STRIP_ADJACENCY);
        assertRejected(VertexGeometryInput.TRIANGLES_ADJACENCY,
                ConditionalTopology.SourcePrimitive.QUADS);
    }

    @Test
    void expansionEmitsTheCanonicalDiagonalInOrder() {
        int quads = 2;
        ByteBuffer source = quadSource(quads);
        ByteBuffer pristine = source.duplicate().order(source.order());
        var expansion = ConditionalTopology.expandQuads(source, 0, quads * 4, STRIDE);
        assertTrue(expansion instanceof ConditionalTopology.QuadExpansion.Expanded);
        var expanded = (ConditionalTopology.QuadExpansion.Expanded) expansion;
        assertEquals(quads, expanded.quadCount());
        assertEquals(quads * 6 * STRIDE, expanded.derived().remaining());

        int[] expected = {0, 1, 3, 1, 2, 3, 4, 5, 7, 5, 6, 7};
        for (int record = 0; record < quads * 6; record++) {
            assertEquals(expected[record], expanded.derived().getInt(record * STRIDE),
                    "record " + record);
        }
        // Source bytes are never modified.
        for (int i = 0; i < source.capacity(); i++) {
            assertEquals(pristine.get(i), source.get(i), "source byte " + i);
        }
    }

    @Test
    void expansionCopiesWholeRecordsBitForBit() {
        ByteBuffer source = quadSource(1);
        // Stamp a distinguishing byte pattern across one full record (identity words,
        // padding, tangent — everything).
        for (int i = 8; i < STRIDE; i++) {
            source.put(i, (byte) (0x80 | (i & 0x7f)));
        }
        var expanded = (ConditionalTopology.QuadExpansion.Expanded)
                ConditionalTopology.expandQuads(source, 0, 4, STRIDE);
        ByteBuffer derived = expanded.derived();
        int[] permutation = {0, 1, 3, 1, 2, 3};
        for (int record = 0; record < 6; record++) {
            for (int i = 0; i < STRIDE; i++) {
                assertEquals(source.get(permutation[record] * STRIDE + i),
                        derived.get(record * STRIDE + i));
            }
        }
    }

    @Test
    void expansionValidatesRangesAndCountsBeforeAllocation() {
        assertEquals(ConditionalTopology.ExpansionRejection.PARTIAL_QUAD,
                ((ConditionalTopology.QuadExpansion.Rejected) ConditionalTopology.expandQuads(
                        quadSource(1), 0, 5, STRIDE)).reason());

        ByteBuffer small = quadSource(1);
        assertEquals(ConditionalTopology.ExpansionRejection.RANGE_OVERFLOW,
                ((ConditionalTopology.QuadExpansion.Rejected) ConditionalTopology.expandQuads(
                        small, small.capacity() + 1, 4, STRIDE)).reason());
        assertEquals(ConditionalTopology.ExpansionRejection.RANGE_OVERFLOW,
                ((ConditionalTopology.QuadExpansion.Rejected) ConditionalTopology.expandQuads(
                        small, 0, 1 << 20, STRIDE)).reason());

        ByteBuffer hugeCount = ByteBuffer.allocateDirect(0);
        assertEquals(ConditionalTopology.ExpansionRejection.RANGE_OVERFLOW,
                ((ConditionalTopology.QuadExpansion.Rejected) ConditionalTopology.expandQuads(
                        hugeCount, 0, 4, STRIDE)).reason());
    }

    @Test
    void expansionOfEmptyRangeYieldsAnEmptyDerivedStream() {
        var expansion = ConditionalTopology.expandQuads(quadSource(1), 0, 0, STRIDE);
        assertTrue(expansion instanceof ConditionalTopology.QuadExpansion.Expanded);
        assertEquals(0, ((ConditionalTopology.QuadExpansion.Expanded) expansion).derived().remaining());
    }

    @Test
    void strideBelowTheClassicFloorIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> ConditionalTopology.expandQuads(quadSource(1), 0, 4, 28));
    }

    private static void assertPreserves(VertexGeometryInput input,
                                        ConditionalTopology.SourcePrimitive... sources) {
        for (ConditionalTopology.SourcePrimitive source : sources) {
            var decision = ConditionalTopology.plan(source, input);
            assertTrue(decision instanceof ConditionalTopology.TopologyDecision.Preserve,
                    source + " under " + input);
            assertEquals(source,
                    ((ConditionalTopology.TopologyDecision.Preserve) decision).source());
        }
    }

    private static void assertRejected(VertexGeometryInput input,
                                       ConditionalTopology.SourcePrimitive... sources) {
        for (ConditionalTopology.SourcePrimitive source : sources) {
            var decision = ConditionalTopology.plan(source, input);
            assertTrue(decision instanceof ConditionalTopology.TopologyDecision.Rejected,
                    source + " under " + input);
        }
    }

    /** Builds little-endian records whose first int marks the source record index. */
    private static ByteBuffer quadSource(int quads) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(quads * 4 * STRIDE)
                .order(ByteOrder.LITTLE_ENDIAN);
        for (int record = 0; record < quads * 4; record++) {
            int base = record * STRIDE;
            buffer.putInt(base, record);
            for (int i = 4; i < STRIDE; i++) {
                buffer.put(base + i, (byte) record);
            }
        }
        return buffer;
    }
}
