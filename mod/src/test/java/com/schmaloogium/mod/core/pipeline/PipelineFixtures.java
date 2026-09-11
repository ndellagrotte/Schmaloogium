// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.engine.buffers.BufferEstateView;
import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferPublicationResult;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.BufferRuntimeInputs;
import com.schmaloogium.engine.buffers.PublishedBufferEstate;
import com.schmaloogium.engine.buffers.ResourceProjectionUnavailableReason;
import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.ShadowEstateResult;
import com.schmaloogium.engine.buffers.ShadowEstateUnavailable;
import com.schmaloogium.engine.buffers.ShadowEstateView;
import com.schmaloogium.engine.buffers.ShadowNeutralReason;
import com.schmaloogium.engine.buffers.ShadowNeutralizationResult;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.CompanionOptionMacros;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.OsFamily;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackFrontEndServices;
import com.schmaloogium.engine.pack.PackFrontEnds;
import com.schmaloogium.engine.pack.PackLoadRequest;
import com.schmaloogium.engine.pack.PackLoadResult;
import com.schmaloogium.engine.pack.PackSelection;
import com.schmaloogium.engine.pack.PersistenceFileAccess;
import com.schmaloogium.engine.pack.PersistenceFileAccessAcquisition;
import com.schmaloogium.engine.pack.PersistenceRootConfiguration;
import com.schmaloogium.engine.pack.RendererFeatureData;
import com.schmaloogium.engine.pack.RuntimeIdentityData;
import com.schmaloogium.engine.preprocess.MacroContribution;
import com.schmaloogium.engine.preprocess.MacroContributor;
import com.schmaloogium.engine.registry.ProgramBindingParticipant;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.ProgramResolutionProjection;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.PublicationFailure;
import com.schmaloogium.engine.registry.PublicationFailureKind;
import com.schmaloogium.engine.registry.PublicationResult;
import com.schmaloogium.engine.registry.PublishedRegistry;
import com.schmaloogium.engine.registry.RegistryBuildFailure;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.uniforms.RegistryGenerationAdoptionResult;
import com.schmaloogium.engine.uniforms.UniformBuildResult;
import com.schmaloogium.engine.uniforms.UniformConfiguration;
import com.schmaloogium.engine.uniforms.UniformReplayErrorSink;
import com.schmaloogium.engine.uniforms.UniformResetReason;
import com.schmaloogium.engine.uniforms.UniformRetirementReason;
import com.schmaloogium.engine.uniforms.UniformRetirementResult;
import com.schmaloogium.engine.uniforms.UniformRuntime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Headless fixtures for the composition root: a real P3 load of a minimal on-disk pack (so
 * the transaction sees an honest {@link PackConfiguration}), and scripted stand-ins for the
 * candidate-owning P4/P5/P6 steps that only the live engine can mint.
 */
final class PipelineFixtures {

    static final String PACK_NAME = "mp-minimal";

    private PipelineFixtures() {
    }

    static GLCapabilityProfile profile() {
        return new GLCapabilityProfile(3, 3, "3.30 synthetic", "Schmaloogium", "mod-test",
                8, 8, 16, 16, 16384, 0, 0, Set.of());
    }

    /** Writes a minimal directory pack under {@code shaderpacks/mp-minimal}. */
    static Path writeMinimalPack(Path gameDir) throws IOException {
        Path shaderpacks = gameDir.resolve("shaderpacks");
        Path shaders = shaderpacks.resolve(PACK_NAME).resolve("shaders");
        Files.createDirectories(shaders);
        Files.writeString(shaders.resolve("gbuffers_terrain.vsh"),
                "#version 120\nvoid main() {\n    gl_Position = ftransform();\n}\n",
                StandardCharsets.UTF_8);
        Files.writeString(shaders.resolve("gbuffers_terrain.fsh"),
                "#version 120\nuniform sampler2D texture;\nvoid main() {\n"
                        + "    gl_FragData[0] = texture2D(texture, gl_TexCoord[0].st);\n}\n",
                StandardCharsets.UTF_8);
        return shaderpacks;
    }

    static PersistenceFileAccess files(PackFrontEndServices services, Path shaderpacks, Path gameDir) {
        PersistenceFileAccessAcquisition acquisition = services.persistenceFiles(
                new PersistenceRootConfiguration(shaderpacks, gameDir));
        if (acquisition instanceof PersistenceFileAccessAcquisition.Acquired acquired) {
            return acquired.files();
        }
        throw new IllegalStateException("persistence roots rejected: " + acquisition);
    }

    /** A real discovery+load of the minimal pack: the honest inputs the transaction consumes. */
    record Loaded(PackConfiguration configuration, PackSelection selection) {
    }

    static Loaded loadMinimal(Path gameDir) throws IOException {
        Path shaderpacks = writeMinimalPack(gameDir);
        PackFrontEndServices services = PackFrontEnds.create();
        List<EngineDiagnostic> diagnostics = new ArrayList<>();
        SelectionResolver resolver = new SelectionResolver(services.frontEnd(), shaderpacks,
                () -> durableFor(services, shaderpacks, PACK_NAME), diagnostics::add);
        PackSelection selection = resolver.get();
        if (!(selection instanceof PackSelection.Filesystem)) {
            throw new IllegalStateException("minimal pack did not resolve: " + diagnostics);
        }
        PackLoadResult result = services.frontEnd().load(new PackLoadRequest(
                shaderpacks, selection,
                new RuntimeIdentityData(1, 12, 2, "schmaloogium", "test", OsFamily.LINUX, java.util.Map.of()),
                profile(), EngineOptionData.empty(), new CompanionOptionMacros(false, false),
                new RendererFeatureData(false, false), files(services, shaderpacks, gameDir),
                null, Optional.empty(), diagnostics::add));
        if (result instanceof PackLoadResult.Loaded loaded) {
            return new Loaded(loaded.configuration(), selection);
        }
        throw new IllegalStateException("minimal pack did not load: " + result + " " + diagnostics);
    }

    /** The durable reference discovery itself issues for a named candidate. */
    static String durableFor(PackFrontEndServices services, Path shaderpacks, String displayName) {
        return services.frontEnd().discover(new com.schmaloogium.engine.pack.PackDiscoveryRequest(
                        shaderpacks, d -> { }))
                .candidates().stream()
                .filter(c -> c.displayName().equals(displayName))
                .flatMap(c -> c.filesystemReference().stream())
                .map(com.schmaloogium.engine.pack.FilesystemCandidateReference::canonicalValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("no candidate " + displayName));
    }

    // ------------------------------------------------------------------ fakes

    static final class FakeRegistryView implements ProgramRegistryView {

        private final RegistryFingerprint fingerprint;

        FakeRegistryView(String fingerprint) {
            this.fingerprint = new RegistryFingerprint(fingerprint);
        }

        @Override
        public StageRegistry stages() {
            return null;
        }

        @Override
        public Optional<ResolvedProgramDescriptor> resolve(ProgramSlotId requested) {
            return Optional.empty();
        }

        @Override
        public List<ProgramResolutionProjection> resolutions() {
            return List.of();
        }

        @Override
        public RegistryFingerprint fingerprint() {
            return fingerprint;
        }

        @Override
        public com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint samplerPolicyFingerprint() {
            return null;
        }
    }

    /** Records adoption/retirement; every other verb is unreachable from the transaction. */
    static final class FakeRuntime implements UniformRuntime {

        final List<UniformRetirementReason> retirements = new ArrayList<>();
        final List<Long> adoptedGenerations = new ArrayList<>();
        RegistryGenerationAdoptionResult adoption = RegistryGenerationAdoptionResult.ADOPTED;
        private final ProgramBindingParticipant participant = (binding, context, uniforms) -> null;

        @Override
        public RegistryGenerationAdoptionResult adoptRegistryGeneration(long registryGeneration,
                                                                         UniformResetReason reason) {
            adoptedGenerations.add(registryGeneration);
            return adoption;
        }

        @Override
        public com.schmaloogium.engine.expr.api.FixedExpressionInputSchema fixedExpressionInputSchema() {
            return null;
        }

        @Override
        public com.schmaloogium.engine.uniforms.FrameBeginResult beginFrame(
                com.schmaloogium.engine.uniforms.FrameBeginInput input) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<com.schmaloogium.engine.uniforms.UniformFrameTiming> frameTiming(
                long registryGeneration, long frameId) {
            return Optional.empty();
        }

        @Override
        public com.schmaloogium.engine.uniforms.UniformEventSink events() {
            return null;
        }

        @Override
        public ProgramBindingParticipant samplerParticipant() {
            return participant;
        }

        @Override
        public ProgramBindingParticipant builtInParticipant() {
            return participant;
        }

        @Override
        public ProgramBindingParticipant customParticipant() {
            return participant;
        }

        @Override
        public MacroContributor centerDepthMacroContributor() {
            return configuration -> new MacroContribution.Empty();
        }

        @Override
        public void installCustomUniformBridge(
                com.schmaloogium.engine.expr.api.CustomUniformBridge bridge) {
        }

        @Override
        public void reset(UniformResetReason reason) {
        }

        @Override
        public UniformRetirementResult retire(UniformRetirementReason reason) {
            retirements.add(reason);
            return new UniformRetirementResult.Retired();
        }
    }

    /** Only generation and shadow disposition are consulted by the transaction. */
    static final class FakeEstateView implements BufferEstateView {

        final long generation;
        ShadowEstateResult shadow;

        FakeEstateView(long generation) {
            this.generation = generation;
            // P5 plans a shadow estate for every classic pack (baseline shadowtex0 minimum of
            // one), so the default disposition is the planned-but-unoperable one.
            this.shadow = new ShadowEstateUnavailable(new BufferFailure(
                    BufferFailureCode.CAPABILITY_LIMIT, "k", "d", List.of(), Optional.empty(),
                    Optional.empty()), generation);
        }

        @Override
        public long generation() {
            return generation;
        }

        @Override
        public ShadowEstateResult shadow() {
            return shadow;
        }

        @Override
        public RegistryFingerprint registryFingerprint() {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.BufferSizing sizing() {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.BufferInventory inventory() {
            return null;
        }

        @Override
        public BufferResourceSnapshot.Available resources() {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.MainDepthRefreshResult refreshMainDepth() {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.FrameBeginResult beginFrame(long frameId) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.VirtualTransitionResult applyVirtualTransition(
                long frameId, com.schmaloogium.engine.registry.PassDescriptor pass) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.DrawBuffersNoneOpenResult openDrawBuffersNone(long frameId) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.PassSnapshotResult snapshot(
                com.schmaloogium.engine.registry.PassDescriptor pass,
                com.schmaloogium.engine.registry.ProgramBindingSelection selection) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.MainMipmapResult generateMainMipmaps(
                com.schmaloogium.engine.buffers.PassBufferSnapshot snapshot) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.PassCompletionResult completePass(
                com.schmaloogium.engine.buffers.PassBufferSnapshot snapshot) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.PassDiscardResult discardPass(
                com.schmaloogium.engine.buffers.PassBufferSnapshot snapshot) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.ClearExecutionPlan clearPlan(
                com.schmaloogium.engine.buffers.ClearRequest request) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.ClearExecutionResult executeClear(
                com.schmaloogium.engine.buffers.ClearExecutionPlan plan) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.DepthCopyResult copyDepth(
                com.schmaloogium.engine.buffers.DepthCopyPoint point, long frameId) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.FrameEndResult commitFrame(long frameId) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.FrameEndResult abortFrame(long frameId, String diagnosticId) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.TextureBindingResult textureBindings(
                com.schmaloogium.engine.buffers.PassBufferSnapshot snapshot,
                com.schmaloogium.engine.buffers.TextureOverlayLease overlay,
                com.schmaloogium.engine.buffers.TextureOverlayPublicationId expectedOverlay) {
            return null;
        }
    }

    /** Records degradeToNeutral; every other verb is unreachable from the transaction. */
    static final class FakeShadowView implements ShadowEstateView {

        final long generation;
        final List<ShadowNeutralReason> neutralizations = new ArrayList<>();
        ShadowNeutralizationResult answer;

        FakeShadowView(long generation) {
            this.generation = generation;
            this.answer = new ShadowNeutralizationResult.Neutralized(generation, "d", false);
        }

        @Override
        public long estateGeneration() {
            return generation;
        }

        @Override
        public ShadowNeutralizationResult degradeToNeutral(long generation, ShadowNeutralReason reason) {
            neutralizations.add(reason);
            return answer;
        }

        @Override
        public com.schmaloogium.engine.buffers.ShadowBeginResult beginPass(long frameId,
                com.schmaloogium.engine.registry.PassDescriptor pass,
                com.schmaloogium.engine.registry.ProgramBindingSelection selection) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.ShadowOperationResult bind(
                com.schmaloogium.engine.buffers.ShadowPassSnapshot snapshot) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.ShadowOperationResult clear(
                com.schmaloogium.engine.buffers.ShadowPassSnapshot snapshot,
                com.schmaloogium.engine.buffers.ClearRequest request) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.ShadowOperationResult copyDepth(
                com.schmaloogium.engine.buffers.ShadowPassSnapshot snapshot,
                com.schmaloogium.engine.buffers.ShadowDepthCopyPoint point) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.TextureBindingResult shadowBindings(long generation,
                long frameId, com.schmaloogium.engine.buffers.ShadowPassSnapshot snapshot,
                com.schmaloogium.engine.buffers.TextureOverlayLease overlay,
                com.schmaloogium.engine.buffers.TextureOverlayPublicationId expectedOverlay) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.ShadowMipmapResult generateShadowMipmaps(long generation,
                long frameId, com.schmaloogium.engine.buffers.ShadowPassSnapshot snapshot,
                com.schmaloogium.engine.buffers.ShadowMipmapPolicy policy) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.ShadowCompletionResult completePass(
                com.schmaloogium.engine.buffers.ShadowPassSnapshot snapshot) {
            return null;
        }

        @Override
        public com.schmaloogium.engine.buffers.ShadowAbortResult abortPass(
                com.schmaloogium.engine.buffers.ShadowPassSnapshot snapshot, String diagnosticId) {
            return null;
        }
    }

    /**
     * Scripted stages: records the call order and every close/off publication; each step's
     * answer is a mutable field so a test flips exactly one branch.
     */
    static final class FakeStages implements PipelineStages {

        final List<String> calls = new ArrayList<>();
        final List<String> offKeys = new ArrayList<>();
        final List<BufferFailure> estateOffCauses = new ArrayList<>();
        final List<RegistryBuildFailure> registryOffCauses = new ArrayList<>();
        FakeRuntime runtime = new FakeRuntime();

        PackLoadResult loadAnswer;
        UniformBuildResult uniformsAnswer;
        boolean compileOff;
        RegistryBuildFailure compileFailure = new RegistryBuildFailure(
                com.schmaloogium.engine.registry.RegistryFailureKind.NO_REQUIRED_TERMINAL,
                List.of(), "test.compile", "no terminal");
        EstateHandle estateAnswer;
        boolean barrierInvalid;
        PublicationResult publishAnswer;
        BufferPublicationResult estatePublishAnswer;
        long publisherGeneration;
        FakeEstateView estateView = new FakeEstateView(1L);
        int registryClosed;
        int barrierClosed;
        int estateClosed;

        FakeStages(PackConfiguration configuration) {
            loadAnswer = new PackLoadResult.Loaded(configuration);
            uniformsAnswer = new UniformBuildResult.Success(runtime);
            estateAnswer = new EstateHandle.Ready() {
                @Override
                public void close() {
                    estateClosed++;
                }
            };
            publishAnswer = new PublicationResult.Accepted(new PublishedRegistry(1L,
                    Optional.of(new FakeRegistryView("fp")), Optional.empty(),
                    () -> {
                        throw new UnsupportedOperationException();
                    }));
            estatePublishAnswer = new BufferPublicationResult.Published(new PublishedBufferEstate(
                    1L, Optional.of(estateView),
                    new BufferResourceSnapshot.Unavailable(ResourceProjectionUnavailableReason.SHADERS_OFF)));
        }

        /** A second drain gets a fresh candidate runtime, as the real factory would mint. */
        FakeRuntime freshRuntime() {
            runtime = new FakeRuntime();
            uniformsAnswer = new UniformBuildResult.Success(runtime);
            calls.clear();
            return runtime;
        }

        @Override
        public PackLoadResult load(PackSelection selection, EngineOptionData options) {
            calls.add("load");
            return loadAnswer;
        }

        @Override
        public UniformBuildResult uniforms(long initialRegistryGeneration,
                                           UniformConfiguration configuration,
                                           UniformReplayErrorSink replayErrors) {
            calls.add("uniforms@" + initialRegistryGeneration);
            return uniformsAnswer;
        }

        @Override
        public RegistryHandle compile(PackConfiguration configuration, DimensionKey dimension,
                                      MacroContribution macroContribution) {
            calls.add("compile@" + dimension);
            if (compileOff) {
                return new RegistryHandle.Off(compileFailure);
            }
            return new RegistryHandle.Ready() {
                @Override
                public ProgramRegistryView view() {
                    return new FakeRegistryView("fp");
                }

                @Override
                public BarrierOutcome compose(ProgramBindingParticipant samplers,
                                              ProgramBindingParticipant builtIns,
                                              ProgramBindingParticipant customs) {
                    calls.add("compose");
                    if (barrierInvalid) {
                        return new BarrierOutcome.Invalid("test.barrier");
                    }
                    return new BarrierOutcome.Ready(() -> barrierClosed++);
                }

                @Override
                public void close() {
                    registryClosed++;
                }
            };
        }

        @Override
        public EstateHandle estate(PackConfiguration configuration, ProgramRegistryView registry,
                                   RegistryFingerprint registryFingerprint, BufferRuntimeInputs runtime) {
            calls.add("estate@" + runtime.displayExtent().width() + "x"
                    + runtime.displayExtent().height() + "/" + runtime.renderQuality());
            return estateAnswer;
        }

        @Override
        public long currentRegistryGeneration() {
            return publisherGeneration;
        }

        @Override
        public PublicationResult publishReady(RegistryHandle.Ready registry, BarrierHandle barrier) {
            calls.add("publishReady");
            return publishAnswer;
        }

        @Override
        public PublicationResult publishOff(RegistryBuildFailure cause) {
            calls.add("publishOff");
            registryOffCauses.add(cause);
            offKeys.add(cause.userMessage());
            return new PublicationResult.Accepted(PublishedRegistry.off(++publisherGeneration,
                    () -> {
                        throw new UnsupportedOperationException();
                    }));
        }

        @Override
        public BufferPublicationResult publishEstate(EstateHandle.Ready estate, RegistryFingerprint accepted) {
            calls.add("publishEstate@" + accepted.value());
            return estatePublishAnswer;
        }

        @Override
        public BufferPublicationResult publishEstateOff(BufferFailure cause) {
            calls.add("publishEstateOff");
            estateOffCauses.add(cause);
            return new BufferPublicationResult.Published(new PublishedBufferEstate(99L,
                    Optional.empty(),
                    new BufferResourceSnapshot.Unavailable(ResourceProjectionUnavailableReason.SHADERS_OFF)));
        }

        static PublicationResult rejected(PublishedRegistry unchanged) {
            return new PublicationResult.Rejected(unchanged, new PublicationFailure(
                    PublicationFailureKind.CONTEXT_EPOCH, "test.rejected", "rejected"));
        }

        static PublicationResult recoveredOff() {
            return new PublicationResult.RecoveredOff(PublishedRegistry.off(7L, () -> {
                throw new UnsupportedOperationException();
            }), new PublicationFailure(PublicationFailureKind.RELEASE_FAILED_SAFE, "test.recovered",
                    "recovered"));
        }
    }

    static final class CollectingDiagnostics implements DiagnosticReporter {

        final List<EngineDiagnostic> reports = new ArrayList<>();

        @Override
        public void report(EngineDiagnostic d) {
            reports.add(d);
        }
    }
}
