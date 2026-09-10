// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The shadow validation outcome (PHASE_7_DOC §5.1). The exact check order is
 * WRONG_ISSUER, INACTIVE, WRONG_EXECUTION, STALE_SLOT_EPOCH, WRONG_THREAD.
 */
public sealed interface ShadowExecutionValidationResult {

    record Valid() implements ShadowExecutionValidationResult {
    }

    record Rejected(ShadowExecutionValidationRejection reason)
            implements ShadowExecutionValidationResult {
    }
}
