// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import java.util.List;

/**
 * The Task F vertex hook health verdict (PHASE_10_DOC §4.11, scoped to the rows Task F
 * installs — ruling 1 of the Task F plan): every audited anchor of the terrain path must
 * have applied exactly once, else the vertex epoch stays vanilla and no half-set of
 * stamping and pointer mixins is admitted.
 */
public record VertexHookHealth(boolean healthy, List<String> disabledRows) {

    public VertexHookHealth {
        disabledRows = List.copyOf(disabledRows);
    }

    public static VertexHookHealth disabled(String reason) {
        return new VertexHookHealth(false, List.of(reason));
    }
}
