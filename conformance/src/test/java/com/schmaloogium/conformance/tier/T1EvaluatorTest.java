// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.tier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.conformance.Manifests;
import com.schmaloogium.conformance.baseline.BaselineManifest;
import com.schmaloogium.conformance.baseline.BaselineRecord;
import com.schmaloogium.conformance.capture.RunManifest;
import com.schmaloogium.conformance.diff.IgnoreMask;
import com.schmaloogium.conformance.diff.PngRaster;
import com.schmaloogium.conformance.diff.TolerancePolicy;
import com.schmaloogium.conformance.wire.FlatDocument;
import com.schmaloogium.conformance.wire.Hashes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/** TierEvaluatorTest (T1 part) + ComparisonDocumentTest: PASS / FAIL / NO_BASELINE / escalation / gates. */
class T1EvaluatorTest {

    static final String H = "3f7a1c9e2b8d4f60a5c3e71d9b0f4a2c6e8d13579bdf2468ace013579bdf2468";
    static final String MACHINE = "nvidia-gl46 / x / linux-amd64";

    static PngRaster raster(int fill) {
        int[] px = new int[128 * 128];
        Arrays.fill(px, fill);
        return new PngRaster(128, 128, px);
    }

    /** The fixture manifest with its two images' hashes set to the raster we hand back. */
    static RunManifest manifest(PngRaster candidate) {
        return Manifests.with(Manifests.valid(), b -> {
            b.set("images.0.pixelSha256", new RunManifest.Value.Token(candidate.pixelSha256()));
            b.set("images.1.pixelSha256", new RunManifest.Value.Token(candidate.pixelSha256()));
            return b;
        });
    }

    static BaselineRecord record(RunManifest m, long ordinal, PngRaster baseline, String machine, String sceneHash) {
        return new BaselineRecord("SHOT", m.family("images").get(0).text("captureId"), ordinal,
            m.token("pack.optionStateSha256"),
            baseline.pixelSha256(), 128, 128, "SAME_MACHINE", machine, "nick", "2026-09-12", "RUN-T1-APPROVE-x", H,
            sceneHash, m.token("environment.worldGenerationSha256"), m.token("environment.worldSha256"),
            m.token("environment.externalModSetSha256"), "schmaloogium", H, H);
    }

    static T1Evaluator.Inputs inputs(RunManifest m, Optional<BaselineManifest> baseline, PngRaster candidate,
            PngRaster expected, boolean allowUncalibrated) {
        Map<String, TolerancePolicy> profiles = TolerancePolicy.parseFile(
            com.schmaloogium.conformance.diff.ImageDifferTest.PROFILES
            + "[profile CROSS_DRIVER]\nchannelTolerance = 3\nmaxDifferingFraction = 0.005\nmaxDelta = 24\n"
            + "maxRmse = 3.0\nmaxClusterArea = 512\nmaxClusters = 4096\ncalibratedOn = \"\"\n");
        return new T1Evaluator.Inputs(m, H, MACHINE, baseline, H, profiles, "SAME_MACHINE",
            id -> IgnoreMask.NONE, path -> candidate, r -> expected, allowUncalibrated);
    }

    @Test
    void identicalBaselinePassesWhenUncalibratedIsAllowedAndWritesComparisons() throws IOException {
        PngRaster img = raster(0xFF336699);
        RunManifest m = manifest(img);
        BaselineManifest baseline = new BaselineManifest("internal-default", "1.0.0", "synthetic-scene",
            List.of(record(m, 1, img, MACHINE, m.token("run.sceneHash")), record(m, 2, img, MACHINE, m.token("run.sceneHash"))));
        T1Evaluator.T1Result r = T1Evaluator.evaluate(inputs(m, Optional.of(baseline), img, img, true));
        assertEquals(TierOutcome.PASS, r.outcome(), r.samples().toString());
        FlatDocument c = r.samples().get(0).comparison();
        assertEquals("T1", c.token("kind"));
        assertEquals("PASS", c.token("result.outcome"));
        assertEquals("ESTABLISHED", c.token("comparability.status"));
        assertTrue(c.bool("result.metricsAvailable"));
        String policyText = c.text("policy.text");
        assertEquals(Hashes.sha256HexOf(policyText), c.token("policy.sha256"));
        FlatDocument.parse(policyText, ComparisonDocument.POLICY_SCHEMA_LINE);
        FlatDocument comparability = FlatDocument.parse(c.text("comparability.text"),
            ComparisonDocument.COMPARABILITY_SCHEMA_LINE);
        FlatDocument.parse(comparability.text("candidateTiming"), RunTimingDocument.SCHEMA_LINE);
        // provisional: uncalibrated profile refused without the invoker's explicit allowance
        assertEquals(TierOutcome.FAIL, T1Evaluator.evaluate(inputs(m, Optional.of(baseline), img, img, false)).outcome());
    }

    @Test
    void differingImageFailsAndMissingBaselineIsNoBaselineNeverPass() throws IOException {
        PngRaster img = raster(0xFF336699);
        PngRaster other = raster(0xFF996633);
        RunManifest m = manifest(img);
        BaselineManifest baseline = new BaselineManifest("internal-default", "1.0.0", "synthetic-scene",
            List.of(record(m, 1, other, MACHINE, m.token("run.sceneHash")), record(m, 2, other, MACHINE, m.token("run.sceneHash"))));
        T1Evaluator.T1Result fail = T1Evaluator.evaluate(inputs(m, Optional.of(baseline), img, other, true));
        assertEquals(TierOutcome.FAIL, fail.outcome());
        T1Evaluator.T1Result none = T1Evaluator.evaluate(inputs(m, Optional.empty(), img, img, true));
        assertEquals(TierOutcome.NO_BASELINE, none.outcome());
        FlatDocument c = none.samples().get(0).comparison();
        assertEquals("UNAVAILABLE", c.token("comparability.status"));
        assertEquals("", c.text("reference.pixelSha256"));
        assertEquals(0, c.integer("reference.width"));
        // a record for only ordinal 1 leaves ordinal 2 NO_BASELINE
        BaselineManifest partial = new BaselineManifest("internal-default", "1.0.0", "synthetic-scene",
            List.of(record(m, 1, img, MACHINE, m.token("run.sceneHash"))));
        assertEquals(TierOutcome.NO_BASELINE, T1Evaluator.evaluate(inputs(m, Optional.of(partial), img, img, true)).outcome());
    }

    @Test
    void invalidationAndEscalationFollowSection474() throws IOException {
        PngRaster img = raster(0xFF336699);
        RunManifest m = manifest(img);
        String otherScene = H.replace('3', '5');
        BaselineManifest stale = new BaselineManifest("internal-default", "1.0.0", "synthetic-scene",
            List.of(record(m, 1, img, MACHINE, otherScene), record(m, 2, img, MACHINE, otherScene)));
        T1Evaluator.T1Result r = T1Evaluator.evaluate(inputs(m, Optional.of(stale), img, img, true));
        assertEquals(TierOutcome.NO_BASELINE, r.outcome());
        assertTrue(r.samples().get(0).reason().contains("sceneSha256"));
        BaselineManifest otherMachine = new BaselineManifest("internal-default", "1.0.0", "synthetic-scene",
            List.of(record(m, 1, img, "amd-gl46 / y / linux-amd64", m.token("run.sceneHash")),
                record(m, 2, img, "amd-gl46 / y / linux-amd64", m.token("run.sceneHash"))));
        T1Evaluator.T1Result escalated = T1Evaluator.evaluate(inputs(m, Optional.of(otherMachine), img, img, true));
        assertEquals(TierOutcome.PASS, escalated.outcome(), escalated.samples().toString());
        assertEquals("CROSS_DRIVER", escalated.samples().get(0).comparison().token("policy.profile"));
    }

    @Test
    void aT0FailingRunCannotPassT1AndDimensionMismatchIsHard() throws IOException {
        PngRaster img = raster(0xFF336699);
        RunManifest failingT0 = Manifests.with(manifest(img), b -> b.set("run.timedOut", new RunManifest.Value.Bool(true)));
        BaselineManifest baseline = new BaselineManifest("internal-default", "1.0.0", "synthetic-scene",
            List.of(record(failingT0, 1, img, MACHINE, failingT0.token("run.sceneHash")),
                record(failingT0, 2, img, MACHINE, failingT0.token("run.sceneHash"))));
        assertEquals(TierOutcome.FAIL, T1Evaluator.evaluate(inputs(failingT0, Optional.of(baseline), img, img, true)).outcome());
        RunManifest m = manifest(img);
        BaselineRecord wrongSize = new BaselineRecord("SHOT", m.family("images").get(0).text("captureId"), 1,
            m.token("pack.optionStateSha256"),
            img.pixelSha256(), 64, 64, "SAME_MACHINE", MACHINE, "nick", "2026-09-12", "RUN-T1-APPROVE-x", H,
            m.token("run.sceneHash"), m.token("environment.worldGenerationSha256"), m.token("environment.worldSha256"),
            m.token("environment.externalModSetSha256"), "schmaloogium", H, H);
        BaselineManifest sized = new BaselineManifest("internal-default", "1.0.0", "synthetic-scene", List.of(wrongSize));
        assertThrows(IllegalStateException.class, () -> T1Evaluator.evaluate(inputs(m, Optional.of(sized), img, img, true)));
    }
}
