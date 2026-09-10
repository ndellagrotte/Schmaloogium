// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import com.schmaloogium.engine.config.id.BlockStampResult;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.LegacyGeometryInputPrimitive;
import com.schmaloogium.engine.gl.LegacyGeometryOutputPrimitive;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.ShaderHandle;
import com.schmaloogium.engine.gl.ShaderStage;
import com.schmaloogium.engine.gl.VertexBindMode;
import com.schmaloogium.engine.gl.VertexBindRejection;
import com.schmaloogium.engine.gl.VertexBindResult;
import com.schmaloogium.engine.gl.VertexSource;
import com.schmaloogium.engine.gl.record.ReplayAssertions;
import com.schmaloogium.engine.gl.record.RecordingGLDevice;
import com.schmaloogium.engine.gl.record.ScriptedResponses;
import com.schmaloogium.engine.registry.ExtendedAttribute;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 10 headless adoption proof over the P1 recorder (PHASE_10_DOC §9 rows
 * "Client/VBO/list capture/replay + facade restoration"): a CLASSIC_56 quad travels a
 * full LIST_CAPTURE → fixture issuance → LIST_REPLAY_GUARD round trip with its
 * extended fields surviving, plans meet the capability gates before binding, and the
 * bind/restore stack is strict one-close LIFO with scripted rollback on failure.
 * Deterministic; no GL context anywhere.
 */
class ClassicVertexCaptureReplayTest {

    private static final int QUAD_BYTES = 4 * Classic56Layout.STRIDE_BYTES;

    private static GLCapabilityProfile profile(int maxVertexAttribs) {
        return new GLCapabilityProfile(3, 3, "3.30 NVIDIA via GL_ARB...", "vendor", "renderer",
                8, 8, 16, maxVertexAttribs, 4096, 256, 0,
                java.util.Set.of("GL_ARB_geometry_shader4"));
    }

    /** One derived CLASSIC_56 quad: worked square, identity (31, 3, 2). */
    private static ByteBuffer quad() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(QUAD_BYTES)
                .order(ByteOrder.LITTLE_ENDIAN);
        float[][] positions = {{0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}};
        float[][] uvs = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
        for (int vertex = 0; vertex < 4; vertex++) {
            int base = vertex * Classic56Layout.STRIDE_BYTES;
            ClassicVertexCodec.beginRecord(buffer, base);
            ClassicVertexCodec.putPosition(buffer, base,
                    positions[vertex][0], positions[vertex][1], positions[vertex][2]);
            ClassicVertexCodec.putUv0(buffer, base, uvs[vertex][0], uvs[vertex][1]);
        }
        QuadAttributeWriter.finalizeQuad(buffer, 0, Classic56Layout.STRIDE_BYTES,
                new BlockStampResult.Present(0x0003001f, 2));
        buffer.rewind();
        return buffer;
    }

    private static VertexInputPlan plan(VertexGeometryInput geometry) {
        return ClassicInputPlans.plan(VertexProducer.BLOCK.participation(),
                EnumSet.allOf(ExtendedAttribute.class), geometry);
    }

    /** Links and uses a program whose effective linked geometry input is TRIANGLES. */
    private static ProgramHandle useTrianglesProgram(RecordingGLDevice device) {
        ShaderHandle vertex = device.shaders().createShader(ShaderStage.VERTEX, "void main(){}");
        ShaderHandle geometry = device.shaders().createShader(ShaderStage.GEOMETRY, "void main(){}");
        ProgramHandle program = device.shaders().createProgram();
        device.shaders().attach(program, vertex);
        device.shaders().attach(program, geometry);
        device.shaders().configureLegacyGeometry(program,
                LegacyGeometryInputPrimitive.TRIANGLES,
                LegacyGeometryOutputPrimitive.TRIANGLE_STRIP, 3);
        device.shaders().link(program);
        device.shaders().use(program);
        // The program retains its attachments; the recorder counts the shaders as
        // owned once deleted, so release them eagerly like the real pipeline does.
        device.shaders().delete(vertex);
        device.shaders().delete(geometry);
        return program;

    }

    @Test
    void captureReplayRoundTripPreservesExtendedFields() {
        RecordingGLDevice device = new RecordingGLDevice(profile(16), new ScriptedResponses());
        ByteBuffer quad = quad();
        VertexInputPlan capturePlan = plan(VertexGeometryInput.TRIANGLES);
        assertTrue(ClassicInputPlans.fitsCapability(capturePlan,
                profile(16).maxVertexAttribs()));

        com.schmaloogium.engine.gl.VertexInputService inputs = device.vertexInputs();
        // LIST_CAPTURE: the prepared-epoch plan carries the capture authority; the
        // recorder does not consult live shader state for it.
        VertexSource.ClientRange source = device.clientVertexSource("quad-capture", quad, 0, 4);
        VertexBindResult.Bound capture = assertInstanceOf(VertexBindResult.Bound.class,
                inputs.bind(source, Classic56Layout.layout(), capturePlan,
                        VertexBindMode.LIST_CAPTURE));
        inputs.restore(capture.binding());

        // The completed immutable captured plan is retained at issuance (P1 D-P1-70).
        VertexSource.DisplayListReplay list =
                device.borrowedVertexList("list-fixture", Classic56Layout.layout(), capturePlan);

        // LIST_REPLAY_GUARD under an effective TRIANGLES program.
        ProgramHandle program = useTrianglesProgram(device);
        VertexBindResult.Bound replay = assertInstanceOf(VertexBindResult.Bound.class,
                inputs.bind(list, Classic56Layout.layout(), capturePlan,
                        VertexBindMode.LIST_REPLAY_GUARD));
        inputs.restore(replay.binding());

        // Release before the recorder's leak invariant runs.
        device.shaders().delete(program);
        ReplayAssertions.of(device.log())
                .calledInOrder("vertexInputs.bind", "vertexInputs.restore",
                        "vertexInputs.bind", "vertexInputs.restore")
                .invariants();

        // The extended fields survive the whole round trip: identity bit patterns,
        // quantized tangent, and midpoint are exactly what was written.
        for (int vertex = 0; vertex < 4; vertex++) {
            int base = vertex * Classic56Layout.STRIDE_BYTES;
            assertEquals(31, ClassicVertexCodec.getIdentityComponents(quad, base)[0]);
            assertArrayEquals(new int[] {32767, 0, 0, 32767},
                    ClassicVertexCodec.getTangentShorts(quad, base));
            assertArrayEquals(new float[] {0.5f, 0.5f},
                    ClassicVertexCodec.getMidTexCoord(quad, base), 0f);
            assertTrue(ClassicVertexCodec.paddingIsZero(quad, base));
        }
    }

    @Test
    void liveDrawComparesExpectedGeometryAgainstTheLinkedRequirement() {
        RecordingGLDevice device = new RecordingGLDevice(profile(13), new ScriptedResponses());
        com.schmaloogium.engine.gl.VertexInputService inputs = device.vertexInputs();
        VertexSource.ClientRange source =
                device.clientVertexSource("quad-live", quad(), 0, 4);

        // No active program: the actual linked requirement is unavailable → reject.
        VertexBindResult rejected = inputs.bind(source, Classic56Layout.layout(),
                plan(VertexGeometryInput.TRIANGLES), VertexBindMode.LIVE_DRAW);
        assertInstanceOf(VertexBindResult.Rejected.class, rejected);
        assertEquals(VertexBindRejection.UNSUPPORTED_INPUT,
                ((VertexBindResult.Rejected) rejected).reason());

        // Under the effective TRIANGLES program the same plan binds live.
        ProgramHandle program = useTrianglesProgram(device);
        VertexBindResult.Bound bound = assertInstanceOf(VertexBindResult.Bound.class,
                inputs.bind(source, Classic56Layout.layout(),
                        plan(VertexGeometryInput.TRIANGLES), VertexBindMode.LIVE_DRAW));
        inputs.restore(bound.binding());
        device.shaders().delete(program);
    }

    @Test
    void bindAndRestoreAreStrictLifo() {
        RecordingGLDevice device = new RecordingGLDevice(profile(13), new ScriptedResponses());
        ProgramHandle program = useTrianglesProgram(device);
        com.schmaloogium.engine.gl.VertexInputService inputs = device.vertexInputs();
        VertexInputPlan triangles = plan(VertexGeometryInput.TRIANGLES);
        VertexSource.ClientRange client =
                device.clientVertexSource("client-a", quad(), 0, 4);
        VertexSource.BorrowedVbo vbo = device.borrowedVertexVbo("vbo-b", QUAD_BYTES);

        VertexBindResult.Bound outer = assertInstanceOf(VertexBindResult.Bound.class,
                inputs.bind(client, Classic56Layout.layout(), triangles, VertexBindMode.LIVE_DRAW));
        VertexBindResult.Bound inner = assertInstanceOf(VertexBindResult.Bound.class,
                inputs.bind(vbo, Classic56Layout.layout(), triangles, VertexBindMode.LIVE_DRAW));

        // Out-of-order restore is refused before GL.
        assertThrows(IllegalStateException.class, () -> inputs.restore(outer.binding()));
        inputs.restore(inner.binding());
        inputs.restore(outer.binding());
        device.shaders().delete(program);
    }

    @Test
    void scriptedBindFailureRollsBackWithoutPushing() {
        ScriptedResponses responses = new ScriptedResponses()
                .glError("vertexInputs.bind", "client-a", GLErrorKind.INVALID_OPERATION);
        RecordingGLDevice device = new RecordingGLDevice(profile(13), responses);
        ProgramHandle program = useTrianglesProgram(device);
        com.schmaloogium.engine.gl.VertexInputService inputs = device.vertexInputs();
        VertexSource.ClientRange source =
                device.clientVertexSource("client-a", quad(), 0, 4);

        VertexBindResult failed = inputs.bind(source, Classic56Layout.layout(),
                plan(VertexGeometryInput.TRIANGLES), VertexBindMode.LIVE_DRAW);
        assertInstanceOf(VertexBindResult.Failed.class, failed);
        // The backend recorded the rollback of the partially applied setup.
        assertTrue(device.log().render().contains("vertexInputs.rollback"));
        assertEquals(1, device.drainErrors().size());

        // Nothing was pushed: the next bind/restore pair is a clean LIFO level.
        VertexBindResult.Bound bound = assertInstanceOf(VertexBindResult.Bound.class,
                inputs.bind(source, Classic56Layout.layout(),
                        plan(VertexGeometryInput.TRIANGLES), VertexBindMode.LIVE_DRAW));
        inputs.restore(bound.binding());
        device.shaders().delete(program);
    }

    @Test
    void invalidSourcesPlansAndModesRejectBeforeAnyNativeCall() {
        RecordingGLDevice device = new RecordingGLDevice(profile(13), new ScriptedResponses());
        com.schmaloogium.engine.gl.VertexInputService inputs = device.vertexInputs();
        VertexSource.ClientRange client =
                device.clientVertexSource("client", quad(), 0, 4);
        VertexSource.BorrowedVbo vbo = device.borrowedVertexVbo("vbo", QUAD_BYTES);

        // Fingerprint mismatch: mutation-free INVALID_PLAN, nothing appended.
        VertexInputPlan foreignFingerprint = new VertexInputPlan("not-this-layout",
                ClassicInputPlans.pointers(EnumSet.allOf(ExtendedAttribute.class),
                        Classic56Layout.layout()),
                VertexProducer.BLOCK.participation(), VertexGeometryInput.TRIANGLES);
        assertEquals(VertexBindRejection.INVALID_PLAN, rejection(inputs.bind(client,
                Classic56Layout.layout(), foreignFingerprint, VertexBindMode.LIVE_DRAW)));

        // Mode/source compatibility: capture needs a client range, replay needs a list.
        assertEquals(VertexBindRejection.UNSUPPORTED_INPUT, rejection(inputs.bind(vbo,
                Classic56Layout.layout(), plan(VertexGeometryInput.TRIANGLES),
                VertexBindMode.LIST_CAPTURE)));
        assertEquals(VertexBindRejection.UNSUPPORTED_INPUT, rejection(inputs.bind(client,
                Classic56Layout.layout(), plan(VertexGeometryInput.TRIANGLES),
                VertexBindMode.LIST_REPLAY_GUARD)));

        // Retired sources are stale; further binds never touch GL.
        device.retireVertexSource(client);
        assertEquals(VertexBindRejection.STALE_SOURCE, rejection(inputs.bind(client,
                Classic56Layout.layout(), plan(VertexGeometryInput.TRIANGLES),
                VertexBindMode.LIVE_DRAW)));

        // Issuance of a list fixture never accepts a plan from another layout.
        assertThrows(IllegalArgumentException.class,
                () -> device.borrowedVertexList("mismatched",
                        Classic56Layout.layout(), foreignFingerprint));

        ReplayAssertions.of(device.log()).neverCalled("vertexInputs.bind");
    }

    private static VertexBindRejection rejection(VertexBindResult result) {
        return assertInstanceOf(VertexBindResult.Rejected.class, result).reason();
    }

}
