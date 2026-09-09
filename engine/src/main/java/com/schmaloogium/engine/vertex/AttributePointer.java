// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

/**
 * One attribute pointer descriptor inside a {@link VertexInputPlan}
 * (PHASE_10_DOC §2.2). Pure engine value.
 */
public record AttributePointer(String name, int location, int byteOffset,
                               int components, StorageType storage, boolean normalized) {

    public AttributePointer {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (storage == null) {
            throw new IllegalArgumentException("storage must not be null");
        }
    }
}
