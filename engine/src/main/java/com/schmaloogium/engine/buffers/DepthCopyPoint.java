// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Ordered depth-copy checkpoints within a frame (PHASE_5_DOC §2.2).
 */
public enum DepthCopyPoint {
    PRE_WEATHER,
    PRE_TRANSLUCENT
}
