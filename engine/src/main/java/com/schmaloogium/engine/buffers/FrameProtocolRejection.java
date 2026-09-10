// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Closed pre-mutation frame-protocol rejection reasons; every rejection performs no GL and
 * leaves the open tokens unchanged (PHASE_5_DOC §2.2).
 */
public enum FrameProtocolRejection {
    STALE_GENERATION,
    STALE_DEPTH_ATTACHMENT_EPOCH,
    FRAME_ALREADY_OPEN,
    NO_OPEN_FRAME,
    WRONG_FRAME_ID,
    DEPTH_COPY_OUT_OF_ORDER,
    INVALID_VIRTUAL_TRANSITION,
    DUPLICATE_VIRTUAL_TRANSITION,
    NON_NORMALIZED_FLIP_STATE,
    OPEN_PASS_SNAPSHOT,
    INVALID_PASS_SNAPSHOT,
    OPEN_DRAW_BUFFERS_NONE_LEASE,
    INVALID_DRAW_BUFFERS_NONE_LEASE
}
