// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.buffers.internal.PlanningArtifacts;

import java.util.Objects;

/**
 * Opaque immutable pure-planning value (PHASE_5_DOC §2.2/§4.1): resolved sizing and
 * inventory, ordered resource descriptors, pass/FBO keys, clear groups, fixed texture-unit
 * rows and teardown order. Value equality covers all those artifacts; the plan contains no
 * GL handle and is stable for equal inputs. {@code create} does not consume a prior plan —
 * it reruns the same deterministic planning operation from the build request.
 */
public final class BufferPlan {

    private final PlanningArtifacts artifacts;

    public BufferPlan(PlanningArtifacts artifacts) {
        this.artifacts = Objects.requireNonNull(artifacts, "artifacts");
    }

    public PlanningArtifacts artifacts() {
        return artifacts;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BufferPlan other && artifacts.equals(other.artifacts);
    }

    @Override
    public int hashCode() {
        return artifacts.hashCode();
    }

    @Override
    public String toString() {
        return "BufferPlan" + artifacts;
    }
}
