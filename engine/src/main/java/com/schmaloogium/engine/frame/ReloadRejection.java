// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * Reload request rejection reasons (PHASE_7_DOC §5.1); all mutation-free.
 */
public enum ReloadRejection {
    WRONG_THREAD,
    INVALID_REQUEST,
    SHUTTING_DOWN
}
