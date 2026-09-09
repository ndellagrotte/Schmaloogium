// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.Diagnostics;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.LogLevel;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.log.LogSink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The §4.9.2/§4.9.4 routing contract exercised headless: every diagnostic reaches the
 * log on its named channel with the severity ceiling applied, SHADER_GUI accumulates
 * in the per-pack store, CHAT with no player downgrades to a log line, and with no
 * reporter installed the engine fallback logs and nothing crashes.
 */
class DiagnosticRoutingTest {

    /** Capturing sink: records (channel, level, message) triples in emission order. */
    private static final class RecordingSink implements LogSink {
        final List<String> events = new ArrayList<>();

        @Override
        public void emit(String channel, LogLevel level, String message, Object[] args,
                         Throwable t) {
            events.add(channel + "|" + level + "|" + message);
        }
    }

    private RecordingSink sink;

    @BeforeEach
    void installSink() {
        sink = new RecordingSink();
        Logs.install(sink);
        ShaderErrorStore.clear();
    }

    @AfterEach
    void uninstallSink() {
        Logs.install(null); // engine falls back to its no-op sink
        Diagnostics.install(null); // engine falls back to log-only
        ShaderErrorStore.clear();
    }

    private static boolean classPresent(String binaryName) {
        try {
            Class.forName(binaryName, false, DiagnosticRoutingTest.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static EngineDiagnostic diagnostic(UserChannel channel, DiagnosticSeverity severity,
                                               String channelName) {
        return new EngineDiagnostic(severity, channel, "test.message", List.of("arg"),
                "detail-line", channelName);
    }

    @Test
    void everyDiagnosticReachesTheLogRegardlessOfChannel() {
        Assumptions.assumeTrue(classPresent("net.minecraft.util.text.ITextComponent"),
                "chat leg needs the Minecraft classpath");
        ClientDiagnosticRouter router = new ClientDiagnosticRouter();
        router.report(diagnostic(UserChannel.LOG_ONLY, DiagnosticSeverity.WARN,
                LogChannels.COMPILE));
        router.report(diagnostic(UserChannel.SHADER_GUI, DiagnosticSeverity.ERROR,
                LogChannels.COMPILE));
        router.report(diagnostic(UserChannel.CHAT, DiagnosticSeverity.INFO,
                LogChannels.GUI));

        assertTrue(sink.events.contains(LogChannels.COMPILE + "|" + LogLevel.WARN
                + "|test.message"));
        assertTrue(sink.events.contains(LogChannels.COMPILE + "|" + LogLevel.ERROR
                + "|test.message"));
        assertTrue(sink.events.contains(LogChannels.GUI + "|" + LogLevel.INFO
                + "|test.message"));
    }

    @Test
    void nonEmptyDetailGetsItsOwnAttributedLine() {
        new ClientDiagnosticRouter().report(
                diagnostic(UserChannel.LOG_ONLY, DiagnosticSeverity.ERROR, LogChannels.GL));
        assertTrue(sink.events.contains(LogChannels.GL + "|" + LogLevel.ERROR
                + "|test.message.detail"));
    }

    @Test
    void fatalLogsAtTheErrorCeiling() {
        new ClientDiagnosticRouter().report(
                diagnostic(UserChannel.LOG_ONLY, DiagnosticSeverity.FATAL, LogChannels.BOOT));
        assertTrue(sink.events.contains(LogChannels.BOOT + "|" + LogLevel.ERROR
                + "|test.message"));
    }

    @Test
    void shaderGuiAccumulatesIntoPerPackStore() {
        EngineDiagnostic d = diagnostic(UserChannel.SHADER_GUI, DiagnosticSeverity.ERROR,
                LogChannels.COMPILE);
        new ClientDiagnosticRouter().report(d);
        new ClientDiagnosticRouter().report(d);

        assertEquals(2, ShaderErrorStore.snapshot().size());
    }

    @Test
    void chatWithoutPlayerDowngradesToLogLine() {
        Assumptions.assumeTrue(classPresent("net.minecraft.util.text.ITextComponent"),
                "needs the Minecraft classpath (skipped on the headless test classpath)");
        // Headless: Minecraft.getMinecraft() is null (or playerless), so the CHAT
        // fan-out must downgrade - never crash, never buffer indefinitely.
        new ClientDiagnosticRouter().report(
                diagnostic(UserChannel.CHAT, DiagnosticSeverity.ERROR, LogChannels.COMPAT));

        assertTrue(sink.events.contains(LogChannels.COMPAT + "|" + LogLevel.WARN
                + "|schmaloogium.error.noPlayer"));
    }

    @Test
    void logOnlyNeverTouchesTheShaderStore() {
        new ClientDiagnosticRouter().report(
                diagnostic(UserChannel.LOG_ONLY, DiagnosticSeverity.WARN, LogChannels.FRAME));
        assertTrue(ShaderErrorStore.snapshot().isEmpty());
    }

    @Test
    void diagnosticsFacadeRoutesToInstalledReporter() {
        // Through the engine facade, not the router directly: preInit installs the
        // router via the proxy, and SHADER_GUI accumulation is the router's job.
        Diagnostics.install(new ClientDiagnosticRouter());
        Diagnostics.report(diagnostic(UserChannel.SHADER_GUI, DiagnosticSeverity.WARN,
                LogChannels.REGISTRY));
        assertEquals(1, ShaderErrorStore.snapshot().size());
        assertEquals(2, sink.events.size()); // message + non-empty .detail line
    }

    @Test
    void noInstalledReporterStillLogsAndDoesNotCrash() {
        // Deferred-sink behaviour: the engine's fallback reporter logs and never throws.
        Diagnostics.install(null);
        Diagnostics.report(diagnostic(UserChannel.LOG_ONLY, DiagnosticSeverity.INFO,
                LogChannels.BOOT));
        assertEquals(1, sink.events.size());
        assertTrue(ShaderErrorStore.snapshot().isEmpty());
    }
}
