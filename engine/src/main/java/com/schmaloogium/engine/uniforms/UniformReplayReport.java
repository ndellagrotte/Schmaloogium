// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import java.util.Objects;
import java.util.List;

/** One replay-evidence report delivered synchronously to Phase 7's observer
 *  (PHASE_6_DOC §2.2/§4.11, D-P6-27): the effective cache key under which the attempt was
 *  observed — not proof of causation — and exactly one {@code ReplayAwareGLError} per
 *  original triggering-drain entry, in drain order. Detached immutable value: the list is
 *  defensively copied and non-empty; the original Phase 1 {@code GLError} objects are
 *  carried unchanged. */
public record UniformReplayReport(
        com.schmaloogium.engine.registry.ProgramUniformCacheKey program,
        java.util.List<com.schmaloogium.engine.gl.ReplayAwareGLError> errors) {

    public UniformReplayReport {
        Objects.requireNonNull(program, "program");
        Objects.requireNonNull(errors, "errors");
        if (errors.isEmpty()) {
            throw new IllegalArgumentException("errors must be non-empty");
        }
        errors = List.copyOf(errors);
    }
}
