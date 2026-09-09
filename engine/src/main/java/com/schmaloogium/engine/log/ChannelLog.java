// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.log;

import java.util.function.Supplier;

/** A {@link Log} bound to one channel name, formatting {@code {}} placeholders log4j-style. */
final class ChannelLog implements Log {

    private final String channel;
    private final Supplier<LogSink> sink;

    ChannelLog(String channel, Supplier<LogSink> sink) {
        this.channel = channel;
        this.sink = sink;
    }

    @Override
    public void debug(String message, Object... args) {
        sink.get().emit(channel, LogLevel.DEBUG, message, args, null);
    }

    @Override
    public void info(String message, Object... args) {
        sink.get().emit(channel, LogLevel.INFO, message, args, null);
    }

    @Override
    public void warn(String message, Object... args) {
        sink.get().emit(channel, LogLevel.WARN, message, args, null);
    }

    @Override
    public void error(String message, Object... args) {
        sink.get().emit(channel, LogLevel.ERROR, message, args, null);
    }

    @Override
    public void error(Throwable t, String message, Object... args) {
        sink.get().emit(channel, LogLevel.ERROR, message, args, t);
    }

    @Override
    public boolean isDebugEnabled() {
        // The SPI carries no level query; enabling debug is the sink's business and the
        // common case is "on" for a mod under development. Channels that want cheaper
        // debug paths check this and pay no formatting cost when true regardless.
        return true;
    }
}
