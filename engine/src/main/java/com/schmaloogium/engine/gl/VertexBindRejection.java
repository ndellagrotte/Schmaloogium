// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed pre-mutation rejection vocabulary (PHASE_1_DOC §4.7.6). Wrong thread returns
 * WRONG_THREAD; forged/wrong-device/null source INVALID_SOURCE; expired source
 * STALE_SOURCE; invalid layout/plan their named reasons; arithmetic/range failures
 * OUT_OF_RANGE; unavailable capability or incompatible mode/input UNSUPPORTED_INPUT. A
 * rejection is mutation-free and appends no native call.
 */
public enum VertexBindRejection {
    INVALID_SOURCE,
    STALE_SOURCE,
    INVALID_LAYOUT,
    INVALID_PLAN,
    WRONG_THREAD,
    OUT_OF_RANGE,
    UNSUPPORTED_INPUT
}
