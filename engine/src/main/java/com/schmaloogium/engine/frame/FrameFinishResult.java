// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The frame-finish outcome (PHASE_7_DOC §5.1). {@code AlreadyTerminal} is the mandatory
 * no-op for the losing TAIL/finally call of the composite guarantee.
 */
public sealed interface FrameFinishResult {

    /** The frame finalized exactly once with this summary. */
    record Finalized(FinalizedFrame frame) implements FrameFinishResult {
    }

    /** The frame was already finalized or aborted; this call did nothing. */
    record AlreadyTerminal() implements FrameFinishResult {
    }

    /** The frame was drained by this losing call instead. */
    record Aborted(FrameAbortReason reason) implements FrameFinishResult {
    }

    /** Best-effort cleanup ran and shaders-off recovery is scheduled. */
    record Failed(FailureId failure) implements FrameFinishResult {
    }
}
