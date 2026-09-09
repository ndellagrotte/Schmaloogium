// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import com.schmaloogium.engine.gl.record.GLCall;
import com.schmaloogium.engine.gl.record.GLCallLog;
import com.schmaloogium.engine.gl.record.ReplayAssertions;
import com.schmaloogium.engine.gl.record.RecordingGLDevice;
import com.schmaloogium.engine.gl.record.ScriptedResponses;
import com.schmaloogium.engine.vertex.AttributePointer;
import com.schmaloogium.engine.vertex.ConventionalInput;
import com.schmaloogium.engine.vertex.Delivery;
import com.schmaloogium.engine.vertex.StorageType;
import com.schmaloogium.engine.vertex.VertexField;
import com.schmaloogium.engine.vertex.VertexGeometryInput;
import com.schmaloogium.engine.vertex.VertexInputPlan;
import com.schmaloogium.engine.vertex.VertexLayout;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Recorder behavior (PHASE_1_DOC §4.7.5, §8.1): one stable event per mutating call,
 * precondition rejections append nothing, canned driver errors drain once per cadence,
 * borrowed-depth markers are never deletable, the linked-input metadata comes only from
 * the script or the native default, and the replay assertions prove fixed terminals and
 * leak-free renders headlessly. Deterministic; no GL context anywhere.
 */
class RecordingGLDeviceTest {

    private static GLCapabilityProfile profile(Set<String> extensions) {
        return new GLCapabilityProfile(3, 3, "3.30 NVIDIA via GL_ARB...", "vendor", "renderer",
                8, 8, 16, 16, 4096, 256, 0, extensions);
    }

    private static GLCapabilityProfile profile() {
        return profile(Set.of("GL_ARB_geometry_shader4"));
    }

    /** Two devices driven through identical calls must render byte-identical logs. */
    @Test
    void deterministicRenderForIdenticalDrives() {
        String rendered = driveProgramLifecycle(profile(), new ScriptedResponses());
        String renderedAgain = driveProgramLifecycle(profile(), new ScriptedResponses());
        assertEquals(rendered, renderedAgain);
        assertTrue(rendered.contains(
                "shaders.configureLegacyGeometry(program#3, TRIANGLES, TRIANGLE_STRIP, 3)"));
    }

    private static String driveProgramLifecycle(GLCapabilityProfile cap, ScriptedResponses responses) {
        RecordingGLDevice device = new RecordingGLDevice(cap, responses);
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
        device.shaders().delete(program);
        device.shaders().delete(vertex);
        device.shaders().delete(geometry);
        ReplayAssertions.of(device.log())
                .calledInOrder("shaders.createShader", "shaders.createProgram", "shaders.attach",
                        "shaders.configureLegacyGeometry", "shaders.link", "shaders.use")
                .invariants();
        return device.log().render();
    }

    /** Fixed-function terminal proof without a special assertion API (§4.7.5). */
    @Test
    void fixedFunctionTerminalProvesNoUseAfterDelete() {
        RecordingGLDevice device = new RecordingGLDevice(profile(), new ScriptedResponses());
        ProgramHandle program = device.shaders().createProgram();
        device.shaders().link(program);
        device.shaders().useFixedFunction();
        ReplayAssertions.of(device.log())
                .calledInOrder("shaders.useFixedFunction")
                .neverCalled("shaders.use")
                .noUseAfterDelete();

        device.shaders().use(program);
        device.shaders().delete(program);
        assertThrows(IllegalStateException.class, () -> device.shaders().use(program));
        ReplayAssertions.of(device.log()).noUseAfterDelete();
    }

    /** A forged log that uses a program after deleting it must trip the assertion. */
    @Test
    void forgedUseAfterDeleteIsDetected() {
        GLCall use = new GLCall("shaders.use", List.of("program#7"));
        GLCall delete = new GLCall("shaders.delete", List.of("program#7"));
        ReplayAssertions.of(List.of(use, delete)).noUseAfterDelete();
        assertThrows(AssertionError.class,
                () -> ReplayAssertions.of(List.of(delete, use)).noUseAfterDelete());
    }

    /** Canned driver errors surface on exactly one drain, then never again (D-P1-30). */
    @Test
    void scriptedErrorsDrainOncePerCadence() {
        ScriptedResponses responses = new ScriptedResponses()
                .glError("shaders.use", "program#1", GLErrorKind.INVALID_OPERATION);
        RecordingGLDevice device = new RecordingGLDevice(profile(), responses);
        ProgramHandle program = device.shaders().createProgram();
        device.shaders().link(program);
        device.shaders().use(program);

        List<GLError> drained = device.drainErrors();
        assertEquals(1, drained.size());
        assertEquals("shaders.use", drained.get(0).op());
        assertEquals(GLErrorKind.INVALID_OPERATION, drained.get(0).kind());
        assertEquals(List.of(), device.drainErrors());

        device.state().viewport(0, 0, 64, 64);
        assertEquals(List.of(), device.drainErrors());
    }

    /** Rejected preconditions append nothing; the extension gate queues without a call. */
    @Test
    void preconditionRejectionsAppendNothing() {
        RecordingGLDevice device = new RecordingGLDevice(profile(), new ScriptedResponses());
        ProgramHandle program = device.shaders().createProgram();
        int before = device.log().calls().size();
        assertThrows(IllegalStateException.class,
                () -> device.shaders().configureLegacyGeometry(program,
                        LegacyGeometryInputPrimitive.TRIANGLES,
                        LegacyGeometryOutputPrimitive.TRIANGLE_STRIP, 3));
        assertEquals(before, device.log().calls().size());
        assertEquals(List.of(), device.drainErrors());

        RecordingGLDevice gateless = new RecordingGLDevice(
                profile(Set.of()), new ScriptedResponses());
        ShaderHandle geometry = gateless.shaders().createShader(
                ShaderStage.GEOMETRY, "void main(){}");
        ProgramHandle gateProgram = gateless.shaders().createProgram();
        gateless.shaders().attach(gateProgram, geometry);
        int gateBefore = gateless.log().calls().size();
        gateless.shaders().configureLegacyGeometry(gateProgram,
                LegacyGeometryInputPrimitive.TRIANGLES,
                LegacyGeometryOutputPrimitive.TRIANGLE_STRIP, 3);
        assertEquals(gateBefore, gateless.log().calls().size());
        List<GLError> drained = gateless.drainErrors();
        assertEquals(1, drained.size());
        assertEquals(GLErrorKind.INVALID_OPERATION, drained.get(0).kind());
    }

    /** Selection rules: fixed function resumes QUADS; scripted TRIANGLES STRIPs. */
    @Test
    void fullscreenSelectionFollowsCommittedInput() {
        RecordingGLDevice fixed = new RecordingGLDevice(profile(), new ScriptedResponses());
        fixed.shaders().useFixedFunction();
        fixed.draw().fullscreenQuad();
        ReplayAssertions.of(fixed.log())
                .calledInOrder("shaders.useFixedFunction", "draw.fullscreenQuad");

        ScriptedResponses triangles = new ScriptedResponses()
                .linkedGeometryInput("program#1", LinkedGeometryInputPrimitive.TRIANGLES);
        RecordingGLDevice scripted = new RecordingGLDevice(profile(), triangles);
        ProgramHandle program = scripted.shaders().createProgram();
        ShaderHandle geometry = scripted.shaders().createShader(
                ShaderStage.GEOMETRY, "void main(){}");
        scripted.shaders().attach(program, geometry);
        scripted.shaders().link(program);
        scripted.shaders().use(program);
        scripted.draw().fullscreenQuad();
        GLCall drawCall = scripted.log().callsMatching("draw.fullscreenQuad").get(0);
        assertEquals(FullscreenPrimitive.TRIANGLE_STRIP, drawCall.args().get(0));
    }

    /** Unknown/uncertain selection rejects the draw: a refusal event, no draw, no error. */
    @Test
    void unknownSelectionRejectsFullscreenDraw() {
        ScriptedResponses responses = new ScriptedResponses()
                .glError("shaders.use", "program#1", GLErrorKind.INVALID_OPERATION);
        RecordingGLDevice device = new RecordingGLDevice(profile(), responses);
        ProgramHandle program = device.shaders().createProgram();
        device.shaders().link(program);
        device.shaders().use(program);
        device.draw().fullscreenQuad();
        ReplayAssertions.of(device.log())
                .calledInOrder("draw.fullscreenQuadRejected")
                .neverCalled("draw.fullscreenQuad");
        List<GLError> drained = device.drainErrors();
        assertEquals(1, drained.size());
        assertEquals("shaders.use", drained.get(0).op());
    }

    /** drawBuffers records the exact route and rejects duplicate attachments. */
    @Test
    void drawBuffersRouteIsRecordedExactly() {
        RecordingGLDevice device = new RecordingGLDevice(profile(), new ScriptedResponses());
        FramebufferHandle fbo = device.framebuffers().create("scene");
        device.framebuffers().drawBuffers(fbo, List.of(
                new FramebufferDrawSlot.Attachment(0), new FramebufferDrawSlot.None()));
        ReplayAssertions.of(device.log())
                .drawBuffersWere(List.of(
                        new FramebufferDrawSlot.Attachment(0), new FramebufferDrawSlot.None()));
        assertThrows(IllegalArgumentException.class,
                () -> device.framebuffers().drawBuffers(fbo, List.of(
                        new FramebufferDrawSlot.Attachment(0),
                        new FramebufferDrawSlot.Attachment(0))));
    }

    /** Integer/floating clear classes, route holes and capability floors (D-P1-67). */
    @Test
    void clearColorAttachmentValidatesClassRouteAndCapability() {
        RecordingGLDevice device = new RecordingGLDevice(profile(), new ScriptedResponses());
        TextureHandle floatTexture = device.textures().create("hdr");
        device.textures().allocate(floatTexture, new TextureSpec.ColorTextureSpec(
                TextureAllocationTarget.TEXTURE_2D, ColorInternalFormat.RGBA32F,
                new PixelLayout.Color(PixelFormat.RGBA, PixelType.FLOAT),
                new TextureExtent(4, 4, 1), 1));
        FramebufferHandle fbo = device.framebuffers().create("hdr");
        device.framebuffers().attachColor(fbo, 0, floatTexture);
        device.framebuffers().drawBuffers(fbo, List.of(
                new FramebufferDrawSlot.Attachment(0), new FramebufferDrawSlot.None()));

        device.framebuffers().clearColorAttachment(fbo, 0,
                new ColorClearValue.Floating(0f, 0f, 0f, 0f));
        assertThrows(IllegalArgumentException.class,
                () -> device.framebuffers().clearColorAttachment(fbo, 1,
                        new ColorClearValue.Floating(0f, 0f, 0f, 0f)));
        assertThrows(IllegalArgumentException.class,
                () -> device.framebuffers().clearColorAttachment(fbo, 0,
                        new ColorClearValue.Signed(0, 0, 0, 0)));

        RecordingGLDevice legacy = new RecordingGLDevice(new GLCapabilityProfile(
                2, 1, "1.20", "vendor", "renderer", 4, 4, 8, 8, 2048, 0, 0, Set.of()),
                new ScriptedResponses());
        TextureHandle integerTexture = legacy.textures().create("ids");
        legacy.textures().allocate(integerTexture, new TextureSpec.ColorTextureSpec(
                TextureAllocationTarget.TEXTURE_2D, ColorInternalFormat.R32I,
                new PixelLayout.Color(PixelFormat.RGBA_INTEGER, PixelType.INT),
                new TextureExtent(4, 4, 1), 1));
        FramebufferHandle legacyFbo = legacy.framebuffers().create("ids");
        legacy.framebuffers().attachColor(legacyFbo, 0, integerTexture);
        legacy.framebuffers().drawBuffers(legacyFbo,
                List.of(new FramebufferDrawSlot.Attachment(0)));
        assertThrows(UnsupportedOperationException.class,
                () -> legacy.framebuffers().clearColorAttachment(legacyFbo, 0,
                        new ColorClearValue.Signed(1, 2, 3, 4)));
    }

    /** Depth verbs carry restored identities; steady copy never redefines storage. */
    @Test
    void depthVerbsCarryRestoredIdentitiesAndEnforceStorage() {
        RecordingGLDevice device = new RecordingGLDevice(profile(), new ScriptedResponses());
        TextureHandle depth = device.textures().create("depth");
        device.textures().allocate(depth, new TextureSpec.DepthTextureSpec(
                TextureAllocationTarget.TEXTURE_2D, DepthAttachmentFormat.DEPTH_COMPONENT,
                new PixelLayout.Depth(DepthTransferLayout.DEPTH_COMPONENT_FLOAT),
                new TextureExtent(64, 64, 1), 1));
        FramebufferHandle fbo = device.framebuffers().create("scene");
        device.framebuffers().attachDepth(fbo, depth);
        FramebufferHandle other = device.framebuffers().create("other");
        device.framebuffers().bind(FramebufferTarget.READ_AND_DRAW, other);

        TextureHandle target = device.textures().create("depthCopy");
        device.framebuffers().initializeDepthTextureFromFramebuffer(
                fbo, target, new TextureRegion(0, 0, 0, 64, 64, 1));
        device.framebuffers().copyDepthToTexture(
                fbo, target, new TextureRegion(0, 0, 0, 64, 64, 1));
        assertThrows(IllegalArgumentException.class,
                () -> device.framebuffers().copyDepthToTexture(
                        fbo, target, new TextureRegion(0, 0, 0, 32, 32, 1)));

        GLCall copy = device.log().callsMatching("framebuffers.copyDepthToTexture").get(0);
        assertEquals(other, copy.args().get(3));
        assertEquals(other, copy.args().get(4));
    }

    /** Borrowed-depth wraps only foreign platform textures and is never deletable. */
    @Test
    void borrowedDepthIsNeverDeletable() {
        RecordingGLDevice device = new RecordingGLDevice(profile(), new ScriptedResponses());
        TextureHandle platform = new TextureHandle() {
        };
        BorrowedDepthAttachmentHandle borrowed =
                device.framebuffers().borrowDepthAttachment(platform);
        assertThrows(IllegalArgumentException.class, () -> device.textures().delete(borrowed));
        assertThrows(IllegalArgumentException.class,
                () -> device.framebuffers().borrowDepthAttachment(borrowed));
        FramebufferHandle fbo = device.framebuffers().create("shadow");
        device.framebuffers().attachDepth(fbo, borrowed);
        assertEquals(FramebufferStatus.COMPLETE, device.framebuffers().check(fbo));
    }

    /** bind/restore is a LIFO transaction; retired sources bind as stale (§4.7.6). */
    @Test
    void vertexInputBindRestoreLifecycle() {
        RecordingGLDevice device = new RecordingGLDevice(profile(), new ScriptedResponses());
        ProgramHandle program = device.shaders().createProgram();
        device.shaders().link(program);
        device.shaders().use(program);

        VertexLayout layout = new VertexLayout("quad56", 20, List.of(
                new VertexField("position", 0, 3, StorageType.FLOAT32, Delivery.FLOAT_VALUE),
                new VertexField("uv", 12, 2, StorageType.FLOAT32, Delivery.FLOAT_VALUE)),
                "fp-quad56");
        VertexInputPlan plan = new VertexInputPlan("fp-quad56", List.of(
                new AttributePointer("position", 0, 0, 3, StorageType.FLOAT32, false),
                new AttributePointer("uv", 1, 12, 2, StorageType.FLOAT32, false)),
                Set.of(ConventionalInput.UV0), VertexGeometryInput.NONE);

        VertexSource.ClientRange source =
                device.clientVertexSource("quad", ByteBuffer.allocate(60), 0, 3);
        VertexBindResult bound = device.vertexInputs()
                .bind(source, layout, plan, VertexBindMode.LIVE_DRAW);
        VertexBinding binding = assertInstanceOf(VertexBindResult.Bound.class, bound).binding();
        device.vertexInputs().restore(binding);

        device.retireVertexSource(source);
        VertexBindResult stale = device.vertexInputs()
                .bind(source, layout, plan, VertexBindMode.LIVE_DRAW);
        assertEquals(VertexBindRejection.STALE_SOURCE,
                assertInstanceOf(VertexBindResult.Rejected.class, stale).reason());

        VertexSource.BorrowedVbo vbo = device.borrowedVertexVbo("mesh", 1024);
        assertEquals(VertexBindRejection.UNSUPPORTED_INPUT,
                assertInstanceOf(VertexBindResult.Rejected.class,
                        device.vertexInputs().bind(vbo, layout, plan, VertexBindMode.LIST_CAPTURE))
                        .reason());
        ReplayAssertions.of(device.log())
                .calledInOrder("vertexInputs.bind", "vertexInputs.restore");
    }

    /** Out-of-order restore rejects without consuming the stack. */
    @Test
    void vertexInputRestoreIsStrictlyLifo() {
        RecordingGLDevice device = new RecordingGLDevice(profile(), new ScriptedResponses());
        ProgramHandle program = device.shaders().createProgram();
        device.shaders().link(program);
        device.shaders().use(program);
        VertexLayout layout = new VertexLayout("quad56", 20, List.of(
                new VertexField("position", 0, 3, StorageType.FLOAT32, Delivery.FLOAT_VALUE)),
                "fp-quad56");
        VertexInputPlan plan = new VertexInputPlan("fp-quad56", List.of(
                new AttributePointer("position", 0, 0, 3, StorageType.FLOAT32, false)),
                Set.of(), VertexGeometryInput.NONE);
        VertexSource.ClientRange source =
                device.clientVertexSource("a", ByteBuffer.allocate(60), 0, 3);
        VertexBinding first = assertInstanceOf(VertexBindResult.Bound.class,
                device.vertexInputs().bind(source, layout, plan, VertexBindMode.LIVE_DRAW))
                .binding();
        VertexBinding second = assertInstanceOf(VertexBindResult.Bound.class,
                device.vertexInputs().bind(source, layout, plan, VertexBindMode.LIVE_DRAW))
                .binding();
        assertThrows(IllegalStateException.class, () -> device.vertexInputs().restore(first));
        device.vertexInputs().restore(second);
        device.vertexInputs().restore(first);
    }

    /** A failed sampler initialization without a provable restore poisons admission. */
    @Test
    void samplerFailureWithoutRestoredSelectionPoisonsAdmission() {
        ScriptedResponses responses = new ScriptedResponses()
                .samplerInitializationFails("program#1", "driver reset", false);
        RecordingGLDevice device = new RecordingGLDevice(profile(), responses);
        ProgramHandle program = device.shaders().createProgram();
        device.shaders().link(program);
        SamplerInitializationResult result = device.shaders().initializeSamplerUnits(
                program, List.of(new SamplerUnitAssignment("texture", 0)));
        SamplerInitializationResult.Failed failed =
                assertInstanceOf(SamplerInitializationResult.Failed.class, result);
        assertFalse(failed.selectionRestored());
        assertThrows(IllegalStateException.class, () -> device.shaders().use(program));
    }

    /** Bounded logs keep the newest calls and count what they dropped (§4.9.3 posture). */
    @Test
    void boundedLogKeepsNewestCallsAndCountsDrops() {
        GLCallLog bounded = GLCallLog.bounded(4);
        RecordingGLDevice device = new RecordingGLDevice(profile(), new ScriptedResponses(),
                bounded);
        device.state().viewport(0, 0, 1, 1);
        device.state().viewport(0, 0, 2, 2);
        device.state().viewport(0, 0, 3, 3);
        device.state().viewport(0, 0, 4, 4);
        device.state().viewport(0, 0, 5, 5);
        assertEquals(1, bounded.droppedCallCount());
        assertEquals(4, bounded.calls().size());
        assertTrue(bounded.render().startsWith("# dropped 1 older calls"));
        assertFalse(bounded.render().contains("0, 0, 1, 1"));
    }
}
