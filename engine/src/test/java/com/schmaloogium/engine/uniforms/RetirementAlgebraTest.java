// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import com.schmaloogium.engine.uniforms.support.UniformFixture;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8 v0.1 headless cases for the retirement algebra (§4.14, R7-11): retirement is
 * terminal, rejected during in-flight callbacks or from foreign threads, the terminal
 * guard precedes every mutation path (frames, adoption, reset, bridge installs,
 * participants), and no GL work follows a successful {@code retire}.
 */
class RetirementAlgebraTest {

    @Test
    void retireDuringParticipantCallbackIsRejected() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("moonPhase", UniformFixture.int1()), List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        AtomicBoolean sawRejection = new AtomicBoolean(false);
        harness.runtime.installCustomUniformBridge(new com.schmaloogium.engine.expr.api
                .CustomUniformBridge() {
            @Override
            public com.schmaloogium.engine.expr.api.CustomRefreshResult refresh(
                    com.schmaloogium.engine.expr.api.BuiltInExpressionView values,
                    com.schmaloogium.engine.expr.api.CustomUniformUploadSink sink) {
                var outcome = harness.runtime.retire(UniformRetirementReason.SHUTDOWN);
                sawRejection.set(outcome instanceof
                        UniformRetirementResult.Rejected);
                return new com.schmaloogium.engine.expr.api.CustomRefreshResult.NoCustoms();
            }
        });
        harness.beginFrame(1L, 0L);
        harness.activate();
        assertTrue(sawRejection.get(),
                "in-callback retire must be rejected as ACTIVE_CALLBACK, not applied");
        assertInstanceOf(UniformRetirementResult.Retired.class,
                harness.runtime.retire(UniformRetirementReason.SHUTDOWN),
                "outside the callback the retirement succeeds");
    }

    @Test
    void wrongThreadRetirementThrows() throws Exception {
        UniformFixture.Harness harness = UniformFixture.harness();
        // The factory pins the constructing thread as the render thread; the wrong
        // thread observes the IllegalStateException at its own call site.
        java.util.concurrent.atomic.AtomicReference<Throwable> seen =
                new java.util.concurrent.atomic.AtomicReference<>();
        Thread foreign = new Thread(() -> {
            try {
                harness.runtime.retire(UniformRetirementReason.SHUTDOWN);
            } catch (Throwable t) {
                seen.set(t);
            }
        });
        foreign.start();
        foreign.join();
        assertTrue(seen.get() instanceof IllegalStateException,
                "wrong-thread retire throws on the calling thread");
    }

    @Test
    void terminalStateRejectsEveryMutationPath() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("moonPhase", UniformFixture.int1()), List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        assertInstanceOf(UniformRetirementResult.Retired.class,
                harness.runtime.retire(UniformRetirementReason.REPLACEMENT));

        assertEquals(FrameBeginResult.REJECTED_GENERATION,
                harness.beginFrame(2L, 0L));
        assertEquals(RegistryGenerationAdoptionResult.REJECTED_RETIRED_GENERATION,
                harness.runtime.adoptRegistryGeneration(8L,
                        UniformResetReason.PACK_REPLACEMENT));
        assertThrows(IllegalStateException.class,
                () -> harness.runtime.reset(UniformResetReason.WORLD_EPOCH));
        assertThrows(IllegalStateException.class, () -> harness.runtime
                        .installCustomUniformBridge(new com.schmaloogium.engine.expr.api
                                .CustomUniformBridge() {
                            @Override
                            public com.schmaloogium.engine.expr.api.CustomRefreshResult
                                    refresh(com.schmaloogium.engine.expr.api
                                            .BuiltInExpressionView values,
                                    com.schmaloogium.engine.expr.api.CustomUniformUploadSink
                                            sink) {
                                return new com.schmaloogium.engine.expr.api
                                        .CustomRefreshResult.NoCustoms();
                            }
                        }));
        assertInstanceOf(UniformRetirementResult.AlreadyRetired.class,
                harness.runtime.retire(UniformRetirementReason.SHUTDOWN));

        int glCalls = harness.device.log().calls().size();
        var results = harness.activate();
        assertEquals("phase6.runtime.retired",
                ((com.schmaloogium.engine.registry.BarrierParticipantResult.Degraded)
                        results.get(1)).diagnosticId());
        assertEquals(glCalls, harness.device.log().calls().size(),
                "retired runtime performs no GL work");
        assertTrue(harness.runtime.frameTiming(UniformFixture.GENERATION, 1L).isEmpty());
    }

    @Test
    void resetReasonGuardAndAdoptionHandshake() {
        UniformFixture.Harness harness = UniformFixture.harness();
        assertThrows(IllegalArgumentException.class,
                () -> harness.runtime.reset(UniformResetReason.PACK_REPLACEMENT),
                "direct reset accepts only WORLD_EPOCH");
        assertThrows(IllegalArgumentException.class,
                () -> harness.runtime.adoptRegistryGeneration(8L,
                        UniformResetReason.WORLD_EPOCH),
                "adoption accepts replacement reasons, not world resets");

        assertEquals(RegistryGenerationAdoptionResult.ADOPTED,
                harness.runtime.adoptRegistryGeneration(8L,
                        UniformResetReason.PACK_REPLACEMENT));
        assertEquals(RegistryGenerationAdoptionResult.ALREADY_CURRENT,
                harness.runtime.adoptRegistryGeneration(8L,
                        UniformResetReason.PACK_REPLACEMENT),
                "same generation re-adoption is a no-op");
        assertEquals(FrameBeginResult.ACCEPTED, harness.runtime.beginFrame(
                new FrameBeginInput(8L, 1L, UniformFixture.WORLD, 0L, 10.0d, 0.05f,
                        800, 600, 800, 600)));
    }
}
