// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.BufferInventory;
import com.schmaloogium.engine.buffers.BufferResourceProjection;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.BufferSizing;
import com.schmaloogium.engine.buffers.CapabilityGate;
import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.MainDepthPreparation;
import com.schmaloogium.engine.buffers.MainDepthSnapshot;
import com.schmaloogium.engine.buffers.MainDepthSource;
import com.schmaloogium.engine.buffers.MainDepthRefreshResult;
import com.schmaloogium.engine.buffers.ResourceEvidenceStage;
import com.schmaloogium.engine.buffers.ShadowResourceProjection;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureExtent;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureSpec;
import com.schmaloogium.engine.gl.record.RecordingGLDevice;
import com.schmaloogium.engine.gl.record.ScriptedResponses;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Main-depth refresh outcomes (PHASE_5_DOC §4.8, §8.3 cases 5-7): identity-only versions
 * stay unchanged, same-extent new versions reattach every depth-using FBO and force a full
 * clear, extent changes resize-gate with zero GL and zero mutation, and unavailable or
 * failed sources advance the attachment epoch and stale the estate.
 */
class RefreshMainDepthTest {

    /** Mutable main-depth source; tests swap the delivered snapshot per scenario. */
    private static final class MutableDepthSource implements MainDepthSource {
        MainDepthSnapshot snapshot;

        @Override
        public MainDepthPreparation prepare(Extent2i requiredExtent) {
            return snapshot instanceof MainDepthSnapshot.Available available
                ? new MainDepthPreparation.Ready(available)
                : new MainDepthPreparation.Pending(expectedVersion());
        }

        private long expectedVersion() {
            return snapshot instanceof MainDepthSnapshot.Available available
                ? available.version() : 1L;
        }

        @Override
        public MainDepthSnapshot current() {
            return snapshot;
        }
    }

    private static final class Harness {
        final RecordingGLDevice device;
        final MutableDepthSource depthSource = new MutableDepthSource();
        final EstateCore core;
        final EstateViewImpl view;

        Harness(Extent2i extent, DepthAttachmentFormat format, long version) {
            GLCapabilityProfile profile = new GLCapabilityProfile(3, 3, "3.30 refresh profile",
                "vendor", "renderer", 8, 8, 16, 16, 4096, 256, 0, Set.of());
            this.device = new RecordingGLDevice(profile, new ScriptedResponses());
            BufferResourceProjection projection = new BufferResourceProjection(
                ResourceEvidenceStage.REALIZED, List.of(), 1,
                new ShadowResourceProjection(0, 0, 0, List.of(), List.of()), false, 0,
                List.of(), List.of(), CapabilityGate.OK, List.of());
            PlanningArtifacts plan = new PlanningArtifacts(
                new BufferSizing(extent, Optional.empty()), new BufferInventory(List.of()),
                1, List.of(), Map.of(), List.of(), List.of(), List.of(), projection);
            this.depthSource.snapshot = snapshot(version, extent, format);
            this.core = new EstateCore(device, diagnostic -> {
            }, new RegistryFingerprint("fp-refresh"), plan,
                new BufferResourceSnapshot.Available(projection), depthSource,
                depthSource.snapshot instanceof MainDepthSnapshot.Available available
                    ? available : null);
            core.generation = 1;

            // One pass FBO plus one copy destination make reattachment observable.
            TextureHandle sourceDepth = device.textures().create("borrowedDepthSource");
            device.textures().allocate(sourceDepth, new TextureSpec.DepthTextureSpec(
                TextureAllocationTarget.TEXTURE_2D, format,
                new com.schmaloogium.engine.gl.PixelLayout.Depth(
                    com.schmaloogium.engine.gl.DepthTransferLayout.DEPTH_COMPONENT_FLOAT),
                new TextureExtent(extent.width(), extent.height(), 1), 1));
            var passFbo = device.framebuffers().create("pass:composite");
            device.framebuffers().attachDepth(passFbo, sourceDepth);
            core.passFbos.put("pass:composite", passFbo);
            TextureHandle depthtex1 = device.textures().create("depthtex1");
            core.copyDestinations.add(core.new DepthDestination(
                new LogicalBuffer(BufferDomain.DEPTH, new BufferIndex(1)), depthtex1,
                device.framebuffers().create("copy:depthtex1")));
            this.view = new EstateViewImpl(core, new ClearExecutor(), new TextureBinder(),
                new ShadowOperator());
        }

        MainDepthSnapshot.Available snapshot(long version, Extent2i extent,
                DepthAttachmentFormat format) {
            TextureHandle platformDepth = new TextureHandle() {
            };
            BorrowedDepthAttachmentHandle borrowed =
                device.framebuffers().borrowDepthAttachment(platformDepth);
            return new MainDepthSnapshot.Available(version, borrowed, format, extent);
        }

        int calls() {
            return device.log().calls().size();
        }
    }

    @Test
    void identityVersionIsUnchangedWithoutGl() {
        Harness harness = new Harness(new Extent2i(32, 32), DepthAttachmentFormat.DEPTH_COMPONENT,
            1L);
        int before = harness.calls();
        MainDepthRefreshResult.Unchanged unchanged = assertInstanceOf(
            MainDepthRefreshResult.Unchanged.class, harness.view.refreshMainDepth());
        assertEquals(1L, unchanged.version());
        assertEquals(before, harness.calls(), "identity refresh performs no GL");
        assertEquals(0L, harness.core.depthAttachmentEpoch, "identity refresh keeps the epoch");
    }

    @Test
    void sameExtentNewVersionReattachesAndForcesFullClear() {
        Harness harness = new Harness(new Extent2i(32, 32), DepthAttachmentFormat.DEPTH_COMPONENT,
            1L);
        harness.depthSource.snapshot = harness.snapshot(2L, new Extent2i(32, 32),
            DepthAttachmentFormat.DEPTH_COMPONENT);
        int before = harness.calls();
        MainDepthRefreshResult.Reattached reattached = assertInstanceOf(
            MainDepthRefreshResult.Reattached.class, harness.view.refreshMainDepth());
        assertEquals(2L, reattached.version());
        assertEquals(1L, reattached.depthAttachmentEpoch());
        assertTrue(harness.core.fullClearRequired, "reattachment forces a full clear");
        long attachCalls = harness.device.log().calls().stream()
            .filter(call -> call.op().equals("framebuffers.attachDepth")).count();
        assertTrue(attachCalls >= 2, "every depth-using FBO reattaches: " + attachCalls);
        assertTrue(harness.calls() > before, "reattachment is observable in the log");
    }

    @Test
    void extentChangeResizeGatesWithZeroGlAndZeroMutation() {
        Harness harness = new Harness(new Extent2i(32, 32), DepthAttachmentFormat.DEPTH_COMPONENT,
            1L);
        harness.depthSource.snapshot = harness.snapshot(2L, new Extent2i(64, 32),
            DepthAttachmentFormat.DEPTH_COMPONENT);
        int before = harness.calls();
        long cachedVersion = harness.core.cachedDepth.version();
        MainDepthRefreshResult.ResizeRequired resize = assertInstanceOf(
            MainDepthRefreshResult.ResizeRequired.class, harness.view.refreshMainDepth());
        assertEquals("MAIN_DEPTH_RESIZE_REQUIRED", resize.failure().code().name());
        assertEquals(before, harness.calls(), "resize gating records zero GL calls");
        assertEquals(cachedVersion, harness.core.cachedDepth.version(),
            "the cached identity is retained");
        assertEquals(0L, harness.core.depthAttachmentEpoch, "no epoch mutation on resize gate");
    }

    @Test
    void unavailableSourceStalesAndAdvancesTheEpoch() {
        Harness harness = new Harness(new Extent2i(32, 32), DepthAttachmentFormat.DEPTH_COMPONENT,
            1L);
        harness.depthSource.snapshot = new MainDepthSnapshot.Unavailable(2L, "diag-refresh-unavailable");
        MainDepthRefreshResult.Failed failed = assertInstanceOf(
            MainDepthRefreshResult.Failed.class, harness.view.refreshMainDepth());
        assertEquals("MAIN_DEPTH_UNAVAILABLE", failed.failure().code().name());
        assertTrue(harness.core.stale, "unavailable main depth stales the estate");
        assertEquals(1L, harness.core.depthAttachmentEpoch, "the epoch advanced exactly once");
        assertEquals(1L, harness.core.cachedDepth.version(),
            "the prior identity is retained for diagnosis");

        // Once stale, every further refresh fails closed without recovery.
        harness.depthSource.snapshot = harness.snapshot(3L, new Extent2i(32, 32),
            DepthAttachmentFormat.DEPTH_COMPONENT);
        MainDepthRefreshResult.Failed stale = assertInstanceOf(
            MainDepthRefreshResult.Failed.class, harness.view.refreshMainDepth());
        assertEquals("STALE_ESTATE", stale.failure().code().name());
        assertEquals(2L, harness.core.depthAttachmentEpoch,
            "every Failed outcome advances the epoch");
    }
}
