// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.harness;

import com.schmaloogium.conformance.glrec.FrameReplay;
import com.schmaloogium.conformance.glrec.ScriptedGLDevice;
import com.schmaloogium.conformance.tier.TierOutcome;
import com.schmaloogium.engine.gl.ClearTarget;
import com.schmaloogium.engine.gl.DrawService;
import com.schmaloogium.engine.gl.FramebufferTarget;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GLError;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.ShaderStage;
import com.schmaloogium.engine.pack.PackInspectionResult;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * The harness's executable T0 leg on a synthetic fixture (context A of §2's three
 * execution contexts — no GL context, no Minecraft, no renderer):
 *
 * <ul>
 *   <li><b>parse leg</b> — the front end produced a configuration with no FATAL/ERROR
 *       diagnostic (T0's "parses", §4.2.1);</li>
 *   <li><b>GL leg</b> — a synthetic frame loop drives the {@link ScriptedGLDevice}
 *       recorder through {@code frames} identical frames; every drained
 *       {@link GLError} and every replay mismatch is recorded (T0's "no GL errors" and
 *       "stable frame loop", executed headlessly through the recorder);</li>
 *   <li><b>compile leg</b> — delegated to the {@link CompileStage} seam; until Phase
 *       4's registry lands it reports the deferral and the overall T0 outcome stays
 *       {@code NOT_ATTEMPTED} ([D-P2-19] — no tier without evidence).</li>
 * </ul>
 *
 * <p>This is a harness-side exercise of the T0 pipeline, not a client run: the real
 * RUN-T0 verdict comes from a {@code schmaloogium.run-manifest/4} document produced by
 * a live capture, evaluated by {@code T0Evaluator} against hand-built or real
 * manifests. The renderer's frame loop replaces {@code SyntheticFrameLoop} at the same
 * seam when Phase 7 lands.
 */
public final class T0HarnessRun {

    public record ParseLeg(boolean parsed, boolean packConfigurationProduced,
        List<String> fatalDiagnostics) {
    }

    public record GlLeg(boolean clean, boolean stableLoop, List<String> errors,
        List<String> replayMismatches, String callLogRender) {
    }

    public record Report(ParseLeg parseLeg, CompileStage.CompileEvidence compileLeg, GlLeg glLeg,
        TierOutcome t0Outcome, String reason) {

        public String t0Note() {
            return reason;
        }
    }

    private static final String VERTEX_SOURCE = """
        #version 120

        void main() {
            gl_Position = ftransform();
        }
        """;

    private static final String FRAGMENT_SOURCE = """
        #version 120

        void main() {
            gl_FragData[0] = vec4(0.0, 0.0, 0.0, 1.0);
        }
        """;

    private T0HarnessRun() {
    }

    /** Runs the harness T0 leg on an already-obtained {@code Inspected} result. */
    public static Report run(PackInspectionResult.Inspected inspected,
            GLCapabilityProfile profile, CompileStage compileStage, int frames) {
        Objects.requireNonNull(inspected, "inspected");
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(compileStage, "compileStage");
        if (frames < 2) {
            throw new IllegalArgumentException("a stable-loop replay needs at least two frames");
        }
        ParseLeg parseLeg = parseLeg(inspected);
        CompileStage.CompileEvidence compileLeg = compileStage.compile(inspected);
        GlLeg glLeg = glLeg(profile, frames);
        return report(parseLeg, compileLeg, glLeg);
    }

    private static ParseLeg parseLeg(PackInspectionResult.Inspected inspected) {
        List<String> fatal = new ArrayList<>();
        inspected.configuration().diagnostics().stream()
            .filter(d -> d.severity() == com.schmaloogium.engine.diag.DiagnosticSeverity.ERROR
                || d.severity() == com.schmaloogium.engine.diag.DiagnosticSeverity.FATAL)
            .forEach(d -> fatal.add(d.severity() + " " + d.messageKey()));
        boolean parsed = !fatal.isEmpty() ? false
            : inspected.configuration().schemaVersion()
                == com.schmaloogium.engine.pack.PackFrontEnd.CURRENT_SCHEMA_VERSION;
        return new ParseLeg(parsed, true, List.copyOf(fatal));
    }

    private static GlLeg glLeg(GLCapabilityProfile profile, int frames) {
        ScriptedGLDevice device = new ScriptedGLDevice(profile);

        // Setup: one recorded, non-repeating prologue (pipeline creation).
        ProgramHandle program = device.shaders().createProgram();
        // The loop: every frame identical, one drained window per frame. The window
        // is the log slice between the markers — the recorder's own bookkeeping
        // never enters a frame's replay window.
        List<String> errors = new ArrayList<>();
        List<String> replayMismatches = new ArrayList<>();
        FrameReplay replay = new FrameReplay();
        for (int frame = 0; frame < frames; frame++) {
            int before = device.log().calls().size();
            device.debug().pushGroup("frame");
            device.state().viewport(0, 0, 128, 128);
            device.state().clearColor(0f, 0f, 0f, 1f);
            device.state().clear(EnumSet.of(ClearTarget.COLOR, ClearTarget.DEPTH));
            device.shaders().use(program);
            device.draw().fullscreenQuad();
            device.debug().popGroup();
            replay.addWindow(device.log().calls().subList(before, device.log().calls().size()));
            for (GLError error : device.drainErrors()) {
                errors.add("frame " + frame + ": " + error.op() + " " + error.kind()
                    + " " + error.detail() + " attribution-pending");
            }
        }
        replayMismatches.addAll(replay.assertStableLoop());
        return new GlLeg(errors.isEmpty(), replayMismatches.isEmpty(),
            List.copyOf(errors), List.copyOf(replayMismatches), device.log().render());
    }

    private static Report report(ParseLeg parseLeg, CompileStage.CompileEvidence compileLeg,
            GlLeg glLeg) {
        if (!compileLeg.runnable()) {
            return new Report(parseLeg, compileLeg, glLeg, TierOutcome.NOT_ATTEMPTED,
                compileLeg.deferredReason());
        }
        // Owner-backed compile leg (P4 landed): apply §4.2.1's programs predicate to
        // the projection, then combine with the other three legs.
        if (!compileLeg.failureReason().isEmpty()) {
            return new Report(parseLeg, compileLeg, glLeg, TierOutcome.FAIL,
                "registry build failed: " + compileLeg.failureReason());
        }
        boolean programsOk = compileLeg.rows().stream()
            .allMatch(row -> !row.status().equals("FAILED"));
        if (!parseLeg.parsed() || !glLeg.clean() || !glLeg.stableLoop() || !programsOk) {
            List<String> reasons = new ArrayList<>();
            reasons.addAll(parseLeg.fatalDiagnostics());
            reasons.addAll(glLeg.errors());
            reasons.addAll(glLeg.replayMismatches());
            compileLeg.rows().stream().filter(row -> row.status().equals("FAILED"))
                .forEach(row -> reasons.add("slot " + row.slot() + " FAILED"));
            return new Report(parseLeg, compileLeg, glLeg, TierOutcome.FAIL,
                String.join("; ", reasons));
        }
        return new Report(parseLeg, compileLeg, glLeg, TierOutcome.PASS, "all four legs green");
    }

}
