// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.engine.buffers.BufferEstateView;
import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferPublicationResult;
import com.schmaloogium.engine.buffers.BufferRuntimeInputs;
import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.PublishedBufferEstate;
import com.schmaloogium.engine.buffers.ShadowEstateAvailable;
import com.schmaloogium.engine.buffers.ShadowEstateNotRequested;
import com.schmaloogium.engine.buffers.ShadowEstateResult;
import com.schmaloogium.engine.buffers.ShadowEstateUnavailable;
import com.schmaloogium.engine.buffers.ShadowNeutralReason;
import com.schmaloogium.engine.buffers.ShadowNeutralizationResult;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.frame.DriverReloadRequest;
import com.schmaloogium.engine.frame.FailureId;
import com.schmaloogium.engine.frame.PipelineIdentity;
import com.schmaloogium.engine.frame.PipelineVersion;
import com.schmaloogium.engine.frame.ReloadIntent;
import com.schmaloogium.engine.frame.ReloadStatus;
import com.schmaloogium.engine.frame.ShadowInvocationSlot;
import com.schmaloogium.engine.shadow.HookDisposition;
import com.schmaloogium.engine.shadow.ShadowHookHealth;
import com.schmaloogium.engine.shadow.ShadowHookRow;
import com.schmaloogium.engine.shadow.ShadowPassBuildInput;
import com.schmaloogium.engine.shadow.ShadowPassBuildResult;
import com.schmaloogium.engine.shadow.ShadowPassFactory;
import com.schmaloogium.engine.shadow.ShadowPlanInput;
import com.schmaloogium.engine.shadow.ShadowPlanResult;
import com.schmaloogium.engine.shadow.ShadowPolicy;
import com.schmaloogium.engine.shadow.ShadowPolicyMapper;
import com.schmaloogium.engine.shadow.ShadowWorldPort;
import com.schmaloogium.engine.shadow.internal.ShadowPlanFactoryImpl;
import com.schmaloogium.engine.frame.lifecycle.FrameComposition;
import com.schmaloogium.engine.frame.lifecycle.ShaderReloadControllerImpl;
import com.schmaloogium.engine.frame.spi.FrameRenderPort;
import com.schmaloogium.engine.log.Log;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.pack.PackLoadResult;
import com.schmaloogium.engine.pack.PackSelection;
import com.schmaloogium.engine.registry.ProgramBuildFailure;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.ProgramResolutionProjection;
import com.schmaloogium.engine.registry.ProgramResolutionStatus;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.PublicationResult;
import com.schmaloogium.engine.registry.PublishedRegistry;
import com.schmaloogium.engine.registry.RegistryBuildFailure;
import com.schmaloogium.engine.registry.RegistryFailureKind;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.uniforms.RegistryGenerationAdoptionResult;
import com.schmaloogium.engine.uniforms.UniformBuildResult;
import com.schmaloogium.engine.uniforms.UniformResetReason;
import com.schmaloogium.engine.uniforms.UniformRetirementReason;
import com.schmaloogium.engine.uniforms.UniformRetirementResult;
import com.schmaloogium.engine.uniforms.UniformRuntime;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * The render-thread composition root: the PHASE_7_DOC §4.1 pipeline transaction behind the
 * {@link ShaderReloadControllerImpl.Drain} seam. One drain turns a resolved selection into
 * one atomically installed {@link FrameComposition} — or, on any failure, into the explicit
 * empty installation plus one reported diagnostic. Every accepted outcome (ready, off,
 * recovered-off) advances the {@link PipelineVersion} exactly once. Never a partial tuple.
 *
 * <p>Ownership law (P4 §5.1, P5 §5.1): candidates are caller-owned until the publisher
 * accepts them; accepted candidates are never caller-closed; a failed rebuild always goes
 * off and never revives the previous composition.
 */
public final class PipelineTransaction implements ShaderReloadControllerImpl.Drain {

    /** Everything the transaction consumes, injected so headless tests script it. */
    public record Services(
            PipelineStages stages,
            Supplier<PackSelection> selection,
            Supplier<EngineOptionData> engineOptions,
            Supplier<DimensionKey> liveDimension,
            Supplier<Extent2i> displayExtent,
            LongSupplier resourceReloadEpoch,
            FrameRenderPort port,
            Consumer<Optional<FrameComposition>> installSink,
            DiagnosticReporter diagnostics,
            ShadowServices shadow,
            IdServices ids) {

        public Services {
            Objects.requireNonNull(stages, "stages");
            Objects.requireNonNull(selection, "selection");
            Objects.requireNonNull(engineOptions, "engineOptions");
            Objects.requireNonNull(liveDimension, "liveDimension");
            Objects.requireNonNull(displayExtent, "displayExtent");
            Objects.requireNonNull(resourceReloadEpoch, "resourceReloadEpoch");
            Objects.requireNonNull(port, "port");
            Objects.requireNonNull(installSink, "installSink");
            Objects.requireNonNull(diagnostics, "diagnostics");
            shadow = shadow == null ? ShadowServices.disabled() : shadow;
            ids = ids == null ? IdServices.disabled() : ids;
        }

        /** The v0.1 shape: no shadow services (every plan is Disabled by hook health). */
        public Services(PipelineStages stages, Supplier<PackSelection> selection,
                Supplier<EngineOptionData> engineOptions, Supplier<DimensionKey> liveDimension,
                Supplier<Extent2i> displayExtent, LongSupplier resourceReloadEpoch, FrameRenderPort port,
                Consumer<Optional<FrameComposition>> installSink, DiagnosticReporter diagnostics) {
            this(stages, selection, engineOptions, liveDimension, displayExtent, resourceReloadEpoch,
                    port, installSink, diagnostics, ShadowServices.disabled(), IdServices.disabled());
        }

        /** The v0.2 shape: shadow services, no ids. */
        public Services(PipelineStages stages, Supplier<PackSelection> selection,
                Supplier<EngineOptionData> engineOptions, Supplier<DimensionKey> liveDimension,
                Supplier<Extent2i> displayExtent, LongSupplier resourceReloadEpoch, FrameRenderPort port,
                Consumer<Optional<FrameComposition>> installSink, DiagnosticReporter diagnostics,
                ShadowServices shadow) {
            this(stages, selection, engineOptions, liveDimension, displayExtent, resourceReloadEpoch,
                    port, installSink, diagnostics, shadow, IdServices.disabled());
        }
    }

    /**
     * The Phase 9 / Phase 10 construction inputs (Task F): the live registry projection,
     * the per-mod sources, the hand-light policy, the vertex hook audit, the world epoch the
     * vertex epoch carries, and the sink the install's id/vertex publication reaches the
     * hooks through.
     */
    public record IdServices(
            Supplier<Optional<com.schmaloogium.mod.glue.id.RegistryProjection.Projection>> registries,
            Supplier<com.schmaloogium.engine.config.id.ModIdSourceSnapshot> modSources,
            Supplier<com.schmaloogium.engine.config.id.HandLightPolicy> handLight,
            Supplier<com.schmaloogium.mod.glue.vertex.VertexHookHealth> vertexHooks,
            LongSupplier worldEpoch,
            com.schmaloogium.engine.config.IdMappingParser parser,
            Consumer<com.schmaloogium.mod.glue.id.IdPublication> publicationSink) {

        public IdServices {
            Objects.requireNonNull(registries, "registries");
            Objects.requireNonNull(modSources, "modSources");
            Objects.requireNonNull(handLight, "handLight");
            Objects.requireNonNull(vertexHooks, "vertexHooks");
            Objects.requireNonNull(worldEpoch, "worldEpoch");
            Objects.requireNonNull(parser, "parser");
            Objects.requireNonNull(publicationSink, "publicationSink");
        }

        /** No registry, no audited vertex hooks: IDs off, vanilla vertex formats. */
        public static IdServices disabled() {
            return new IdServices(Optional::empty,
                    com.schmaloogium.engine.config.id.ModIdSourceSnapshot::empty,
                    com.schmaloogium.engine.config.id.HandLightPolicy::allDefault,
                    () -> com.schmaloogium.mod.glue.vertex.VertexHookHealth.disabled("not installed"),
                    () -> 0L,
                    new com.schmaloogium.engine.config.IdMappingParserImpl(),
                    publication -> { });
        }
    }

    /**
     * The Phase 8 construction inputs (PHASE_8_DOC §4.1): the live hook-health audit, the
     * world port factory over the mapped policy's content switches and render-thread predicate.
     */
    public record ShadowServices(
            Supplier<ShadowHookHealth> hookHealth,
            Function<ShadowPolicy, ShadowWorldPort> worldPort,
            BooleanSupplier renderThread,
            Consumer<Boolean> planReadySink) {

        public ShadowServices {
            Objects.requireNonNull(hookHealth, "hookHealth");
            Objects.requireNonNull(worldPort, "worldPort");
            Objects.requireNonNull(renderThread, "renderThread");
            Objects.requireNonNull(planReadySink, "planReadySink");
        }

        /** No audited hooks: every plan is {@code Disabled(HookUnavailable)}, estates neutralized. */
        public static ShadowServices disabled() {
            return new ShadowServices(
                    () -> ShadowHookHealth.of(ShadowHookHealth.catalogue().stream()
                            .map(id -> new ShadowHookRow(id, 1, 0, HookDisposition.FEATURE_DISABLED)).toList()),
                    policy -> {
                        throw new IllegalStateException("no shadow world port");
                    },
                    () -> true,
                    ready -> { });
        }
    }

    static final String FAILURE_PREFIX = "schmaloogium.pipeline.";
    private static final Log LOG = Logs.channel(LogChannels.FRAME);

    private final Services services;
    private final PipelineVersion.Counter versions = new PipelineVersion.Counter();
    private final com.schmaloogium.mod.glue.id.IdEventSinkRelay idSink =
            new com.schmaloogium.mod.glue.id.IdEventSinkRelay();
    private final com.schmaloogium.engine.config.id.IdRuntimeBuilder idBuilder;
    private final com.schmaloogium.engine.config.id.IdRuntimePublisher idPublisher;
    private ActivePipeline active;
    private com.schmaloogium.engine.textures.TextureSystem activeTextures;
    private long drainSerial;
    private long awaitingMainDepthVersion = -1L;

    public PipelineTransaction(Services services) {
        this.services = Objects.requireNonNull(services, "services");
        this.idBuilder = com.schmaloogium.engine.config.id.IdRuntimeBuilder.create(services.ids().parser());
        this.idPublisher = com.schmaloogium.engine.config.id.IdRuntimePublisher.create(idSink,
                services.diagnostics());
    }

    /** The installed pipeline, when one is active. */
    public Optional<ActivePipeline> active() {
        return Optional.ofNullable(active);
    }

    /** The status a NONE-lifecycle request answers with: the current publication. */
    public ReloadStatus currentStatus() {
        ActivePipeline current = active;
        return current == null
                ? ShaderReloadControllerImpl.offStatus(versions.current())
                : ShaderReloadControllerImpl.activeStatus(
                        current.composition().identity(), current.composition().version());
    }

    /** Resource NONE: replace only texture state over the retained accepted configuration. */
    public ReloadStatus refreshResources() {
        if (active == null || active.composition().resourceReloadEpoch()
                == services.resourceReloadEpoch().getAsLong()) {
            return currentStatus();
        }
        ActivePipeline previous = active;
        FrameCompositionRecord old = previous.composition();
        Attempt attempt = new Attempt(previous, activeTextures, services.resourceReloadEpoch().getAsLong());
        attempt.configuration = previous.configuration();
        services.installSink().accept(Optional.empty());
        active = null;
        activeTextures = null;
        try {
            attempt.retireOldTextures();
            var creation = services.stages().textures();
            if (creation instanceof com.schmaloogium.engine.textures.TextureSystemCreationResult.Failed failed) {
                return attempt.fail("texture-refresh-create", failed.failure().toString(), List.of());
            }
            attempt.textures = ((com.schmaloogium.engine.textures.TextureSystemCreationResult.Created) creation).system();
            var registry = old.registry().registry().orElseThrow();
            var request = services.stages().textureInputs(previous.configuration(), registry,
                    old.estate().generation(), old.registry().generation(), attempt.resourceReloadEpoch);
            var result = attempt.textures.build(request);
            if (result instanceof com.schmaloogium.engine.textures.TextureBuildResult.Failed failed) {
                return attempt.fail("texture-refresh-build", failed.failure().toString(), List.of());
            }
            var publication = ((com.schmaloogium.engine.textures.TextureBuildResult.Ready) result).publication();
            if (!publication.registryFingerprint().equals(registry.fingerprint())
                    || publication.id().generation() != old.estate().generation()
                    || publication.registryGeneration() != old.registry().generation()
                    || publication.resourceReloadEpoch() != attempt.resourceReloadEpoch
                    || !publication.plan().inputs().configuration().fingerprint().equals(old.identity().configuration())
                    || services.resourceReloadEpoch().getAsLong() != attempt.resourceReloadEpoch) {
                return attempt.fail("texture-refresh-identity", "resource identity changed", List.of());
            }
            services.stages().attachTextures(attempt.textures, attempt.resourceReloadEpoch);
            var composition = new FrameCompositionRecord(old.identity(), versions.next(), old.registry(),
                    old.estate(), old.uniforms(), old.port(), old.engineFlags(), old.handDepthMultiplier(),
                    attempt.resourceReloadEpoch, publication, attempt.textures::lease,
                    old.shadowSlot(), old.idRuntime(), old.vertexEpoch());
            active = new ActivePipeline(composition, previous.configuration(), previous.uniforms(), previous.replay());
            activeTextures = attempt.textures;
            services.installSink().accept(Optional.of(composition));
            LOG.info("H13-PUBLISH-01 resource texture publication installed: id={} resourceEpoch={}",
                    publication.id(), attempt.resourceReloadEpoch);
            return currentStatus();
        } catch (RuntimeException failure) {
            return attempt.fail("texture-refresh", failure.toString(), List.of());
        }
    }

    /** The main-depth version P5 asked to wait for, or -1 when no retry is pending. */
    public long awaitingMainDepthVersion() {
        return awaitingMainDepthVersion;
    }

    public PipelineVersion currentVersion() {
        return versions.current();
    }

    @Override
    public ReloadStatus drain(DriverReloadRequest request) {
        Objects.requireNonNull(request, "request");
        long serial = ++drainSerial;
        LOG.info("H-PIPE-00 drain #{} intent {} reasons {}", serial,
                request.intent().getClass().getSimpleName(), request.reasons().values());
        awaitingMainDepthVersion = -1L;
        // Step 1: admission closes first; the previous composition never comes back.
        services.installSink().accept(Optional.empty());
        services.ids().publicationSink().accept(com.schmaloogium.mod.glue.id.IdPublication.none());
        Attempt attempt = new Attempt(active, activeTextures, services.resourceReloadEpoch().getAsLong());
        active = null;
        activeTextures = null;
        try {
            return run(attempt, request);
        } catch (RuntimeException e) {
            LOG.error(e, "H-PIPE-02 unexpected failure in drain #{}", serial);
            return attempt.fail("unexpected", String.valueOf(e), List.of());
        }
    }

    private ReloadStatus run(Attempt a, DriverReloadRequest request) {
        PackSelection selection = services.selection().get();
        if (request.intent() instanceof ReloadIntent.RebuildActive rebuild
                && (a.old == null || !a.old.composition().identity().equals(rebuild.expectedActive()))) {
            LOG.warn("rebuild requested for {} but the active identity differs; loading the durable selection",
                    rebuild.expectedActive());
        }
        if (!(selection instanceof PackSelection.Filesystem)) {
            return a.goOff("selection");
        }
        EngineOptionData options = services.engineOptions().get();
        PackLoadResult loaded = services.stages().load(selection, options);
        switch (loaded) {
            case PackLoadResult.Off off -> {
                return a.goOff("load");
            }
            case PackLoadResult.Failed failed -> {
                EngineDiagnostic primary = failed.failure().primaryDiagnostic();
                return a.fail("load", failed.failure().code() + ": " + primary.messageKey()
                        + " " + primary.detail(), List.of(primary));
            }
            case PackLoadResult.Loaded ok -> a.configuration = ok.configuration();
        }
        PackConfiguration cfg = a.configuration;
        if (cfg.schemaVersion() != PackFrontEnd.CURRENT_SCHEMA_VERSION) {
            return a.fail("schema", "schema " + cfg.schemaVersion() + " != "
                    + PackFrontEnd.CURRENT_SCHEMA_VERSION, List.of());
        }
        DimensionKey dimension = pickDimension(cfg, services.liveDimension().get());
        var textureCreation = services.stages().textures();
        if (textureCreation instanceof com.schmaloogium.engine.textures.TextureSystemCreationResult.Failed failed) {
            return a.fail("texture-create", failed.failure().toString(), List.of());
        }
        a.textures = ((com.schmaloogium.engine.textures.TextureSystemCreationResult.Created) textureCreation).system();
        // The shadow demand is the planned projection's (P5 sizes from the registry's
        // sampler declarations plus the P3 minima floor), read once the estate exists.
        // Step 2: the P6 runtime precedes P4 (its participants compose the barrier).
        a.collector = new ReplayErrorCollector(cfg.fingerprint().value(), services.diagnostics());
        UniformBuildResult uniforms = services.stages().uniforms(
                services.stages().currentRegistryGeneration(),
                UniformConfigurations.derive(cfg), a.collector);
        if (uniforms instanceof UniformBuildResult.Failure failure) {
            return a.fail("uniforms", failure.diagnosticId(), List.of());
        }
        a.runtime = ((UniformBuildResult.Success) uniforms).runtime();
        // Step 3: compile P4 — the first real shader compilation — then plan/create P5.
        PipelineStages.RegistryHandle compiled = services.stages().compile(cfg, dimension,
                a.runtime.centerDepthMacroContributor().contribute(cfg));
        if (compiled instanceof PipelineStages.RegistryHandle.Off off) {
            return a.fail("compile", off.failure().kind() + ": " + off.failure().diagnosticId()
                    + " " + off.failure().userMessage(), programDiagnostics(off.failure()));
        }
        a.registry = (PipelineStages.RegistryHandle.Ready) compiled;
        ProgramRegistryView view = a.registry.view();
        RegistryFingerprint fingerprint = view.fingerprint();
        PipelineStages.EstateHandle estate = services.stages().estate(cfg, view, fingerprint,
                new BufferRuntimeInputs(services.displayExtent().get(),
                        multiplier(options, "renderResMul", 1.0d), multiplier(options, "shadowResMul", 1.0d)));
        switch (estate) {
            case PipelineStages.EstateHandle.AwaitingMainDepth awaiting -> {
                awaitingMainDepthVersion = awaiting.expectedVersion();
                return a.fail("awaiting-main-depth",
                        "main depth version " + awaiting.expectedVersion(), List.of());
            }
            case PipelineStages.EstateHandle.Off off -> {
                return a.fail("estate", off.failure().code() + ": " + off.failure().diagnosticId(),
                        List.of());
            }
            case PipelineStages.EstateHandle.Ready ready -> a.estate = ready;
        }
        // Step 4: compose the barrier with exactly this runtime's three participants.
        PipelineStages.BarrierOutcome barrier = a.registry.compose(
                a.runtime.samplerParticipant(), a.runtime.builtInParticipant(),
                com.schmaloogium.mod.glue.vertex.VertexProgramInputTracker.participant(
                        a.runtime.customParticipant()));
        if (barrier instanceof PipelineStages.BarrierOutcome.Invalid invalid) {
            return a.fail("barrier", invalid.diagnosticId(), List.of());
        }
        a.barrier = ((PipelineStages.BarrierOutcome.Ready) barrier).barrier();
        // Step 5: publish P4 with a release context minted immediately before.
        PublicationResult publication = services.stages().publishReady(a.registry, a.barrier);
        PublishedRegistry published;
        switch (publication) {
            case PublicationResult.Rejected rejected -> {
                return a.fail("publish", rejected.cause().kind() + ": "
                        + rejected.cause().diagnosticId(), List.of());
            }
            case PublicationResult.RecoveredOff recovered -> {
                a.registryTransferred = true;
                a.registryAlreadyOff = true;
                return a.fail("publish", "recovered off: " + recovered.cause().kind() + ": "
                        + recovered.cause().diagnosticId(), List.of());
            }
            case PublicationResult.Accepted accepted -> published = accepted.published();
        }
        a.registryTransferred = true;
        a.registryAccepted = true;
        a.retireOld();
        // Step 6: adopt the actual accepted generation.
        RegistryGenerationAdoptionResult adoption = a.runtime.adoptRegistryGeneration(
                published.generation(), UniformResetReason.PACK_REPLACEMENT);
        if (adoption == RegistryGenerationAdoptionResult.REJECTED_RETIRED_GENERATION) {
            return a.fail("adopt", "generation " + published.generation(), List.of());
        }
        // Step 7: publish P5 against the exact accepted fingerprint.
        BufferPublicationResult estatePublication =
                services.stages().publishEstate(a.estate, fingerprint);
        PublishedBufferEstate publishedEstate;
        switch (estatePublication) {
            case BufferPublicationResult.ProvenanceRejected rejected -> {
                return a.fail("estate-provenance", rejected.candidateRegistry() + " != "
                        + rejected.acceptedRegistry(), List.of());
            }
            case BufferPublicationResult.ConsumerFailed consumerFailed -> {
                a.estateTransferred = true; // already installed off, not caller-owned
                return a.fail("estate-consumer", consumerFailed.consumerId() + " after "
                        + consumerFailed.deliveredCount(), List.of());
            }
            case BufferPublicationResult.Published ok -> publishedEstate = ok.publication();
        }
        a.estateTransferred = true;
        a.estateAccepted = true;
        Optional<BufferEstateView> estateView = publishedEstate.estate();
        if (estateView.isEmpty() || estateView.get().generation() != publishedEstate.generation()) {
            return a.fail("estate-view", "generation " + publishedEstate.generation(), List.of());
        }
        // D-P7-46 / D-P7-59: one shadow disposition read. Task E: the Phase 8 plan decides
        // whether the available estate is used (slot ready) or neutralized (plan disabled).
        var plannedShadow = estateView.get().resources().projection().shadow();
        boolean requestedShadow = plannedShadow.depthTextures() > 0
                || plannedShadow.colorTextures() > 0;
        ShadowPolicy shadowPolicy = ShadowPolicyMapper.map(cfg.resources().shadow(),
                cfg.resources().world(), cfg.properties().engineFlags());
        ShadowHookHealth hookHealth = services.shadow().hookHealth().get();
        ShadowPlanResult planned = new ShadowPlanFactoryImpl().plan(
                new ShadowPlanInput(shadowPolicy, hookHealth, requestedShadow));
        Optional<ShadowInvocationSlot> shadowSlot = Optional.empty();
        String shadowVerdict;
        if (planned instanceof ShadowPlanResult.Ready ready
                && estateView.get().shadow() instanceof ShadowEstateAvailable) {
            ShadowPassBuildResult built = ShadowPassFactory.standard().create(new ShadowPassBuildInput(
                    ready.plan(), fingerprint, a.runtime,
                    services.shadow().worldPort().apply(ready.plan().policy()),
                    services.shadow().renderThread(), services.diagnostics()));
            if (built instanceof ShadowPassBuildResult.Ready slotReady) {
                shadowSlot = Optional.of(slotReady.slot());
                shadowVerdict = "ready(fp " + ready.plan().fingerprint().canonicalSha256().substring(0, 12)
                        + ", " + plannedShadow.depthTextures() + " depth / "
                        + plannedShadow.colorTextures() + " colour)";
            } else {
                shadowVerdict = "disabled(" + built + ")";
            }
        } else if (planned instanceof ShadowPlanResult.Disabled disabled) {
            shadowVerdict = "disabled(" + disabled.reason() + ")";
        } else if (planned instanceof ShadowPlanResult.NotRequested) {
            shadowVerdict = "not requested";
        } else {
            shadowVerdict = "disabled(estate " + estateView.get().shadow().getClass().getSimpleName() + ")";
        }
        if (shadowSlot.isEmpty()) {
            String shadowFailure = shadowDisposition(estateView.get(), publishedEstate.generation(),
                    requestedShadow);
            if (shadowFailure != null) {
                return a.fail(shadowFailure.startsWith("mismatch") ? "shadow-mismatch"
                        : "shadow-neutralize", shadowFailure, List.of());
            }
        }
        services.shadow().planReadySink().accept(shadowSlot.isPresent());
        // P13 builds only against the actual accepted registry and estate generations.
        var textureRequest = services.stages().textureInputs(cfg, view, publishedEstate.generation(),
                published.generation(), a.resourceReloadEpoch);
        var textureBuild = a.textures.build(textureRequest);
        if (textureBuild instanceof com.schmaloogium.engine.textures.TextureBuildResult.Failed failed) {
            return a.fail("texture-build", failed.failure().toString(), List.of());
        }
        var texturePublication = ((com.schmaloogium.engine.textures.TextureBuildResult.Ready) textureBuild).publication();
        if (texturePublication.id().generation() != publishedEstate.generation()
                || texturePublication.registryGeneration() != published.generation()
                || !texturePublication.registryFingerprint().equals(fingerprint)
                || texturePublication.resourceReloadEpoch() != a.resourceReloadEpoch
                || !texturePublication.plan().inputs().configuration().fingerprint().equals(cfg.fingerprint())
                || services.resourceReloadEpoch().getAsLong() != a.resourceReloadEpoch) {
            return a.fail("texture-identity", "accepted texture tuple changed", List.of());
        }
        services.stages().attachTextures(a.textures, a.resourceReloadEpoch);
        // Step 8 (Task F, PHASE_9_DOC §5.3): the id runtime is published after the texture
        // stage and before atomic Active. IDs are a
        // FEATURE: a failed build or snapshot leaves them off and the pipeline installs.
        Optional<com.schmaloogium.engine.config.id.PublishedIdRuntime> idRuntime = Optional.empty();
        com.schmaloogium.mod.glue.id.IdIdentityMaps idMaps = com.schmaloogium.mod.glue.id.IdIdentityMaps.EMPTY;
        String idVerdict;
        Optional<com.schmaloogium.mod.glue.id.RegistryProjection.Projection> projection =
                services.ids().registries().get();
        if (projection.isEmpty()) {
            idVerdict = "off(no registry snapshot)";
        } else {
            com.schmaloogium.engine.config.id.HandLightPolicy handLight = services.ids().handLight().get();
            com.schmaloogium.engine.config.id.IdBuildResult built = idBuilder.build(
                    new com.schmaloogium.engine.config.id.IdBuildRequest(cfg.idMappings(),
                            projection.get().snapshot(), services.ids().modSources().get(),
                            com.schmaloogium.engine.config.id.CompatibilityAliasCatalog.v0_3(),
                            com.schmaloogium.engine.config.id.LegacyTagCatalog.empty(),
                            handLight, services.diagnostics()));
            if (built instanceof com.schmaloogium.engine.config.id.IdBuildResult.Built ok) {
                idSink.retarget(a.runtime.events());
                var publishResult = idPublisher.publish(ok.candidate(),
                        new com.schmaloogium.engine.config.id.IdPublishContext("post-texture"));
                if (publishResult instanceof com.schmaloogium.engine.config.id.IdPublishResult.Published p) {
                    idRuntime = Optional.of(p.runtime());
                    idMaps = projection.get().maps();
                    var v = ok.candidate().view();
                    idVerdict = "gen " + p.runtime().generation() + " (states " + v.blockStateCount()
                            + " items " + v.itemOrdinalCount() + " entities " + v.entityTypeCount()
                            + "; aliases b/i/e " + v.blockAliasAssignments() + "/" + v.itemAliasAssignments()
                            + "/" + v.entityAliasAssignments() + ", layers " + v.layerAssignments() + ")";
                } else {
                    ok.candidate().close();
                    idVerdict = "off(publish " + publishResult + ")";
                }
            } else {
                idVerdict = "off(build " + ((com.schmaloogium.engine.config.id.IdBuildResult.Failed) built)
                        .failure() + ")";
            }
        }
        a.idRuntime = idRuntime;
        a.dropOldIds();
        // Step 8b: the vertex epoch is admitted only under healthy vertex hooks when at least
        // one program declares a classic attribute (PHASE_10_DOC §4.6, §4.11).
        com.schmaloogium.mod.glue.vertex.VertexHookHealth vertexHealth =
                Boolean.getBoolean("schmaloogium.debug.disableExtendedVertex")
                        ? com.schmaloogium.mod.glue.vertex.VertexHookHealth.disabled("disabled by debug flag")
                        : services.ids().vertexHooks().get();
        java.util.Set<com.schmaloogium.engine.registry.ExtendedAttribute> declaredUnion = declaredClassicAttributes(view);
        boolean declaresAttributes = !declaredUnion.isEmpty();
        // Step 9: atomically install the complete accepted tuple.
        PipelineVersion version = versions.next();
        Optional<com.schmaloogium.engine.vertex.VertexEpoch> vertexEpoch =
                vertexHealth.healthy() && declaresAttributes
                        ? Optional.of(new com.schmaloogium.engine.vertex.VertexEpoch(version.value(),
                                services.ids().worldEpoch().getAsLong(),
                                idRuntime.map(com.schmaloogium.engine.config.id.PublishedIdRuntime::generation)
                                        .orElse(0L),
                                com.schmaloogium.engine.vertex.Classic56Layout.layout().fingerprint()))
                        : Optional.empty();
        PipelineIdentity identity = new PipelineIdentity(cfg.pack(), dimension, cfg.fingerprint());
        FrameCompositionRecord composition = new FrameCompositionRecord(identity, version,
                published, publishedEstate, a.runtime, services.port(), cfg.properties().engineFlags(),
                multiplier(options, "handDepthMul", 0.125d),
                a.resourceReloadEpoch, texturePublication, a.textures::lease, shadowSlot, idRuntime, vertexEpoch);
        active = new ActivePipeline(composition, cfg, a.runtime, a.collector);
        activeTextures = a.textures;
        services.ids().publicationSink().accept(new com.schmaloogium.mod.glue.id.IdPublication(
                idRuntime, idMaps, idSink,
                projection.isPresent() ? services.ids().handLight().get()
                        : com.schmaloogium.engine.config.id.HandLightPolicy.allDefault(),
                vertexEpoch, declaredUnion));
        services.installSink().accept(Optional.of(composition));
        LOG.info("H13-PUBLISH-01 texture publication installed: id={} registry={} estate={} "
                        + "resourceEpoch={} custom={} noise={}",
                texturePublication.id(), published.generation(), publishedEstate.generation(),
                a.resourceReloadEpoch, texturePublication.plan().customTextures().size(),
                texturePublication.plan().noise());
        LOG.info("H-PIPE-01 composition installed: pack {} dimension {} registry generation {} "
                        + "estate generation {} version {} programs {} shadow={} ids={} vertexEpoch={}",
                cfg.pack().selectedRoot().canonicalString(), dimension, published.generation(),
                publishedEstate.generation(), version.value(), histogram(view), shadowVerdict,
                idVerdict, vertexEpoch.map(e -> "serial " + e.serial()).orElse(
                        declaresAttributes ? "vanilla(hooks)" : "vanilla(no attributes)"));
        LOG.info("H9-IDS-01 id runtime: {} sources block={} item={} entity={} layer={}", idVerdict,
                mappingState(cfg.idMappings().blocks()), mappingState(cfg.idMappings().items()),
                mappingState(cfg.idMappings().entities()), mappingState(cfg.idMappings().layers()));
        LOG.info("H9-HOOKS-01 id hook anchors: {}", com.schmaloogium.mod.glue.vertex.McVertexHookHealth.anchors(
                List.of("H9-ENTITY-ID-01-ENTER", "H9-ENTITY-ID-01-EXIT", "H9-BLOCK-ENTITY-ID-01-SLOW",
                        "H9-BLOCK-ENTITY-ID-01-FAST", "H9-COLOR-01", "H9-COLOR-02")));
        LOG.info("H10-HEALTH-01 vertex hook health: enabled={} disabledRows={} attributesDeclared={}",
                vertexHealth.healthy(), vertexHealth.disabledRows(), declaresAttributes);
        LOG.info("H8-HEALTH-01 shadow hook health: enabled={} disabledRows={} fingerprint={}",
                hookHealth.shadowEnabled(),
                com.schmaloogium.mod.glue.shadow.McShadowHookHealth.disabledRows(hookHealth),
                hookHealth.fingerprint().canonicalSha256().substring(0, 12));
        logProgramSet(view);
        services.diagnostics().report(new EngineDiagnostic(DiagnosticSeverity.INFO,
                UserChannel.LOG_ONLY, "schmaloogium.info.pipeline.active",
                List.of(cfg.pack().selectedRoot().canonicalString()),
                "version " + version.value(), LogChannels.FRAME));
        return ShaderReloadControllerImpl.activeStatus(identity, version);
    }

    /** "{state}/{ordinary rules}+{forced 1.13 rules}" for one id mapping file. */
    private static String mappingState(com.schmaloogium.engine.config.IdMappingFileInput file) {
        return file.state() + "/" + file.ordinaryRules().size() + "+" + file.forced11300Rules().size();
    }

    /** The union of classic attributes the sourced programs' state bundles declare. */
    private static java.util.Set<com.schmaloogium.engine.registry.ExtendedAttribute> declaredClassicAttributes(
            ProgramRegistryView view) {
        java.util.EnumSet<com.schmaloogium.engine.registry.ExtendedAttribute> union =
                java.util.EnumSet.noneOf(com.schmaloogium.engine.registry.ExtendedAttribute.class);
        for (ProgramResolutionProjection projection : view.resolutions()) {
            if (projection.status() != ProgramResolutionStatus.SOURCED) {
                continue;
            }
            var resolved = view.resolve(projection.slot());
            resolved.ifPresent(r -> union.addAll(r.state().attributes()));
        }
        return union;
    }

    /** Null when the disposition is acceptable, else a short reason. */
    private static String shadowDisposition(BufferEstateView view, long generation,
                                            boolean requested) {
        ShadowEstateResult shadow = view.shadow();
        return switch (shadow) {
            case ShadowEstateNotRequested notRequested ->
                    requested ? "mismatch: estate reports not-requested" : null;
            case ShadowEstateUnavailable unavailable -> null; // already neutral
            case ShadowEstateAvailable available -> {
                if (!requested) {
                    yield "mismatch: estate reports an available shadow estate";
                }
                ShadowNeutralizationResult result = available.view().degradeToNeutral(
                        generation, ShadowNeutralReason.EXPLICIT_FEATURE_DISABLE);
                yield switch (result) {
                    case ShadowNeutralizationResult.Neutralized n ->
                            n.generation() == generation && !n.openSnapshotAborted()
                                    ? null : "neutralize: " + n;
                    case ShadowNeutralizationResult.AlreadyNeutral n ->
                            n.generation() == generation ? null : "neutralize: " + n;
                    case ShadowNeutralizationResult.Rejected r -> "neutralize: " + r.reason();
                };
            }
        };
    }

    private static DimensionKey pickDimension(PackConfiguration cfg, DimensionKey live) {
        return live != null && cfg.dimensions().containsKey(live) ? live : DimensionKey.BASE;
    }

    private static double multiplier(EngineOptionData options, String key, double fallback) {
        String raw = options.values().get(key);
        if (raw == null) {
            return fallback;
        }
        try {
            double value = Double.parseDouble(raw);
            return Double.isFinite(value) && value > 0d ? value : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /** The compiled program set as evidence: one COMPILE line per sourced/chained/failed slot. */
    private static void logProgramSet(ProgramRegistryView view) {
        Log compile = Logs.channel(LogChannels.COMPILE);
        for (ProgramResolutionProjection projection : view.resolutions()) {
            switch (projection.status()) {
                case SOURCED -> compile.info("program {} compiled+linked (own build {})",
                        projection.slot().packName(), projection.ownBuild());
                case CHAIN -> compile.info("program {} chains to {}",
                        projection.slot().packName(),
                        projection.from().map(ProgramSlotId::packName).orElse("?"));
                case FAILED -> compile.warn("program {} FAILED (own build {}): {}",
                        projection.slot().packName(), projection.ownBuild(),
                        projection.driverLog().isBlank() ? "(no driver log)" : projection.driverLog());
                case ABSENT -> {
                }
            }
        }
    }

    private static String histogram(ProgramRegistryView view) {
        Map<ProgramResolutionStatus, Integer> counts = new EnumMap<>(ProgramResolutionStatus.class);
        for (ProgramResolutionProjection projection : view.resolutions()) {
            counts.merge(projection.status(), 1, Integer::sum);
        }
        return view.resolutions().size() + " " + counts;
    }

    private static List<EngineDiagnostic> programDiagnostics(RegistryBuildFailure failure) {
        List<EngineDiagnostic> out = new ArrayList<>();
        for (ProgramBuildFailure program : failure.programFailures()) {
            out.add(new EngineDiagnostic(DiagnosticSeverity.ERROR, UserChannel.SHADER_GUI,
                    "schmaloogium.error.pipeline.compile", List.of(String.valueOf(program)),
                    String.valueOf(program), LogChannels.COMPILE));
        }
        return out;
    }

    private static RegistryBuildFailure offRegistryCause(String key) {
        return new RegistryBuildFailure(RegistryFailureKind.UNEXPECTED_BACKEND, List.of(),
                FAILURE_PREFIX + "off", key);
    }

    private static BufferFailure offEstateCause(String key) {
        return new BufferFailure(BufferFailureCode.INVALID_INPUT, key, FAILURE_PREFIX + "off",
                List.of(), Optional.empty(), Optional.empty());
    }

    /** One drain's ownership ledger: what is caller-owned, accepted, retired. */
    private final class Attempt {

        final ActivePipeline old;
        final com.schmaloogium.engine.textures.TextureSystem oldTextures;
        final long resourceReloadEpoch;
        com.schmaloogium.engine.textures.TextureSystem textures;
        PackConfiguration configuration;
        ReplayErrorCollector collector;
        UniformRuntime runtime;
        PipelineStages.RegistryHandle.Ready registry;
        PipelineStages.BarrierHandle barrier;
        PipelineStages.EstateHandle.Ready estate;
        boolean registryTransferred;
        boolean registryAccepted;
        boolean registryAlreadyOff;
        boolean estateTransferred;
        boolean estateAccepted;
        boolean oldRetired;
        boolean oldTexturesRetired;
        Optional<com.schmaloogium.engine.config.id.PublishedIdRuntime> idRuntime = Optional.empty();

        Attempt(ActivePipeline old, com.schmaloogium.engine.textures.TextureSystem oldTextures,
                long resourceReloadEpoch) {
            this.old = old;
            this.oldTextures = oldTextures;
            this.resourceReloadEpoch = resourceReloadEpoch;
        }

        /** Off selection or Off load: the accepted-off outcome, one version increment. */
        ReloadStatus goOff(String reason) {
            String key = FAILURE_PREFIX + "off." + reason;
            dropOldIds();
            retireOld();
            publishBothOff(key);
            PipelineVersion version = versions.next();
            LOG.info("H-PIPE-02 composition cleared (shaders off): reason {} version {}",
                    key, version.value());
            services.diagnostics().report(new EngineDiagnostic(DiagnosticSeverity.INFO,
                    UserChannel.LOG_ONLY, "schmaloogium.info.pipeline.off", List.of(key),
                    key, LogChannels.FRAME));
            return ShaderReloadControllerImpl.offStatus(version);
        }

        /**
         * Any failure: close what is still caller-owned, retire runtimes, publish both
         * publishers off, advance the version once, report one diagnostic, stay empty.
         */
        ReloadStatus fail(String step, String detail, List<EngineDiagnostic> extra) {
            active = null;
            activeTextures = null;
            services.installSink().accept(Optional.empty());
            if (textures != null) {
                retireTextures(textures);
                textures = null;
            }
            closeOwned();
            idPublisher.deactivate(new com.schmaloogium.engine.config.id.IdPublishContext("off"));
            closeIdRuntime(idRuntime, "candidate");
            idRuntime = Optional.empty();
            dropOldIds();
            retireCandidateRuntime();
            retireOld();
            publishBothOff(FAILURE_PREFIX + step);
            PipelineVersion version = versions.next();
            String packName = configuration == null ? "?"
                    : configuration.pack().selectedRoot().canonicalString();
            LOG.warn("H-PIPE-02 composition cleared (failed at {}): {} version {}", step, detail,
                    version.value());
            services.diagnostics().report(new EngineDiagnostic(DiagnosticSeverity.ERROR,
                    UserChannel.CHAT, "schmaloogium.error.pipeline." + step,
                    List.of(packName, detail), detail, LogChannels.FRAME));
            for (EngineDiagnostic diagnostic : extra) {
                services.diagnostics().report(diagnostic);
            }
            return ShaderReloadControllerImpl.failedStatus(new FailureId(FAILURE_PREFIX + step));
        }

        void closeOwned() {
            if (estate != null && !estateTransferred) {
                close(estate, "estate");
            }
            if (barrier != null && !registryTransferred) {
                close(barrier, "barrier");
            }
            if (registry != null && !registryTransferred) {
                close(registry, "registry");
            }
        }

        private void close(AutoCloseable candidate, String what) {
            try {
                candidate.close();
            } catch (Exception e) {
                LOG.warn("closing the {} candidate failed: {}", what, e.toString());
            }
        }

        void retireCandidateRuntime() {
            if (runtime != null) {
                retire(runtime, UniformRetirementReason.UNPUBLISHED_ABORT, "candidate");
                runtime = null;
            }
        }

        void retireOld() {
            if (old != null && !oldRetired) {
                oldRetired = true;
                retireOldTextures();
                retire(old.uniforms(), UniformRetirementReason.REPLACEMENT, "replaced");
            }
        }

        private void retireOldTextures() {
            if (oldTextures != null && !oldTexturesRetired) {
                oldTexturesRetired = true;
                retireTextures(oldTextures);
            }
        }

        private void retireTextures(com.schmaloogium.engine.textures.TextureSystem owner) {
            try {
                services.stages().detachTextures(owner);
            } finally {
                owner.close();
            }
        }

        boolean oldIdsDropped;

        /**
         * The previous id runtime is closed only after the publisher has retired it (a
         * replacement retires it in {@code publish}; otherwise deactivate first), because
         * the publisher reads the retired runtime's generation for its notice.
         */
        void dropOldIds() {
            if (oldIdsDropped) {
                return;
            }
            oldIdsDropped = true;
            if (idRuntime.isEmpty()) {
                idPublisher.deactivate(new com.schmaloogium.engine.config.id.IdPublishContext("off"));
            }
            if (old != null) {
                closeIdRuntime(old.composition().idRuntime(), "replaced");
            }
        }

        private void closeIdRuntime(
                Optional<com.schmaloogium.engine.config.id.PublishedIdRuntime> runtime, String what) {
            if (runtime == null || runtime.isEmpty()) {
                return;
            }
            try {
                runtime.get().close();
            } catch (RuntimeException e) {
                LOG.warn("closing the {} id runtime failed: {}", what, e.toString());
            }
        }

        private void retire(UniformRuntime target, UniformRetirementReason reason, String what) {
            UniformRetirementResult result = target.retire(reason);
            if (result instanceof UniformRetirementResult.Rejected rejected) {
                LOG.warn("retiring the {} uniform runtime ({}) was rejected: {}", what, reason,
                        rejected.reason());
            }
        }

        /** P5 first, then P4 (§4.1 step 10); a RecoveredOff P4 result is already off. */
        void publishBothOff(String key) {
            BufferPublicationResult estateOff = services.stages().publishEstateOff(offEstateCause(key));
            if (!(estateOff instanceof BufferPublicationResult.Published)) {
                LOG.warn("estate off publication answered {}", estateOff);
            }
            if (!registryAlreadyOff) {
                PublicationResult registryOff = services.stages().publishOff(offRegistryCause(key));
                if (!(registryOff instanceof PublicationResult.Accepted)) {
                    LOG.warn("registry off publication answered {}", registryOff);
                }
            }
        }
    }
}
