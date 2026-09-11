// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferPublicationResult;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.PublishedBufferEstate;
import com.schmaloogium.engine.buffers.ResourceProjectionUnavailableReason;
import com.schmaloogium.engine.buffers.ShadowEstateUnavailable;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.frame.DriverReloadRequest;
import com.schmaloogium.engine.frame.ReloadIntent;
import com.schmaloogium.engine.frame.ReloadReason;
import com.schmaloogium.engine.frame.ReloadReasons;
import com.schmaloogium.engine.frame.ReloadStatus;
import com.schmaloogium.engine.frame.lifecycle.FrameComposition;
import com.schmaloogium.engine.frame.spi.FrameRenderPort;
import com.schmaloogium.engine.frame.spi.FullscreenDraw;
import com.schmaloogium.engine.frame.spi.PortResult;
import com.schmaloogium.engine.frame.spi.StateSnapshot;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackLoadFailure;
import com.schmaloogium.engine.pack.PackLoadFailureCode;
import com.schmaloogium.engine.pack.PackLoadResult;
import com.schmaloogium.engine.pack.PackSelection;
import com.schmaloogium.engine.uniforms.RegistryGenerationAdoptionResult;
import com.schmaloogium.engine.uniforms.UniformBuildResult;
import com.schmaloogium.engine.uniforms.UniformRetirementReason;
import com.schmaloogium.mod.core.pipeline.PipelineFixtures.CollectingDiagnostics;
import com.schmaloogium.mod.core.pipeline.PipelineFixtures.FakeStages;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The §4.1 transaction, branch by branch: call order, ownership (what is closed, what is
 * transferred), retirement reasons, exactly one version increment per outcome, the sink
 * always empty on failure and present only after every acceptance, one diagnostic.
 */
class PipelineTransactionTest {

    @TempDir
    static Path gameDir;

    static PackConfiguration configuration;
    static PackSelection resolved;

    FakeStages stages;
    CollectingDiagnostics diagnostics;
    List<Optional<FrameComposition>> installs;
    PackSelection selection;
    PipelineTransaction transaction;

    @BeforeAll
    static void loadPack() throws Exception {
        PipelineFixtures.Loaded loaded = PipelineFixtures.loadMinimal(gameDir);
        configuration = loaded.configuration();
        resolved = loaded.selection();
    }

    @BeforeEach
    void setUp() {
        stages = new FakeStages(configuration);
        diagnostics = new CollectingDiagnostics();
        installs = new ArrayList<>();
        selection = resolved;
        transaction = new PipelineTransaction(new PipelineTransaction.Services(
                stages, () -> selection, EngineOptionData::empty, () -> DimensionKey.BASE,
                () -> new Extent2i(854, 480), () -> 0L, new InertPort(), installs::add, diagnostics));
    }

    private static DriverReloadRequest select() {
        return new DriverReloadRequest(new ReloadIntent.Select(new PackSelection.Off()),
                new ReloadReasons(Set.of(ReloadReason.PACK_SELECTION)), 0L);
    }

    private long errors() {
        return diagnostics.reports.stream()
                .filter(d -> d.severity() == DiagnosticSeverity.ERROR).count();
    }

    @Test
    void happyPath_installsAfterEveryAcceptanceInContractOrder() {
        ReloadStatus status = transaction.drain(select());

        assertInstanceOf(ReloadStatus.Active.class, status);
        assertEquals(List.of("load", "uniforms@0", "compile@" + DimensionKey.BASE,
                "estate@854x480/1.0", "compose", "publishReady", "publishEstate@fp"), stages.calls);
        assertEquals(List.of(Optional.empty(), Optional.of(installs.get(1).orElseThrow())),
                installs);
        assertEquals(1L, ((ReloadStatus.Active) status).version().value());
        assertEquals(List.of(1L), stages.runtime.adoptedGenerations);
        assertTrue(stages.runtime.retirements.isEmpty());
        assertEquals(0, stages.registryClosed + stages.barrierClosed + stages.estateClosed);
        assertEquals(0, errors());
        FrameComposition composition = installs.get(1).orElseThrow();
        assertEquals(1L, composition.registry().generation());
        assertEquals(1L, composition.estate().generation());
        assertTrue(composition.shadowSlot().isEmpty());
        assertTrue(composition.texturePublication().isEmpty());
        assertEquals(configuration.pack(), composition.identity().pack());
        assertTrue(transaction.active().isPresent());
    }

    @Test
    void offSelection_publishesBothOffAndAnswersOffWithOneIncrement() {
        selection = new PackSelection.Off();

        ReloadStatus status = transaction.drain(select());

        assertInstanceOf(ReloadStatus.Off.class, status);
        assertEquals(1L, ((ReloadStatus.Off) status).version().value());
        assertEquals(List.of("publishEstateOff", "publishOff"), stages.calls);
        assertEquals(List.of(Optional.empty()), installs);
        assertEquals(0, errors());
        assertTrue(transaction.active().isEmpty());
    }

    @Test
    void loadFailure_goesOffWithOneErrorDiagnostic() {
        stages.loadAnswer = new PackLoadResult.Failed(new PackLoadFailure(
                PackLoadFailureCode.values()[0],
                new com.schmaloogium.engine.diag.EngineDiagnostic(DiagnosticSeverity.ERROR,
                        com.schmaloogium.engine.diag.UserChannel.LOG_ONLY, "test.load",
                        List.of(), "detail", "schmaloogium.pack")));

        ReloadStatus status = transaction.drain(select());

        assertEquals("schmaloogium.pipeline.load", ((ReloadStatus.Failed) status).failure().diagnosticId());
        assertEquals(List.of("load", "publishEstateOff", "publishOff"), stages.calls);
        assertEquals(1, diagnostics.reports.stream()
                .filter(d -> d.messageKey().equals("schmaloogium.error.pipeline.load")).count());
        assertEquals(1L, transaction.currentVersion().value());
    }

    @Test
    void uniformFailure_neverCompiles() {
        stages.uniformsAnswer = new UniformBuildResult.Failure("test.uniforms");

        ReloadStatus status = transaction.drain(select());

        assertEquals("schmaloogium.pipeline.uniforms",
                ((ReloadStatus.Failed) status).failure().diagnosticId());
        assertFalse(stages.calls.contains("compile@" + DimensionKey.BASE));
        assertTrue(stages.runtime.retirements.isEmpty());
    }

    @Test
    void compileOff_retiresCandidateRuntimeAndRoutesProgramFailuresToGui() {
        stages.compileOff = true;

        ReloadStatus status = transaction.drain(select());

        assertEquals("schmaloogium.pipeline.compile",
                ((ReloadStatus.Failed) status).failure().diagnosticId());
        assertEquals(List.of(UniformRetirementReason.UNPUBLISHED_ABORT), stages.runtime.retirements);
        assertEquals(List.of(Optional.empty()), installs);
        assertEquals(1, errors());
    }

    @Test
    void estateAwaitingDepth_closesRegistryAndLatchesRetry() {
        stages.estateAnswer = new PipelineStages.EstateHandle.AwaitingMainDepth(5L);

        ReloadStatus status = transaction.drain(select());

        assertEquals("schmaloogium.pipeline.awaiting-main-depth",
                ((ReloadStatus.Failed) status).failure().diagnosticId());
        assertEquals(1, stages.registryClosed);
        assertEquals(5L, transaction.awaitingMainDepthVersion());
        assertEquals(List.of(UniformRetirementReason.UNPUBLISHED_ABORT), stages.runtime.retirements);
    }

    @Test
    void estateOff_closesRegistry() {
        stages.estateAnswer = new PipelineStages.EstateHandle.Off(new BufferFailure(
                BufferFailureCode.TEXTURE_ALLOCATION, "k", "test.estate", List.of(),
                Optional.empty(), Optional.empty()));

        transaction.drain(select());

        assertEquals(1, stages.registryClosed);
        assertEquals(0, stages.estateClosed);
        assertEquals(-1L, transaction.awaitingMainDepthVersion());
    }

    @Test
    void barrierInvalid_closesEstateAndRegistry() {
        stages.barrierInvalid = true;

        ReloadStatus status = transaction.drain(select());

        assertEquals("schmaloogium.pipeline.barrier",
                ((ReloadStatus.Failed) status).failure().diagnosticId());
        assertEquals(1, stages.registryClosed);
        assertEquals(1, stages.estateClosed);
        assertEquals(0, stages.barrierClosed);
    }

    @Test
    void publishRejected_closesAllThreeCandidates() {
        stages.publishAnswer = FakeStages.rejected(
                com.schmaloogium.engine.registry.PublishedRegistry.off(0L, () -> {
                    throw new UnsupportedOperationException();
                }));

        transaction.drain(select());

        assertEquals(1, stages.registryClosed);
        assertEquals(1, stages.barrierClosed);
        assertEquals(1, stages.estateClosed);
        assertEquals(List.of(UniformRetirementReason.UNPUBLISHED_ABORT), stages.runtime.retirements);
        assertTrue(stages.calls.contains("publishOff"));
    }

    @Test
    void publishRecoveredOff_neverClosesTransferredCandidatesNorRepublishesOff() {
        stages.publishAnswer = FakeStages.recoveredOff();

        ReloadStatus status = transaction.drain(select());

        assertEquals("schmaloogium.pipeline.publish",
                ((ReloadStatus.Failed) status).failure().diagnosticId());
        assertEquals(0, stages.registryClosed);
        assertEquals(0, stages.barrierClosed);
        assertEquals(1, stages.estateClosed);
        assertTrue(stages.calls.contains("publishEstateOff"));
        assertFalse(stages.calls.contains("publishOff"));
        assertEquals(1L, transaction.currentVersion().value());
    }

    @Test
    void adoptionRejected_compensatesAfterRegistryAcceptance() {
        stages.runtime.adoption = RegistryGenerationAdoptionResult.REJECTED_RETIRED_GENERATION;

        ReloadStatus status = transaction.drain(select());

        assertEquals("schmaloogium.pipeline.adopt",
                ((ReloadStatus.Failed) status).failure().diagnosticId());
        assertEquals(0, stages.registryClosed);
        assertEquals(1, stages.estateClosed);
        assertEquals(List.of("publishEstateOff", "publishOff"),
                stages.calls.subList(stages.calls.size() - 2, stages.calls.size()));
    }

    @Test
    void estateConsumerFailed_isAlreadyOffAndNotCallerClosed() {
        stages.estatePublishAnswer = new BufferPublicationResult.ConsumerFailed(2L,
                new PublishedBufferEstate(3L, Optional.empty(), new BufferResourceSnapshot.Unavailable(
                        ResourceProjectionUnavailableReason.SHADERS_OFF)), "consumer", 0);

        transaction.drain(select());

        assertEquals(0, stages.estateClosed);
        assertEquals(List.of(Optional.empty()), installs);
    }

    @Test
    void estateViewGenerationMismatch_compensates() {
        stages.estatePublishAnswer = new BufferPublicationResult.Published(new PublishedBufferEstate(
                2L, Optional.of(stages.estateView), new BufferResourceSnapshot.Unavailable(
                        ResourceProjectionUnavailableReason.SHADERS_OFF)));

        ReloadStatus status = transaction.drain(select());

        assertEquals("schmaloogium.pipeline.estate-view",
                ((ReloadStatus.Failed) status).failure().diagnosticId());
    }

    @Test
    void shadowUnavailable_isAcceptedAsNeutral() {
        stages.estateView.shadow = new ShadowEstateUnavailable(new BufferFailure(
                BufferFailureCode.CAPABILITY_LIMIT, "k", "d", List.of(), Optional.empty(),
                Optional.empty()), 1L);

        assertInstanceOf(ReloadStatus.Active.class, transaction.drain(select()));
    }

    @Test
    void shadowAvailable_isNeutralizedAtV01() {
        PipelineFixtures.FakeShadowView shadow = new PipelineFixtures.FakeShadowView(1L);
        stages.estateView.shadow = new com.schmaloogium.engine.buffers.ShadowEstateAvailable(shadow);

        assertInstanceOf(ReloadStatus.Active.class, transaction.drain(select()));
        assertEquals(List.of(com.schmaloogium.engine.buffers.ShadowNeutralReason.EXPLICIT_FEATURE_DISABLE),
                shadow.neutralizations);
    }

    @Test
    void shadowNeutralizationRejected_compensates() {
        PipelineFixtures.FakeShadowView shadow = new PipelineFixtures.FakeShadowView(1L);
        shadow.answer = new com.schmaloogium.engine.buffers.ShadowNeutralizationResult.Rejected(
                com.schmaloogium.engine.buffers.ShadowProtocolRejection.STALE_GENERATION);
        stages.estateView.shadow = new com.schmaloogium.engine.buffers.ShadowEstateAvailable(shadow);

        ReloadStatus status = transaction.drain(select());

        assertEquals("schmaloogium.pipeline.shadow-neutralize",
                ((ReloadStatus.Failed) status).failure().diagnosticId());
    }

    @Test
    void shadowNotRequestedWhilePlanned_isATupleMismatch() {
        stages.estateView.shadow = new com.schmaloogium.engine.buffers.ShadowEstateNotRequested(1L);

        ReloadStatus status = transaction.drain(select());

        assertEquals("schmaloogium.pipeline.shadow-mismatch",
                ((ReloadStatus.Failed) status).failure().diagnosticId());
        assertEquals(List.of(Optional.empty()), installs);
    }

    @Test
    void replacement_retiresTheOldRuntimeOnceAfterTheNewAcceptance() {
        transaction.drain(select());
        var oldRuntime = stages.runtime;
        var newRuntime = stages.freshRuntime();
        stages.publisherGeneration = 1L;

        ReloadStatus status = transaction.drain(select());

        assertInstanceOf(ReloadStatus.Active.class, status);
        assertEquals(List.of(UniformRetirementReason.REPLACEMENT), oldRuntime.retirements);
        assertTrue(newRuntime.retirements.isEmpty());
        assertEquals(2L, ((ReloadStatus.Active) status).version().value());
        assertEquals("uniforms@1", stages.calls.get(1));
    }

    @Test
    void replacementFailure_retiresOldAndStaysOff() {
        transaction.drain(select());
        var oldRuntime = stages.runtime;
        var newRuntime = stages.freshRuntime();
        stages.compileOff = true;

        ReloadStatus status = transaction.drain(select());

        assertInstanceOf(ReloadStatus.Failed.class, status);
        assertEquals(List.of(UniformRetirementReason.REPLACEMENT), oldRuntime.retirements);
        assertEquals(List.of(UniformRetirementReason.UNPUBLISHED_ABORT), newRuntime.retirements);
        assertTrue(transaction.active().isEmpty());
        assertEquals(Optional.empty(), installs.get(installs.size() - 1));
    }

    @Test
    void unexpectedException_isContainedAsFailure() {
        transaction = new PipelineTransaction(new PipelineTransaction.Services(
                stages, () -> {
                    throw new IllegalStateException("boom");
                }, EngineOptionData::empty, () -> DimensionKey.BASE,
                () -> new Extent2i(1, 1), () -> 0L, new InertPort(), installs::add, diagnostics));

        ReloadStatus status = transaction.drain(select());

        assertEquals("schmaloogium.pipeline.unexpected",
                ((ReloadStatus.Failed) status).failure().diagnosticId());
        assertEquals(List.of(Optional.empty()), installs);
        assertEquals(1, errors());
    }

    @Test
    void currentStatus_reflectsThePublication() {
        assertInstanceOf(ReloadStatus.Off.class, transaction.currentStatus());
        transaction.drain(select());
        assertInstanceOf(ReloadStatus.Active.class, transaction.currentStatus());
    }

    @Test
    void renderMultipliers_comeFromEngineOptions() {
        transaction = new PipelineTransaction(new PipelineTransaction.Services(
                stages, () -> selection,
                () -> new EngineOptionData(Map.of("renderResMul", "0.5", "shadowResMul", "2")),
                () -> DimensionKey.BASE, () -> new Extent2i(10, 20), () -> 0L, new InertPort(),
                installs::add, diagnostics));

        transaction.drain(select());

        assertTrue(stages.calls.contains("estate@10x20/0.5"));
    }

    private static final class InertPort implements FrameRenderPort {

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
