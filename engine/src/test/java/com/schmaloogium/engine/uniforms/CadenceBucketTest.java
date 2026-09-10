// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import com.schmaloogium.engine.uniforms.runtime.UniformCore;
import com.schmaloogium.engine.uniforms.runtime.UniformValue;
import com.schmaloogium.engine.uniforms.spi.TickUniformSample;
import com.schmaloogium.engine.uniforms.support.UniformFixture;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8 v0.1 headless cases for cadence buckets and frame-begin ordering: ONCE samples at
 * construction, PER_TICK only on logical-tick change, PER_FRAME once per accepted
 * frame, duplicate/stale/obsolete frames are no-ops or rejections, frame timing
 * answers only the exact latest identity, and center depth reads once per accepted
 * frame with the half-pixel offset when required by configuration.
 */
class CadenceBucketTest {

    @Test
    void onceBucketSamplesOnlyAtConstruction() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.beginFrame(1L, 0L);
        harness.beginFrame(2L, 1L);
        harness.beginFrame(3L, 2L);
        assertEquals(1, harness.platform.onceCalls,
                "ONCE producers sample exactly once, at runtime construction");
    }

    @Test
    void tickBucketFiresOnlyOnLogicalTickChange() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.beginFrame(1L, 5L);
        assertEquals(1, harness.platform.tickCalls);
        harness.beginFrame(2L, 5L);
        assertEquals(1, harness.platform.tickCalls,
                "same logical tick: PER_FRAME bucket only");
        harness.beginFrame(3L, 6L);
        assertEquals(2, harness.platform.tickCalls);
    }

    @Test
    void frameBucketFiresOncePerAcceptedFrameOnly() {
        UniformFixture.Harness harness = UniformFixture.harness();
        assertEquals(FrameBeginResult.ACCEPTED, harness.beginFrame(1L, 0L));
        assertEquals(1, harness.platform.frameCalls);

        assertEquals(FrameBeginResult.DUPLICATE, harness.beginFrame(1L, 0L));
        assertEquals(1, harness.platform.frameCalls, "duplicate frame never resamples");
        assertEquals(FrameBeginResult.REJECTED_STALE_FRAME, harness.beginFrame(0L, 0L),
                "an older frame of the same world is stale");
        assertEquals(1, harness.platform.frameCalls, "no sampling on rejected frames");
        assertEquals(FrameBeginResult.ACCEPTED, harness.beginFrame(2L, 0L),
                "a newer frame is simply the next frame");
    }

    @Test
    void wrongGenerationAndObsoleteWorldAreRejected() {
        UniformFixture.Harness harness = UniformFixture.harness();
        assertEquals(FrameBeginResult.ACCEPTED, harness.beginFrame(1L, 0L),
                "establish the world before testing stale epochs");
        assertEquals(FrameBeginResult.REJECTED_GENERATION,
                harness.runtime.beginFrame(UniformFixture.frameInput(
                        UniformFixture.GENERATION + 1L, 2L, UniformFixture.WORLD, 0L, 10.0d,
                        0.05f, 800, 600, 800, 600)));
        assertEquals(FrameBeginResult.REJECTED_STALE_FRAME,
                harness.runtime.beginFrame(UniformFixture.frameInput(
                        UniformFixture.GENERATION, 2L, UniformFixture.WORLD - 1L, 0L, 10.0d,
                        0.05f, 800, 600, 800, 600)),
                "an older world epoch is stale after a frame established the world");
        assertEquals(1, harness.platform.frameCalls);
    }

    @Test
    void frameTimingAnswersOnlyTheExactLatestIdentity() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.beginFrame(3L, 2L);
        var timing = harness.runtime.frameTiming(UniformFixture.GENERATION, 3L);
        assertTrue(timing.isPresent());
        assertEquals(3L, timing.get().frameId());
        assertEquals(20.0d, timing.get().smoothingTimeTicks());
        assertTrue(harness.runtime.frameTiming(UniformFixture.GENERATION, 4L).isEmpty());
        assertTrue(harness.runtime.frameTiming(UniformFixture.GENERATION + 1L, 3L).isEmpty());
    }

    @Test
    void worldTimeDrivesOnlyFromAcceptedTickSamples() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("worldTime", UniformFixture.float1()), List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        harness.platform.tick = new TickUniformSample(UniformFixture.WORLD, 1L, 24600L, 1,
                0.5f);
        harness.beginFrame(1L, 1L);
        harness.activate();
        assertEquals(Integer.valueOf(600),
                (Integer) harness.uploads().get(0).args().get(1));
        assertEquals(Integer.valueOf(600),
                ((UniformValue.I) ((UniformCore) harness.runtime)
                        .cellValue("worldTime")).x());
    }
}
