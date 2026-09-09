// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import com.schmaloogium.engine.config.VertexAttribute;

/**
 * The fixed extended-attribute table exposed to Phase 10 (PHASE_4_DOC §4.9, App A.3):
 * {@code mc_Entity} → 10, {@code mc_midTexCoord} → 11, {@code at_tangent} → 12. The
 * numeric values are contract-fixed; Pintonium's conflicting 11/11/12/13/14 numbering is
 * the negative fixture and is never adopted (D-P4-6). Binding occurs only for attributes
 * declared by Phase 3's direct {@code vertices()} projection.
 */
public enum ExtendedAttribute {
    MC_ENTITY(10, 11),
    MC_MID_TEX_COORD(11, 12),
    AT_TANGENT(12, 13);

    private final int location;
    private final int requiredMaxVertexAttribs;

    ExtendedAttribute(int location, int requiredMaxVertexAttribs) {
        this.location = location;
        this.requiredMaxVertexAttribs = requiredMaxVertexAttribs;
    }

    /** The fixed bind location (10/11/12). */
    public int location() {
        return location;
    }

    /** The minimum {@code maxVertexAttribs} capability this attribute requires. */
    public int requiredMaxVertexAttribs() {
        return requiredMaxVertexAttribs;
    }

    /** Lossless adaptation from the Phase 3 opt-in enum. */
    public static ExtendedAttribute of(VertexAttribute attribute) {
        return switch (attribute) {
            case MC_ENTITY -> MC_ENTITY;
            case MC_MID_TEX_COORD -> MC_MID_TEX_COORD;
            case AT_TANGENT -> AT_TANGENT;
        };
    }
}
