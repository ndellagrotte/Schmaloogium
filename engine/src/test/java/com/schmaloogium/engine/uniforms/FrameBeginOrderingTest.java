// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import com.schmaloogium.engine.uniforms.runtime.UniformValue;
import com.schmaloogium.engine.uniforms.runtime.UniformCore;
import com.schmaloogium.engine.uniforms.spi.CenterDepthResult;
import com.schmaloogium.engine.uniforms.support.UniformFixture;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8 v0.1 headless cases for frame-begin ordering (§4.6): the terminal-state guard
 * runs before any sampling or GL work, center depth reads exactly once with the
 * half-pixel offset when configuration requires it and a program declares it,
 * unavailable reads retain the prior accumulator, camera rotation primes the
 * previous-camera cell, and world reset forces same-frame previous-current equality.
 */
class FrameBeginOrderingTest {

    private static final com.schmaloogium.engine.registry.StageId STAGE =
            com.schmaloogium.engine.registry.StageId.GBUFFERS;
    private static final com.schmaloogium.engine.registry.StageBand BAND =
            com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE;

    private static UniformValue cell(UniformFixture.Harness harness, String name) {
        return ((UniformCore) harness.runtime).cellValue(name);
    }

    @Test
    void centerDepthReadsOncePerFrameWithHalfPixelOffset() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("centerDepthSmooth", UniformFixture.float1()), List.of(),
                STAGE, BAND);
        harness.beginFrame(1L, 0L);

        assertEquals(1, harness.center.requests.size(), "one read, before resize/clear");
        var request = harness.center.requests.get(0);
        assertEquals(400, request.pixelX());
        assertEquals(300, request.pixelY());
        assertEquals(Float.valueOf(0.5f),
                ((UniformValue.F) cell(harness, "centerDepthSmooth")).x());
    }

    @Test
    void centerDepthReadsOnlyWhenRequiredOrDeclared() {
        UniformFixture.Harness harness = UniformFixture.harness(
                UniformFixture.configuration(false), UniformFixture.fixedUnits(List.of()));
        harness.program(Map.of("centerDepthSmooth", UniformFixture.float1()), List.of(),
                STAGE, BAND);
        harness.beginFrame(1L, 0L);
        assertTrue(harness.center.requests.isEmpty(),
                "not required by configuration: no source call");

        UniformFixture.Harness undeclared = UniformFixture.harness();
        undeclared.beginFrame(1L, 0L);
        assertTrue(undeclared.center.requests.isEmpty(),
                "no program declares centerDepthSmooth: no source call");
    }

    @Test
    void emptyFramebufferSkipsTheSourceCall() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("centerDepthSmooth", UniformFixture.float1()), List.of(),
                STAGE, BAND);
        var zero = new FrameBeginInput(UniformFixture.GENERATION, 1L, UniformFixture.WORLD,
                0L, 10.0d, 0.05f, 800, 600, 0, 0);
        assertEquals(FrameBeginResult.ACCEPTED, harness.runtime.beginFrame(zero));
        assertTrue(harness.center.requests.isEmpty());
        assertEquals(1L, harness.diagnostics.count("phase6.center.depth.empty.framebuffer"));
        assertNull(cell(harness, "centerDepthSmooth"),
                "no sample: the cell stays invalid, never zero");
    }

    @Test
    void unavailableReadRetainsThePriorAccumulator() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("centerDepthSmooth", UniformFixture.float1()), List.of(),
                STAGE, BAND);
        harness.center.answers.clear();
        harness.center.answers.add(new CenterDepthResult.Unavailable("scripted"));
        harness.center.answers.add(new CenterDepthResult.Sample(0.25f));

        harness.beginFrame(1L, 0L);
        assertNull(cell(harness, "centerDepthSmooth"),
                "unavailable read: cell stays invalid, prior accumulator retained");
        harness.beginFrame(2L, 0L);
        assertEquals(Float.valueOf(0.25f),
                ((UniformValue.F) cell(harness, "centerDepthSmooth")).x(),
                "first sampled frame initializes to that sample");
    }

    @Test
    void cameraRotationPrimesPreviousCameraWithPriorFrameValue() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("previousCameraPosition", UniformFixture.vec3()), List.of(),
                STAGE, BAND);
        harness.beginFrame(1L, 0L);
        assertNull(cell(harness, "previousCameraPosition"),
                "frame one has no previous camera");

        harness.platform.camera =
                new com.schmaloogium.engine.uniforms.Double3(4.0d, 5.0d, 6.0d);
        harness.beginFrame(2L, 0L);
        var previous = (UniformValue.F3) cell(harness, "previousCameraPosition");
        assertEquals(new UniformValue.F3(1.0f, 2.0f, 3.0f), previous);
    }

    @Test
    void worldResetForcesPreviousCameraEqualToCurrentThisFrame() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("previousCameraPosition", UniformFixture.vec3()), List.of(),
                STAGE, BAND);
        harness.beginFrame(1L, 0L);

        harness.platform.camera =
                new com.schmaloogium.engine.uniforms.Double3(7.0d, 8.0d, 9.0d);
        harness.runtime.reset(UniformResetReason.WORLD_EPOCH);
        assertEquals(FrameBeginResult.ACCEPTED, harness.runtime.beginFrame(
                new FrameBeginInput(UniformFixture.GENERATION, 2L,
                        UniformFixture.WORLD + 5L, 0L, 10.0d, 0.05f, 800, 600, 800, 600)));
        var previous = (UniformValue.F3) cell(harness, "previousCameraPosition");
        assertEquals(new UniformValue.F3(7.0f, 8.0f, 9.0f),
                previous, "reset world: previous equals current, no cross-world motion");
    }

    @Test
    void terminalStateGuardPrecedesSamplingAndGl() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.runtime.retire(UniformRetirementReason.SHUTDOWN);
        int callsBefore = harness.device.log().calls().size();
        int framesBefore = harness.platform.frameCalls;
        assertEquals(FrameBeginResult.REJECTED_GENERATION,
                harness.runtime.beginFrame(new FrameBeginInput(
                        UniformFixture.GENERATION, 99L, UniformFixture.WORLD + 1L, 0L,
                        10.0d, 0.05f, 800, 600, 800, 600)),
                "fresh generation cannot revive a retired runtime");
        assertEquals(framesBefore, harness.platform.frameCalls);
        assertEquals(callsBefore, harness.device.log().calls().size());
    }
}
