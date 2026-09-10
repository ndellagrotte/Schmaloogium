// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

/**
 * The ordinary source semantics and their CLASSIC_56 destination elements
 * (PHASE_10_DOC §4.1, §4.2): scalar setter dispatch selects the authenticated source
 * semantic first, then this semantic's physical destination offset — never the previous
 * physical cursor (D-P10-27). The offsets are the classic byte floor; only
 * {@link Classic56Layout}'s declaration and byte-oracle fixtures contain them
 * elsewhere.
 */
public enum ClassicSemantic {

    POSITION(0, 12),
    COLOR(12, 4),
    UV0(16, 8),
    UV1(24, 4),
    NORMAL(28, 3);

    private final int byteOffset;
    private final int byteSize;

    ClassicSemantic(int byteOffset, int byteSize) {
        this.byteOffset = byteOffset;
        this.byteSize = byteSize;
    }

    /** The classic destination byte offset for this semantic. */
    public int byteOffset() {
        return byteOffset;
    }

    /** The classic destination byte size for this semantic. */
    public int byteSize() {
        return byteSize;
    }
}
