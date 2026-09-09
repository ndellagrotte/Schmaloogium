// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.harness;

import com.schmaloogium.conformance.FixtureInternalPackSource;
import com.schmaloogium.conformance.SyntheticProfiles;
import com.schmaloogium.conformance.golden.GoldenComparer;
import com.schmaloogium.conformance.golden.GoldenDocument;
import com.schmaloogium.conformance.golden.GoldenProjectionAdapter;
import com.schmaloogium.conformance.golden.GoldenUpdatePolicy;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.PackInspectionResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The golden-run driver (§4.11.5): compare-only by default, opt-in update, and
 * byte-deterministic renders that make the committed goldens meaningful.
 */
class FrontEndGoldenRunTest {

    private static GoldenProjectionAdapter.GoldenInputs inputs(String packId) {
        return new GoldenProjectionAdapter.GoldenInputs(packId, "1.0.0", Optional.empty(),
            "synthetic-gl33", SyntheticProfiles.syntheticGl33(),
            FrontEndSession.ENGINE_VERSION);
    }

    private static FrontEndSession session() {
        return new FrontEndSession();
    }

    @Test
    void updateThenCompareIsStable(@TempDir Path tmp) {
        Path golden = tmp.resolve("internal-default.golden");
        FrontEndSession session = session();
        FixtureInternalPackSource source = new FixtureInternalPackSource(
            Path.of("src/test/resources/packs/internal-default"), "shaders");

        FrontEndGoldenRun.Result update = FrontEndGoldenRun.runInternal(session, source,
            inputs("internal-default"), SyntheticProfiles.syntheticGl33(),
            GoldenUpdatePolicy.UPDATE_AND_FAIL, golden);
        assertEquals(FrontEndGoldenRun.Kind.UPDATED, update.kind());
        assertTrue(Files.isRegularFile(golden));

        FrontEndGoldenRun.Result compare = FrontEndGoldenRun.runInternal(session, source,
            inputs("internal-default"), SyntheticProfiles.syntheticGl33(),
            GoldenUpdatePolicy.COMPARE_ONLY, golden);
        assertEquals(FrontEndGoldenRun.Kind.MATCHED, compare.kind());
        assertTrue(compare.differences().isEmpty());
    }

    @Test
    void differentPackProducesNamedDifferences(@TempDir Path tmp) throws Exception {
        Path golden = tmp.resolve("internal-default.golden");
        FixtureInternalPackSource defaultSource = new FixtureInternalPackSource(
            Path.of("src/test/resources/packs/internal-default"), "shaders");
        FrontEndGoldenRun.runInternal(session(), defaultSource, inputs("internal-default"),
            SyntheticProfiles.syntheticGl33(), GoldenUpdatePolicy.UPDATE_AND_FAIL, golden);

        FixtureInternalPackSource minimalSource = new FixtureInternalPackSource(
            Path.of("src/test/resources/packs/mp-minimal"), "shaders");
        FrontEndGoldenRun.Result result = FrontEndGoldenRun.runInternal(session(), minimalSource,
            inputs("mp-minimal"), SyntheticProfiles.syntheticGl33(),
            GoldenUpdatePolicy.COMPARE_ONLY, golden);
        assertEquals(FrontEndGoldenRun.Kind.DIFFERENT, result.kind());
        assertFalse(result.differences().isEmpty());
        assertTrue(result.differences().stream()
            .anyMatch(d -> d.kind() == GoldenComparer.Difference.Kind.CHANGED_VALUE));
    }

    @Test
    void compareOnlyAgainstMissingGoldenFailsLoudly(@TempDir Path tmp) {
        FrontEndGoldenRun.Result result = FrontEndGoldenRun.runInternal(session(),
            new FixtureInternalPackSource(
                Path.of("src/test/resources/packs/internal-default"), "shaders"),
            inputs("internal-default"), SyntheticProfiles.syntheticGl33(),
            GoldenUpdatePolicy.COMPARE_ONLY, tmp.resolve("absent.golden"));
        assertEquals(FrontEndGoldenRun.Kind.DIFFERENT, result.kind());
        assertTrue(result.differences().stream()
            .anyMatch(d -> d.kind() == GoldenComparer.Difference.Kind.MISSING_SECTION));
    }

    @Test
    void rendersAreByteIdenticalAcrossRuns() {
        FixtureInternalPackSource source = new FixtureInternalPackSource(
            Path.of("src/test/resources/packs/internal-default"), "shaders");
        GoldenDocument first = new GoldenProjectionAdapter().project(
            inspected(source), inputs("internal-default"));
        GoldenDocument second = new GoldenProjectionAdapter().project(
            inspected(source), inputs("internal-default"));
        assertEquals(first.render(), second.render());
    }

    private PackInspectionResult.Inspected inspected(FixtureInternalPackSource source) {
        PackInspectionResult result = session().inspectInternal(source,
            SyntheticProfiles.syntheticGl33());
        assertTrue(result instanceof PackInspectionResult.Inspected,
            "front end rejected the fixture: " + session().diagnostics());
        return (PackInspectionResult.Inspected) result;
    }
}
