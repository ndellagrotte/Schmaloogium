// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Closed reasons a resize-consumer registration can be rejected (PHASE_5_DOC §2.2).
 */
public enum BufferResizeRegistrationRejection {
    BLANK_CONSUMER_ID,
    DUPLICATE_LIVE_CONSUMER_ID,
    FUTURE_ACKNOWLEDGED_GENERATION,
    UNKNOWN_ACKNOWLEDGED_GENERATION,
    ACKNOWLEDGED_SIZING_MISMATCH
}
