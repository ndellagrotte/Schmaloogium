// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.PassCompletionResult;
import com.schmaloogium.engine.buffers.PassDiscardResult;
import com.schmaloogium.engine.buffers.PhysicalSide;
import com.schmaloogium.engine.buffers.ClearExecutionPlan;
import com.schmaloogium.engine.buffers.ClearExecutionResult;
import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.DepthCopyPoint;
import com.schmaloogium.engine.buffers.DepthCopyResult;
import com.schmaloogium.engine.buffers.DrawBuffersNoneCloseResult;
import com.schmaloogium.engine.buffers.DrawBuffersNoneOpenResult;
import com.schmaloogium.engine.buffers.FrameBeginResult;
import com.schmaloogium.engine.buffers.FrameEndResult;
import com.schmaloogium.engine.buffers.FrameProtocolRejection;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.PassBufferSnapshot;
import com.schmaloogium.engine.buffers.PassDrawTarget;
import com.schmaloogium.engine.buffers.PassSnapshotResult;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.DepthTransferLayout;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.record.GLCall;
import com.schmaloogium.engine.gl.PixelLayout;
import com.schmaloogium.engine.gl.TextureExtent;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureSpec;
import com.schmaloogium.engine.gl.record.ReplayAssertions;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.PassPopulation;
import com.schmaloogium.engine.registry.PassResourceAccess;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageStep;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Frame-protocol rejection ordering of the accepted estate (PHASE_5_DOC §4.4) over a
 * scripted recorder: begin/commit/abort/snapshot/discard/lease/depth-copy checks run in
 * documented precedence, every rejection appends no GL and leaves the open tokens
 * unchanged, committed frames rebase flip state so the next frame begins, and
 * executeClear rejects plans stamped with a foreign generation/epoch or a stale estate.
 */
class EstateFrameProtocolTest {

    private static BuffersEstateFixture estate() {
        return BuffersEstateFixture.create(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8, ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {new ResourceClearPolicy.FogRgbAlphaOne(),
                new ResourceClearPolicy.FogRgbAlphaOne()},
            new boolean[] {true, true}, 0);
    }

    private static EstateViewImpl view(BuffersEstateFixture fixture) {
        return new EstateViewImpl(fixture.core, new ClearExecutor(), new TextureBinder(),
            new ShadowOperator());
    }

    /**
     * {@link ProgramBindingSelection} is mint-only inside Phase 4 barriers, so tests
     * allocate an inert instance: every rejection path under test null-checks the
     * selection but never dereferences it before returning.
     */
    private static ProgramBindingSelection inertSelection() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Object unsafe = theUnsafe.get(null);
            Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
            return (ProgramBindingSelection) allocateInstance.invoke(unsafe,
                ProgramBindingSelection.class);
        } catch (ReflectiveOperationException failure) {
            throw new AssertionError("cannot allocate an inert selection", failure);
        }
    }

    private static PassDescriptor deferredDescriptor() {
        return new PassDescriptor(
            new StageStep(StageId.DEFERRED, StageBand.FRAME_END, new PassPopulation.Singleton()),
            new ProgramSlotId("composite"), Optional.empty(), PassResourceAccess.empty(),
            Set.of());
    }

    private static PassBufferSnapshot forgedSnapshot(long frameId) {
        return new PassBufferSnapshot(1, 0, frameId, deferredDescriptor(), inertSelection(),
            List.of(), Map.of(), Set.of(), PassDrawTarget.Screen.INSTANCE);
    }

    private static FrameProtocolRejection beginRejection(FrameBeginResult result) {
        return assertInstanceOf(FrameBeginResult.Rejected.class, result).reason();
    }

    private static FrameProtocolRejection endRejection(FrameEndResult result) {
        return assertInstanceOf(FrameEndResult.Rejected.class, result).reason();
    }

    @Test
    void secondBeginIsRejectedAndLeavesTheOpenTokenIntact() {
        BuffersEstateFixture fixture = estate();
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(11));
        assertEquals(11, fixture.core.openFrameId);

        int before = fixture.device.log().calls().size();
        assertEquals(FrameProtocolRejection.FRAME_ALREADY_OPEN,
            beginRejection(view.beginFrame(12)));
        assertEquals(11, fixture.core.openFrameId, "the rejected begin must not move the token");
        assertEquals(before, fixture.device.log().calls().size(),
            "a rejection performs no GL");

        assertInstanceOf(FrameEndResult.Committed.class, view.commitFrame(11));
        assertEquals(-1, fixture.core.openFrameId);
        assertEquals(FrameProtocolRejection.NO_OPEN_FRAME,
            endRejection(view.commitFrame(11)), "the consumed token cannot commit twice");
    }

    @Test
    void beginChecksStaleGenerationBeforeFlipNormalization() {
        BuffersEstateFixture fixture = estate();
        EstateViewImpl view = view(fixture);

        fixture.core.stale = true;
        assertEquals(FrameProtocolRejection.STALE_GENERATION,
            beginRejection(view.beginFrame(1)));
        assertEquals(-1, fixture.core.openFrameId);

        fixture.core.stale = false;
        fixture.core.colorPairs.get(0).flipped = true;
        assertEquals(FrameProtocolRejection.NON_NORMALIZED_FLIP_STATE,
            beginRejection(view.beginFrame(1)));
        assertEquals(-1, fixture.core.openFrameId, "the rejected begin opened nothing");
    }

    @Test
    void commitAndAbortRequireAnOpenFrameWithAMatchingId() {
        BuffersEstateFixture fixture = estate();
        EstateViewImpl view = view(fixture);

        assertEquals(FrameProtocolRejection.NO_OPEN_FRAME, endRejection(view.commitFrame(5)));
        assertEquals(FrameProtocolRejection.NO_OPEN_FRAME,
            endRejection(view.abortFrame(5, "d")));
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(5));

        assertEquals(FrameProtocolRejection.WRONG_FRAME_ID, endRejection(view.commitFrame(6)));
        assertEquals(FrameProtocolRejection.WRONG_FRAME_ID,
            endRejection(view.abortFrame(6, "d")));
        assertEquals(5, fixture.core.openFrameId, "mismatched ids leave the frame open");

        FrameEndResult.Aborted aborted = assertInstanceOf(FrameEndResult.Aborted.class,
            view.abortFrame(5, "schmaloogium.test.abort"));
        assertEquals(5, aborted.frameId());
        assertEquals(-1, fixture.core.openFrameId);
    }

    @Test
    void snapshotRequiresAnOpenFrameAndAnOpenLeaseBlocksIt() {
        BuffersEstateFixture fixture = estate();
        EstateViewImpl view = view(fixture);
        PassDescriptor descriptor = deferredDescriptor();

        assertEquals(FrameProtocolRejection.NO_OPEN_FRAME,
            assertInstanceOf(PassSnapshotResult.Rejected.class,
                view.snapshot(descriptor, inertSelection())).reason());
        assertThrows(NullPointerException.class, () -> view.snapshot(descriptor, null),
            "the selection is null-checked before any protocol state is read");

        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(5));
        DrawBuffersNoneOpenResult.Opened opened = assertInstanceOf(
            DrawBuffersNoneOpenResult.Opened.class, view.openDrawBuffersNone(5));
        assertEquals(FrameProtocolRejection.OPEN_DRAW_BUFFERS_NONE_LEASE,
            assertInstanceOf(PassSnapshotResult.Rejected.class,
                view.snapshot(descriptor, inertSelection())).reason(),
            "an open draw-buffers-none lease blocks snapshots even with a frame open");

        assertInstanceOf(DrawBuffersNoneCloseResult.Restored.class, opened.lease().close());
        assertEquals(FrameProtocolRejection.INVALID_DRAW_BUFFERS_NONE_LEASE,
            assertInstanceOf(DrawBuffersNoneCloseResult.Rejected.class,
                opened.lease().close()).reason(), "a closed lease cannot close twice");

        PassSnapshotResult.Failed failed = assertInstanceOf(PassSnapshotResult.Failed.class,
            view.snapshot(descriptor, inertSelection()));
        assertEquals(BufferFailureCode.INVALID_INPUT, failed.failure().code(),
            "past the protocol gates, an unplanned route is an input failure");
        assertTrue(failed.frameAborted());
    }

    @Test
    void openDrawBuffersNoneFollowsTheSameFrameGatesAndRejectsDoubleLeases() {
        BuffersEstateFixture fixture = estate();
        EstateViewImpl view = view(fixture);

        assertEquals(FrameProtocolRejection.NO_OPEN_FRAME,
            assertInstanceOf(DrawBuffersNoneOpenResult.Rejected.class,
                view.openDrawBuffersNone(5)).reason());
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(5));
        assertEquals(FrameProtocolRejection.WRONG_FRAME_ID,
            assertInstanceOf(DrawBuffersNoneOpenResult.Rejected.class,
                view.openDrawBuffersNone(6)).reason());

        DrawBuffersNoneOpenResult.Opened opened = assertInstanceOf(
            DrawBuffersNoneOpenResult.Opened.class, view.openDrawBuffersNone(5));
        assertEquals(FrameProtocolRejection.OPEN_DRAW_BUFFERS_NONE_LEASE,
            assertInstanceOf(DrawBuffersNoneOpenResult.Rejected.class,
                view.openDrawBuffersNone(5)).reason(), "exactly one lease may be open");
        assertInstanceOf(DrawBuffersNoneCloseResult.Restored.class, opened.lease().close());
    }

    @Test
    void discardAndCompleteNeedAnOpenFrameAndAMatchingOpenPass() {
        BuffersEstateFixture fixture = estate();
        EstateViewImpl view = view(fixture);
        PassBufferSnapshot forged = forgedSnapshot(5);

        assertEquals(FrameProtocolRejection.NO_OPEN_FRAME,
            assertInstanceOf(PassDiscardResult.Rejected.class, view.discardPass(forged)).reason());
        assertEquals(FrameProtocolRejection.NO_OPEN_FRAME,
            assertInstanceOf(PassCompletionResult.Rejected.class,
                view.completePass(forged)).reason());

        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(5));
        assertEquals(FrameProtocolRejection.INVALID_PASS_SNAPSHOT,
            assertInstanceOf(PassDiscardResult.Rejected.class, view.discardPass(forged)).reason());
        assertEquals(FrameProtocolRejection.INVALID_PASS_SNAPSHOT,
            assertInstanceOf(PassCompletionResult.Rejected.class,
                view.completePass(forged)).reason());
        assertEquals(5, fixture.core.openFrameId, "the rejected calls left the frame open");
    }

    @Test
    void commitRebasesFlipStateSoTheNextFrameBegins() {
        BuffersEstateFixture fixture = estate();
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(3));
        EstateCore.ColorPair pair = fixture.core.colorPairs.get(0);
        pair.flipped = true;
        assertInstanceOf(FrameEndResult.Committed.class, view.commitFrame(3));
        assertFalse(pair.flipped, "commit rebases the flipped metadata");
        assertEquals(PhysicalSide.B, pair.committedMain, "the committed main carried over");
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(4));
    }

    @Test
    void executeClearRejectsPlansStampedWithForeignGenerationEpochOrStaleEstate() {
        BuffersEstateFixture fixture = estate();
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(21));
        fixture.core.fullClearRequired = false;
        ClearExecutionPlan plan = view.clearPlan(new ClearRequest(21, 0f, 0f, 0f, false));
        assertFalse(plan.isEmpty());

        int glCalls = fixture.device.log().calls().size();
        fixture.core.generation = 2;
        assertEquals(ClearExecutionResult.STALE_OR_PROTOCOL_REJECTED, view.executeClear(plan));
        fixture.core.generation = 1;
        fixture.core.depthAttachmentEpoch = 3;
        assertEquals(ClearExecutionResult.STALE_OR_PROTOCOL_REJECTED, view.executeClear(plan));
        fixture.core.depthAttachmentEpoch = 0;
        fixture.core.stale = true;
        assertEquals(ClearExecutionResult.STALE_OR_PROTOCOL_REJECTED, view.executeClear(plan));
        assertEquals(glCalls, fixture.device.log().calls().size(),
            "stale-plan rejections perform no GL");

        fixture.core.stale = false;
        assertEquals(ClearExecutionResult.SUCCESS, view.executeClear(plan));
        assertEquals(1, fixture.exactOpCount("framebuffers.create"));
        ReplayAssertions.of(fixture.device.log())
            .calledInOrder("framebuffers.create", "framebuffers.attachColor",
                "framebuffers.drawBuffers", "framebuffers.bind",
                "framebuffers.clearColorAttachment");
    }

    @Test
    void depthCopiesAreOrderedTieredAndDeduplicated() {
        BuffersEstateFixture fixture = BuffersEstateFixture.create(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8, ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {new ResourceClearPolicy.FogRgbAlphaOne(),
                new ResourceClearPolicy.FogRgbAlphaOne()},
            new boolean[] {true, true}, 3);
        EstateCore core = fixture.core;

        TextureHandle sourceDepth = fixture.device.textures().create("borrowedDepthSource");
        fixture.device.textures().allocate(sourceDepth, new TextureSpec.DepthTextureSpec(
            TextureAllocationTarget.TEXTURE_2D, DepthAttachmentFormat.DEPTH_COMPONENT,
            new PixelLayout.Depth(DepthTransferLayout.DEPTH_COMPONENT_FLOAT),
            new TextureExtent(BuffersEstateFixture.EXTENT.width(),
                BuffersEstateFixture.EXTENT.height(), 1), 1));
        FramebufferHandle passFbo = fixture.device.framebuffers().create("pass:composite");
        fixture.device.framebuffers().attachDepth(passFbo, sourceDepth);
        core.passFbos.put("pass:composite", passFbo);
        TextureHandle depthtex1 = fixture.device.textures().create("depthtex1");
        TextureHandle depthtex2 = fixture.device.textures().create("depthtex2");
        core.copyDestinations.add(core.new DepthDestination(
            new LogicalBuffer(BufferDomain.DEPTH, new BufferIndex(1)), depthtex1,
            fixture.device.framebuffers().create("copy:depthtex1")));
        core.copyDestinations.add(core.new DepthDestination(
            new LogicalBuffer(BufferDomain.DEPTH, new BufferIndex(2)), depthtex2,
            fixture.device.framebuffers().create("copy:depthtex2")));

        EstateViewImpl view = view(fixture);
        assertEquals(FrameProtocolRejection.NO_OPEN_FRAME,
            assertInstanceOf(DepthCopyResult.Rejected.class,
                view.copyDepth(DepthCopyPoint.PRE_WEATHER, 31)).reason());
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(31));

        assertEquals(FrameProtocolRejection.DEPTH_COPY_OUT_OF_ORDER,
            assertInstanceOf(DepthCopyResult.Rejected.class,
                view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 31)).reason(),
            "PRE_TRANSLUCENT before PRE_WEATHER is out of order");

        DepthCopyResult.Copied weather = assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_WEATHER, 31));
        assertTrue(weather.initialized());
        assertTrue(core.copyDestinations.get(1).initialized,
            "depthCount 3 tiers PRE_WEATHER to the depthtex2 destination");
        assertFalse(core.copyDestinations.get(0).initialized);
        GLCall initialize = fixture.device.log()
            .callsMatching("framebuffers.initializeDepthTextureFromFramebuffer").get(0);
        assertEquals(depthtex2, initialize.args().get(1));

        assertInstanceOf(DepthCopyResult.DuplicateIgnored.class,
            view.copyDepth(DepthCopyPoint.PRE_WEATHER, 31));
        assertEquals(1, fixture.diagnostics.size(), "the duplicate is diagnosed and ignored");

        assertEquals(FrameProtocolRejection.WRONG_FRAME_ID,
            assertInstanceOf(DepthCopyResult.Rejected.class,
                view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 99)).reason());

        assertInstanceOf(DepthCopyResult.Copied.class,
            view.copyDepth(DepthCopyPoint.PRE_TRANSLUCENT, 31));
        assertTrue(core.copyDestinations.get(0).initialized,
            "PRE_TRANSLUCENT always copies to the depthtex1 destination");
    }
}
