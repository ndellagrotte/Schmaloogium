// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * Shadow close rejection reasons (PHASE_7_DOC §5.1): WRONG_ISSUER, INACTIVE, WRONG_EXECUTION,
 * checked in that order — the view is invalidated before returning in every branch.
 */
public enum ShadowExecutionCloseRejection {
    WRONG_ISSUER,
    INACTIVE,
    WRONG_EXECUTION
}
