// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.ReplayAwareGLError;
import com.schmaloogium.engine.gl.UniformService;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.UniformLocation;
import com.schmaloogium.engine.uniforms.spi.TickUniformSample;
import com.schmaloogium.engine.uniforms.support.UniformFixture;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8 v0.1 headless cases for the §4.11 attempt-batch replay protocol over the
 * recorder: an error surfacing inside a batch triggers the rung-2 per-command
 * isolation replay, the guilty command's name is disabled for
 * generation+program+name with one diagnostic, the evidence report carries per-entry
 * attribution, and replay delivery failures latch into fast-failing immediate
 * uploads. Errors are injected at the upload call through a delegating
 * {@link UniformService} because the recorder's pre-drain would otherwise flush any
 * pre-queued error as un-attributable leftover evidence (§4.7.5).
 */
class ReplayIsolationTest {

    /** Delegates to the real service; queues one replay error on the target upload. */
    private static final class FaultingUniformService implements UniformService {
        private final UniformService delegate;
        private final com.schmaloogium.engine.gl.record.ScriptedResponses responses;
        private final String targetName;
        private final String errorLabel;
        private int faultsRemaining = 3; // batch upload + isolation probe re-runs

        FaultingUniformService(UniformService delegate,
                com.schmaloogium.engine.gl.record.ScriptedResponses responses,
                String targetName, String errorLabel) {
            this.delegate = delegate;
            this.responses = responses;
            this.targetName = targetName;
            this.errorLabel = errorLabel;
        }

        private void fault(String uniformName) {
            if (faultsRemaining > 0 && uniformName.equals(targetName)) {
                faultsRemaining--;
                responses.glError("uniforms.upload", errorLabel, GLErrorKind.INVALID_OPERATION);
            }
        }

        @Override
        public UniformLocation locate(ProgramHandle p, String name) {
            return delegate.locate(p, name);
        }

        @Override
        public void upload(UniformLocation loc, int v) {
            fault(loc.toString().substring(loc.toString().indexOf('.') + 1));
            delegate.upload(loc, v);
        }

        @Override
        public void upload(UniformLocation loc, int x, int y) {
            delegate.upload(loc, x, y);
        }

        @Override
        public void upload(UniformLocation loc, int x, int y, int z, int w) {
            delegate.upload(loc, x, y, z, w);
        }

        @Override
        public void upload(UniformLocation loc, float v) {
            delegate.upload(loc, v);
        }

        @Override
        public void upload(UniformLocation loc, float x, float y) {
            delegate.upload(loc, x, y);
        }

        @Override
        public void upload(UniformLocation loc, float x, float y, float z) {
            delegate.upload(loc, x, y, z);
        }

        @Override
        public void upload(UniformLocation loc, float x, float y, float z, float w) {
            delegate.upload(loc, x, y, z, w);
        }

        @Override
        public void uploadMatrix4(UniformLocation loc, float[] m16, boolean transpose) {
            delegate.uploadMatrix4(loc, m16, transpose);
        }
    }

    @Test
    void inBatchErrorIsolatesTheGuiltyCommandName() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("moonPhase", UniformFixture.int1(),
                        "far", UniformFixture.float1()),
                List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        harness.beginFrame(1L, 0L);
        harness.platform.tick = new TickUniformSample(UniformFixture.WORLD, 0L, 700L, 5, 0.5f);
        // Wrap the device: the moonPhase upload surfaces a replay error in-batch.
        ((com.schmaloogium.engine.uniforms.runtime.UniformRuntimeImpl) harness.runtime)
                .replayUniformServiceOverride(new FaultingUniformService(
                harness.device.uniforms(), harness.responses,
                "moonPhase", harness.programLabel + ".moonPhase"));

        var result = harness.runtime.builtInParticipant().afterBind(
                harness.descriptor, harness.barrierContext, harness.access);

        assertEquals(com.schmaloogium.engine.registry.BarrierParticipantResult.Continue.class,
                result.getClass(),
                "an isolated one-off replay must not degrade the whole participant");
        assertEquals(1L,
                harness.diagnostics.count("phase6.builtin.upload.disabled.moonPhase"));
        assertTrue(harness.replay.reports.size() >= 1, "evidence was delivered");
        ReplayAwareGLError entry = harness.replay.reports.get(0).errors().get(0);
        assertTrue(entry.attributed(), "the probe attributed the error to moonPhase");
        assertEquals(harness.programLabel + ".moonPhase",
                entry.error().subjectLabel());

        // The disabled name uploads nothing on later activations; the healthy one does.
        harness.platform.tick = new TickUniformSample(UniformFixture.WORLD, 1L, 700L, 6, 0.5f);
        harness.beginFrame(2L, 1L);
        int before = harness.uploads().size();
        harness.activate();
        var names = harness.uploads().subList(before, harness.uploads().size()).stream()
                .map(call -> String.valueOf(call.args().get(0)))
                .map(n -> n.substring(n.indexOf('.') + 1)).toList();
        assertTrue(!names.contains("moonPhase"), "disabled name must not upload");
    }

    @Test
    void deliveryFailureLatchesAndFailsImmediateUploadsFast() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("moonPhase", UniformFixture.int1()), List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        harness.beginFrame(1L, 0L);
        // A pre-queued foreign error becomes leftover evidence; the observer is broken.
        harness.responses.glError("shaders.link", "foreign", GLErrorKind.INVALID_OPERATION);
        harness.replay.throwOnAccept = true;
        var results = harness.activate();

        assertEquals(com.schmaloogium.engine.registry.BarrierParticipantResult.Degraded.class,
                results.get(1).getClass());
        assertEquals("phase6.replay.delivery.failed",
                ((com.schmaloogium.engine.registry.BarrierParticipantResult.Degraded)
                        results.get(1)).diagnosticId());

        int glCalls = harness.device.log().calls().size();
        harness.activate();
        assertEquals(glCalls, harness.device.log().calls().size(),
                "latched failure: no further GL work from the builtin batch");

        assertThrows(IllegalStateException.class,
                () -> harness.events.updateCelestial(new CelestialSample(100L, 1L,
                        new com.schmaloogium.engine.uniforms.Float3(0, 0, 1),
                        new com.schmaloogium.engine.uniforms.Float3(0, 0, -1),
                        new com.schmaloogium.engine.uniforms.Float3(1, 0, 0),
                        new com.schmaloogium.engine.uniforms.Float3(0, 1, 0))),
                "delivery failure must fail immediate uploads fast");
    }

    @Test
    void leftoverErrorsAreDeliveredWithoutHaltingTheBatch() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("moonPhase", UniformFixture.int1()), List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        harness.beginFrame(1L, 0L);
        // Never matched by a runtime call: a leftover from foreign code (§4.7.5).
        harness.responses.glError("shaders.link", "foreign", GLErrorKind.INVALID_OPERATION);
        var results = harness.activate();

        assertEquals(com.schmaloogium.engine.registry.BarrierParticipantResult.Continue.class,
                results.get(1).getClass());
        assertEquals(1, harness.replay.reports.size());
        assertFalse(harness.replay.reports.get(0).errors().get(0).attributed(),
                "pre-drain leftovers are never attributed to this batch");
        assertEquals(harness.programLabel + ".moonPhase",
                String.valueOf(harness.uploads().get(0).args().get(0)));
    }

    private static void assertFalse(boolean condition, String message) {
        org.junit.jupiter.api.Assertions.assertFalse(condition, message);
    }
}
