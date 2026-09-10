// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/** Immutable by-value int 2-vector (PHASE_6_DOC §4.2). Full {@code int} components. */
public record Int2(int x, int y) {

    /** The zero vector — the pending-value neutral for {@code atlasSize}. */
    public static Int2 zero() {
        return new Int2(0, 0);
    }
}
