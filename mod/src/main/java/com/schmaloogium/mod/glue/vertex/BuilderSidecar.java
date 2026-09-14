// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import java.nio.ByteBuffer;

/**
 * The lazy builder sidecar (PHASE_10_DOC §4.2) as a duck interface the
 * {@code BufferBuilder} mixin implements: extended or not for this {@code begin}, the
 * epoch serial it writes under, how many vertices are finalized, and the raw buffer and
 * counts the ingress needs. Null-fast: an inactive builder answers {@code false} and does
 * no extended work.
 */
public interface BuilderSidecar {

    boolean schmaloogium$extended();

    void schmaloogium$setExtended(boolean extended, long serial);

    long schmaloogium$serial();

    int schmaloogium$finalizedThrough();

    void schmaloogium$setFinalizedThrough(int vertices);

    ByteBuffer schmaloogium$byteBuffer();

    int schmaloogium$vertexCount();

    int schmaloogium$drawMode();

    /** Semantic dispatch: point the physical cursor at the given element before a store. */
    void schmaloogium$selectElement(int elementIndex);
}
