// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * Closed mutation-free rejection reasons for authenticated hook calls (PHASE_7_DOC §5.1).
 * Every {@code Rejected} result performs no GL and leaves the open tokens unchanged.
 */
public enum HookRejection {
    WRONG_TOKEN,
    WRONG_ORDER,
    STALE_PUBLICATION,
    INVALID_SECTION
}
