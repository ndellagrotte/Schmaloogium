// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * Why a shader frame is aborted (PHASE_7_DOC §5.1). An {@code Aborted} result has already
 * drained scopes, aborted the Phase-5 frame, released Phase 4 and invalidated every token.
 */
public enum FrameAbortReason {
    PROTOCOL_REJECTION,
    BACKEND_FAILURE,
    RESIZE_EPOCH,
    WORLD_CHANGE,
    HOOK_UNHEALTHY
}
