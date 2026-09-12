// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The agent's checkpoint ledger (D-P2-50): the origin observed at checkpoint0, every actual
 * controlled step in receipt order, and the close state. Values are copied observations —
 * the ledger never manufactures a counter from the schedule. Logical/animation ticks are
 * projected relative to the origin's actual world time because the mod's P6 logical tick is
 * the vanilla world time (recorded as a Task C deviation, see the result doc).
 */
final class TimingLedger {

    record Origin(String checkpointId, long worldTick, long acceptedFrames, long finalizedFrames,
            boolean quiescent, boolean freshRuntime, boolean frameTimingAbsent,
            long registryGeneration, long estateGeneration, long resourceEpoch, String pipelineIdentity) {
    }

    /** One observed controlled step; every field is what the owner reported at the hook. */
    record Step(String phase, int captureIndex, long ordinal, boolean valid,
            long registryGeneration, long frameId, long worldEpoch, long logicalTick,
            double smoothingTimeTicks, float frameTimeSeconds, long frameCounter, float frameTimeCounter,
            long worldTick, long animationTick, double partialTicks, long clockStep,
            long clientTicks, long serverTicks, long acceptedFrames, long finalizedFrames) {
    }

    private Origin origin;
    private final List<Step> steps = new ArrayList<>();
    private String failureReason = "";
    private String restoration = "NOT_REACHED";
    private boolean identitiesUnchanged;

    void admit(Origin o) {
        origin = o;
    }

    Origin origin() {
        return origin;
    }

    boolean available() {
        return origin != null;
    }

    void record(Step step) {
        steps.add(step);
    }

    List<Step> steps() {
        return Collections.unmodifiableList(steps);
    }

    long clockStep() {
        return steps.size();
    }

    void fail(String reason) {
        if (failureReason.isEmpty()) {
            failureReason = reason;
        }
    }

    String failureReason() {
        return failureReason;
    }

    void close(boolean restored, boolean identitiesStillEqual) {
        restoration = restored ? "RESTORED" : "FAILED";
        identitiesUnchanged = identitiesStillEqual;
    }

    void pending() {
        restoration = "PENDING";
    }

    String restoration() {
        return restoration;
    }

    boolean identitiesUnchanged() {
        return identitiesUnchanged;
    }

    /** complete = exact coverage, valid steps, RESTORED, unchanged identities, no failure. */
    boolean complete(long expectedSteps) {
        return available() && failureReason.isEmpty() && steps.size() == expectedSteps
                && steps.stream().allMatch(Step::valid) && restoration.equals("RESTORED") && identitiesUnchanged;
    }
}
