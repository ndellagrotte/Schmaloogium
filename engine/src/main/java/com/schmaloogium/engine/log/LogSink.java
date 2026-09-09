// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.log;

/**
 * The transport a {@link Log} channel writes through (PHASE_1_DOC §4.9.1).
 * Implementations must be thread-safe ({@code Logs} is documented any-thread).
 */
public interface LogSink {
    void emit(String channel, LogLevel level, String message, Object[] args, Throwable t);
}
