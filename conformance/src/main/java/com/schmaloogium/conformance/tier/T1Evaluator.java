// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.tier;

import com.schmaloogium.conformance.baseline.BaselineManifest;
import com.schmaloogium.conformance.baseline.BaselineRecord;
import com.schmaloogium.conformance.capture.RunManifest;
import com.schmaloogium.conformance.diff.DiffResult;
import com.schmaloogium.conformance.diff.IgnoreMask;
import com.schmaloogium.conformance.diff.ImageDiffer;
import com.schmaloogium.conformance.diff.PngRaster;
import com.schmaloogium.conformance.diff.TolerancePolicy;
import com.schmaloogium.conformance.wire.FlatDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * T1 — renders plausibly (§4.2.2): per captured sample, select the approved baseline for
 * the exact {@code (kind, id, ordinal, optionState, profile, machineClass)} key, apply
 * §4.7.4's invalidation (scene/world/generation/external-mod hashes → {@code NO_BASELINE}
 * naming the field; a different machine class escalates to {@code CROSS_DRIVER} and says
 * so), run one {@link ImageDiffer} call, and publish one comparison artifact. A numeric
 * diff alone is not PASS: the run must be {@code COMPLETE} and T0-passing, and the policy
 * must be calibrated (an uncalibrated profile yields a provisional result reported as
 * {@code FAIL}-with-reason, never PASS — §4.6.3).
 */
public final class T1Evaluator {

    public record SampleOutcome(String captureKind, String captureId, long sampleOrdinal,
            TierOutcome outcome, String reason, DiffResult metrics, FlatDocument comparison) {
    }

    public record T1Result(List<SampleOutcome> samples) {

        public TierOutcome outcome() {
            if (samples.isEmpty()) {
                return TierOutcome.NOT_ATTEMPTED;
            }
            if (samples.stream().anyMatch(s -> s.outcome() == TierOutcome.FAIL)) {
                return TierOutcome.FAIL;
            }
            if (samples.stream().anyMatch(s -> s.outcome() == TierOutcome.NO_BASELINE)) {
                return TierOutcome.NO_BASELINE;
            }
            if (samples.stream().anyMatch(s -> s.outcome() == TierOutcome.SKIPPED)) {
                return TierOutcome.SKIPPED;
            }
            return TierOutcome.PASS;
        }
    }

    /** Everything the evaluator needs besides the manifest. */
    public record Inputs(
            RunManifest manifest,
            String manifestSha256,
            String machineClass,
            Optional<BaselineManifest> baseline,
            String baselineManifestSha256,
            Map<String, TolerancePolicy> profiles,
            String requestedProfile,
            Function<String, IgnoreMask> maskForCapture,
            Function<String, PngRaster> candidateRaster,
            Function<BaselineRecord, PngRaster> baselineRaster,
            boolean allowUncalibrated) {
    }

    private T1Evaluator() {
    }

    public static T1Result evaluate(Inputs in) throws IOException {
        RunManifest m = in.manifest();
        T0Evaluator.T0Result t0 = T0Evaluator.evaluate(m);
        String runGate = null;
        if (!m.token("run.exitStatus").equals("COMPLETE")) {
            runGate = "run.exitStatus is " + m.token("run.exitStatus");
        } else if (t0.outcome() != TierOutcome.PASS) {
            runGate = "T0 failed: " + t0.verdicts().stream().filter(v -> !v.passed())
                .map(v -> v.predicate() + " " + v.failures()).toList();
        }
        String candidateTiming = RunTimingDocument.render(m);
        List<SampleOutcome> outcomes = new ArrayList<>();
        for (RunManifest.Row image : m.family("images")) {
            String kind = image.token("captureKind");
            String id = image.text("captureId");
            long ordinal = image.integer("sampleOrdinal");
            String optionState = m.token("pack.optionStateSha256");
            ComparisonDocument.Identity identity = new ComparisonDocument.Identity("T1",
                m.text("pack.id"), m.text("pack.version"), m.token("run.sceneId"), kind, id, ordinal, "");
            ComparisonDocument.Candidate candidate = new ComparisonDocument.Candidate(m.token("run.id"),
                in.manifestSha256(), optionState, image.token("pixelSha256"),
                (int) image.integer("width"), (int) image.integer("height"));
            IgnoreMask mask = in.maskForCapture().apply(id);
            TolerancePolicy policy = TolerancePolicy.require(in.profiles(), in.requestedProfile());
            String escalation = "";
            Optional<BaselineRecord> record = Optional.empty();
            String noBaseline = null;
            if (in.baseline().isEmpty()) {
                noBaseline = "no baseline manifest for this pack version and scene";
            } else {
                record = in.baseline().get().find(kind, id, ordinal, optionState, policy.name(),
                    in.machineClass());
                if (record.isEmpty()) {
                    // A record for another machine class escalates to CROSS_DRIVER (§4.7.4).
                    Optional<BaselineRecord> other = in.baseline().get().records().stream()
                        .filter(r -> r.captureKind().equals(kind) && r.captureId().equals(id)
                            && r.sampleOrdinal() == ordinal && r.optionStateSha256().equals(optionState))
                        .findFirst();
                    if (other.isPresent() && !other.get().machineClass().equals(in.machineClass())) {
                        escalation = "machine class changed: baseline " + other.get().machineClass()
                            + ", candidate " + in.machineClass() + " → CROSS_DRIVER";
                        policy = TolerancePolicy.require(in.profiles(), "CROSS_DRIVER");
                        record = other;
                    } else {
                        noBaseline = "no approved record for key " + kind + "/" + id + "/" + ordinal
                            + " option-state " + optionState + " profile " + policy.name()
                            + " machine " + in.machineClass();
                    }
                }
            }
            if (record.isPresent() && noBaseline == null) {
                noBaseline = invalidation(m, record.get());
            }
            String policyText = ComparisonDocument.policyText(policy, mask, escalation);
            ComparisonDocument.Reference reference;
            ComparisonDocument.Comparability comparability;
            ComparisonDocument.Result result;
            DiffResult metrics = null;
            if (noBaseline != null) {
                reference = ComparisonDocument.Reference.unavailable("BASELINE");
                comparability = new ComparisonDocument.Comparability("UNAVAILABLE", noBaseline,
                    candidateTiming, "", "reference unavailable");
                result = new ComparisonDocument.Result(TierOutcome.NO_BASELINE, noBaseline, null);
            } else {
                BaselineRecord r = record.get();
                reference = new ComparisonDocument.Reference("BASELINE", in.baselineManifestSha256(),
                    r.recordKey(), r.optionStateSha256(), r.pixelSha256(), r.width(), r.height());
                String checks = "sceneSha256 equal; worldGenerationSha256 equal; worldSha256 equal;"
                    + " externalModSetSha256 equal; optionStateSha256 equal; dimensions "
                    + (r.width() == candidate.width() && r.height() == candidate.height() ? "equal" : "DIFFER");
                if (r.width() != candidate.width() || r.height() != candidate.height()) {
                    throw new IllegalStateException("candidate and baseline dimensions differ for "
                        + kind + "/" + id + "/" + ordinal + " (hard error, §4.6.1)");
                }
                PngRaster expected = in.baselineRaster().apply(r);
                if (!expected.pixelSha256().equals(r.pixelSha256())) {
                    throw new IllegalStateException("baseline raster for " + r.recordKey()
                        + " does not match its approved pixelSha256");
                }
                PngRaster actual = in.candidateRaster().apply(image.text("path"));
                if (!actual.pixelSha256().equals(image.token("pixelSha256"))) {
                    throw new IllegalStateException("candidate raster " + image.text("path")
                        + " does not match its manifest pixelSha256");
                }
                metrics = ImageDiffer.compare(expected, actual, policy, mask);
                comparability = new ComparisonDocument.Comparability("ESTABLISHED",
                    "approved baseline with equal scene, world, generation, external-mod and option"
                    + " identities", candidateTiming, "", checks);
                TierOutcome outcome;
                String reason;
                if (runGate != null) {
                    outcome = TierOutcome.FAIL;
                    reason = runGate + "; " + metrics.summary();
                } else if (!metrics.passed()) {
                    outcome = TierOutcome.FAIL;
                    reason = metrics.summary();
                } else if (!policy.calibrated() && !in.allowUncalibrated()) {
                    outcome = TierOutcome.FAIL;
                    reason = "provisional: numeric agreement at uncalibrated profile " + policy.name()
                        + " (§4.6.5 calibration required before tier acceptance); " + metrics.summary();
                } else {
                    outcome = TierOutcome.PASS;
                    reason = (policy.calibrated() ? "" : "provisional (uncalibrated profile, allowed"
                        + " by the invoker); ") + metrics.summary();
                }
                result = new ComparisonDocument.Result(outcome, reason, metrics);
            }
            FlatDocument comparison = ComparisonDocument.build(identity, candidate, reference,
                policyText, comparability, result, List.of());
            outcomes.add(new SampleOutcome(kind, id, ordinal, result.outcome(), result.reason(),
                metrics, comparison));
        }
        return new T1Result(outcomes);
    }

    /** §4.7.4 invalidation: the changed field, or null when the baseline is comparable. */
    static String invalidation(RunManifest m, BaselineRecord r) {
        if (!m.token("run.sceneHash").equals(r.sceneSha256())) {
            return "scene file changed (sceneSha256)";
        }
        if (!m.token("environment.worldGenerationSha256").equals(r.worldGenerationSha256())) {
            return "generation descriptor changed (worldGenerationSha256)";
        }
        if (!m.token("environment.worldSha256").equals(r.worldSha256())) {
            return "world save changed (worldSha256)";
        }
        if (!m.token("environment.externalModSetSha256").equals(r.externalModSetSha256())) {
            return "external mod set changed (externalModSetSha256)";
        }
        return null;
    }
}
