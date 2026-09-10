// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.DepthCopyPoint;
import com.schmaloogium.engine.buffers.DepthCopyResult;
import com.schmaloogium.engine.buffers.FrameBeginResult;
import com.schmaloogium.engine.buffers.FrameEndResult;
import com.schmaloogium.engine.buffers.FrameProtocolRejection;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.DepthTransferLayout;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.PixelLayout;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureExtent;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureRegion;
import com.schmaloogium.engine.gl.TextureSpec;
import com.schmaloogium.engine.gl.record.GLCall;
import com.schmaloogium.engine.registry.BufferDomain;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * The §4.9 depth-copy protocol (§8.3 item 9): the first copy of every owned
 * {@code depthtex1/2} destination initializes through
 * {@code initializeDepthTextureFromFramebuffer} and every later copy uses the steady
 * {@code copyDepthToTexture} verb with identical source, destination, and region
 * arguments; a failed copy degrades the destination back to the borrowed-depthtex0
 * binding (DEGRADED_TO_DEPTHTEX0) until the next successful initialization; duplicates
 * and out-of-order points reject pre-GL; a sub-two depth estate reports
 * {@code DEPTH_COPY_UNAVAILABLE} per point without touching any GL verb.
 */
class DepthCopyParityTest {

    private static final String INITIALIZE_OP =
        "framebuffers.initializeDepthTextureFromFramebuffer";
    private static final String STEADY_OP = "framebuffers.copyDepthToTexture";
    private static final TextureRegion MAIN_REGION =
        new TextureRegion(0, 0, 0, BuffersEstateFixture.EXTENT.width(),
            BuffersEstateFixture.EXTENT.height(), 1);

    private static BuffersEstateFixture estate(int depthTextureCount) {
        return BuffersEstateFixture.create(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {new ResourceClearPolicy.FogRgbAlphaOne()},
            new boolean[] {true}, depthTextureCount);
    }

    private static EstateViewImpl view(BuffersEstateFixture fixture) {
        return new EstateViewImpl(fixture.core, new ClearExecutor(), new TextureBinder(),
            new ShadowOperator());
    }

    /** One pass FBO carrying the borrowed depthtex0 as its depth attachment (§4.9 source). */
    private static FramebufferHandle attachPassFbo(BuffersEstateFixture fixture, String label) {
        TextureHandle sourceDepth = fixture.device.textures().create("borrowedDepthSource");
        fixture.device.textures().allocate(sourceDepth, new TextureSpec.DepthTextureSpec(
            TextureAllocationTarget.TEXTURE_2D, DepthAttachmentFormat.DEPTH_COMPONENT,
            new PixelLayout.Depth(DepthTransferLayout.DEPTH_COMPONENT_FLOAT),
            new TextureExtent(BuffersEstateFixture.EXTENT.width(),
                BuffersEstateFixture.EXTENT.height(), 1), 1));
        FramebufferHandle passFbo = fixture.device.framebuffers().create(label);
        fixture.device.framebuffers().attachDepth(passFbo, sourceDepth);
        fixture.core.passFbos.put(label, passFbo);
        return passFbo;
    }

    /** One owned copy destination (depthtexN plus its destination FBO). */
    private static EstateCore.DepthDestination destination(BuffersEstateFixture fixture,
            int index) {
        EstateCore core = fixture.core;
        EstateCore.DepthDestination target = core.new DepthDestination(
            new LogicalBuffer(BufferDomain.DEPTH, new BufferIndex(index)),
            fixture.device.textures().create("depthtex" + index),
            fixture.device.framebuffers().create("copy:depthtex" + index));
        core.copyDestinations.add(target);
        return target;
    }

    @Test
    void firstCopyInitializesAndEveryLaterCopyUsesTheSteadyVerb() {
        BuffersEstateFixture fixture = estate(2);
        EstateCore core = fixture.core;
        FramebufferHandle passFbo = attachPassFbo(fixture, "pass:composite");
        EstateCore.DepthDestination depthtex1 = destination(fixture, 1);
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(31));

        assertEquals(core.cachedDepth.texture(), depthtex1.boundTexture(),
            "an UNINITIALIZED destination binds the borrowed depthtex0 fallback");

        assertEquals(FrameProtocolRejection.DEPTH_COPY_OUT_OF_ORDER,
            assertInstanceOf(DepthCopyResult.Rejected.class,
                view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 31)).reason(),
            "PRE_TRANSLUCENT cannot precede the frame's first PRE_WEATHER copy");
        assertTrue(fixture.device.log().callsMatching(STEADY_OP).isEmpty(),
            "the out-of-order rejection performs no copy");

        DepthCopyResult.Copied weather = assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_WEATHER, 31));
        assertTrue(weather.initialized());
        assertTrue(depthtex1.initialized);
        assertEquals(depthtex1.texture, depthtex1.boundTexture(),
            "a VALID destination binds its owned copy");
        GLCall initialize = fixture.device.log().callsMatching(INITIALIZE_OP).get(0);
        assertEquals(passFbo, initialize.args().get(0));
        assertEquals(depthtex1.texture, initialize.args().get(1));
        assertEquals(MAIN_REGION, initialize.args().get(2));

        DepthCopyResult.Copied translucent = assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 31));
        assertFalse(translucent.initialized(), "the steady copy reuses the initialized tier");
        GLCall steady = fixture.device.log().callsMatching(STEADY_OP).get(0);
        assertEquals(passFbo, steady.args().get(0));
        assertEquals(depthtex1.texture, steady.args().get(1));
        assertEquals(MAIN_REGION, steady.args().get(2));

        assertInstanceOf(FrameEndResult.Committed.class, view.commitFrame(31));
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(32));
        assertFalse(((DepthCopyResult.Copied) assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_WEATHER, 32))).initialized());
        assertFalse(((DepthCopyResult.Copied) assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 32))).initialized());
        assertEquals(1, fixture.device.log().callsMatching(INITIALIZE_OP).size(),
            "a healthy destination initializes exactly once in its lifetime");
        assertEquals(3, fixture.device.log().callsMatching(STEADY_OP).size(),
            "every copy after the first uses the steady verb");
    }

    @Test
    void backendFailureDegradesToBorrowedFallbackAndRecoversOnReinitialization() {
        BuffersEstateFixture fixture = estate(2);
        EstateCore core = fixture.core;
        attachPassFbo(fixture, "pass:composite");
        EstateCore.DepthDestination depthtex1 = destination(fixture, 1);
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(31));
        assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_WEATHER, 31));

        // The steady copy fails mid-frame when the source FBO loses its depth attachment;
        // the recorder rejects a depth copy from a depth-less framebuffer before any
        // recorded event, which is the scripted backend failure.
        core.passFbos.put("pass:composite", fixture.device.framebuffers().create("pass:bare"));
        DepthCopyResult.BackendDegraded degraded = assertInstanceOf(
            DepthCopyResult.BackendDegraded.class,
            view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 31));
        assertEquals(BufferFailureCode.UNEXPECTED_BACKEND, degraded.failure().code());
        assertEquals("schmaloogium.buffers.diagnostic." + core.generation,
            degraded.diagnosticId(), "the degraded copy reports the stable diagnostic");
        assertFalse(depthtex1.initialized,
            "the failure demotes VALID to DEGRADED_TO_DEPTHTEX0");
        assertEquals(core.cachedDepth.texture(), depthtex1.boundTexture(),
            "the degraded destination binds the borrowed depthtex0 fallback again");

        int glCalls = fixture.device.log().calls().size();
        int diagnostics = fixture.diagnostics.size();
        assertInstanceOf(DepthCopyResult.DuplicateIgnored.class,
            view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 31));
        assertEquals(diagnostics + 1, fixture.diagnostics.size(),
            "the duplicate after failure is diagnosed");
        assertEquals(glCalls, fixture.device.log().calls().size(),
            "the failed copy appended no recorded event and the duplicate performs no GL");

        assertInstanceOf(FrameEndResult.Committed.class, view.commitFrame(31));
        attachPassFbo(fixture, "pass:composite");
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(32));
        DepthCopyResult.Copied recovered = assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_WEATHER, 32));
        assertTrue(recovered.initialized(), "the degraded destination re-initializes");
        assertTrue(depthtex1.initialized);
        assertEquals(depthtex1.texture, depthtex1.boundTexture(),
            "recovery restores the owned binding");
        assertEquals(2, fixture.device.log().callsMatching(INITIALIZE_OP).size());
        assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 32));
        assertEquals(1, fixture.device.log().callsMatching(STEADY_OP).size(),
            "the failed steady copy never reached the recorder");
    }

    @Test
    void depthCountBelowTwoDegradesBothCopyPointsAsUnavailable() {
        BuffersEstateFixture fixture = estate(1);
        EstateCore core = fixture.core;
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(7));

        assertEquals(BufferFailureCode.DEPTH_COPY_UNAVAILABLE,
            assertInstanceOf(DepthCopyResult.BackendDegraded.class,
                view.copyDepth(DepthCopyPoint.PRE_WEATHER, 7)).failure().code());
        assertEquals(BufferFailureCode.DEPTH_COPY_UNAVAILABLE,
            assertInstanceOf(DepthCopyResult.BackendDegraded.class,
                view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 7)).failure().code());
        assertTrue(core.consumedPoints.contains(DepthCopyPoint.PRE_WEATHER),
            "the unavailable tier still consumes its scheduled slot");
        assertTrue(core.consumedPoints.contains(DepthCopyPoint.PRE_TRANSLUCENT));

        int glCalls = fixture.device.log().calls().size();
        assertInstanceOf(DepthCopyResult.DuplicateIgnored.class,
            view.copyDepth(DepthCopyPoint.PRE_WEATHER, 7));
        assertEquals(glCalls, fixture.device.log().calls().size(),
            "unavailable tiers and their duplicates perform no GL");
        assertTrue(fixture.device.log().callsMatching(INITIALIZE_OP).isEmpty());
        assertTrue(fixture.device.log().callsMatching(STEADY_OP).isEmpty());

        assertInstanceOf(FrameEndResult.Committed.class, view.commitFrame(7));
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(8));
        assertTrue(core.consumedPoints.isEmpty(), "copy consumption is per-frame");
        assertEquals(BufferFailureCode.DEPTH_COPY_UNAVAILABLE,
            assertInstanceOf(DepthCopyResult.BackendDegraded.class,
                view.copyDepth(DepthCopyPoint.PRE_WEATHER, 8)).failure().code());
    }

    @Test
    void tieredDestinationsInitializeOnceThenCopySteadilyPerFrame() {
        BuffersEstateFixture fixture = estate(3);
        attachPassFbo(fixture, "pass:composite");
        EstateCore.DepthDestination depthtex1 = destination(fixture, 1);
        EstateCore.DepthDestination depthtex2 = destination(fixture, 2);
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(11));

        DepthCopyResult.Copied weather = assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_WEATHER, 11));
        assertTrue(weather.initialized());
        assertTrue(depthtex2.initialized, "PRE_WEATHER tiers to the depthtex2 destination");
        assertFalse(depthtex1.initialized);
        DepthCopyResult.Copied translucent = assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 11));
        assertTrue(translucent.initialized(),
            "PRE_TRANSLUCENT initializes its own depthtex1 tier");
        assertEquals(depthtex2.texture, depthtex2.boundTexture());
        assertEquals(depthtex1.texture, depthtex1.boundTexture());
        assertEquals(List.of(depthtex2.texture, depthtex1.texture),
            fixture.device.log().callsMatching(INITIALIZE_OP).stream()
                .map(call -> call.args().get(1)).toList(),
            "each tier initializes its own texture exactly once");

        assertInstanceOf(FrameEndResult.Committed.class, view.commitFrame(11));
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(12));
        assertFalse(((DepthCopyResult.Copied) assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_WEATHER, 12))).initialized());
        assertFalse(((DepthCopyResult.Copied) assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 12))).initialized());
        assertEquals(2, fixture.device.log().callsMatching(INITIALIZE_OP).size());
        assertEquals(2, fixture.device.log().callsMatching(STEADY_OP).size());
    }
}
