// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.diag;

import com.schmaloogium.engine.log.Log;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.LogLevel;
import com.schmaloogium.engine.log.Logs;

/**
 * Where the engine holds its {@link DiagnosticReporter} (PHASE_1_DOC §4.9.4). Before
 * {@code mod.core} installs one, diagnostics fall back to the log alone — a
 * cannot-explode path mirroring the pre-install log sink.
 */
public final class Diagnostics {

    private static volatile DiagnosticReporter reporter = Diagnostics::logFallback;

    private static final Log LOG = Logs.channel(LogChannels.BOOT);

    private Diagnostics() {
    }

    /** Installs the routing reporter. Called once by {@code mod.core} at boot. */
    public static void install(DiagnosticReporter newReporter) {
        reporter = (newReporter != null) ? newReporter : Diagnostics::logFallback;
    }

    public static void report(EngineDiagnostic d) {
        reporter.report(d);
    }

    private static void logFallback(EngineDiagnostic d) {
        // LOG-only transcript; severity maps one-to-one with the FATAL ceiling at
        // ERROR — LogLevel has no FATAL (D-P1-57: severity ceiling, not a new level).
        LogLevel level = switch (d.severity()) {
            case INFO -> LogLevel.INFO;
            case WARN -> LogLevel.WARN;
            case ERROR, FATAL -> LogLevel.ERROR;
        };
        Logs.sink().emit(d.logChannel(), level,
                d.messageKey(), d.args().toArray(), null);
    }
}
