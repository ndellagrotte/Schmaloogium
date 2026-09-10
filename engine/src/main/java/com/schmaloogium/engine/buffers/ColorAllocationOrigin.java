// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** How a color buffer came to hold its format (PHASE_5_DOC §2.2). */
public enum ColorAllocationOrigin {
    REQUESTED,
    RGBA_FALLBACK
}
