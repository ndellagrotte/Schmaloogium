// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

/**
 * The uploaded product's sidecar (PHASE_10_DOC §4.5): actual stride, vertex count from the
 * uploaded byte range and that stride, and the epoch serial it was built under. Set only
 * after a successful upload; cleared on delete.
 */
public interface VertexBufferSidecar {

    boolean schmaloogium$extended();

    long schmaloogium$serial();

    int schmaloogium$count();

    void schmaloogium$setUpload(boolean extended, long serial, int count);

    void schmaloogium$clearUpload();
}
