// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.engine.buffers.BufferArchitecture;
import com.schmaloogium.engine.buffers.BufferArchitectures;
import com.schmaloogium.engine.buffers.BufferBuildRequest;
import com.schmaloogium.engine.buffers.BufferBuildResult;
import com.schmaloogium.engine.buffers.BufferEstateCandidate;
import com.schmaloogium.engine.buffers.BufferEstatePublisher;
import com.schmaloogium.engine.buffers.BufferEstatePublishers;
import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferPlanRequest;
import com.schmaloogium.engine.buffers.BufferPlanResult;
import com.schmaloogium.engine.buffers.BufferPublicationResult;
import com.schmaloogium.engine.buffers.BufferRuntimeInputs;
import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.buffers.MainDepthSource;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.pack.CompanionOptionMacros;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.pack.PackLoadRequest;
import com.schmaloogium.engine.pack.PackLoadResult;
import com.schmaloogium.engine.pack.PackSelection;
import com.schmaloogium.engine.pack.PersistenceFileAccess;
import com.schmaloogium.engine.pack.RendererFeatureData;
import com.schmaloogium.engine.pack.RuntimeIdentityData;
import com.schmaloogium.engine.preprocess.MacroContribution;
import com.schmaloogium.engine.registry.BarrierConstructionResult;
import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierPublicationCandidate;
import com.schmaloogium.engine.registry.CompiledRegistryCandidate;
import com.schmaloogium.engine.registry.ProductionBarrierComposer;
import com.schmaloogium.engine.registry.ProgramBindingParticipant;
import com.schmaloogium.engine.registry.ProgramRegistries;
import com.schmaloogium.engine.registry.ProgramRegistryCompiler;
import com.schmaloogium.engine.registry.ProgramRegistryPublisher;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.PublicationResult;
import com.schmaloogium.engine.registry.RegistryBuildFailure;
import com.schmaloogium.engine.registry.RegistryBuildRequest;
import com.schmaloogium.engine.registry.RegistryBuildResult;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.RegistryPublication;
import com.schmaloogium.engine.uniforms.UniformBuildResult;
import com.schmaloogium.engine.uniforms.UniformConfiguration;
import com.schmaloogium.engine.uniforms.UniformReplayErrorSink;
import com.schmaloogium.engine.uniforms.UniformRuntimeFactory;
import com.schmaloogium.engine.uniforms.spi.CenterDepthSource;
import com.schmaloogium.engine.uniforms.spi.UniformPlatformProvider;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * The production {@link PipelineStages}: each method is one public P3/P4/P5/P6 facade call
 * in the shape its §5.1 row prescribes. Retains exactly one P4 publisher and one P5
 * publisher for the session (each accepted publication increments their generations), and
 * hands the one real {@link GLDevice} into compilation and estate creation. Render-thread
 * only, like the facades it wraps.
 */
public final class EnginePipelineStages implements PipelineStages {

    private final PackFrontEnd frontEnd;
    private final Path shaderpacksDirectory;
    private final PersistenceFileAccess persistenceFiles;
    private final RuntimeIdentityData runtimeIdentity;
    private final GLCapabilityProfile capabilities;
    private final GLDevice device;
    private final UniformPlatformProvider platform;
    private final CenterDepthSource centerDepth;
    private final MainDepthSource mainDepth;
    private final DiagnosticReporter diagnostics;

    private final ProgramRegistryCompiler compiler = ProgramRegistries.compiler();
    private final ProgramRegistryPublisher publisher = ProgramRegistries.publisher();
    private final ProductionBarrierComposer composer = ProgramRegistries.productionComposer();
    private final BufferArchitecture architecture = BufferArchitectures.create();
    private final BufferEstatePublisher estatePublisher = BufferEstatePublishers.create();

    public EnginePipelineStages(PackFrontEnd frontEnd, Path shaderpacksDirectory,
                                PersistenceFileAccess persistenceFiles,
                                RuntimeIdentityData runtimeIdentity,
                                GLCapabilityProfile capabilities, GLDevice device,
                                UniformPlatformProvider platform, CenterDepthSource centerDepth,
                                MainDepthSource mainDepth, DiagnosticReporter diagnostics) {
        this.frontEnd = Objects.requireNonNull(frontEnd, "frontEnd");
        this.shaderpacksDirectory = Objects.requireNonNull(shaderpacksDirectory, "shaderpacksDirectory");
        this.persistenceFiles = Objects.requireNonNull(persistenceFiles, "persistenceFiles");
        this.runtimeIdentity = Objects.requireNonNull(runtimeIdentity, "runtimeIdentity");
        this.capabilities = Objects.requireNonNull(capabilities, "capabilities");
        this.device = Objects.requireNonNull(device, "device");
        this.platform = Objects.requireNonNull(platform, "platform");
        this.centerDepth = Objects.requireNonNull(centerDepth, "centerDepth");
        this.mainDepth = Objects.requireNonNull(mainDepth, "mainDepth");
        this.diagnostics = Objects.requireNonNull(diagnostics, "diagnostics");
    }

    @Override
    public PackLoadResult load(PackSelection selection, EngineOptionData options) {
        // P3 §2.2 request order; the companion pair follows engineOptions immediately, and
        // no internal source is consulted for a filesystem selection.
        return frontEnd.load(new PackLoadRequest(
                shaderpacksDirectory,
                selection,
                runtimeIdentity,
                capabilities,
                options,
                new CompanionOptionMacros(flag(options, "normalMapEnabled"),
                        flag(options, "specularMapEnabled")),
                new RendererFeatureData(false, false),
                persistenceFiles,
                null,
                Optional.empty(),
                diagnostics));
    }

    @Override
    public UniformBuildResult uniforms(long initialRegistryGeneration,
                                       UniformConfiguration configuration,
                                       UniformReplayErrorSink replayErrors) {
        return UniformRuntimeFactory.factory().create(initialRegistryGeneration, configuration,
                FixedSamplerPolicies.resolver(), platform, centerDepth, device, diagnostics,
                replayErrors);
    }

    @Override
    public RegistryHandle compile(PackConfiguration configuration, DimensionKey dimension,
                                  MacroContribution macroContribution) {
        RegistryBuildResult result = compiler.compile(new RegistryBuildRequest(
                configuration, Optional.empty(), dimension, macroContribution,
                FixedSamplerPolicies.appB3(), capabilities, device, diagnostics));
        return switch (result) {
            case RegistryBuildResult.Ready ready -> new ReadyRegistry(ready.candidate());
            case RegistryBuildResult.ShadersOff off -> new RegistryHandle.Off(off.failure());
        };
    }

    @Override
    public EstateHandle estate(PackConfiguration configuration, ProgramRegistryView registry,
                               RegistryFingerprint registryFingerprint, BufferRuntimeInputs runtime) {
        BufferPlanResult plan = architecture.plan(new BufferPlanRequest(
                configuration, registry, registryFingerprint, capabilities, runtime));
        if (plan instanceof BufferPlanResult.Invalid invalid) {
            return new EstateHandle.Off(invalid.failure());
        }
        BufferBuildResult built = architecture.create(new BufferBuildRequest(
                configuration, registry, registryFingerprint, capabilities, runtime,
                mainDepth, device, diagnostics));
        return switch (built) {
            case BufferBuildResult.Ready ready -> new ReadyEstate(ready.candidate());
            case BufferBuildResult.AwaitingMainDepth awaiting ->
                    new EstateHandle.AwaitingMainDepth(awaiting.expectedVersion());
            case BufferBuildResult.ShadersOff off -> new EstateHandle.Off(off.failure());
        };
    }

    @Override
    public long currentRegistryGeneration() {
        return publisher.current().generation();
    }

    @Override
    public PublicationResult publishReady(RegistryHandle.Ready registry, BarrierHandle barrier) {
        ReadyRegistry ready = (ReadyRegistry) registry;
        ReadyBarrier composed = (ReadyBarrier) barrier;
        // The release context is minted from the current publication immediately before the
        // publish; any intervening beginFrame would retire it (P4 §5.1).
        BarrierContext release = publisher.current().contexts().beginFrame().release();
        return publisher.publish(
                new RegistryPublication.Ready(ready.candidate, composed.candidate), release);
    }

    @Override
    public PublicationResult publishOff(RegistryBuildFailure cause) {
        BarrierContext release = publisher.current().contexts().beginFrame().release();
        return publisher.publish(new RegistryPublication.ShadersOff(cause), release);
    }

    @Override
    public BufferPublicationResult publishEstate(EstateHandle.Ready estate,
                                                 RegistryFingerprint accepted) {
        return estatePublisher.publish(((ReadyEstate) estate).candidate, accepted);
    }

    @Override
    public BufferPublicationResult publishEstateOff(BufferFailure cause) {
        return estatePublisher.publishOff(cause);
    }

    private static boolean flag(EngineOptionData options, String key) {
        return "true".equals(options.values().get(key));
    }

    private final class ReadyRegistry implements RegistryHandle.Ready {

        private final CompiledRegistryCandidate candidate;

        ReadyRegistry(CompiledRegistryCandidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public ProgramRegistryView view() {
            return candidate.view();
        }

        @Override
        public BarrierOutcome compose(ProgramBindingParticipant samplers,
                                      ProgramBindingParticipant builtIns,
                                      ProgramBindingParticipant customs) {
            BarrierConstructionResult result = composer.compose(candidate, samplers, builtIns, customs);
            return switch (result) {
                case BarrierConstructionResult.Ready ready -> new BarrierOutcome.Ready(
                        new ReadyBarrier(ready.candidate()));
                case BarrierConstructionResult.Invalid invalid ->
                        new BarrierOutcome.Invalid(invalid.diagnosticId());
            };
        }

        @Override
        public void close() {
            candidate.close();
        }
    }

    private static final class ReadyBarrier implements BarrierHandle {

        private final BarrierPublicationCandidate candidate;

        ReadyBarrier(BarrierPublicationCandidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public void close() {
            candidate.close();
        }
    }

    private static final class ReadyEstate implements EstateHandle.Ready {

        private final BufferEstateCandidate candidate;

        ReadyEstate(BufferEstateCandidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public void close() {
            candidate.close();
        }
    }
}
