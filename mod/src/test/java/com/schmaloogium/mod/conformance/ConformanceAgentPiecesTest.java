// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/** CapturePlanReaderTest + ManifestEmitterCanonicalTest + ControlledClockStateTest + RasterHashTest. */
class ConformanceAgentPiecesTest {

    static final String PLAN = CapturePlanReader.SCHEMA_LINE + "\n"
        + "captures.0.captureSampleCount = 2\n"
        + "captures.0.captureStartSample = 0\n"
        + "captures.0.heldMain = \"minecraft:torch\"\n"
        + "captures.0.heldOff = \"\"\n"
        + "captures.0.id = \"main\"\n"
        + "captures.0.kind = SHOT\n"
        + "captures.0.note = \"n\"\n"
        + "captures.0.samples.0.look.pitch = 15.0\n"
        + "captures.0.samples.0.look.yaw = 45.0\n"
        + "captures.0.samples.0.pos.x = 0.5\n"
        + "captures.0.samples.0.pos.y = 80.0\n"
        + "captures.0.samples.0.pos.z = -0.5\n"
        + "captures.0.samples.1.look.pitch = 15.0\n"
        + "captures.0.samples.1.look.yaw = 45.0\n"
        + "captures.0.samples.1.pos.x = 0.5\n"
        + "captures.0.samples.1.pos.y = 80.0\n"
        + "captures.0.samples.1.pos.z = -0.5\n"
        + "captures.0.samples.count = 2\n"
        + "captures.0.warmupFrames = 3\n"
        + "captures.count = 1\n"
        + "clock.ticksPerFrame = 1\n"
        + "world.gamerules.0.name = \"doFireTick\"\n"
        + "world.gamerules.0.value = \"false\"\n"
        + "world.gamerules.count = 1\n"
        + "world.prepTicks = 2\n";

    @Test
    void dumbReaderSplitsTypesAndAbortsOnMissingKeys() {
        CapturePlanReader plan = CapturePlanReader.parse(PLAN);
        assertEquals(1, plan.captures().size());
        CapturePlanReader.Capture c = plan.captures().get(0);
        assertEquals("SHOT", c.kind());
        assertEquals("main", c.id());
        assertEquals("minecraft:torch", c.heldMain());
        assertEquals(2, c.samples().size());
        assertEquals(-0.5, c.samples().get(1).z());
        assertEquals("false", plan.namedMap("world.gamerules").get("doFireTick"));
        assertThrows(IllegalArgumentException.class, () -> plan.integer("clock.frameTimeNanos"));
        assertThrows(IllegalArgumentException.class, () -> CapturePlanReader.parse(PLAN.replace("plan/4", "plan/3")));
        assertThrows(IllegalArgumentException.class, () -> CapturePlanReader.parse(
            PLAN.replace("captures.count = 1\n", "captures.count = 1\ncaptures.0.id = \"x\"\n")));
        assertEquals(64, plan.planHash().length());
    }

    @Test
    void emitterRendersSortedCanonicalLinesAndRejectsDuplicates() {
        ManifestEmitter m = new ManifestEmitter();
        m.token("run.id", "RUN-T0").text("run.failureReason", "a \"quoted\"\nline")
            .bool("run.timedOut", false).integer("run.hangCeilingMillis", 5).decimal("clock.partialTicks", 0.0);
        m.family("images");
        String p = m.row("frames");
        m.integer(p + "worldTick", 7);
        String text = m.render();
        assertTrue(text.startsWith(ManifestEmitter.SCHEMA_LINE + "\n"));
        assertTrue(text.contains("run.failureReason = \"a \\\"quoted\\\"\\nline\"\n"));
        assertTrue(text.contains("frames.0.worldTick = 7\nframes.count = 1\nimages.count = 0\n"));
        assertTrue(text.contains("clock.partialTicks = 0.0\n"));
        assertThrows(IllegalArgumentException.class, () -> m.token("run.id", "again"));
        assertThrows(IllegalArgumentException.class, () -> m.token("x", ""));
        assertEquals("0.5", CanonicalScalars.formatDouble(0.5));
        assertEquals("2.0", CanonicalScalars.formatDouble(2));
        assertEquals(new java.math.BigDecimal(1.0E-5).toPlainString(), CanonicalScalars.formatDouble(1.0E-5));
        assertEquals("hi\n", CanonicalScalars.decodeJson("\"hi\\n\""));
    }

    @Test
    void controlledClockStepsServerInLockstepAndReleasesOnTimeout() throws Exception {
        ControlledClock.arm(2, 0.25f, 0.05f, 2_000L);
        ControlledClock.gate();
        CountDownLatch started = new CountDownLatch(1);
        Thread server = new Thread(() -> {
            started.countDown();
            for (int i = 0; i < 4; i++) {
                ControlledClock.awaitServerPermit();
                ControlledClock.serverTickDone();
            }
        }, "fake-server");
        server.start();
        assertTrue(started.await(1, TimeUnit.SECONDS));
        assertTrue(ControlledClock.stepFrame());
        assertEquals(2, ControlledClock.serverTicks());
        assertEquals(2, ControlledClock.clientTicks());
        assertEquals(1, ControlledClock.renderedFrames());
        assertTrue(ControlledClock.stepFrame());
        assertEquals(4, ControlledClock.serverTicks());
        assertEquals(2, ControlledClock.renderedFrames());
        server.join(1_000);
        // no server thread now: the hang ceiling trips, the clock disarms and reports the failure
        ControlledClock.arm(1, 0.0f, 0.05f, 200L);
        ControlledClock.gate();
        assertFalse(ControlledClock.stepFrame());
        assertNotNull(ControlledClock.failure());
        assertFalse(ControlledClock.isArmed());
        assertFalse(ControlledClock.isGated());
        ControlledClock.release();
    }

    @Test
    void rasterHashMatchesTheHarnessDefinition() {
        // the same bytes the harness hashes: row-major, big-endian ARGB
        int[] argb = {0x01020304, 0x05060708};
        String hash = FrameGrabber.pixelSha256(2, 1, argb);
        java.security.MessageDigest md;
        try {
            md = java.security.MessageDigest.getInstance("SHA-256");
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        md.update(new byte[] {1, 2, 3, 4, 5, 6, 7, 8});
        assertEquals(TreeHash.hex(md.digest()), hash);
    }
}
