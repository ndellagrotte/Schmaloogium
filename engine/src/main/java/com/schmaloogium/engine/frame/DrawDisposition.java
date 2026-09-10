// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * What the vanilla caller should do inside an opened scope (PHASE_7_DOC §5.1).
 * Exhaustive: draw the shader scope, let vanilla draw under fixed function, or cancel
 * only that named operation.
 */
public enum DrawDisposition {
    DRAW_SHADER,
    DRAW_FIXED_FUNCTION,
    OMIT_OPERATION
}
