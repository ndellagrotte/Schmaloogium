// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of executing a planned clear (PHASE_5_DOC §2.2).
 */
public enum ClearExecutionResult {
    SUCCESS,
    STALE_OR_PROTOCOL_REJECTED,
    BACKEND_FAILED
}
