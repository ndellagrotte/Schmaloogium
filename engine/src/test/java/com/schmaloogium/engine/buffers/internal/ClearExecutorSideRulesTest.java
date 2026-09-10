// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.ClearExecutionPlan;
import com.schmaloogium.engine.buffers.ClearExecutionResult;
import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.PhysicalSide;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.gl.ColorClearValue;
import com.schmaloogium.engine.gl.FramebufferDrawSlot;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.record.GLCall;
import com.schmaloogium.engine.gl.record.ReplayAssertions;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Typed-clear planning and execution side rules (PHASE_5_DOC §4.6) against a scripted
 * {@link RecordingGLDevice}: full clears take sides A AND B, normal clears take only the
 * current read side until the pair is flipped, disabled rows contribute nothing, a frame
 * mismatch yields an empty plan stamped with the foreign frame, grouping keys on the
 * converted typed value (signed and unsigned never alias), execution clears every
 * occupied route position in ascending order through the typed facade and reuses the
 * cached clear FBO, and a clear never toggles flip state.
 */
class ClearExecutorSideRulesTest {

    private final ClearExecutor executor = new ClearExecutor();

    private static ResourceClearPolicy fog() {
        return new ResourceClearPolicy.FogRgbAlphaOne();
    }

    private static ClearRequest request(long frameId, boolean fullClear) {
        return new ClearRequest(frameId, 0.25f, 0.5f, 0.75f, fullClear);
    }

    @Test
    void fullClearPlansBothSidesOfEveryEnabledRowGroupedByConvertedValue() {
        BuffersEstateFixture fixture = BuffersEstateFixture.create(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8, ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {fog(), fog()},
            new boolean[] {true, true}, 0);
        EstateCore core = fixture.core;
        core.fullClearRequired = false;
        core.openFrameId = 7;

        ClearExecutionPlan plan = executor.plan(core, request(7, true), 7);
        assertFalse(plan.isEmpty());
        assertEquals(1, plan.batches().size(),
            "two rows converting to the same typed value group into one batch");
        ClearExecutionPlan.Batch batch = plan.batches().get(0);
        EstateCore.ColorPair row0 = core.colorPairs.get(0);
        EstateCore.ColorPair row1 = core.colorPairs.get(1);
        assertEquals(List.of(row0.sideA, row0.sideB, row1.sideA, row1.sideB),
            batch.textures());
        assertEquals(4, batch.values().size());
        for (ColorClearValue value : batch.values()) {
            assertEquals(new ColorClearValue.Floating(0.25f, 0.5f, 0.75f, 1.0f), value,
                "fog RGB with alpha one, converted at the row's normalized class");
        }
        assertEquals(1, plan.estateGeneration());
        assertEquals(0, plan.depthAttachmentEpoch());
        assertEquals(7, plan.frameId());
    }

    @Test
    void normalClearTakesOnlyTheReadSideUntilThePairIsFlipped() {
        BuffersEstateFixture fixture = BuffersEstateFixture.create(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8, ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {fog(), fog()},
            new boolean[] {true, true}, 0);
        EstateCore core = fixture.core;
        core.fullClearRequired = false;
        core.openFrameId = 7;

        ClearExecutionPlan unflipped = executor.plan(core, request(7, false), 7);
        List<TextureHandle> expected = List.of(core.colorPairs.get(0).readSide(),
            core.colorPairs.get(1).readSide());
        assertEquals(1, unflipped.batches().size());
        assertEquals(expected, unflipped.batches().get(0).textures());

        core.colorPairs.get(0).flipped = true;
        ClearExecutionPlan flipped = executor.plan(core, request(7, false), 7);
        assertEquals(1, flipped.batches().size());
        assertEquals(List.of(core.colorPairs.get(0).sideA, core.colorPairs.get(0).sideB,
            core.colorPairs.get(1).sideA), flipped.batches().get(0).textures(),
            "a flipped pair clears sides A AND B; the unflipped pair only its read side");
    }

    @Test
    void clearDisabledRowsContributeNoTargets() {
        BuffersEstateFixture allDisabled = BuffersEstateFixture.create(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8, ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {fog(), fog()},
            new boolean[] {false, false}, 0);
        allDisabled.core.openFrameId = 7;
        assertTrue(executor.plan(allDisabled.core, request(7, true), 7).isEmpty(),
            "no enabled row, no clear work");

        BuffersEstateFixture half = BuffersEstateFixture.create(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8, ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {fog(), fog()},
            new boolean[] {true, false}, 0);
        EstateCore halfCore = half.core;
        halfCore.fullClearRequired = false;
        halfCore.openFrameId = 7;
        ClearExecutionPlan plan = executor.plan(halfCore, request(7, true), 7);
        assertEquals(1, plan.batches().size());
        assertEquals(List.of(halfCore.colorPairs.get(0).sideA, halfCore.colorPairs.get(0).sideB),
            plan.batches().get(0).textures(),
            "the disabled row's sides never enter the plan");
    }

    @Test
    void frameMismatchYieldsAnEmptyPlanStampedWithTheForeignFrame() {
        BuffersEstateFixture fixture = BuffersEstateFixture.create(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {fog()}, new boolean[] {true}, 0);
        EstateCore core = fixture.core;
        core.openFrameId = 7;

        ClearExecutionPlan plan = executor.plan(core, request(99, true), 99);
        assertTrue(plan.isEmpty());
        assertEquals(99, plan.frameId());
        assertEquals(1, plan.estateGeneration());
        assertEquals(0, plan.depthAttachmentEpoch());
    }

    @Test
    void integerRowsConvertToDistinctTypedBatchesThatNeverAlias() {
        BuffersEstateFixture fixture = BuffersEstateFixture.create(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA32I, ColorInternalFormat.RGBA32UI},
            new ResourceClearPolicy[] {
                new ResourceClearPolicy.Constant(1.9, -1.9, 3.0e9, -3.0e9),
                new ResourceClearPolicy.Constant(7.9, -1.0, 5.0e9, 0.5)},
            new boolean[] {true, true}, 0);
        EstateCore core = fixture.core;
        core.fullClearRequired = false;
        core.openFrameId = 7;

        ClearExecutionPlan plan = executor.plan(core, request(7, false), 7);
        assertEquals(2, plan.batches().size(),
            "signed and unsigned payloads are distinct groups even at equal component counts");
        ClearExecutionPlan.Batch signedBatch = plan.batches().get(0);
        ClearExecutionPlan.Batch unsignedBatch = plan.batches().get(1);
        ColorClearValue.Signed signed = assertInstanceOf(ColorClearValue.Signed.class,
            signedBatch.values().get(0));
        assertEquals(1, signed.r());
        assertEquals(-1, signed.g());
        assertEquals(Integer.MAX_VALUE, signed.b());
        assertEquals(Integer.MIN_VALUE, signed.a());
        assertEquals(List.of(core.colorPairs.get(0).readSide()), signedBatch.textures());
        ColorClearValue.Unsigned unsigned = assertInstanceOf(ColorClearValue.Unsigned.class,
            unsignedBatch.values().get(0));
        assertEquals(7L, unsigned.r());
        assertEquals(0L, unsigned.g());
        assertEquals(4294967295L, unsigned.b());
        assertEquals(0L, unsigned.a());
        assertEquals(List.of(core.colorPairs.get(1).readSide()), unsignedBatch.textures());
    }

    @Test
    void executeClearsEveryOccupiedRoutePositionAndReusesTheCachedFramebuffer() {
        BuffersEstateFixture fixture = BuffersEstateFixture.create(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8, ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {fog(), fog()},
            new boolean[] {true, true}, 0);
        EstateCore core = fixture.core;
        core.openFrameId = 7;
        ClearExecutionPlan plan = executor.plan(core, request(7, false), 7);
        assertEquals(1, plan.batches().size());

        assertEquals(ClearExecutionResult.SUCCESS, executor.execute(core, plan));
        assertFalse(core.fullClearRequired, "a successful clear retires the pending full clear");
        assertEquals(1, fixture.exactOpCount("framebuffers.create"),
            "one clear FBO for the one batch");
        assertEquals(4, fixture.exactOpCount("framebuffers.clearColorAttachment"));
        assertEquals(4, fixture.exactOpCount("framebuffers.clearColorAttachment.restore"));
        ReplayAssertions.of(fixture.device.log())
            .calledInOrder("framebuffers.create", "framebuffers.attachColor",
                "framebuffers.drawBuffers", "framebuffers.bind",
                "framebuffers.clearColorAttachment")
            .drawBuffersWere(List.of(new FramebufferDrawSlot.Attachment(0),
                new FramebufferDrawSlot.Attachment(1), new FramebufferDrawSlot.Attachment(2),
                new FramebufferDrawSlot.Attachment(3)));
        for (EstateCore.ColorPair pair : core.colorPairs) {
            assertFalse(pair.flipped, "a clear never toggles flip");
            assertEquals(PhysicalSide.A, pair.committedMain, "a clear never moves committed main");
        }

        assertEquals(ClearExecutionResult.SUCCESS, executor.execute(core, plan));
        assertEquals(1, fixture.exactOpCount("framebuffers.create"),
            "the second execution reuses the cached clear FBO");
        assertEquals(8, fixture.exactOpCount("framebuffers.clearColorAttachment"),
            "every occupied route position clears again");
    }

    @Test
    void executedPayloadsAreTypedToTheRealizedFormatOfEachRow() {
        BuffersEstateFixture fixture = BuffersEstateFixture.create(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA32I, ColorInternalFormat.RGBA32UI},
            new ResourceClearPolicy[] {
                new ResourceClearPolicy.Constant(1.9, -1.9, 3.0e9, -3.0e9),
                new ResourceClearPolicy.Constant(7.9, -1.0, 5.0e9, 0.5)},
            new boolean[] {true, true}, 0);
        EstateCore core = fixture.core;
        core.fullClearRequired = false;
        core.openFrameId = 7;
        ClearExecutionPlan plan = executor.plan(core, request(7, false), 7);

        assertEquals(ClearExecutionResult.SUCCESS, executor.execute(core, plan));
        List<GLCall> clears = fixture.device.log().calls().stream()
            .filter(call -> call.op().equals("framebuffers.clearColorAttachment")).toList();
        assertEquals(2, clears.size());
        assertInstanceOf(ColorClearValue.Signed.class, clears.get(0).args().get(2),
            "the RGBA32I attachment receives a signed typed clear");
        assertInstanceOf(ColorClearValue.Unsigned.class, clears.get(1).args().get(2),
            "the RGBA32UI attachment receives an unsigned typed clear");
        assertEquals(List.of(), fixture.device.drainErrors(),
            "the recorder validates type-vs-format and must record no driver error");
    }
}
