// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core;

import com.schmaloogium.engine.log.LogLevel;
import com.schmaloogium.engine.log.LogSink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

/**
 * Log4j-backed {@link LogSink}, installed by {@code mod.core} at {@code preInit}
 * (PHASE_1_DOC §4.9.1). One log4j logger per channel so a user's {@code log4j2.xml}
 * filter is meaningful. Thread-safe (log4j2 loggers are).
 */
public final class Log4jLogSink implements LogSink {

    public Log4jLogSink() {
    }

    private static Logger logger(String channel) {
        return LogManager.getLogger(channel);
    }

    @Override
    public void emit(String channel, LogLevel level, String message, Object[] args, Throwable t) {
        Logger logger = logger(channel);
        ParameterizedMessage formatted = new ParameterizedMessage(message, args, t);
        switch (level) {
            case DEBUG -> logger.debug(formatted);
            case INFO -> logger.info(formatted);
            case WARN -> logger.warn(formatted);
            case ERROR -> logger.error(formatted);
        }
    }
}
