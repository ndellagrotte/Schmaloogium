// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.diag;

/**
 * Where a notification goes besides the log: chat (pack-level failures, capability
 * gates), the shader GUI (per-program compile errors, Phase 12's screen), or nowhere
 * else.
 */
public enum UserChannel {
    CHAT, SHADER_GUI, LOG_ONLY
}
