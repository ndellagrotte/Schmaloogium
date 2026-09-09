// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.harness;

import com.schmaloogium.conformance.FixtureInternalPackSource;
import com.schmaloogium.conformance.SyntheticProfiles;
import com.schmaloogium.conformance.glrec.ScriptedGLDevice;
import com.schmaloogium.conformance.tier.TierOutcome;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.PackInspectionResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The harness's executable T0 leg end to end: the parse leg through the real front
 * end on synthetic fixtures, the GL leg through the scripted recorder with frame-replay
 * assertions, and the compile leg's honest deferral ([D-P2-19]).
 */
class T0HarnessRunTest {

    private static final int FRAMES = 5;

    private static PackInspectionResult.Inspected inspectFixture(
            FixtureInternalPackSource source, GLCapabilityProfile profile) {
        FrontEndSession session = new FrontEndSession();
        PackInspectionResult result = session.inspectInternal(source, profile);
        assertTrue(result instanceof PackInspectionResult.Inspected,
            "front end rejected the fixture: " + session.diagnostics());
        return (PackInspectionResult.Inspected) result;
    }

    @Test
    void internalDefaultPackParsesAndRunsAStableSilentLoop(@TempDir Path tmp) {
        FixtureInternalPackSource source = new FixtureInternalPackSource(
            Path.of("src/test/resources/packs/internal-default"), "shaders");
        PackInspectionResult.Inspected inspected = inspectFixture(source,
            SyntheticProfiles.syntheticGl33());
        T0HarnessRun.Report report = T0HarnessRun.run(inspected,
            SyntheticProfiles.syntheticGl33(),
            new RegistryCompileStage(SyntheticProfiles.syntheticGl33()), FRAMES);

        // Parse leg: schema 23, configuration produced, no ERROR/FATAL diagnostics.
        assertTrue(report.parseLeg().parsed(), report.parseLeg().fatalDiagnostics().toString());
        assertTrue(report.parseLeg().packConfigurationProduced());

        // GL leg: silent (no drained GL errors) and replay-stable (frame windows equal).
        assertTrue(report.glLeg().clean(), report.glLeg().errors().toString());
        assertTrue(report.glLeg().stableLoop(), report.glLeg().replayMismatches().toString());
        assertEquals(FRAMES, report.glLeg().callLogRender().split("frame", -1).length - 1);

        // Compile leg: owner-backed through P4's registry compiler — real slot
        // resolution over the scripted device, no live GL context.
        assertTrue(report.compileLeg().runnable());
        assertTrue(report.compileLeg().failureReason().isEmpty(),
            report.compileLeg().failureReason());
        for (CompileStage.SlotRow row : report.compileLeg().rows()) {
            assertTrue(List.of("SOURCED", "CHAIN", "ABSENT").contains(row.status()),
                row.slot() + " -> " + row.status());
        }
        assertEquals(TierOutcome.PASS, report.t0Outcome(), report.t0Note());
    }

    @Test
    void minimumProfileRunsTheSameSilentStableLoop() {
        FixtureInternalPackSource source = new FixtureInternalPackSource(
            Path.of("src/test/resources/packs/internal-default"), "shaders");
        PackInspectionResult.Inspected inspected = inspectFixture(source,
            SyntheticProfiles.minimumGl21());
        T0HarnessRun.Report report = T0HarnessRun.run(inspected,
            SyntheticProfiles.minimumGl21(),
            new RegistryCompileStage(SyntheticProfiles.minimumGl21()), FRAMES);
        assertTrue(report.compileLeg().runnable());
        assertTrue(report.compileLeg().failureReason().isEmpty(),
            report.compileLeg().failureReason());
        assertEquals(TierOutcome.PASS, report.t0Outcome(), report.t0Note());
        assertTrue(report.glLeg().clean());
        assertTrue(report.glLeg().stableLoop());
    }

    @Test
    void microPackCorpusParsesCleanly() {
        Path packs = Path.of("src/test/resources/packs");
        List<String> names = List.of("mp-minimal", "mp-buffers", "mp-shadow", "mp-includes",
            "mp-options", "mp-properties", "mp-dimension", "mp-broken");
        for (String name : names) {
            FrontEndSession session = new FrontEndSession();
            PackInspectionResult result = session.inspect(packs,
                session.resolveCandidate(packs, name), SyntheticProfiles.syntheticGl33());
            assertTrue(result instanceof PackInspectionResult.Inspected,
                name + " failed to parse: " + session.diagnostics());
        }
    }

    @Test
    void scriptedRecorderDrainsInjectedErrorsExactlyOnce() {
        ScriptedGLDevice device = new ScriptedGLDevice(SyntheticProfiles.syntheticGl33());
        device.armError("state.viewport", "gbuffers_terrain.fsh",
            com.schmaloogium.engine.gl.GLErrorKind.INVALID_VALUE,
            "synthetic injection");
        device.debug().pushGroup("probe");
        device.state().viewport(0, 0, 4, 4);
        device.debug().popGroup();
        assertEquals(1, device.drainErrors().size());
        // Drain clears: the next frame window is silent ([D-P1-30]).
        assertTrue(device.drainErrors().isEmpty());
    }
}
