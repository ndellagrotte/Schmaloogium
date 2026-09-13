// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.tier;

import com.schmaloogium.conformance.capture.RunManifest;
import com.schmaloogium.conformance.diff.DiffResult;
import com.schmaloogium.conformance.diff.IgnoreMask;
import com.schmaloogium.conformance.diff.ImageDiffer;
import com.schmaloogium.conformance.diff.PngRaster;
import com.schmaloogium.conformance.diff.TolerancePolicy;
import com.schmaloogium.conformance.oracle.OracleManifest;
import com.schmaloogium.conformance.oracle.OracleRecord;
import com.schmaloogium.conformance.wire.FlatDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * T2 (PHASE_2_DOC §4.2.3): same-machine pixel parity of the candidate's static SHOT samples
 * against the manually captured OptiFine G6_pre1 oracle at {@code CROSS_ENGINE}. PATH samples
 * are outside the T2 domain and are not evaluated. Outcomes per sample: {@code PASS} /
 * {@code FAIL} on the numeric verdict, {@code NO_ORACLE} when the committed manifest or its
 * record is absent (the designed non-pass until the maintainer captures the oracle),
 * {@code SKIPPED} ("timing not comparable") when the oracle's comparability is not
 * {@code ESTABLISHED} (§4.8.2), even if the numbers agree. Dual-spec packs are refused by the
 * runner before this evaluator runs ([D-P2-12]).
 */
public final class T2Evaluator {

    public record SampleOutcome(String captureKind, String captureId, long sampleOrdinal,
            TierOutcome outcome, String reason, DiffResult metrics, FlatDocument comparison) {
    }

    public record T2Result(List<SampleOutcome> samples, String oracleProvenance) {

        public TierOutcome outcome() {
            if (samples.isEmpty()) {
                return TierOutcome.NOT_ATTEMPTED;
            }
            if (samples.stream().anyMatch(s -> s.outcome() == TierOutcome.FAIL)) {
                return TierOutcome.FAIL;
            }
            if (samples.stream().anyMatch(s -> s.outcome() == TierOutcome.NO_ORACLE)) {
                return TierOutcome.NO_ORACLE;
            }
            if (samples.stream().anyMatch(s -> s.outcome() == TierOutcome.SKIPPED)) {
                return TierOutcome.SKIPPED;
            }
            return TierOutcome.PASS;
        }
    }

    public record Inputs(
            RunManifest manifest,
            String manifestSha256,
            Optional<OracleManifest> oracle,
            String oracleManifestSha256,
            Map<String, TolerancePolicy> profiles,
            Function<String, IgnoreMask> maskForCapture,
            Function<String, PngRaster> candidateRaster,
            Function<OracleRecord, PngRaster> oracleRaster,
            boolean allowUncalibrated) {
    }

    private T2Evaluator() {
    }

    public static T2Result evaluate(Inputs in) throws IOException {
        RunManifest m = in.manifest();
        T0Evaluator.T0Result t0 = T0Evaluator.evaluate(m);
        String runGate = null;
        if (!m.token("run.exitStatus").equals("COMPLETE")) {
            runGate = "run.exitStatus is " + m.token("run.exitStatus");
        } else if (t0.outcome() != TierOutcome.PASS) {
            runGate = "T0 failed";
        }
        String candidateTiming = RunTimingDocument.render(m);
        TolerancePolicy policy = TolerancePolicy.require(in.profiles(), "CROSS_ENGINE");
        List<SampleOutcome> outcomes = new ArrayList<>();
        for (RunManifest.Row image : m.family("images")) {
            String kind = image.token("captureKind");
            if (!kind.equals("SHOT")) {
                continue; // §4.2.3: no PATH oracle is required or inferred
            }
            String id = image.text("captureId");
            long ordinal = image.integer("sampleOrdinal");
            String optionState = m.token("pack.optionStateSha256");
            ComparisonDocument.Identity identity = new ComparisonDocument.Identity("T2",
                m.text("pack.id"), m.text("pack.version"), m.token("run.sceneId"), kind, id, ordinal, "");
            ComparisonDocument.Candidate candidate = new ComparisonDocument.Candidate(m.token("run.id"),
                in.manifestSha256(), optionState, image.token("pixelSha256"),
                (int) image.integer("width"), (int) image.integer("height"));
            IgnoreMask mask = in.maskForCapture().apply(id);
            String policyText = ComparisonDocument.policyText(policy, mask, "");
            Optional<OracleRecord> record = in.oracle().flatMap(o -> o.find(kind, id, ordinal, optionState));
            String noOracle = in.oracle().isEmpty() ? "no oracle manifest for this pack version and scene"
                : record.isEmpty() ? "no oracle record for key " + kind + "/" + id + "/" + ordinal
                    + " option-state " + optionState : null;
            if (noOracle == null) {
                OracleManifest o = in.oracle().get();
                if (!o.worldGenerationSha256().equals(m.token("environment.worldGenerationSha256"))
                        || !o.worldSha256().equals(m.token("environment.worldSha256"))
                        || !o.externalModSetSha256().equals(m.token("environment.externalModSetSha256"))) {
                    noOracle = "oracle world identities differ from the candidate's (stale oracle, §4.8.3)";
                }
            }
            ComparisonDocument.Reference reference;
            ComparisonDocument.Comparability comparability;
            ComparisonDocument.Result result;
            DiffResult metrics = null;
            if (noOracle != null) {
                reference = ComparisonDocument.Reference.unavailable("ORACLE");
                comparability = new ComparisonDocument.Comparability("UNAVAILABLE", noOracle,
                    candidateTiming, "", "reference unavailable");
                result = new ComparisonDocument.Result(TierOutcome.NO_ORACLE, noOracle, null);
            } else {
                OracleRecord r = record.get();
                reference = new ComparisonDocument.Reference("ORACLE", in.oracleManifestSha256(),
                    r.recordKey(), r.optionStateSha256(), r.pixelSha256(), r.width(), r.height());
                if (r.width() != candidate.width() || r.height() != candidate.height()) {
                    throw new IllegalStateException("candidate and oracle dimensions differ for "
                        + kind + "/" + id + "/" + ordinal + " (hard error, §4.6.1)");
                }
                PngRaster expected = in.oracleRaster().apply(r);
                if (!expected.pixelSha256().equals(r.pixelSha256())) {
                    throw new IllegalStateException("oracle raster for " + r.recordKey()
                        + " does not match its manifest pixelSha256");
                }
                PngRaster actual = in.candidateRaster().apply(image.text("path"));
                if (!actual.pixelSha256().equals(image.token("pixelSha256"))) {
                    throw new IllegalStateException("candidate raster " + image.text("path")
                        + " does not match its manifest pixelSha256");
                }
                metrics = ImageDiffer.compare(expected, actual, policy, mask);
                String oracleTiming = "timing.control = " + OracleRecord.CONTROL + "; timing.evidence = "
                    + r.timingEvidence() + "; timing.comparability = " + r.timingComparability()
                    + " (" + r.timingComparabilityReason() + ")";
                boolean comparable = r.timingComparability().equals("ESTABLISHED");
                comparability = new ComparisonDocument.Comparability(comparable ? "ESTABLISHED" : "UNAVAILABLE",
                    comparable ? "oracle timing independently established (§4.8.2)" : "timing not comparable",
                    candidateTiming, oracleTiming, "world identities equal; dimensions equal");
                TierOutcome outcome;
                String reason;
                if (runGate != null) {
                    outcome = TierOutcome.FAIL;
                    reason = runGate + "; " + metrics.summary();
                } else if (!comparable) {
                    outcome = TierOutcome.SKIPPED;
                    reason = "timing not comparable (" + r.timingComparabilityReason() + "); " + metrics.summary();
                } else if (!metrics.passed()) {
                    outcome = TierOutcome.FAIL;
                    reason = metrics.summary();
                } else if (!policy.calibrated() && !in.allowUncalibrated()) {
                    outcome = TierOutcome.FAIL;
                    reason = "provisional: numeric agreement at uncalibrated profile CROSS_ENGINE"
                        + " (§4.6.5 step 3 calibration required); " + metrics.summary();
                } else {
                    outcome = TierOutcome.PASS;
                    reason = (policy.calibrated() ? "" : "provisional (uncalibrated profile, allowed by the"
                        + " invoker); ") + metrics.summary();
                }
                result = new ComparisonDocument.Result(outcome, reason, metrics);
            }
            FlatDocument comparison = ComparisonDocument.build(identity, candidate, reference, policyText,
                comparability, result, List.of());
            outcomes.add(new SampleOutcome(kind, id, ordinal, result.outcome(), result.reason(), metrics,
                comparison));
        }
        return new T2Result(outcomes, in.oracle().map(OracleManifest::provenance).orElse("(no oracle)"));
    }
}
