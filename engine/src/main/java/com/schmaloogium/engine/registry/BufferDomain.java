// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * Logical buffer domain of a {@link BufferRef} (PHASE_4_DOC §2.2). Purely logical —
 * ping-pong sides, FBO attachment packing and texture objects are Phase 5's estate.
 */
public enum BufferDomain {
    COLORTEX,
    SHADOWCOLOR,
    SHADOWTEX,
    DEPTH,
    SCREEN,
    EXTERNAL
}
