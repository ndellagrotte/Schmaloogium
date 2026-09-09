// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import java.util.List;
import java.util.Set;

/**
 * The complete input plan consumed by {@code engine.gl.VertexInputService.bind}
 * (PHASE_10_DOC §2.2; PHASE_1_DOC §4.7.6, D-P1-68). The pointers and conventionalInputs
 * are immutable defensive copies, nonnull and without null members. Complete plan
 * identity includes layout fingerprint, ordered pointer descriptors, conventionalInputs
 * in enum order and expectedGeometryInput; equality is necessary, never source authority.
 */
public record VertexInputPlan(String layoutFingerprint, List<AttributePointer> pointers,
                              Set<ConventionalInput> conventionalInputs,
                              VertexGeometryInput expectedGeometryInput) {

    public VertexInputPlan {
        if (layoutFingerprint == null) {
            throw new IllegalArgumentException("layoutFingerprint must not be null");
        }
        if (expectedGeometryInput == null) {
            throw new IllegalArgumentException("expectedGeometryInput must not be null");
        }
        pointers = List.copyOf(pointers);         // immutable, null-hostile, defensive
        conventionalInputs = Set.copyOf(conventionalInputs);
    }
}
