// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.frame.ReloadReason;
import com.schmaloogium.engine.frame.ReloadStatus;
import com.schmaloogium.engine.frame.spi.FrameRenderPort;
import com.schmaloogium.engine.frame.spi.FullscreenDraw;
import com.schmaloogium.engine.frame.spi.PortResult;
import com.schmaloogium.engine.frame.spi.StateSnapshot;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.PackSelection;
import com.schmaloogium.mod.core.pipeline.PipelineFixtures.CollectingDiagnostics;
import com.schmaloogium.mod.core.pipeline.PipelineFixtures.FakeStages;
import com.schmaloogium.mod.gui.model.ReloadCause;
import com.schmaloogium.mod.gui.model.ReloadCoordinator;
import com.schmaloogium.mod.gui.model.ReloadTrigger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

/** Merge-then-drain: N submits, one transaction per drain; NONE touches no engine state. */
class ShaderReloadCoordinatorTest {

    @TempDir
    static Path gameDir;

    static PipelineFixtures.Loaded loaded;

    @BeforeAll
    static void loadPack() throws Exception {
        loaded = PipelineFixtures.loadMinimal(gameDir);
    }

    private record Rig(ShaderReloadCoordinator coordinator, FakeStages stages) {
    }

    private static Rig rig() {
        FakeStages stages = new FakeStages(loaded.configuration());
        PipelineTransaction transaction = new PipelineTransaction(new PipelineTransaction.Services(
                stages, loaded::selection, EngineOptionData::empty, () -> DimensionKey.BASE,
                () -> new Extent2i(2, 2), () -> 0L, new NoPort(), new ArrayList<Optional<
                        com.schmaloogium.engine.frame.lifecycle.FrameComposition>>()::add,
                new CollectingDiagnostics()));
        return new Rig(new ShaderReloadCoordinator(transaction, () -> true, loaded::selection), stages);
    }

    @Test
    void burst_ofSubmitsDrainsAsOneTransaction() {
        Rig rig = rig();
        for (int i = 0; i < 5; i++) {
            rig.coordinator.submit(ReloadTrigger.PACK_SELECTION_CHANGED.request());
        }
        Optional<ReloadStatus> status = rig.coordinator.drainOnce();
        assertInstanceOf(ReloadStatus.Active.class, status.orElseThrow());
        assertEquals(1, rig.stages.calls.stream().filter("load"::equals).count());
        assertTrue(rig.coordinator.drainOnce().isEmpty());
    }

    @Test
    void resourceOnlyRequest_answersCurrentStatusWithoutEngineWork() {
        Rig rig = rig();
        rig.coordinator.submit(ReloadTrigger.RESOURCE_MANAGER_RELOAD.request());
        Optional<ReloadStatus> status = rig.coordinator.drainOnce();
        assertInstanceOf(ReloadStatus.Off.class, status.orElseThrow());
        assertTrue(rig.stages.calls.isEmpty());
    }

    @Test
    void mergedLifecycle_takesTheMaximum() {
        Rig rig = rig();
        rig.coordinator.submit(ReloadTrigger.RESOURCE_MANAGER_RELOAD.request());
        rig.coordinator.submit(ReloadTrigger.OPTION_APPLY_OR_DONE_DIRTY.request());
        assertInstanceOf(ReloadStatus.Active.class, rig.coordinator.drainOnce().orElseThrow());
    }

    @Test
    void causeMapping_isTotal() {
        for (ReloadCause cause : ReloadCause.values()) {
            assertTrue(ShaderReloadCoordinator.reason(cause) != null);
        }
        assertEquals(ReloadReason.PACK_SELECTION, ShaderReloadCoordinator.reason(ReloadCause.KEYBIND));
        assertEquals(ReloadReason.OPTION_CHANGE, ShaderReloadCoordinator.reason(ReloadCause.PROFILE_APPLY));
        assertEquals(ReloadReason.RESOLUTION_MULTIPLIER,
                ShaderReloadCoordinator.reason(ReloadCause.ENGINE_SETTING));
    }

    @Test
    void installedCoordinator_receivesGuiSubmits() {
        Rig rig = rig();
        ReloadCoordinator.install(rig.coordinator);
        try {
            ReloadCoordinator.submitOrReportInert(ReloadTrigger.F3R_KEYBIND.request(), null);
            assertInstanceOf(ReloadStatus.Active.class, rig.coordinator.drainOnce().orElseThrow());
        } finally {
            ReloadCoordinator.clear();
        }
    }

    private static final class NoPort implements FrameRenderPort {

        @Override
        public StateSnapshot snapshotState() {
            return new StateSnapshot() {
            };
        }

        @Override
        public PortResult normalizeForEngine() {
            return new PortResult.Completed();
        }

        @Override
        public PortResult bind(com.schmaloogium.engine.buffers.PassDrawTarget target,
                               com.schmaloogium.engine.frame.AnaglyphEye eye) {
            return new PortResult.Completed();
        }

        @Override
        public PortResult drawFullscreen(FullscreenDraw draw) {
            return new PortResult.Completed();
        }

        @Override
        public PortResult restore(StateSnapshot snapshot) {
            return new PortResult.Completed();
        }
    }
}
