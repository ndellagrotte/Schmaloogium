// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Engine-only reasons why resource evidence is unavailable (PHASE_5_DOC §2.2); serialization sends ONLY
 * {@code resources.available=false}.
 */
public enum ResourceProjectionUnavailableReason {
    INPUT_INVALID,
    DERIVATION_INCOMPLETE,
    AWAITING_MAIN_DEPTH,
    SHADERS_OFF
}
