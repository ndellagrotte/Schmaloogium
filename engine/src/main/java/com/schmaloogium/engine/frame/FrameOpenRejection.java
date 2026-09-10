// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * Mutation-free frame-open rejection reasons (PHASE_7_DOC §5.1).
 */
public enum FrameOpenRejection {
    WRONG_THREAD,
    FRAME_ALREADY_OPEN,
    STALE_PUBLICATION,
    NON_WORLD_PASS,
    MISSING_WORLD_OR_CAMERA,
    INVALID_INPUT,
    SHADERS_OFF
}
