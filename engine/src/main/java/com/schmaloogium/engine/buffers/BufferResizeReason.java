// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Why a buffer resize was published (PHASE_5_DOC §2.2). Declaration order is the deterministic priority
 * when several reasons change in one publication.
 */
public enum BufferResizeReason {
    DISPLAY_EXTENT,
    RENDER_QUALITY,
    MAIN_DEPTH_EXTENT,
    SHADOW_RESOLUTION,
    SHADOW_QUALITY,
    PACK_CONFIGURATION,
    REGISTRY_PLAN,
    COLOR_INVENTORY_OR_FORMAT
}
