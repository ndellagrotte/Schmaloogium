// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed bind-mode vocabulary (PHASE_1_DOC §4.7.6). LIVE_DRAW accepts client or VBO
 * sources; LIST_CAPTURE accepts only an authenticated prepared-epoch client range and
 * immutable capture plan; LIST_REPLAY_GUARD accepts a matching replay-safe list source
 * and performs only its required external guard/current-value state. Other source/mode
 * combinations reject {@link VertexBindRejection#UNSUPPORTED_INPUT}.
 */
public enum VertexBindMode {
    LIVE_DRAW,
    LIST_CAPTURE,
    LIST_REPLAY_GUARD
}
