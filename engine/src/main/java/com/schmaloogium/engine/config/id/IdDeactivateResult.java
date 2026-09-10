// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/** Closed deactivation outcome (PHASE_9_DOC §4.1). */
public enum IdDeactivateResult {
    /** The current publication was retired; borrowers keep it alive until drained. */
    DEACTIVATED,
    /** No current publication existed. */
    ALREADY_INACTIVE
}
