// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Pre-GL protocol rejections for shadow pass and operation calls (§4.10).
 */
public enum ShadowProtocolRejection {
    STALE_GENERATION, STALE_DEPTH_ATTACHMENT_EPOCH, PASS_ALREADY_OPEN, NO_OPEN_PASS,
    WRONG_FRAME_ID, FOREIGN_SNAPSHOT, CLOSED_SNAPSHOT
}
