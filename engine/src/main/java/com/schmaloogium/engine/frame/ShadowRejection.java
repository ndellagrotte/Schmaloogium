// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * Shadow invocation rejection reasons (PHASE_7_DOC §5.1).
 */
public enum ShadowRejection {
    WRONG_FRAME,
    STALE_PUBLICATION,
    UNSUPPORTED
}
