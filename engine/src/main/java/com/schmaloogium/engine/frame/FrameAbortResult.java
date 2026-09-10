// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The frame-abort outcome (PHASE_7_DOC §5.1). {@code Failed} performs best-effort cleanup
 * and schedules shaders-off recovery; it never transfers partial ownership.
 */
public sealed interface FrameAbortResult {

    /** Cleanup completed for this reason. */
    record Aborted(FrameAbortReason reason) implements FrameAbortResult {
    }

    /** The frame was already terminal; this call did nothing. */
    record AlreadyTerminal() implements FrameAbortResult {
    }

    /** Best-effort cleanup ran; shaders-off recovery is scheduled. */
    record Failed(FailureId failure) implements FrameAbortResult {
    }
}
