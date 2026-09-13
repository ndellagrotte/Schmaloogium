// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.tier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.conformance.Manifests;
import com.schmaloogium.conformance.capture.RunManifest;
import com.schmaloogium.conformance.diff.IgnoreMask;
import com.schmaloogium.conformance.diff.PngRaster;
import com.schmaloogium.conformance.diff.TolerancePolicy;
import com.schmaloogium.conformance.oracle.OracleManifest;
import com.schmaloogium.conformance.oracle.OracleRecord;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/** §4.2.3: SHOT-only parity at CROSS_ENGINE; NO_ORACLE and SKIPPED are the non-pass answers. */
class T2EvaluatorTest {

    private static final String H = "0".repeat(64);

    private static PngRaster raster(int fill) {
        int[] px = new int[128 * 128];
        Arrays.fill(px, fill);
        return new PngRaster(128, 128, px);
    }

    private static RunManifest manifest(PngRaster candidate) {
        return Manifests.with(Manifests.valid(), b -> {
            b.set("images.0.pixelSha256", new RunManifest.Value.Token(candidate.pixelSha256()));
            b.set("images.1.pixelSha256", new RunManifest.Value.Token(candidate.pixelSha256()));
            return b;
        });
    }

    private static OracleManifest oracle(RunManifest m, PngRaster image, String comparability) {
        RunManifest.Row first = m.family("images").get(0);
        List<OracleRecord> records = new java.util.ArrayList<>();
        for (RunManifest.Row row : m.family("images")) {
            if (!row.token("captureKind").equals("SHOT")) {
                continue;
            }
            records.add(new OracleRecord("SHOT", row.text("captureId"), row.integer("sampleOrdinal"),
                m.token("pack.optionStateSha256"), image.pixelSha256(), 128, 128,
                "operator waited 60 s at the pose; two screenshots 10 s apart hashed identically",
                comparability, comparability.equals("ESTABLISHED")
                    ? "static shot, pixels time-insensitive over the sampled range" : "no independent timing evidence"));
        }
        return new OracleManifest(m.text("pack.id"), m.text("pack.version"), m.token("run.sceneId"),
            "HD_U_G6_pre1", "NVIDIA GeForce RTX 3080", "610.57.04", "2026-09-13", "nick",
            m.token("environment.worldGenerationSha256"), m.token("environment.worldSha256"),
            m.token("environment.externalModSetSha256"), records);
    }

    private static Map<String, TolerancePolicy> profiles(boolean calibrated) {
        return TolerancePolicy.parseFile(com.schmaloogium.conformance.diff.ImageDifferTest.PROFILES
            + "[profile CROSS_ENGINE]\nchannelTolerance = 6\nmaxDifferingFraction = 0.02\nmaxDelta = 48\n"
            + "maxRmse = 6.0\nmaxClusterArea = 4096\nmaxClusters = 16384\ncalibratedOn = "
            + (calibrated ? "\"2026-09-13, gpu, driver\"" : "\"\"") + "\n");
    }

    private static T2Evaluator.Inputs inputs(RunManifest m, Optional<OracleManifest> oracle, PngRaster candidate,
            PngRaster expected, boolean calibrated, boolean allowUncalibrated) {
        return new T2Evaluator.Inputs(m, H, oracle, H, profiles(calibrated), id -> IgnoreMask.NONE,
            path -> candidate, r -> expected, allowUncalibrated);
    }

    @Test
    void identicalOracleWithEstablishedTimingPasses() throws IOException {
        PngRaster img = raster(0xFF336699);
        RunManifest m = manifest(img);
        T2Evaluator.T2Result r = T2Evaluator.evaluate(inputs(m, Optional.of(oracle(m, img, "ESTABLISHED")),
            img, img, true, false));
        assertEquals(TierOutcome.PASS, r.outcome(), r.samples().toString());
        assertTrue(r.samples().stream().allMatch(s -> s.captureKind().equals("SHOT")), "PATH samples are outside T2");
        assertTrue(r.oracleProvenance().startsWith("OptiFine HD_U_G6_pre1"));
        assertEquals("T2", r.samples().get(0).comparison().token("kind"));
    }

    @Test
    void missingOracleIsNoOracleAndNeverPass() throws IOException {
        PngRaster img = raster(0xFF336699);
        RunManifest m = manifest(img);
        T2Evaluator.T2Result r = T2Evaluator.evaluate(inputs(m, Optional.empty(), img, img, true, false));
        assertEquals(TierOutcome.NO_ORACLE, r.outcome());
        assertEquals("(no oracle)", r.oracleProvenance());
    }

    @Test
    void unestablishedTimingIsSkippedEvenWhenTheNumbersAgree() throws IOException {
        PngRaster img = raster(0xFF336699);
        RunManifest m = manifest(img);
        T2Evaluator.T2Result r = T2Evaluator.evaluate(inputs(m, Optional.of(oracle(m, img, "UNAVAILABLE")),
            img, img, true, false));
        assertEquals(TierOutcome.SKIPPED, r.outcome());
        assertTrue(r.samples().get(0).reason().startsWith("timing not comparable"));
    }

    @Test
    void differingOracleFailsAndUncalibratedProfileIsProvisional() throws IOException {
        PngRaster img = raster(0xFF336699);
        PngRaster other = raster(0xFF993366);
        RunManifest m = manifest(img);
        assertEquals(TierOutcome.FAIL, T2Evaluator.evaluate(inputs(m, Optional.of(oracle(m, other, "ESTABLISHED")),
            img, other, true, false)).outcome());
        T2Evaluator.T2Result provisional = T2Evaluator.evaluate(inputs(m,
            Optional.of(oracle(m, img, "ESTABLISHED")), img, img, false, false));
        assertEquals(TierOutcome.FAIL, provisional.outcome());
        assertTrue(provisional.samples().get(0).reason().startsWith("provisional"));
        assertEquals(TierOutcome.PASS, T2Evaluator.evaluate(inputs(m, Optional.of(oracle(m, img, "ESTABLISHED")),
            img, img, false, true)).outcome(), "allowed uncalibrated");
    }

    @Test
    void staleOracleWorldIdentitiesAreNoOracle() throws IOException {
        PngRaster img = raster(0xFF336699);
        RunManifest m = manifest(img);
        OracleManifest o = oracle(m, img, "ESTABLISHED");
        OracleManifest stale = new OracleManifest(o.packId(), o.packVersion(), o.sceneId(), o.optifineBuild(), o.gpu(),
            o.driver(), o.capturedOn(), o.operator(), "1".repeat(64), o.worldSha256(), o.externalModSetSha256(),
            o.records());
        T2Evaluator.T2Result r = T2Evaluator.evaluate(inputs(m, Optional.of(stale), img, img, true, false));
        assertEquals(TierOutcome.NO_ORACLE, r.outcome());
        assertTrue(r.samples().get(0).reason().contains("stale oracle"));
    }
}
