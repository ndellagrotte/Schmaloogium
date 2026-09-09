// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import java.util.List;

/**
 * Immutable vertex layout (PHASE_10_DOC §2.2): id, byte stride, ordered fields and the
 * content fingerprint. Fingerprints identify immutable layout content, never act as
 * credentials. Pure engine value — declared at Phase 1 as the compile floor of the
 * §4.7.6 vertex-input grant; Phase 10 adopts these exact shapes.
 */
public record VertexLayout(String id, int strideBytes, List<VertexField> fields,
                           String fingerprint) {

    public VertexLayout {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (fingerprint == null) {
            throw new IllegalArgumentException("fingerprint must not be null");
        }
        fields = List.copyOf(fields); // immutable, null-hostile, defensive
    }
}
