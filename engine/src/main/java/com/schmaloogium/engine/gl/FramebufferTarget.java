// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed engine vocabulary for framebuffer bind targets (PHASE_1_DOC §4.7.4): no GL
 * constants appear in any signature; the backend maps these to their native values.
 */
public enum FramebufferTarget {
    READ,
    DRAW,
    READ_AND_DRAW
}
