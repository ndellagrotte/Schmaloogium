// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.Objects;

/**
 * Replay evidence for one triggering drained error (PHASE_1_DOC §4.7.4, [D-P1-42]). There
 * is exactly one result for each {@link GLError} from the triggering non-empty drain; the
 * CALLER, not the backend, performs the isolation replay and supplies the verdict.
 * {@code attributed} is true only when the caller's replay isolates the named facade call
 * and the error recurs in that call's one-call drain window — a clean replay, a
 * batched/ambiguous window, a foreign-context error, or a non-reproducing error yields
 * {@code false}; neither {@code op} nor {@code subjectLabel} can manufacture {@code true}.
 */
public record ReplayAwareGLError(GLError error, boolean attributed) {

    public ReplayAwareGLError {
        Objects.requireNonNull(error, "error");
    }
}
