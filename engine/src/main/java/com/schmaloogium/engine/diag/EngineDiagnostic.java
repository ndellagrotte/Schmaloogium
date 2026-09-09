// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.diag;

import java.util.List;

/**
 * Loader-neutral diagnostic crossing the engine/mod seam (PHASE_1_DOC §4.9.4, D-P1-22).
 * Message keys, not message text, cross the seam: the engine holds no user-facing
 * English, and Phase 12 needs lang keys for the GUI anyway. Every diagnostic reaches the
 * log regardless of channel — the log is the transcript, the other two are notifications.
 *
 * @param severity    INFO, WARN, ERROR, FATAL
 * @param channel     CHAT, SHADER_GUI, LOG_ONLY
 * @param messageKey  a lang key, e.g. {@code "schmaloogium.error.program.link"}
 * @param args        message-format arguments for the key
 * @param detail      driver log / stack detail; GUI and log only, never chat
 * @param logChannel  one of {@link com.schmaloogium.engine.log.LogChannels}
 */
public record EngineDiagnostic(
        DiagnosticSeverity severity,
        UserChannel channel,
        String messageKey,
        List<Object> args,
        String detail,
        String logChannel) {

    public EngineDiagnostic {
        args = List.copyOf(args);
    }
}
