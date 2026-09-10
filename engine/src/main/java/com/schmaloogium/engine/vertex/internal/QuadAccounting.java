// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex.internal;

/**
 * Per-builder quad accounting and dirty-range tracking (PHASE_10_DOC §4.2, §4.5):
 * vertices consumed by the current quad, pending quad count, the first unfinalized
 * quad and the dirty tail. Quads group from vertex zero of each begin and never across
 * builders, scopes or draw modes.
 *
 * <p>Incremental and bulk ingress share one accounting path, so a quad may span an
 * incremental/bulk boundary only between completed vertices and a completed quad is
 * never finalized twice. Bulk entry requires no pending partial vertex; a partially
 * written vertex is never implicitly ended or overwritten. A partial quad at a block
 * boundary or seal is malformed and throws — the caller rejects that product instead
 * of combining adjacent blocks into one tangent computation.
 */
public final class QuadAccounting {

    private long consumedInQuad;
    private boolean partialVertex;
    private long quads;
    private long finalizedThrough;
    private long dirtyFrom = -1;

    /** Begins one incremental destination vertex. */
    public void beginVertex() {
        if (partialVertex) {
            throw new IllegalStateException("a vertex is already open");
        }
        partialVertex = true;
    }

    /** Completes the open incremental vertex; may complete the current quad. */
    public void endVertex() {
        if (!partialVertex) {
            throw new IllegalStateException("no open vertex to complete");
        }
        partialVertex = false;
        advance(1);
    }

    /**
     * Accounts {@code count} whole bulk-appended vertices. Requires no pending partial
     * vertex; whole records only.
     */
    public void bulkVertices(long count) {
        if (partialVertex) {
            throw new IllegalStateException("bulk entry with a pending partial vertex");
        }
        if (count < 0) {
            throw new IllegalArgumentException("negative bulk vertex count: " + count);
        }
        advance(count);
    }

    /** The number of complete quads formed so far since the last reset. */
    public long quads() {
        return quads;
    }

    /** Complete quads not yet finalized (identity stamp plus derived attributes). */
    public long pendingFinalization() {
        return quads - finalizedThrough;
    }

    /** Acknowledges finalization of all quads below {@code quadCountExclusive}. */
    public void markFinalizedThrough(long quadCountExclusive) {
        if (quadCountExclusive < finalizedThrough || quadCountExclusive > quads) {
            throw new IllegalStateException("finalization watermark out of order: "
                    + quadCountExclusive + " over " + finalizedThrough + ".." + quads);
        }
        finalizedThrough = quadCountExclusive;
    }

    /** Marks one complete quad dirty (late mutation of its tail records). */
    public void markDirty(long quadIndex) {
        if (quadIndex < 0 || quadIndex >= quads) {
            throw new IndexOutOfBoundsException("no quad " + quadIndex + " of " + quads);
        }
        if (dirtyFrom < 0 || quadIndex < dirtyFrom) {
            dirtyFrom = quadIndex;
        }
    }

    /** The lowest dirty complete quad, or −1 when the tail is clean. */
    public long dirtyFrom() {
        return dirtyFrom;
    }

    /** Clears the dirty tail up to {@code quadCountExclusive} after finalization. */
    public void clearDirtyThrough(long quadCountExclusive) {
        if (dirtyFrom >= 0 && dirtyFrom < quadCountExclusive) {
            dirtyFrom = -1;
        }
    }

    /** True when the builder sits on a quad boundary with no partial vertex. */
    public boolean atQuadBoundary() {
        return !partialVertex && consumedInQuad == 0;
    }

    /**
     * Rejects a block-scope or seal boundary off a quad boundary: a partial quad is
     * malformed, never combined with adjacent blocks.
     */
    public void requireQuadBoundary(String boundary) {
        if (!atQuadBoundary()) {
            throw new IllegalStateException(
                    "partial quad at " + boundary + " is malformed");
        }
    }

    /** Resets all accounting for a fresh begin: quads group from vertex zero. */
    public void reset() {
        consumedInQuad = 0;
        partialVertex = false;
        quads = 0;
        finalizedThrough = 0;
        dirtyFrom = -1;
    }

    private void advance(long vertices) {
        long total = consumedInQuad + vertices;
        quads += total / 4;
        consumedInQuad = total % 4;
    }
}
