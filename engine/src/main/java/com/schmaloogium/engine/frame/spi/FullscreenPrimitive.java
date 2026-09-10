// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

/**
 * Descriptive planning metadata for fullscreen draws (PHASE_7_DOC §5.1) — never an
 * override: the active linked program's primitive compatibility decides the route.
 */
public enum FullscreenPrimitive {
    QUADS,
    TRIANGLE_STRIP
}
