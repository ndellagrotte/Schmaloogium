// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.FrameBeginResult;
import com.schmaloogium.engine.buffers.PassBufferSnapshot;
import com.schmaloogium.engine.buffers.PassCompletionResult;
import com.schmaloogium.engine.buffers.PassDiscardResult;
import com.schmaloogium.engine.buffers.PassSnapshotResult;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.record.ScriptedResponses;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.BufferRef;
import com.schmaloogium.engine.registry.DrawRoutingSlot;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.PassPopulation;
import com.schmaloogium.engine.registry.PassResourceAccess;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageStep;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * PHASE_5_DOC §4.4.2 steps 3-5: a snapshot freezes the write/alt side for deferred and
 * composite writes and the read/main side for gbuffers writes, and the pass FBO's colour
 * attachment is derived from that frozen handle. The candidate builder attached side A
 * once; the view re-attaches only when the frozen side differs from what the FBO holds,
 * re-checks completeness, and takes the Failed(...,true) terminal transition on failure.
 */
class PassSnapshotAttachmentTest {

    private static final ProgramSlotId COMPOSITE = new ProgramSlotId("composite");
    private static final ProgramSlotId TERRAIN = new ProgramSlotId("gbuffers_terrain");
    private static final ProgramSlotId FINAL = new ProgramSlotId("final");
    private static final BufferRef COLORTEX0 = new BufferRef(BufferDomain.COLORTEX, 0);

    private static PlanningArtifacts.PlannedRoute route(ProgramSlotId slot) {
        return new PlanningArtifacts.PlannedRoute(slot,
            List.of(new DrawRoutingSlot.Attachment(COLORTEX0)),
            List.of(BuffersEstateFixture.colorRow(0)));
    }

    private static BuffersEstateFixture estate(ScriptedResponses responses) {
        return BuffersEstateFixture.createWithRoutes(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {new ResourceClearPolicy.FogRgbAlphaOne()},
            new boolean[] {true}, 0,
            Map.of(COMPOSITE, route(COMPOSITE), TERRAIN, route(TERRAIN), FINAL, route(FINAL)),
            responses);
    }

    private static FramebufferHandle seedPassFbo(BuffersEstateFixture fixture,
            ProgramSlotId slot) {
        String key = CandidateBuilder.passKey(route(slot));
        FramebufferHandle fbo = fixture.device.framebuffers().create(key);
        EstateCore.ColorPair pair = fixture.core.colorPairs.get(0);
        fixture.device.framebuffers().attachColor(fbo, 0, pair.sideA);
        fixture.core.passFbos.put(key, fbo);
        fixture.core.attachedColor.put(fbo, new java.util.LinkedHashMap<>(Map.of(0, pair.sideA)));
        return fbo;
    }

    private static EstateViewImpl view(BuffersEstateFixture fixture) {
        return new EstateViewImpl(fixture.core, new ClearExecutor(), new TextureBinder(),
            new ShadowOperator());
    }

    private static PassDescriptor descriptor(ProgramSlotId slot, StageId stage, StageBand band) {
        return new PassDescriptor(new StageStep(stage, band, new PassPopulation.Singleton()),
            slot, Optional.empty(),
            new PassResourceAccess(Set.of(), Set.of(COLORTEX0), Map.of(), Set.of()), Set.of());
    }

    private static PassDescriptor composite() {
        return descriptor(COMPOSITE, StageId.COMPOSITE, StageBand.FRAME_END);
    }

    private static PassDescriptor terrain() {
        return descriptor(TERRAIN, StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE);
    }

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

    private static PassBufferSnapshot acquired(PassSnapshotResult result) {
        return assertInstanceOf(PassSnapshotResult.Acquired.class, result).snapshot();
    }

    @Test
    void compositeSnapshotsAttachTheWriteSideAndAlternateAcrossFlips() {
        BuffersEstateFixture fixture = estate(new ScriptedResponses());
        FramebufferHandle fbo = seedPassFbo(fixture, COMPOSITE);
        EstateCore.ColorPair pair = fixture.core.colorPairs.get(0);
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(1));
        long attaches = fixture.exactOpCount("framebuffers.attachColor");

        // First composite: side A is committed main, so the write side is B.
        PassBufferSnapshot first = acquired(view.snapshot(composite(), inertSelection()));
        assertSame(pair.sideB, first.colorAttachments().get(0).physicalTexture());
        assertSame(pair.writeSide(), first.colorAttachments().get(0).physicalTexture());
        assertEquals(attaches + 1, fixture.exactOpCount("framebuffers.attachColor"),
            "the frozen write side differs from the built side A: one re-attach");
        assertSame(pair.sideB, fixture.core.attachedColor.get(fbo).get(0));
        assertInstanceOf(PassCompletionResult.Completed.class, view.completePass(first));
        assertTrue(pair.flipped, "a completed composite write flips the buffer");

        // Second composite in the same frame: flipped, so the write side is A again.
        PassBufferSnapshot second = acquired(view.snapshot(composite(), inertSelection()));
        assertSame(pair.sideA, second.colorAttachments().get(0).physicalTexture());
        assertEquals(attaches + 2, fixture.exactOpCount("framebuffers.attachColor"));
        assertInstanceOf(PassDiscardResult.Discarded.class, view.discardPass(second));

        // Third snapshot with no flip in between: the FBO already holds side A.
        PassBufferSnapshot third = acquired(view.snapshot(composite(), inertSelection()));
        assertSame(pair.sideA, third.colorAttachments().get(0).physicalTexture());
        assertEquals(attaches + 2, fixture.exactOpCount("framebuffers.attachColor"),
            "an unchanged frozen side performs no GL attachment");
        assertInstanceOf(PassDiscardResult.Discarded.class, view.discardPass(third));
    }

    @Test
    void finalSnapshotTargetsTheScreenAndFlipsNothing() {
        BuffersEstateFixture fixture = estate(new ScriptedResponses());
        EstateCore.ColorPair pair = fixture.core.colorPairs.get(0);
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(1));
        long revision = pair.writeRevision;

        PassBufferSnapshot snapshot = acquired(view.snapshot(
            descriptor(FINAL, StageId.FINAL, StageBand.SCREEN), inertSelection()));
        assertInstanceOf(com.schmaloogium.engine.buffers.PassDrawTarget.Screen.class,
            snapshot.drawTarget());
        assertTrue(snapshot.flipAfterPass().isEmpty(), "SCREEN invents no flip");
        assertEquals(revision, pair.writeRevision, "the screen pass prepares no estate write");
        assertInstanceOf(PassCompletionResult.Completed.class, view.completePass(snapshot));
        assertTrue(!pair.flipped);
    }

    @Test
    void gbuffersSnapshotsAttachTheReadSide() {
        BuffersEstateFixture fixture = estate(new ScriptedResponses());
        FramebufferHandle fbo = seedPassFbo(fixture, TERRAIN);
        EstateCore.ColorPair pair = fixture.core.colorPairs.get(0);
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(1));
        long attaches = fixture.exactOpCount("framebuffers.attachColor");

        PassBufferSnapshot snapshot = acquired(view.snapshot(terrain(), inertSelection()));
        assertSame(pair.readSide(), snapshot.colorAttachments().get(0).physicalTexture());
        assertSame(pair.sideA, snapshot.colorAttachments().get(0).physicalTexture());
        assertEquals(attaches, fixture.exactOpCount("framebuffers.attachColor"),
            "gbuffers write the built side A: nothing to re-attach");
        assertTrue(snapshot.flipAfterPass().isEmpty(), "gbuffers never auto-flip");
        assertInstanceOf(PassCompletionResult.Completed.class, view.completePass(snapshot));

        // After a flip (forced here) the read side is B, and a gbuffers write re-attaches B.
        pair.flipped = true;
        PassBufferSnapshot flipped = acquired(view.snapshot(terrain(), inertSelection()));
        assertSame(pair.sideB, flipped.colorAttachments().get(0).physicalTexture());
        assertEquals(attaches + 1, fixture.exactOpCount("framebuffers.attachColor"));
        assertSame(pair.sideB, fixture.core.attachedColor.get(fbo).get(0));
    }

    @Test
    void incompleteReattachmentIsATerminalSnapshotFailure() {
        String key = CandidateBuilder.passKey(route(COMPOSITE));
        BuffersEstateFixture fixture = estate(new ScriptedResponses()
            .framebufferStatus(key, FramebufferStatus.INCOMPLETE_ATTACHMENT));
        seedPassFbo(fixture, COMPOSITE);
        EstateCore.ColorPair pair = fixture.core.colorPairs.get(0);
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(1));
        long revision = pair.writeRevision;

        PassSnapshotResult.Failed failed = assertInstanceOf(PassSnapshotResult.Failed.class,
            view.snapshot(composite(), inertSelection()));
        assertTrue(failed.frameAborted());
        assertEquals(BufferFailureCode.FRAMEBUFFER_INCOMPLETE, failed.failure().code());
        assertTrue(fixture.core.stale, "a mutation-bearing failure leaves the estate stale");
        assertTrue(fixture.core.fullClearRequired);
        assertEquals(revision, pair.writeRevision, "no §4.2.1 preparation after failure");
        assertEquals(null, fixture.core.openPass, "no partially prepared snapshot escapes");
        assertTrue(fixture.diagnostics.stream().anyMatch(d ->
            d.toString().contains("schmaloogium.buffers.error.snapshot.reattach")));
    }
}
