// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.tier;

import com.schmaloogium.conformance.capture.RunManifest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The T0 gate, decided entirely off the {@code RunManifest} (§4.2.1) so a verdict is
 * re-derivable from artifacts months later. Four predicates:
 *
 * <ul>
 *   <li><b>parses</b> — front end completed and produced a {@code PackConfiguration}
 *       with zero {@code FATAL}/{@code ERROR} diagnostics;</li>
 *   <li><b>programs compile</b> — every slot resolved to a linked and validated program
 *       or is legitimately absent; any {@code FAILED} slot fails;</li>
 *   <li><b>no GL errors</b> — zero {@code gl_errors} records. Every recorded error
 *       fails T0 whether {@code attributed} is true or false (§4.2.1); the
 *       unattributable count is diagnosis-only;</li>
 *   <li><b>stable frame loop</b> — complete exit, no uncaught exception, no Bail, no
 *       shaders-off transition, exact plan coverage with dense sample ordinals,
 *       planned/actual pose equality, durations within the hang ceiling, and complete
 *       owner timing evidence (D-P2-50).</li>
 * </ul>
 *
 * <p>The input must already be structurally valid ({@link
 * com.schmaloogium.conformance.capture.RunManifestReader}); a document that cannot
 * parse is broken evidence for [D-P2-19]'s {@code NOT_ATTEMPTED}, not a T0 failure.
 */
public final class T0Evaluator {

    /** One predicate's verdict with its named failures. */
    public record Verdict(String predicate, boolean passed, List<String> failures) {
    }

    public record T0Result(List<Verdict> verdicts, int unattributedGlErrors) {

        public TierOutcome outcome() {
            return verdicts.stream().allMatch(Verdict::passed)
                ? TierOutcome.PASS
                : TierOutcome.FAIL;
        }

        public Verdict verdict(String predicate) {
            return verdicts.stream().filter(v -> v.predicate().equals(predicate))
                .findFirst().orElseThrow();
        }
    }

    public static final String PARSES = "parses";
    public static final String PROGRAMS_COMPILE = "programs compile";
    public static final String NO_GL_ERRORS = "no GL errors";
    public static final String STABLE_FRAME_LOOP = "stable frame loop";

    private T0Evaluator() {
    }

    public static T0Result evaluate(RunManifest manifest) {
        List<Verdict> verdicts = List.of(
            parses(manifest),
            programsCompile(manifest),
            noGlErrors(manifest),
            stableFrameLoop(manifest));
        int unattributed = (int) manifest.family("gl_errors").stream()
            .filter(row -> !row.bool("attributed"))
            .count();
        return new T0Result(verdicts, unattributed);
    }

    // ------------------------------------------------------------------

    private static Verdict verdict(String name, List<String> failures) {
        return new Verdict(name, failures.isEmpty(), List.copyOf(failures));
    }

    private static Verdict parses(RunManifest manifest) {
        List<String> failures = new ArrayList<>();
        if (!manifest.bool("frontEnd.completed")) {
            failures.add("frontEnd.completed is false");
        }
        if (!manifest.bool("frontEnd.packConfigurationProduced")) {
            failures.add("frontEnd.packConfigurationProduced is false");
        }
        for (RunManifest.Row row : manifest.family("diagnostics")) {
            String severity = row.token("severity");
            if (severity.equals("ERROR") || severity.equals("FATAL")) {
                failures.add("diagnostics." + row.index() + " severity=" + severity
                    + " code=" + row.text("code"));
            }
        }
        return verdict(PARSES, failures);
    }

    private static Verdict programsCompile(RunManifest manifest) {
        List<String> failures = new ArrayList<>();
        for (RunManifest.Row row : manifest.family("programs")) {
            String status = row.token("status");
            // P4's closed statuses; unknown statuses fail closed as invalid evidence.
            switch (status) {
                case "SOURCED", "CHAIN", "ABSENT" -> {
                    // Legitimately resolved or legitimately absent.
                }
                case "FAILED" -> failures.add("programs." + row.index() + " slot="
                    + row.token("slot") + " FAILED");
                default -> failures.add("programs." + row.index() + " slot="
                    + row.token("slot") + " has invalid status " + status);
            }
            boolean chain = status.equals("CHAIN");
            if (chain != !row.text("from").isEmpty()) {
                failures.add("programs." + row.index() + " from must be non-empty exactly"
                    + " when status is CHAIN");
            }
        }
        return verdict(PROGRAMS_COMPILE, failures);
    }

    private static Verdict noGlErrors(RunManifest manifest) {
        List<String> failures = new ArrayList<>();
        for (RunManifest.Row row : manifest.family("gl_errors")) {
            failures.add("gl_errors." + row.index() + " op=" + row.token("op")
                + " kind=" + row.token("kind")
                + " attributed=" + row.bool("attributed"));
        }
        return verdict(NO_GL_ERRORS, failures);
    }

    // ------------------------------------------------------------------

    private static Verdict stableFrameLoop(RunManifest manifest) {
        List<String> failures = new ArrayList<>();
        if (!manifest.token("run.exitStatus").equals("COMPLETE")) {
            failures.add("run.exitStatus is " + manifest.token("run.exitStatus"));
        }
        if (!manifest.text("run.uncaughtException").isEmpty()) {
            failures.add("run.uncaughtException is non-empty");
        }
        if (!manifest.token("run.compatVerdict").equals("Continue")) {
            failures.add("run.compatVerdict is " + manifest.token("run.compatVerdict"));
        }
        if (!manifest.bool("run.shadersActiveThroughout")) {
            failures.add("run.shadersActiveThroughout is false");
        }
        if (manifest.bool("run.timedOut")) {
            failures.add("run.timedOut is true");
        }
        if (!manifest.bool("gl.available")) {
            failures.add("gl.available is false");
        }
        if (!manifest.bool("resources.available")) {
            failures.add("resources.available is false");
        }
        if (!manifest.bool("hooks.available")) {
            failures.add("hooks.available is false");
        }
        long hangCeiling = manifest.integer("run.hangCeilingMillis");

        List<RunManifest.Row> captures = manifest.family("captures");
        List<RunManifest.Row> frames = manifest.family("frames");
        for (RunManifest.Row frame : frames) {
            long duration = frame.integer("durationMillis");
            if (duration > hangCeiling) {
                failures.add("frames." + frame.index() + " durationMillis=" + duration
                    + " exceeds hangCeilingMillis=" + hangCeiling);
            }
        }
        checkCaptureCoverage(manifest, captures, frames, failures);
        checkTiming(manifest, captures, frames, failures);
        return verdict(STABLE_FRAME_LOOP, failures);
    }

    private static void checkCaptureCoverage(RunManifest manifest, List<RunManifest.Row> captures,
            List<RunManifest.Row> frames, List<String> failures) {
        List<RunManifest.Row> images = manifest.family("images");
        for (RunManifest.Row capture : captures) {
            String prefix = "captures." + capture.index();
            String kind = capture.token("kind");
            String id = capture.text("id");
            long plannedSamples = capture.integer("plannedSamples");
            if (plannedSamples != capture.integer("actualSamples")) {
                failures.add(prefix + " plannedSamples=" + plannedSamples
                    + " actualSamples=" + capture.integer("actualSamples"));
            }
            if (capture.integer("plannedWarmupFrames") != capture.integer("actualWarmupFrames")) {
                failures.add(prefix + " plannedWarmupFrames="
                    + capture.integer("plannedWarmupFrames") + " actualWarmupFrames="
                    + capture.integer("actualWarmupFrames"));
            }
            long start = capture.integer("captureStartSample");
            long window = capture.integer("captureSampleCount");
            List<RunManifest.Row> own = frames.stream()
                .filter(f -> f.token("captureKind").equals(kind) && f.text("captureId").equals(id))
                .toList();
            if (own.size() != plannedSamples) {
                failures.add(prefix + " expects " + plannedSamples + " frame records, found "
                    + own.size());
            }
            Set<Long> capturedOrdinals = new HashSet<>();
            for (int ordinal = 0; ordinal < own.size(); ordinal++) {
                RunManifest.Row frame = own.get(ordinal);
                String frameId = "frames[" + kind + "/" + id + "/" + ordinal + "]";
                if (frame.integer("sampleOrdinal") != ordinal) {
                    failures.add(frameId + " sampleOrdinal dense from 0");
                }
                boolean inWindow = ordinal >= start && ordinal < start + window;
                if (frame.bool("captured") != inWindow) {
                    failures.add(frameId + " captured=" + frame.bool("captured")
                        + " but planned window is [" + start + "," + (start + window) + ")");
                }
                checkPoses(frame, frameId, failures);
                if (frame.bool("captured")) {
                    capturedOrdinals.add((long) ordinal);
                }
            }
            List<RunManifest.Row> ownImages = images.stream()
                .filter(img -> img.token("captureKind").equals(kind)
                    && img.text("captureId").equals(id))
                .toList();
            if (ownImages.size() != capturedOrdinals.size()) {
                failures.add(prefix + " expects " + capturedOrdinals.size()
                    + " image records, found " + ownImages.size());
            }
            for (RunManifest.Row image : ownImages) {
                long ordinal = image.integer("sampleOrdinal");
                if (!capturedOrdinals.contains(ordinal)) {
                    failures.add("images." + image.index() + " sampleOrdinal=" + ordinal
                        + " outside the captured window of " + kind + "/" + id);
                }
            }
        }
    }

    private static void checkPoses(RunManifest.Row frame, String frameId,
            List<String> failures) {
        if (!componentEquals(frame, "plannedCurrent", "actualCurrent")) {
            failures.add(frameId + " plannedCurrent != actualCurrent");
        }
        if (!componentEquals(frame, "plannedPrevious", "actualPrevious")) {
            failures.add(frameId + " plannedPrevious != actualPrevious");
        }
        // Sample 0 has previous = current = first pose (§4.3.4).
        if (frame.integer("sampleOrdinal") == 0) {
            if (!componentEquals(frame, "plannedPrevious", "plannedCurrent")) {
                failures.add(frameId + " sample 0 plannedPrevious must equal plannedCurrent");
            }
            if (!componentEquals(frame, "actualPrevious", "actualCurrent")) {
                failures.add(frameId + " sample 0 actualPrevious must equal actualCurrent");
            }
        }
    }

    private static boolean componentEquals(RunManifest.Row frame, String leftRole,
            String rightRole) {
        for (String axis : new String[] {"posX", "posY", "posZ", "yaw", "pitch"}) {
            if (frame.decimal(leftRole + "." + axis) != frame.decimal(rightRole + "." + axis)) {
                return false;
            }
        }
        return true;
    }

    private static void checkTiming(RunManifest manifest, List<RunManifest.Row> captures,
            List<RunManifest.Row> frames, List<String> failures) {
        boolean available = manifest.bool("timing.available");
        if (!available) {
            failures.add("timing.available is false");
            return;
        }
        if (!manifest.bool("timing.complete")) {
            failures.add("timing.complete is false: " + manifest.text("timing.failureReason"));
        }
        if (!manifest.token("timing.restoration").equals("RESTORED")) {
            failures.add("timing.restoration is " + manifest.token("timing.restoration"));
        }
        if (!manifest.bool("timing.identitiesUnchanged")) {
            failures.add("timing.identitiesUnchanged is false");
        }
        long ticksPerFrame = manifest.integer("clock.ticksPerFrame");
        double partialTicks = manifest.decimal("clock.partialTicks");
        if (ticksPerFrame <= 0) {
            failures.add("clock.ticksPerFrame must be positive: " + ticksPerFrame);
            return;
        }
        List<RunManifest.Row> steps = manifest.family("timing.steps");
        long originWorldTick = manifest.integer("timing.origin.worldTick");

        // Expected schedule: dense PREPARATION, then per capture WARMUP then SAMPLE.
        int preparationSteps = 0;
        while (preparationSteps < steps.size()
                && steps.get(preparationSteps).token("phase").equals("PREPARATION")) {
            preparationSteps++;
        }
        long expectedSamples = captures.stream()
            .mapToLong(c -> c.integer("plannedWarmupFrames") + c.integer("plannedSamples"))
            .sum();
        if (steps.size() != preparationSteps + expectedSamples) {
            failures.add("timing.steps.count=" + steps.size() + " but the plan schedule needs "
                + preparationSteps + " preparation + " + expectedSamples
                + " warm-up/sample steps");
            return;
        }
        int cursor = 0;
        for (; cursor < preparationSteps; cursor++) {
            RunManifest.Row step = steps.get(cursor);
            if (step.integer("captureIndex") != -1) {
                failures.add("timing.steps." + cursor + " PREPARATION requires captureIndex=-1");
            }
            if (step.integer("ordinal") != cursor) {
                failures.add("timing.steps." + cursor + " preparation ordinal must be dense");
            }
        }
        for (RunManifest.Row capture : captures) {
            for (long ordinal = 0; ordinal < capture.integer("plannedWarmupFrames"); ordinal++) {
                checkStep(manifest, steps, cursor++, "WARMUP", capture.index(), ordinal,
                    ticksPerFrame, partialTicks, originWorldTick, null, frames, failures);
            }
            for (long ordinal = 0; ordinal < capture.integer("plannedSamples"); ordinal++) {
                checkStep(manifest, steps, cursor++, "SAMPLE", capture.index(), ordinal,
                    ticksPerFrame, partialTicks, originWorldTick, capture, frames, failures);
            }
        }
    }

    private static void checkStep(RunManifest manifest, List<RunManifest.Row> steps, int position,
            String phase, int captureIndex, long ordinal, long ticksPerFrame, double partialTicks,
            long originWorldTick, RunManifest.Row capture, List<RunManifest.Row> frames,
            List<String> failures) {
        if (position >= steps.size()) {
            return; // count mismatch already reported
        }
        RunManifest.Row step = steps.get(position);
        String where = "timing.steps." + position;
        if (!step.token("phase").equals(phase)) {
            failures.add(where + " phase must be " + phase + " but is " + step.token("phase"));
        }
        if (step.integer("captureIndex") != captureIndex) {
            failures.add(where + " captureIndex must be " + captureIndex);
        }
        if (step.integer("ordinal") != ordinal) {
            failures.add(where + " ordinal must be " + ordinal);
        }
        if (!step.token("validation").equals("VALID")) {
            failures.add(where + " validation must be VALID");
        }
        long clockStep = position + 1;
        if (step.integer("clockStep") != clockStep) {
            failures.add(where + " clockStep must be " + clockStep);
        }
        if (step.integer("acceptedFrames") != clockStep
                || step.integer("finalizedFrames") != clockStep) {
            failures.add(where + " acceptedFrames/finalizedFrames must equal clockStep");
        }
        if (step.integer("clientTicks") != clockStep * ticksPerFrame
                || step.integer("serverTicks") != clockStep * ticksPerFrame) {
            failures.add(where + " client/server ticks must advance ticksPerFrame per step");
        }
        if (step.integer("logicalTick") != clockStep * ticksPerFrame
                || step.integer("animationTick") != clockStep * ticksPerFrame) {
            failures.add(where + " logical/animation ticks must advance ticksPerFrame per step");
        }
        if (step.integer("worldTick") != originWorldTick + clockStep * ticksPerFrame) {
            failures.add(where + " worldTick must advance ticksPerFrame per step from the origin");
        }
        if (step.integer("frameCounter") != clockStep) {
            failures.add(where + " frameCounter must equal clockStep");
        }
        if (phase.equals("SAMPLE") && capture != null) {
            joinFramesRow(manifest, step, position, capture, ordinal, frames, failures);
        }
    }

    private static void joinFramesRow(RunManifest manifest, RunManifest.Row step, int position,
            RunManifest.Row capture, long ordinal, List<RunManifest.Row> frames,
            List<String> failures) {
        String kind = capture.token("kind");
        String id = capture.text("id");
        RunManifest.Row joined = frames.stream()
            .filter(f -> f.token("captureKind").equals(kind) && f.text("captureId").equals(id)
                && f.integer("sampleOrdinal") == ordinal)
            .findFirst()
            .orElse(null);
        if (joined == null) {
            failures.add("timing.steps." + position + " SAMPLE joins no frame row for "
                + kind + "/" + id + "/" + ordinal);
            return;
        }
        String[] overlapping = {"clockStep", "logicalTick", "animationTick", "worldTick",
            "frameCounter"};
        for (String field : overlapping) {
            if (step.integer(field) != joined.integer(field)) {
                failures.add("timing.steps." + position + " " + field + "=" + step.integer(field)
                    + " != frames " + field + "=" + joined.integer(field));
            }
        }
        for (String field : new String[] {"partialTicks", "frameTimeSeconds", "frameTimeCounter",
            "smoothingTimeTicks"}) {
            if (step.decimal(field) != joined.decimal(field)) {
                failures.add("timing.steps." + position + " " + field + " != frames " + field);
            }
        }
    }
}
