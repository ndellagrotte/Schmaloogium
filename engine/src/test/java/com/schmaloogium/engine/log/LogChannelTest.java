// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.log;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8.1 row {@code LogChannelTest} (PHASE_1_DOC §4.9.2, D-P1-20; §4.9.1): every
 * {@code LogChannels} constant is unique and starts with {@code schmaloogium.}; the no-op
 * sink is active before installation.
 */
class LogChannelTest {

    @Test
    void everyConstantIsUniqueAndPrefixed() throws IllegalAccessException {
        Set<String> seen = new HashSet<>();
        int constants = 0;
        for (Field field : LogChannels.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || field.getType() != String.class) {
                continue;
            }
            String channel = (String) field.get(null);
            assertNotNull(channel, field.getName() + " must not be null");
            assertTrue(channel.startsWith("schmaloogium."),
                    field.getName() + " must start with \"schmaloogium.\": " + channel);
            assertTrue(seen.add(channel),
                    "channel values must be unique: " + channel + " repeated");
            constants++;
        }
        assertTrue(constants >= 16, "the fixed list covers all fourteen phases: " + constants);
    }

    @Test
    void noopSinkIsActiveBeforeInstallation() {
        LogSink sink = Logs.sink();
        assertNotNull(sink, "a sink is always active, even before installation");

        // Emitting before any install must be a silent no-op: it can never be the thing
        // that breaks startup (§4.9.1), so it neither throws nor disturbs the sink.
        assertDoesNotThrow(() -> sink.emit(LogChannels.BOOT, LogLevel.INFO, "before install", null, null));
        assertDoesNotThrow(() -> Logs.channel(LogChannels.GL).info("before install: {0}", "arg"));
        assertSame(sink, Logs.sink(), "emitting must not install or replace a sink");
    }
}
