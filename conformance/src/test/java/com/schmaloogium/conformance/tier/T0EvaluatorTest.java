// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.tier;

import com.schmaloogium.conformance.Manifests;
import com.schmaloogium.conformance.capture.RunManifest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The four T0 predicates (§4.2.1), each decided from manifest evidence alone. */
class T0EvaluatorTest {

    @Test
    void validManifestPassesAllFourPredicates() {
        T0Evaluator.T0Result result = T0Evaluator.evaluate(Manifests.valid());
        assertEquals(TierOutcome.PASS, result.outcome());
        assertEquals(0, result.unattributedGlErrors());
        assertTrue(result.verdict(T0Evaluator.PARSES).passed());
        assertTrue(result.verdict(T0Evaluator.PROGRAMS_COMPILE).passed());
        assertTrue(result.verdict(T0Evaluator.NO_GL_ERRORS).passed());
        assertTrue(result.verdict(T0Evaluator.STABLE_FRAME_LOOP).passed());
    }

    @Test
    void frontEndFailureFailsParses() {
        RunManifest broken = Manifests.with(Manifests.valid(), b ->
            b.set("frontEnd.completed", new RunManifest.Value.Bool(false)));
        T0Evaluator.T0Result result = T0Evaluator.evaluate(broken);
        assertFalse(result.verdict(T0Evaluator.PARSES).passed());
        assertTrue(result.verdict(T0Evaluator.STABLE_FRAME_LOOP).passed());
        assertEquals(TierOutcome.FAIL, result.outcome());
    }

    @Test
    void errorDiagnosticFailsParses() {
        RunManifest broken = Manifests.with(Manifests.valid(), b -> {
            b.set("diagnostics.count", new RunManifest.Value.Int(1));
            b.row("diagnostics", 0, java.util.Map.of(
                "code", new RunManifest.Value.Text("\"E_INCLUDE_CYCLE\""),
                "severity", new RunManifest.Value.Token("ERROR"),
                "channel", new RunManifest.Value.Token("LOG_ONLY"),
                "file", new RunManifest.Value.Text("\"shaders/a.glsl\""),
                "line", new RunManifest.Value.Int(3)));
            return b;
        });
        assertFalse(T0Evaluator.evaluate(broken).verdict(T0Evaluator.PARSES).passed());
    }

    @Test
    void failedSlotFailsProgramsCompile() {
        RunManifest broken = Manifests.with(Manifests.valid(), b -> {
            b.set("programs.1.status", new RunManifest.Value.Token("FAILED"));
            b.set("programs.1.from", new RunManifest.Value.Text(""));
            return b;
        });
        assertFalse(T0Evaluator.evaluate(broken).verdict(T0Evaluator.PROGRAMS_COMPILE).passed());
    }

    @Test
    void chainWithoutFromFailsProgramsCompile() {
        RunManifest broken = Manifests.with(Manifests.valid(), b ->
            b.set("programs.1.from", new RunManifest.Value.Text("")));
        assertFalse(T0Evaluator.evaluate(broken).verdict(T0Evaluator.PROGRAMS_COMPILE).passed());
    }

    @Test
    void anyGlErrorFailsNoGlErrorsWhateverItsAttribution() {
        RunManifest attributed = Manifests.with(Manifests.valid(), b -> {
            b.set("gl_errors.count", new RunManifest.Value.Int(1));
            b.row("gl_errors", 0, java.util.Map.of(
                "op", new RunManifest.Value.Token("glCompileShader"),
                "subject", new RunManifest.Value.Token("gbuffers_terrain.fsh"),
                "kind", new RunManifest.Value.Token("GL_INVALID_VALUE"),
                "detail", new RunManifest.Value.Text("\"synthetic\""),
                "attributed", new RunManifest.Value.Bool(true)));
            return b;
        });
        T0Evaluator.T0Result result = T0Evaluator.evaluate(attributed);
        assertFalse(result.verdict(T0Evaluator.NO_GL_ERRORS).passed());
        assertEquals(0, result.unattributedGlErrors());

        RunManifest unattributed = Manifests.with(Manifests.valid(), b -> {
            b.set("gl_errors.count", new RunManifest.Value.Int(1));
            b.row("gl_errors", 0, java.util.Map.of(
                "op", new RunManifest.Value.Token("drain"),
                "subject", new RunManifest.Value.Token("unknown"),
                "kind", new RunManifest.Value.Token("GL_INVALID_OPERATION"),
                "detail", new RunManifest.Value.Text("\"orphan flag\""),
                "attributed", new RunManifest.Value.Bool(false)));
            return b;
        });
        result = T0Evaluator.evaluate(unattributed);
        assertFalse(result.verdict(T0Evaluator.NO_GL_ERRORS).passed());
        assertEquals(1, result.unattributedGlErrors());
    }

    @Test
    void runLifecycleFailuresFailStableFrameLoop() {
        for (RunManifest.Value.Token exit : new RunManifest.Value.Token[] {
            new RunManifest.Value.Token("FAILED"), new RunManifest.Value.Token("SKIPPED")}) {
            RunManifest broken = Manifests.with(Manifests.valid(), b ->
                b.set("run.exitStatus", exit));
            assertFalse(T0Evaluator.evaluate(broken)
                .verdict(T0Evaluator.STABLE_FRAME_LOOP).passed(), exit.value());
        }
        RunManifest bailed = Manifests.with(Manifests.valid(), b ->
            b.set("run.compatVerdict", new RunManifest.Value.Token("Bail")));
        assertFalse(T0Evaluator.evaluate(bailed)
            .verdict(T0Evaluator.STABLE_FRAME_LOOP).passed());

        RunManifest shadersOff = Manifests.with(Manifests.valid(), b ->
            b.set("run.shadersActiveThroughout", new RunManifest.Value.Bool(false)));
        assertFalse(T0Evaluator.evaluate(shadersOff)
            .verdict(T0Evaluator.STABLE_FRAME_LOOP).passed());

        RunManifest hung = Manifests.with(Manifests.valid(), b -> {
            b.set("run.timedOut", new RunManifest.Value.Bool(true));
            b.set("frames.1.durationMillis", new RunManifest.Value.Int(5_000));
            return b;
        });
        assertFalse(T0Evaluator.evaluate(hung)
            .verdict(T0Evaluator.STABLE_FRAME_LOOP).passed());
    }

    @Test
    void missingImageForCapturedSampleFailsStableFrameLoop() {
        RunManifest broken = Manifests.with(Manifests.valid(), b -> {
            b.set("images.count", new RunManifest.Value.Int(1));
            for (String key : List.of("captureKind", "captureId", "sampleOrdinal", "path",
                "width", "height", "pixelSha256")) {
                b.unset("images.1." + key);
            }
            return b;
        });
        assertFalse(T0Evaluator.evaluate(broken)
            .verdict(T0Evaluator.STABLE_FRAME_LOOP).passed());
    }

    @Test
    void incompleteOwnerTimingFailsStableFrameLoop() {
        RunManifest incomplete = Manifests.with(Manifests.valid(), b ->
            b.set("timing.complete", new RunManifest.Value.Bool(false)));
        assertFalse(T0Evaluator.evaluate(incomplete)
            .verdict(T0Evaluator.STABLE_FRAME_LOOP).passed());

        RunManifest pending = Manifests.with(Manifests.valid(), b ->
            b.set("timing.restoration", new RunManifest.Value.Token("PENDING")));
        assertFalse(T0Evaluator.evaluate(pending)
            .verdict(T0Evaluator.STABLE_FRAME_LOOP).passed());
    }

    @Test
    void clockScheduleMismatchFailsStableFrameLoop() {
        // Frame 2's clock step disagrees with the plan schedule.
        RunManifest broken = Manifests.with(Manifests.valid(), b ->
            b.set("frames.2.clockStep", new RunManifest.Value.Int(99)));
        assertFalse(T0Evaluator.evaluate(broken)
            .verdict(T0Evaluator.STABLE_FRAME_LOOP).passed());
    }

    @Test
    void poseDriftFailsStableFrameLoop() {
        RunManifest broken = Manifests.with(Manifests.valid(), b ->
            b.set("frames.2.actualCurrent.posX", new RunManifest.Value.Dec(9.75)));
        assertFalse(T0Evaluator.evaluate(broken)
            .verdict(T0Evaluator.STABLE_FRAME_LOOP).passed());
    }

    @Test
    void unplannedWindowCaptureFailsStableFrameLoop() {
        RunManifest broken = Manifests.with(Manifests.valid(), b ->
            b.set("captures.0.captureStartSample", new RunManifest.Value.Int(0)));
        assertFalse(T0Evaluator.evaluate(broken)
            .verdict(T0Evaluator.STABLE_FRAME_LOOP).passed());
    }
}
