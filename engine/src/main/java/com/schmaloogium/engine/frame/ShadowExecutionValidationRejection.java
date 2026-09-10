// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * Shadow validation rejection reasons (PHASE_7_DOC §5.1), in exact check order.
 */
public enum ShadowExecutionValidationRejection {
    WRONG_ISSUER,
    INACTIVE,
    WRONG_EXECUTION,
    STALE_SLOT_EPOCH,
    WRONG_THREAD
}
