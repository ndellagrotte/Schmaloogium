// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.log;

/**
 * Minimal logging facade, roughly twenty lines on purpose (PHASE_1_DOC §4.9.1, D-P1-19):
 * log4j on 1.12.2 is supplied by the Minecraft runtime, so an :engine dependency on it
 * would be a production dependency that exists only because Minecraft happens to
 * provide it — the soft coupling the seam removes.
 *
 * <p>Messages use log4j-style {@code {}} placeholders.
 */
public interface Log {
    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);

    void error(Throwable t, String message, Object... args);

    boolean isDebugEnabled();
}
