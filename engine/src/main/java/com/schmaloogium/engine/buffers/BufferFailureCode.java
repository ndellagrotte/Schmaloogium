// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Closed set of buffer pipeline failure codes (PHASE_5_DOC §2.2). */
public enum BufferFailureCode {
    INVALID_INPUT,
    UNSUPPORTED_POST_V05_BUFFER_INDEX,
    CAPABILITY_LIMIT,
    MAIN_DEPTH_UNAVAILABLE,
    BORROWED_DEPTH_CONTRACT_UNAVAILABLE,
    TEXTURE_ALLOCATION,
    FRAMEBUFFER_INCOMPLETE,
    FORMAT_FALLBACK_FAILED,
    DEPTH_COPY_UNAVAILABLE,
    MAIN_DEPTH_RESIZE_REQUIRED,
    STALE_ESTATE,
    PROTOCOL_VIOLATION,
    UNEXPECTED_BACKEND
}
