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
        assertEquals(List.of("load", "textures", "uniforms@0", "compile@" + DimensionKey.BASE,
                "estate@854x480/1.0", "compose", "publishReady", "publishEstate@fp",
                "textureInputs", "attachTextures"), stages.calls);
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
        assertEquals(composition.estate().generation(), composition.texturePublication().id().generation());
        assertEquals(0.125, composition.handDepthMultiplier());
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
        assertTrue(installs.stream().allMatch(Optional::isEmpty));
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
        assertTrue(installs.stream().allMatch(Optional::isEmpty));
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

    private static PipelineTransaction.IdServices idServices(
            java.util.function.Supplier<Optional<com.schmaloogium.mod.glue.id.RegistryProjection.Projection>> registries,
            boolean healthyVertexHooks,
            List<com.schmaloogium.mod.glue.id.IdPublication> publications) {
        return new PipelineTransaction.IdServices(registries,
                com.schmaloogium.engine.config.id.ModIdSourceSnapshot::empty,
                com.schmaloogium.engine.config.id.HandLightPolicy::allDefault,
                () -> new com.schmaloogium.mod.glue.vertex.VertexHookHealth(healthyVertexHooks,
                        healthyVertexHooks ? List.of() : List.of("H10-TASK=absent")),
                () -> 42L,
                new com.schmaloogium.engine.config.IdMappingParserImpl(),
                publications::add);
    }

    private static com.schmaloogium.mod.glue.id.RegistryProjection.Projection smallRegistry() {
        var stone = new Object();
        return com.schmaloogium.mod.glue.id.RegistryProjection.project(3,
                List.of(new com.schmaloogium.mod.glue.id.RegistryProjection.BlockInput("minecraft", "stone", 1,
                        List.of(new com.schmaloogium.mod.glue.id.RegistryProjection.StateInput(stone, 0,
                                new java.util.TreeMap<>(), 3, true, 0)))),
                List.of(new com.schmaloogium.mod.glue.id.RegistryProjection.ItemInput(new Object(), "minecraft",
                        "stone", Optional.of(stone))),
                List.of(new com.schmaloogium.mod.glue.id.RegistryProjection.EntityInput(new Object(), "minecraft",
                        "cow")),
                com.schmaloogium.engine.config.id.TagMembershipSnapshot.empty());
    }

    @Test
    void ids_publishWithTheInstallAndClearOnTheNextDrain() {
        List<com.schmaloogium.mod.glue.id.IdPublication> publications = new ArrayList<>();
        transaction = new PipelineTransaction(new PipelineTransaction.Services(
                stages, () -> selection, EngineOptionData::empty, () -> DimensionKey.BASE,
                () -> new Extent2i(854, 480), () -> 0L, new InertPort(), installs::add, diagnostics,
                PipelineTransaction.ShadowServices.disabled(),
                idServices(() -> Optional.of(smallRegistry()), true, publications)));

        ReloadStatus status = transaction.drain(select());

        assertInstanceOf(ReloadStatus.Active.class, status);
        FrameComposition composition = installs.get(1).orElseThrow();
        assertTrue(composition.idRuntime().isPresent(), "the id runtime is published with the install");
        assertEquals(1L, composition.idRuntime().get().generation());
        assertEquals(1, composition.idRuntime().get().aliases().blockId(0).shaderId(),
                "legacy numeric fallback: stone's live id 1 (block.properties absent)");
        // Healthy hooks but no program declares a classic attribute: vanilla formats.
        assertTrue(composition.vertexEpoch().isEmpty());
        assertEquals(2, publications.size(), "one clear at step 1, one publication at install");
        assertTrue(publications.get(0).runtime().isEmpty());
        assertEquals(composition.idRuntime(), publications.get(1).runtime());
        assertEquals(-1, publications.get(1).maps().stateOrdinal(smallRegistryStoneIsUnknownHere()));
        assertEquals(0, errors());

        // The next drain retires the previous id runtime with its pipeline.
        ReloadStatus second = transaction.drain(select());
        assertInstanceOf(ReloadStatus.Active.class, second, () -> second + " " + diagnostics.reports);
        assertEquals(2L, installs.get(3).orElseThrow().idRuntime().get().generation());
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> composition.idRuntime().get().aliases());
    }

    /** Identity maps are keyed by the projection's own objects; a foreign object is unknown (-1). */
    private static Object smallRegistryStoneIsUnknownHere() {
        return new Object();
    }

    @Test
    void ids_offWhenTheRegistrySnapshotIsUnavailable_pipelineStillInstalls() {
        List<com.schmaloogium.mod.glue.id.IdPublication> publications = new ArrayList<>();
        transaction = new PipelineTransaction(new PipelineTransaction.Services(
                stages, () -> selection, EngineOptionData::empty, () -> DimensionKey.BASE,
                () -> new Extent2i(854, 480), () -> 0L, new InertPort(), installs::add, diagnostics,
                PipelineTransaction.ShadowServices.disabled(),
                idServices(Optional::empty, false, publications)));

        ReloadStatus status = transaction.drain(select());

        assertInstanceOf(ReloadStatus.Active.class, status);
        FrameComposition composition = installs.get(1).orElseThrow();
        assertTrue(composition.idRuntime().isEmpty());
        assertTrue(composition.vertexEpoch().isEmpty());
        assertTrue(publications.get(1).runtime().isEmpty());
        assertEquals(0, errors());
    }

    @Test
    void shadowAvailable_withHealthyHooksAndAShadowProgram_installsTheSlot() {
        PipelineFixtures.FakeShadowView shadow = new PipelineFixtures.FakeShadowView(1L);
        stages.estateView.shadow = new com.schmaloogium.engine.buffers.ShadowEstateAvailable(shadow);
        java.util.List<com.schmaloogium.engine.shadow.ShadowPolicy> ports = new java.util.ArrayList<>();
        java.util.List<Boolean> readiness = new java.util.ArrayList<>();
        transaction = new PipelineTransaction(new PipelineTransaction.Services(
                stages, () -> selection, EngineOptionData::empty, () -> DimensionKey.BASE,
                () -> new Extent2i(854, 480), () -> 0L, new InertPort(), installs::add, diagnostics,
                new PipelineTransaction.ShadowServices(
                        () -> com.schmaloogium.engine.shadow.ShadowHookHealth.of(
                                com.schmaloogium.engine.shadow.ShadowHookHealth.catalogue().stream()
                                        .map(id -> new com.schmaloogium.engine.shadow.ShadowHookRow(id, 1, 1,
                                                com.schmaloogium.engine.shadow.HookDisposition.HEALTHY)).toList()),
                        policy -> {
                            ports.add(policy);
                            return new PipelineFixtures.InertWorldPort();
                        },
                        () -> true,
                        readiness::add)));

        ReloadStatus status = transaction.drain(select());

        assertInstanceOf(ReloadStatus.Active.class, status);
        FrameComposition composition = installs.get(1).orElseThrow();
        assertTrue(composition.shadowSlot().isPresent(), "a ready plan over an available estate installs the slot");
        assertTrue(shadow.neutralizations.isEmpty(), "the estate is used, not neutralized");
        assertEquals(1, ports.size(), "one world port over the mapped policy");
        assertEquals(com.schmaloogium.engine.shadow.ShadowPolicy.ShadowContent.ALL, ports.get(0).content());
        assertEquals(List.of(true), readiness, "blob shadows are suppressed for the publication");
        assertEquals(0, errors());
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
        assertTrue(installs.stream().allMatch(Optional::isEmpty));
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
        assertTrue(stages.calls.contains("uniforms@1"));
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
        assertTrue(installs.stream().allMatch(Optional::isEmpty));
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

    @Test
    void textures_publishAgainstAcceptedGenerationsAndResourceEpoch() {
        stages.estateView = new PipelineFixtures.FakeEstateView(17L);
        stages.estatePublishAnswer = new BufferPublicationResult.Published(new PublishedBufferEstate(
                17L, Optional.of(stages.estateView), stages.estateView.resources()));
        stages.publishAnswer = new com.schmaloogium.engine.registry.PublicationResult.Accepted(
                new com.schmaloogium.engine.registry.PublishedRegistry(29L,
                        Optional.of(new PipelineFixtures.FakeRegistryView("fp")), Optional.empty(),
                        () -> { throw new AssertionError("no registry release in this fixture"); }));
        transaction = new PipelineTransaction(new PipelineTransaction.Services(
                stages, () -> selection, EngineOptionData::empty, () -> DimensionKey.BASE,
                () -> new Extent2i(854, 480), () -> 43L, new InertPort(), installs::add, diagnostics));

        assertInstanceOf(ReloadStatus.Active.class, transaction.drain(select()));

        FrameComposition composition = installs.getLast().orElseThrow();
        var publication = composition.texturePublication();
        assertEquals(17L, publication.id().generation());
        assertEquals(29L, publication.registryGeneration());
        assertEquals(43L, publication.resourceReloadEpoch());
        assertEquals(composition.registry().registry().orElseThrow().fingerprint(), publication.registryFingerprint());
    }

    @Test
    void textures_wrongAcceptedEstateNeverInstallsAndClosesCandidate() {
        stages.textureEstateOffset = 1L;

        assertInstanceOf(ReloadStatus.Failed.class, transaction.drain(select()));

        assertTrue(installs.stream().allMatch(Optional::isEmpty));
        assertTrue(transaction.active().isEmpty());
        org.junit.jupiter.api.Assertions.assertNull(stages.attachedTextures);
        assertTextureOwnerClosed(0, 0);
    }

    @Test
    void textures_sourceEpochRejectionClosesCandidateWithoutRevivingPriorPublication() {
        assertInstanceOf(ReloadStatus.Active.class, transaction.drain(select()));
        stages.freshRuntime();
        stages.textureSourceEpochOffset = 1L;

        assertInstanceOf(ReloadStatus.Failed.class, transaction.drain(select()));

        assertTrue(transaction.active().isEmpty());
        assertEquals(Optional.empty(), installs.getLast());
        org.junit.jupiter.api.Assertions.assertNull(stages.attachedTextures);
        assertTextureOwnerClosed(0, 0);
        assertTextureOwnerClosed(1, 1);
        assertInstanceOf(ReloadStatus.Off.class, transaction.currentStatus());
    }

    @Test
    void textures_replacementRetiresPriorOwnerButLeavesNewPublicationLive() {
        assertInstanceOf(ReloadStatus.Active.class, transaction.drain(select()));
        stages.freshRuntime();

        assertInstanceOf(ReloadStatus.Active.class, transaction.drain(select()));

        assertTextureOwnerClosed(0, 0);
        org.junit.jupiter.api.Assertions.assertSame(stages.textureOwners.get(1), stages.attachedTextures);
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> stages.textureOwners.get(1).build(stages.textureRequests.get(1)),
                "a live owner already published and cannot build twice");
    }

    @Test
    void resourceRefresh_rejectionClosesBothOwnersAndDoesNotRestoreOldPipeline() {
        var epoch = new java.util.concurrent.atomic.AtomicLong(7L);
        transaction = new PipelineTransaction(new PipelineTransaction.Services(
                stages, () -> selection, EngineOptionData::empty, () -> DimensionKey.BASE,
                () -> new Extent2i(854, 480), epoch::get, new InertPort(), installs::add, diagnostics));
        assertInstanceOf(ReloadStatus.Active.class, transaction.drain(select()));
        stages.calls.clear();
        stages.textureSourceEpochOffset = 1L;
        epoch.set(8L);

        assertInstanceOf(ReloadStatus.Failed.class, transaction.refreshResources());

        assertFalse(stages.calls.contains("load"));
        assertFalse(stages.calls.stream().anyMatch(call -> call.startsWith("uniforms@")));
        assertEquals(List.of(UniformRetirementReason.REPLACEMENT), stages.runtime.retirements);
        assertTextureOwnerClosed(0, 0);
        assertTextureOwnerClosed(1, 1);
        org.junit.jupiter.api.Assertions.assertNull(stages.attachedTextures);
        assertTrue(transaction.active().isEmpty());
        assertEquals(Optional.empty(), installs.getLast());
        stages.calls.clear();
        assertInstanceOf(ReloadStatus.Off.class, transaction.refreshResources());
        assertTrue(stages.calls.isEmpty());
    }

    private void assertTextureOwnerClosed(int owner, int request) {
        var failed = assertInstanceOf(com.schmaloogium.engine.textures.TextureBuildResult.Failed.class,
                stages.textureOwners.get(owner).build(stages.textureRequests.get(request)));
        assertEquals(com.schmaloogium.engine.textures.TextureFailureCode.OWNER_UNAVAILABLE,
                failed.failure().code());
    }

    private static final class InertPort implements FrameRenderPort {

        @Override
        public com.schmaloogium.engine.frame.spi.AtlasBindingEvidence textureEvidence(
                com.schmaloogium.engine.frame.PipelineVersion version, long frameId, boolean hand) {
            throw new AssertionError("pipeline transactions never render");
        }

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
