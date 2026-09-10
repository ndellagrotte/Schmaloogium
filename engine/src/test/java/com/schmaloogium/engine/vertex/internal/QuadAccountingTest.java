// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T10-INGRESS pure kernel (PHASE_10_DOC §4.2, §8): incremental and bulk ingress share
 * one accounting path — a quad may split across scalar/bulk boundaries only between
 * completed vertices, completed quads never finalize twice, a partial quad at a seal
 * or scope boundary is malformed, and bulk entry with a pending partial vertex
 * rejects.
 */
class QuadAccountingTest {

    @Test
    void fourIncrementalVerticesCompleteExactlyOneQuad() {
        QuadAccounting accounting = new QuadAccounting();
        for (int vertex = 0; vertex < 4; vertex++) {
            accounting.beginVertex();
            accounting.endVertex();
        }
        assertEquals(1, accounting.quads());
        assertEquals(1, accounting.pendingFinalization());
        accounting.markFinalizedThrough(1);
        assertEquals(0, accounting.pendingFinalization());
        assertTrue(accounting.atQuadBoundary());
    }

    @Test
    void quadSplitsAcrossScalarAndBulkWithoutDoubleFinalization() {
        QuadAccounting accounting = new QuadAccounting();
        accounting.beginVertex();
        accounting.endVertex(); // vertex 0 complete
        accounting.bulkVertices(3); // vertices 1..3 complete the quad
        assertEquals(1, accounting.quads());
        assertEquals(1, accounting.pendingFinalization());
        accounting.markFinalizedThrough(1);

        // A second quad split the other way: bulk first, scalar tail.
        accounting.bulkVertices(2);
        assertFalse(accounting.atQuadBoundary());
        accounting.beginVertex();
        accounting.endVertex();
        accounting.beginVertex();
        accounting.endVertex();
        assertEquals(2, accounting.quads());
        assertEquals(1, accounting.pendingFinalization());
    }

    @Test
    void bulkEntryWithAPendingPartialVertexRejects() {
        QuadAccounting accounting = new QuadAccounting();
        accounting.beginVertex();
        assertThrows(IllegalStateException.class, () -> accounting.bulkVertices(4));
        accounting.endVertex();
        // After the ordinary completion bulk entry is legal again.
        accounting.bulkVertices(4);
        assertEquals(1, accounting.quads());
    }

    @Test
    void bulkAppendsWholeRecordsOnlyInCount() {
        QuadAccounting accounting = new QuadAccounting();
        accounting.bulkVertices(5);
        assertEquals(1, accounting.quads());
        assertFalse(accounting.atQuadBoundary());
        accounting.bulkVertices(3);
        assertEquals(2, accounting.quads());
        assertTrue(accounting.atQuadBoundary());
    }

    @Test
    void partialQuadAtSealOrScopeBoundaryIsMalformed() {
        QuadAccounting accounting = new QuadAccounting();
        accounting.beginVertex();
        assertThrows(IllegalStateException.class,
                () -> accounting.requireQuadBoundary("seal"));
        accounting.endVertex();
        assertThrows(IllegalStateException.class,
                () -> accounting.requireQuadBoundary("block scope exit"));
        accounting.bulkVertices(3);
        accounting.requireQuadBoundary("seal");
    }

    @Test
    void dirtyTailTracksTheLowestMutatedQuad() {
        QuadAccounting accounting = new QuadAccounting();
        accounting.bulkVertices(12); // three complete quads
        accounting.markFinalizedThrough(3);
        accounting.markDirty(2);
        accounting.markDirty(1);
        assertEquals(1, accounting.dirtyFrom());
        accounting.clearDirtyThrough(2);
        assertEquals(-1, accounting.dirtyFrom());
        accounting.markDirty(0);
        accounting.clearDirtyThrough(1);
        assertEquals(-1, accounting.dirtyFrom());
    }

    @Test
    void finalizationWatermarkIsMonotonic() {
        QuadAccounting accounting = new QuadAccounting();
        accounting.bulkVertices(4);
        accounting.markFinalizedThrough(1);
        // Re-acknowledging the same batch is an idempotent no-op.
        accounting.markFinalizedThrough(1);
        assertThrows(IllegalStateException.class, () -> accounting.markFinalizedThrough(0));
        assertThrows(IllegalStateException.class, () -> accounting.markFinalizedThrough(2));
    }

    @Test
    void resetStartsQuadsFromVertexZero() {
        QuadAccounting accounting = new QuadAccounting();
        accounting.bulkVertices(6);
        accounting.reset();
        assertEquals(0, accounting.quads());
        assertTrue(accounting.atQuadBoundary());
        assertEquals(-1, accounting.dirtyFrom());
        assertFalse(accounting.atQuadBoundary() && accounting.quads() != 0);
    }

    @Test
    void negativeBulkCountAndWatermarkOverflowReject() {
        QuadAccounting accounting = new QuadAccounting();
        assertThrows(IllegalArgumentException.class, () -> accounting.bulkVertices(-1));
        assertThrows(IllegalStateException.class, () -> accounting.markFinalizedThrough(1));
        assertThrows(IndexOutOfBoundsException.class, () -> accounting.markDirty(0));
    }
}
