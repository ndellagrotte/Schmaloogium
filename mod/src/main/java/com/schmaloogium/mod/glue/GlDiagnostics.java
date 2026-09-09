// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.log.LogChannels;

import java.util.List;

/**
 * Shared construction of the glue's data-problem diagnostics: ERROR severity, LOG_ONLY
 * user channel, routed on {@code schmaloogium.gl} (PHASE_1_DOC §4.9.2 - shader/program
 * data problems never reach chat; P4 surfaces them through its own reporting).
 */
final class GlDiagnostics {

    private GlDiagnostics() {
    }

    static EngineDiagnostic diagnostic(String messageKey, String detail) {
        return new EngineDiagnostic(DiagnosticSeverity.ERROR, UserChannel.LOG_ONLY,
                messageKey, List.of(), detail, LogChannels.GL);
    }
}
