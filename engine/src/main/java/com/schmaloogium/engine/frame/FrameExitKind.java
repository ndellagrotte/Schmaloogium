// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * Why a frame reached the composite guarantee's finally (PHASE_7_DOC §5.1). NORMAL is the
 * H-FRAME-06 TAIL call; EARLY_RETURN and THROWN arrive from H-FRAME-07's outer finally.
 * Both callers race only through the same opaque token.
 */
public enum FrameExitKind {
    NORMAL,
    EARLY_RETURN,
    THROWN
}
