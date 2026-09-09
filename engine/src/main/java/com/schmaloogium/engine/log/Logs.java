// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sink installation and channel access (PHASE_1_DOC §4.9.1).
 *
 * <p>{@code mod.core} installs a log4j-backed sink during {@code preInit}; tests install
 * a capturing sink and assert over it. Before installation a no-op sink is active — so a
 * static initializer that logs cannot explode during class loading.
 */
public final class Logs {

    private static volatile LogSink sink = NoopSink.INSTANCE;

    private static final Map<String, Log> CHANNELS = new ConcurrentHashMap<>();

    private Logs() {
    }

    /** Installs the sink. Called once by {@code mod.core} at boot; tests may replace it. */
    public static void install(LogSink newSink) {
        sink = (newSink != null) ? newSink : NoopSink.INSTANCE;
    }

    /** The currently installed sink (never null; a no-op sink before installation). */
    public static LogSink sink() {
        return sink;
    }

    /** Returns the channel logger for one of the fixed {@link LogChannels} names. */
    public static Log channel(String name) {
        return CHANNELS.computeIfAbsent(name, n -> new ChannelLog(n, () -> sink));
    }

    private static final class NoopSink implements LogSink {
        static final NoopSink INSTANCE = new NoopSink();

        @Override
        public void emit(String channel, LogLevel level, String message, Object[] args, Throwable t) {
            // Deliberately nothing: logging can never be the thing that breaks startup.
        }
    }
}
