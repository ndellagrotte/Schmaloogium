// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Why the shadow estate degraded to the neutral objects (§4.10).
 */
public enum ShadowNeutralReason {
    BIND_BACKEND_FAILURE,
    CLEAR_BACKEND_FAILURE,
    DEPTH_COPY_BACKEND_FAILURE,
    MIPMAP_FILTER_RESTORE_FAILURE,
    PASS_BACKEND_FAILURE,
    EXPLICIT_FEATURE_DISABLE
}
