// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.harness;

import com.schmaloogium.conformance.FixtureInternalPackSource;
import com.schmaloogium.conformance.SyntheticProfiles;
import com.schmaloogium.conformance.golden.GoldenUpdatePolicy;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The committed golden corpus (§4.11.5/§4.11.6): every hermetic pack we own pins a
 * source-free provenance golden. Default runs compare only; {@code -PupdateGoldens}
 * rewrites and then fails the build so the diff always gets human review.
 */
class GoldenCorpusTest {

    /** Every hermetic pack; {@code mp-broken} is excluded because a golden pins a
     * successful inspection and that pack's contract is to fail it. */
    private static final List<String> CORPUS = List.of("internal-default",
        "mp-minimal", "mp-buffers", "mp-shadow", "mp-includes", "mp-options",
        "mp-properties", "mp-dimension");

    @Test
    void committedGoldenCorpusMatchesBehaviour() {
        GoldenUpdatePolicy policy = GoldenUpdatePolicy.fromSystemProperty();
        List<String> changed = new ArrayList<>();
        for (String name : CORPUS) {
            Path golden = Path.of("src/test/resources/golden", name + ".golden");
            FrontEndGoldenRun.Result result = FrontEndGoldenRun.runInternal(new FrontEndSession(),
                new FixtureInternalPackSource(
                    Path.of("src/test/resources/packs").resolve(name), "shaders"),
                new com.schmaloogium.conformance.golden.GoldenProjectionAdapter.GoldenInputs(
                    name, "1.0.0", java.util.Optional.empty(), "synthetic-gl33",
                    SyntheticProfiles.syntheticGl33(), FrontEndSession.ENGINE_VERSION),
                SyntheticProfiles.syntheticGl33(), policy, golden);
            switch (policy) {
                case COMPARE_ONLY -> assertEquals(FrontEndGoldenRun.Kind.MATCHED, result.kind(),
                    () -> name + " drifted from its committed golden; regenerate with"
                        + " ./gradlew :conformance:test -PupdateGoldens and review the diff:"
                        + " " + result.differences());
                case UPDATE_AND_FAIL -> {
                    if (result.kind() == FrontEndGoldenRun.Kind.UPDATED) {
                        changed.add(name);
                    } else {
                        assertTrue(result.kind() == FrontEndGoldenRun.Kind.MATCHED,
                            () -> name + ": unexpected " + result.kind());
                    }
                }
            }
        }
        assertTrue(changed.isEmpty(),
            () -> "goldens rewritten for " + changed + "; review the diff and commit it");
    }
}
