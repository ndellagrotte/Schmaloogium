// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import com.schmaloogium.engine.uniforms.spi.TickUniformSample;
import com.schmaloogium.engine.uniforms.support.UniformFixture;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8 v0.1 headless cases: built-in activation plans build in catalog (name) order,
 * upload exactly the declared cells whose canonical value changed, cache absent
 * locations after one lookup, withhold pending-producer values, gate current gbuffer
 * matrices on a capture for the latest frame, and degrade without GL work after
 * retirement. Evidence comes from the recorded call log; no GL context.
 */
class BuiltInUploadSequenceTest {

    private static List<String> names(UniformFixture.Harness harness) {
        return harness.uploads().stream()
                .map(call -> String.valueOf(call.args().get(0)))
                .map(name -> name.substring(name.indexOf('.') + 1))
                .toList();
    }

    @Test
    void uploadsInCatalogOrderWithExactValuesThenSkipsEqual() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("worldTime", UniformFixture.float1(),
                        "moonPhase", UniformFixture.int1(),
                        "cameraPosition", UniformFixture.vec3(),
                        "far", UniformFixture.float1()),
                List.of(), com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        harness.platform.tick = new TickUniformSample(UniformFixture.WORLD, 5L, 600L, 5, 0.5f);

        assertEquals(FrameBeginResult.ACCEPTED, harness.beginFrame(1L, 5L));
        harness.activate();

        // Catalog order is Unicode name order: cameraPosition, far, moonPhase, worldTime.
        assertEquals(List.of("cameraPosition", "far", "moonPhase", "worldTime"),
                names(harness));
        var calls = harness.uploads();
        java.util.Map<String, Object> uploaded = new java.util.LinkedHashMap<>();
        for (var call : calls) {
            String name = String.valueOf(call.args().get(0));
            uploaded.put(name.substring(name.indexOf('.') + 1), call.args().get(1));
        }
        assertEquals(Integer.valueOf(5), (Integer) uploaded.get("moonPhase"));
        assertEquals(Float.valueOf(1000.0f), (Float) uploaded.get("far"));

        // Same cells re-activated: every upload skips.
        int before = calls.size();
        harness.activate();
        assertEquals(before, harness.uploads().size());
    }

    @Test
    void changedCellsReuploadOnNextActivation() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("moonPhase", UniformFixture.int1()), List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        harness.platform.tick = new TickUniformSample(UniformFixture.WORLD, 0L, 700L, 5, 0.5f);
        harness.beginFrame(1L, 0L);
        harness.activate();

        harness.platform.tick = new TickUniformSample(UniformFixture.WORLD, 10L, 700L, 6,
                0.5f);
        harness.beginFrame(2L, 10L);
        int before = harness.uploads().size();
        harness.activate();
        assertEquals(1, harness.uploads().size() - before,
                "only the changed tick cell uploads");
        var last = harness.uploads().get(harness.uploads().size() - 1);
        assertEquals(Integer.valueOf(6), (Integer) last.args().get(1));
    }

    @Test
    void absentLocationCachedAsAbsentAndNeverRequeried() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.responses.uniformAbsent("far");
        harness.program(Map.of("far", UniformFixture.float1()), List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        harness.beginFrame(1L, 0L);

        int locatesBefore = harness.access.locateCalls;
        harness.activate();
        assertTrue(harness.uploads().isEmpty(), "absent location must never upload");
        assertEquals(locatesBefore + 1, harness.access.locateCalls);

        harness.activate();
        assertEquals(locatesBefore + 1, harness.access.locateCalls,
                "absent marker is cached, never re-queried");
    }

    @Test
    void pendingProducerValuesAreWithheldWithOneWarning() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("heldItemId", UniformFixture.int1()), List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        harness.beginFrame(1L, 0L);
        harness.activate();
        harness.activate();

        assertTrue(harness.uploads().isEmpty(),
                "a v0.3 producer value is withheld, never a neutral upload");
        assertEquals(1L, harness.diagnostics.count("phase6.producer.pending.heldItemId"),
                "once-per-pack warning");
    }

    @Test
    void currentMatricesRequireACaptureForTheLatestFrame() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(new LinkedHashMap<>(Map.of(
                        "gbufferModelView", UniformFixture.mat4(),
                        "gbufferPreviousModelView", UniformFixture.mat4())),
                List.of(), com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        harness.beginFrame(1L, 0L);
        harness.activate();
        assertTrue(harness.uploads().isEmpty(), "no capture: matrix cells stay invalid");

        harness.events.captureGbufferMatrices(1L,
                com.schmaloogium.engine.uniforms.Matrix4Value.identity(),
                com.schmaloogium.engine.uniforms.Matrix4Value.identity());
        int before = harness.uploads().size();
        harness.activate();
        // The first valid capture installs the current matrix; previous stays invalid
        // until the next frame (mirroring the previous-camera rule).
        var uploaded = harness.uploads().stream()
                .map(call -> String.valueOf(call.args().get(0)))
                .map(name -> name.substring(name.indexOf('.') + 1))
                .filter(n -> n.startsWith("gbuffer")).toList();
        assertEquals(List.of("gbufferModelView"), uploaded);

        // New frame without a capture: the current matrix does not upload; the
        // previous-current rotation installs the prior frame's capture instead.
        harness.beginFrame(2L, 0L);
        harness.activate();
        assertEquals(before + 2, harness.uploads().size(),
                "capture upload plus the frame-two rotation upload");
        assertEquals("gbufferPreviousModelView",
                String.valueOf(harness.uploads().get(harness.uploads().size() - 1)
                        .args().get(0)).substring(
                        String.valueOf(harness.uploads()
                                .get(harness.uploads().size() - 1).args().get(0))
                                .indexOf('.') + 1));
    }

    @Test
    void retiredActivationDegradesWithoutGl() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("moonPhase", UniformFixture.int1()), List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        harness.beginFrame(1L, 0L);
        assertInstanceOf(UniformRetirementResult.Retired.class,
                harness.runtime.retire(UniformRetirementReason.SHUTDOWN));
        int glCalls = harness.device.log().calls().size();
        var results = harness.activate();

        assertEquals(com.schmaloogium.engine.registry.BarrierParticipantResult.Degraded.class,
                results.get(1).getClass());
        assertEquals("phase6.runtime.retired",
                ((com.schmaloogium.engine.registry.BarrierParticipantResult.Degraded)
                        results.get(1)).diagnosticId());
        assertEquals(glCalls, harness.device.log().calls().size(),
                "retired runtime performs no GL work");
    }
}
