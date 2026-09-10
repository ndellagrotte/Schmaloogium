// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.registry.FixedUnitSamplerConflict;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.uniforms.support.UniformFixture;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8 v0.1 headless cases for the sampler step of the afterBind sequence: sampler unit
 * re-point uploads precede all built-in uploads, plans are keyed by (stage, band,
 * layout fingerprints) and reused without re-upload, invalid plans degrade once and
 * stick, and the real Phase 5 resolver integrates inside the 16-row unit range.
 */
class SamplerPlanTest {

    @Test
    void samplerRepointUploadsBeforeBuiltins() {
        UniformFixture.Harness harness = UniformFixture.harness(
                UniformFixture.configuration(true),
                UniformFixture.fixedUnits(List.of(
                        new com.schmaloogium.engine.buffers.ResolvedSamplerBinding(
                                "texture", (DeclaredGlslType.Sampler) UniformFixture.sampler2d(), 3))));
        harness.program(Map.of("moonPhase", UniformFixture.int1()),
                List.of(UniformFixture.samplerDeclaration("texture", 0)),
                StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE);
        harness.beginFrame(1L, 0L);
        harness.activate();

        var calls = harness.uploads();
        assertEquals(harness.programLabel + ".texture", String.valueOf(calls.get(0).args().get(0)));
        assertEquals(Integer.valueOf(3), (Integer) calls.get(0).args().get(1));
        assertEquals(harness.programLabel + ".moonPhase", String.valueOf(calls.get(1).args().get(0)));
    }

    @Test
    void planReuseAcrossActivations() {
        UniformFixture.Harness harness = UniformFixture.harness(
                UniformFixture.configuration(true),
                UniformFixture.fixedUnits(List.of(
                        new com.schmaloogium.engine.buffers.ResolvedSamplerBinding(
                                "texture", (DeclaredGlslType.Sampler) UniformFixture.sampler2d(), 3))));
        harness.program(Map.of("moonPhase", UniformFixture.int1()),
                List.of(UniformFixture.samplerDeclaration("texture", 0)),
                StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE);
        harness.beginFrame(1L, 0L);
        harness.activate();
        int uploadsAfterFirst = harness.uploads().size();

        harness.beginFrame(2L, 0L);
        harness.activate();
        assertEquals(uploadsAfterFirst,
                harness.uploads().size(),
                "cached plan with equal units: no duplicate sampler upload");
        long textureUploads = harness.uploads().stream()
                .filter(call -> String.valueOf(call.args().get(0)).endsWith(".texture"))
                .count();
        assertEquals(1L, textureUploads, "one re-point per plan adoption, not per frame");
    }

    @Test
    void invalidPlanDegradesAndSticks() {
        var conflict = new SamplerLayoutValidation.ConflictingTypes(
                List.of(new FixedUnitSamplerConflict(0,
                        List.of(UniformFixture.samplerDeclaration("texture", 0)))),
                List.of());
        UniformFixture.Harness harness = UniformFixture.harness(
                UniformFixture.configuration(true),
                UniformFixture.failingResolver(conflict));
        harness.program(Map.of("moonPhase", UniformFixture.int1()),
                List.of(UniformFixture.samplerDeclaration("texture", 0)),
                StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE);
        harness.beginFrame(1L, 0L);
        var results = harness.activate();

        assertEquals(com.schmaloogium.engine.registry.BarrierParticipantResult.Degraded.class,
                results.get(0).getClass());
        assertEquals("phase6.sampler.layout.conflict",
                ((com.schmaloogium.engine.registry.BarrierParticipantResult.Degraded)
                        results.get(0)).diagnosticId());
        assertEquals(1L, harness.diagnostics.count("phase6.sampler.layout.conflict"));
        assertTrue(harness.uploads().stream().map(call -> String.valueOf(call.args().get(0)))
                        .noneMatch(name -> name.endsWith(".texture")),
                "degraded sampler step uploads no re-point");
        harness.activate();
        assertEquals(1L, harness.diagnostics.count("phase6.sampler.layout.conflict"),
                "the invalid plan is cached; no repeated resolution or diagnostics");
    }

    @Test
    void realPhaseFiveResolverProducesUnitUpload() {
        UniformFixture.Harness harness = UniformFixture.harness(
                UniformFixture.configuration(true),
                FixedSamplerPolicies.resolver());
        harness.program(Map.of("moonPhase", UniformFixture.int1()),
                List.of(UniformFixture.samplerDeclaration("texture", 0)),
                StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE,
                FixedSamplerPolicies.appB3Fingerprint());
        harness.beginFrame(1L, 0L);
        var results = harness.activate();
        assertEquals(com.schmaloogium.engine.registry.BarrierParticipantResult.Continue.class,
                results.get(0).getClass());

        var calls = harness.uploads();
        assertEquals(harness.programLabel + ".texture", String.valueOf(calls.get(0).args().get(0)));
        int unit = (Integer) calls.get(0).args().get(1);
        assertTrue(unit >= 0 && unit < 16, "unit inside the 16-row protocol range");
    }

    @Test
    void noSamplersProducesNoSamplerUploads() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("moonPhase", UniformFixture.int1()), List.of(),
                StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE);
        harness.beginFrame(1L, 0L);
        harness.activate();
        assertEquals(List.of(harness.programLabel + ".moonPhase"),
                harness.uploads().stream()
                        .map(call -> String.valueOf(call.args().get(0))).toList());
    }

    @Test
    void declaredGlslTypeSamplerShapeIsAccepted() {
        // The engine-side seam: Phase 6 maps only the declared sampler2d shape; the
        // pack-facing spellings stay Phase 5's concern (R7-10).
        DeclaredGlslType shape = UniformFixture.sampler2d();
        assertEquals(DeclaredGlslType.Sampler.class, shape.getClass());
    }
}
