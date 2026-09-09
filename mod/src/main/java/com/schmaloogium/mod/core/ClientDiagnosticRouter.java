// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.log.LogLevel;
import com.schmaloogium.engine.log.Logs;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Fans engine diagnostics out to their user channels (PHASE_1_DOC §4.9.4): CHAT becomes
 * a translated chat message via the client player (dropped, with a log line, if no
 * player exists yet), SHADER_GUI accumulates into a per-pack error store that Phase 12's
 * screen renders, LOG_ONLY goes nowhere else. Every diagnostic reaches the log
 * regardless of channel.
 */
@SideOnly(Side.CLIENT)
public final class ClientDiagnosticRouter implements DiagnosticReporter {

    public ClientDiagnosticRouter() {
    }

    @Override
    public void report(EngineDiagnostic d) {
        // The log is the transcript; it happens first and unconditionally.
        // FATAL has no LogLevel twin - it logs as ERROR (the log ceiling).
        LogLevel level = switch (d.severity()) {
            case INFO -> LogLevel.INFO;
            case WARN -> LogLevel.WARN;
            case ERROR, FATAL -> LogLevel.ERROR;
        };
        Logs.sink().emit(d.logChannel(), level, d.messageKey(), d.args().toArray(), null);
        if (d.detail() != null && !d.detail().isEmpty()) {
            Logs.sink().emit(d.logChannel(), level, d.messageKey() + ".detail",
                    new Object[]{d.detail()}, null);
        }

        switch (d.channel()) {
            case CHAT -> ChatDelivery.deliver(d);
            case SHADER_GUI -> ShaderErrorStore.add(d);
            case LOG_ONLY -> {
                // Already logged.
            }
        }
    }

}
