// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.catalog;

import java.util.Objects;

/**
 * One built-in catalog row (PHASE_6_DOC §4.4): the exact Appendix D name and type, the
 * acquisition bucket, the activation policy, the owning producer of the audit table, the
 * value milestone and the closed signal-event name when the row is signal-fed. The
 * catalog contains no sampler-name/unit table — sampler routing is Phase 5's sole policy
 * consumed through the shared resolver (R7-10). Immutable.
 *
 * @param name        exact pack-facing GLSL identifier
 * @param type        closed value type
 * @param cadence     acquisition bucket
 * @param activation  activation policy (matrices ALWAYS_UPLOAD; per-draw dynamics and
 *                    other signals IMMEDIATE_IF_ACTIVE; everything else SKIP_EQUAL)
 * @param producer    §4.12 audit-table producer description
 * @param milestone   value-producer milestone (interface always v0.1)
 * @param signalEvent the {@code UniformEventSink} method for SIGNAL rows, else ""
 */
public record BuiltInUniform(
        String name,
        BuiltInUniformType type,
        Cadence cadence,
        ActivationKind activation,
        String producer,
        Milestone milestone,
        String signalEvent) {

    public BuiltInUniform {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(cadence, "cadence");
        Objects.requireNonNull(activation, "activation");
        Objects.requireNonNull(producer, "producer");
        Objects.requireNonNull(milestone, "milestone");
        Objects.requireNonNull(signalEvent, "signalEvent");
    }

    public boolean isSignal() {
        return cadence == Cadence.SIGNAL;
    }

    public boolean isMatrix() {
        return type == BuiltInUniformType.MAT4;
    }
}
