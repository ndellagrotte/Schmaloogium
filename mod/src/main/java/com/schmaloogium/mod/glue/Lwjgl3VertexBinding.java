// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.VertexBinding;
import com.schmaloogium.engine.vertex.VertexInputPlan;


/**
 * The opaque LIFO restore obligation (PHASE_1_DOC §4.7.6, [D-P1-50]): a binding is valid
 * until consumed by exactly one successful {@code restore}; repeated restore attempts and
 * stale/foreign bindings are rejected. It carries the captured predecessor internally —
 * the backend restorer reads its own captured state, never GLSM's private map ([D-P1-57]).
 */
final class Lwjgl3VertexBinding implements VertexBinding {

    final Lwjgl3GLDevice owner;
    private final Lwjgl3VertexInputService.Predecessor predecessor;
    private final VertexInputPlan plan;
    private boolean consumed;

    Lwjgl3VertexBinding(Lwjgl3GLDevice owner, Lwjgl3VertexInputService.Predecessor predecessor,
                        VertexInputPlan plan) {
        this.owner = owner;
        this.predecessor = predecessor;
        this.plan = plan;
    }

    /** Consumed-once: the second call is a caller error, not a silent no-op. */
    void restoreOnce() {
        if (consumed) {
            throw new IllegalStateException(
                    "vertexInput.restore: binding already consumed by a successful restore");
        }
        consumed = true;
        predecessor.restoreAll();
    }

    VertexInputPlan plan() {
        return plan;
    }
}
