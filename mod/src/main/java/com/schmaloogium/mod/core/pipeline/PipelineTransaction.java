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
import com.schmaloogium.engine.config.BufferMinima;
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
            DiagnosticReporter diagnostics) {

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
        }
    }

    static final String FAILURE_PREFIX = "schmaloogium.pipeline.";
    private static final Log LOG = Logs.channel(LogChannels.FRAME);

    private final Services services;
    private final PipelineVersion.Counter versions = new PipelineVersion.Counter();
    private ActivePipeline active;
    private long drainSerial;
    private long awaitingMainDepthVersion = -1L;

    public PipelineTransaction(Services services) {
        this.services = Objects.requireNonNull(services, "services");
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
        Attempt attempt = new Attempt(active);
        active = null;
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
        BufferMinima minima = cfg.resources().minima();
        boolean requestedShadow = minima.shadowDepthBuffers() > 0 || minima.shadowColorBuffers() > 0;
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
                        multiplier(options, "renderResMul"), multiplier(options, "shadowResMul")));
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
                a.runtime.customParticipant());
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
        // D-P7-46 / D-P7-59: one shadow disposition read, neutralized at v0.1.
        String shadowFailure = shadowDisposition(estateView.get(), publishedEstate.generation(),
                requestedShadow);
        if (shadowFailure != null) {
            return a.fail(shadowFailure.startsWith("mismatch") ? "shadow-mismatch"
                    : "shadow-neutralize", shadowFailure, List.of());
        }
        // Steps 8–9: P13 stays the explicit empty publication, P9 dormant. Atomic install.
        PipelineVersion version = versions.next();
        PipelineIdentity identity = new PipelineIdentity(cfg.pack(), dimension, cfg.fingerprint());
        FrameCompositionRecord composition = new FrameCompositionRecord(identity, version,
                published, publishedEstate, a.runtime, services.port(),
                services.resourceReloadEpoch().getAsLong());
        active = new ActivePipeline(composition, cfg, a.runtime, a.collector);
        services.installSink().accept(Optional.of(composition));
        LOG.info("H-PIPE-01 composition installed: pack {} dimension {} registry generation {} "
                        + "estate generation {} version {} programs {}",
                cfg.pack().selectedRoot().canonicalString(), dimension, published.generation(),
                publishedEstate.generation(), version.value(), histogram(view));
        logProgramSet(view);
        services.diagnostics().report(new EngineDiagnostic(DiagnosticSeverity.INFO,
                UserChannel.LOG_ONLY, "schmaloogium.info.pipeline.active",
                List.of(cfg.pack().selectedRoot().canonicalString()),
                "version " + version.value(), LogChannels.FRAME));
        return ShaderReloadControllerImpl.activeStatus(identity, version);
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

    private static double multiplier(EngineOptionData options, String key) {
        String raw = options.values().get(key);
        if (raw == null) {
            return 1.0d;
        }
        try {
            double value = Double.parseDouble(raw);
            return Double.isFinite(value) && value > 0d ? value : 1.0d;
        } catch (NumberFormatException e) {
            return 1.0d;
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

        Attempt(ActivePipeline old) {
            this.old = old;
        }

        /** Off selection or Off load: the accepted-off outcome, one version increment. */
        ReloadStatus goOff(String reason) {
            String key = FAILURE_PREFIX + "off." + reason;
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
            closeOwned();
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
                retire(old.uniforms(), UniformRetirementReason.REPLACEMENT, "replaced");
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
