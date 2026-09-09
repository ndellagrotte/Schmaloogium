// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

/**
 * One named field of a {@link VertexLayout} (PHASE_10_DOC §2.2). Pure engine value — no
 * Minecraft, Forge, or GL types. Declared at Phase 1 as the compile floor of the §4.7.6
 * vertex-input grant; Phase 10 adopts these exact shapes.
 */
public record VertexField(String name, int byteOffset, int components,
                          StorageType storage, Delivery delivery) {

    public VertexField {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (storage == null) {
            throw new IllegalArgumentException("storage must not be null");
        }
        if (delivery == null) {
            throw new IllegalArgumentException("delivery must not be null");
        }
    }
}
