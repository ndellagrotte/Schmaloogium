// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * Shadow open rejection reasons (PHASE_7_DOC §5.1).
 */
public enum ShadowExecutionOpenRejection {
    WRONG_THREAD,
    ALREADY_ACTIVE
}
