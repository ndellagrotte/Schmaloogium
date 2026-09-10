// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The frame-step outcome (PHASE_7_DOC §5.1). {@code Rejected} is mutation-free;
 * {@code Aborted} has already drained scopes, aborted the open Phase-5 frame, released
 * Phase 4 and invalidated every token; {@code Failed} additionally schedules shaders-off
 * recovery.
 */
public sealed interface FrameStepResult {

    /** The step advanced the machine to the reported state. */
    record Advanced(FrameState state) implements FrameStepResult {
    }

    /** Mutation-free refusal; the frame continues unchanged. */
    record Rejected(HookRejection reason) implements FrameStepResult {
    }

    /** The frame was already drained by this call. */
    record Aborted(FrameAbortReason reason) implements FrameStepResult {
    }

    /** Internal failure; cleanup ran and shaders-off recovery is scheduled. */
    record Failed(FailureId failure) implements FrameStepResult {
    }
}
