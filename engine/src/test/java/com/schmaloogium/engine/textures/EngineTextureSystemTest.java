// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.buffers.CandidateOrigin;
import com.schmaloogium.engine.buffers.FixedSamplerName;
import com.schmaloogium.engine.buffers.TextureCandidateEntry;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.config.EngineFlags;
import com.schmaloogium.engine.config.IdMappingFileFingerprint;
import com.schmaloogium.engine.config.IdMappingFileInput;
import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.IdMappingMacroEnvironment;
import com.schmaloogium.engine.config.MappingFileState;
import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.config.MacroIdentityPolicy;
import com.schmaloogium.engine.config.NoiseRequirement;
import com.schmaloogium.engine.config.NoiseTextureSpec;
import com.schmaloogium.engine.config.MacroConfiguration;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.OptionCatalogs;
import com.schmaloogium.engine.config.ProgramStateModel;
import com.schmaloogium.engine.config.ResourceRequirements;
import com.schmaloogium.engine.config.ShaderPropertiesModel;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureService;
import com.schmaloogium.engine.gl.record.RecordingGLDevice;
import com.schmaloogium.engine.textures.TextureCaptureSink;
import com.schmaloogium.engine.gl.record.ScriptedResponses;
import com.schmaloogium.engine.pack.CompanionOptionMacros;
import com.schmaloogium.engine.pack.CompatibilityStatus;
import com.schmaloogium.engine.pack.ConfigurationFingerprint;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.pack.PackAssetSnapshot;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.pack.PackIdentity;
import com.schmaloogium.engine.preprocess.SourceCatalog;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.PassIndex;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.registry.StageStep;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.lang.reflect.Field;
import java.util.Set;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Owner lifecycle over the recorder (§8.1): exact lease preflight order and atomicity
 * (rejections never touch the lease count; a held lease defers destruction until drain),
 * the atlasSize value source, and the companion/default/noise candidate rows.
 */
class EngineTextureSystemTest {

    private static final long EPOCH = 42L;
    private static final long ESTATE = 7L;
    private static final long REGISTRY_GEN = 3L;
    private static final String REGISTRY_FP = "rfp-1";

    @AfterEach
    void resetObserver() {
        AtlasBindingObservers.install(new AtlasBindingObserver() {
            @Override
            public AtlasBindingObservation authenticate(AtlasBindingEvidence evidence) {
                return new AtlasBindingObservation.InvalidBase();
            }

            @Override
            public boolean isLatest(Object token) {
                return false;
            }
        });
    }

    @Test
    void lease_preflightOrderAndAtomicity() {
        // Phase A: default ABSENT observer (rejects all evidence) is active at factory
        // time, so preflight rejections before base-binding observation hold.
        var device = new RecordingGLDevice(profile(), new ScriptedResponses());
        TextureSystem system = system(device);
        var evidence = new Evidence();
        var selection = selection();

        // 1. No publication yet.
        assertEquals(TextureLeaseRejection.PUBLICATION_UNAVAILABLE,
            rejection(system.lease(id(ESTATE), selection, evidence)));

        var publication = build(system, device);
        assertTrue(publication != null, "build must be ready");
        var liveId = publication.id();
        assertEquals(ESTATE, liveId.generation());

        // 2. Wrong generation over the live content fingerprint.
        assertEquals(TextureLeaseRejection.PUBLICATION_ID_MISMATCH,
            rejection(system.lease(new TextureOverlayPublicationId(ESTATE + 1,
                liveId.contentFingerprint()), selection, evidence)));
        // 3. Wrong registry fingerprint.
        assertEquals(TextureLeaseRejection.REGISTRY_FINGERPRINT_MISMATCH,
            rejection(system.lease(liveId, selection("other-fp", REGISTRY_GEN),
                evidence)));
        // 4. Stale selection generation.
        assertEquals(TextureLeaseRejection.STALE_SELECTION,
            rejection(system.lease(liveId, selection(REGISTRY_FP, REGISTRY_GEN + 5),
                evidence)));
        // 5. Invalid base binding (factory-time observer rejects all evidence).
        assertEquals(TextureLeaseRejection.INVALID_BASE_BINDING,
            rejection(system.lease(liveId, selection, evidence)));

        // Phase B: authenticated but not latest observer, installed before factory time.
        var staleDevice = new RecordingGLDevice(profile(), new ScriptedResponses());
        installObserver(new Object(), false);
        TextureSystem staleSystem = system(staleDevice);
        var stalePublication = build(staleSystem, staleDevice);
        assertTrue(stalePublication != null, "stale-phase build must be ready");
        assertEquals(TextureLeaseRejection.STALE_BASE_BINDING,
            rejection(staleSystem.lease(stalePublication.id(), selection, evidence)));

        // Phase C: current observer. Atomicity: close retires immediately but deletes
        // nothing while a lease is outstanding; drain destroys in reverse order.
        var liveDevice = new RecordingGLDevice(profile(), new ScriptedResponses());
        Object token = new Object();
        installObserver(token, true);
        TextureSystem liveSystem = system(liveDevice);
        var livePublication = build(liveSystem, liveDevice);
        assertTrue(livePublication != null, "live-phase build must be ready");
        var acquired = assertInstanceOf(TextureLeaseResult.Acquired.class,
            liveSystem.lease(livePublication.id(), selection, evidence));
        var lease = acquired.lease();
        assertTrue(lease.isCurrent());

        int creations = liveDevice.log().callsMatching("textures.create").size();
        assertTrue(creations > 0, "build must have created objects");

        liveSystem.close();
        assertFalse(lease.isCurrent());
        assertEquals(0, liveDevice.log().callsMatching("textures.delete").size());
        assertEquals(TextureLeaseRejection.PUBLICATION_UNAVAILABLE,
            rejection(liveSystem.lease(livePublication.id(), selection, evidence)));

        lease.close();
        assertFalse(lease.isCurrent());
        int deletions = liveDevice.log().callsMatching("textures.delete").size();
        assertEquals(creations, deletions, "all owned objects deleted exactly once");

        liveSystem.close();
        assertEquals(creations, liveDevice.log().callsMatching("textures.delete").size());
    }

    @Test
    void lease_acquiredViewCarriesPublicationFacts() {
        Object token = new Object();
        installObserver(token, true);
        var device = new RecordingGLDevice(profile(), new ScriptedResponses());
        TextureSystem system = system(device);
        var publication = build(system, device);
        assertTrue(publication != null, "build must be ready");
        var lease = assertInstanceOf(TextureLeaseResult.Acquired.class,
            system.lease(publication.id(), selection(), new Evidence())).lease();
        assertEquals(ESTATE, lease.id().generation());
        assertEquals(EPOCH, lease.resourceReloadEpoch());
        assertEquals(REGISTRY_FP, lease.registryFingerprint().value());
        assertEquals(REGISTRY_GEN, lease.registryGeneration());
        lease.close();
    }

    @Test
    void atlasSize_valueSourceLaw() {
        var device = new RecordingGLDevice(profile(), new ScriptedResponses());
        var system = system(device);
        assertTrue(system.atlasSize() instanceof AtlasSizeResult.Unknown);
        assertTrue(system.atlasSize(new AtlasId("blocks.png"))
            instanceof AtlasSizeResult.Unknown);
        build(system, device);
        // Post-build but pre-capture: still Unknown (Known ≠ bound evidence, but nothing
        // has been captured at this epoch yet).
        ((TextureCaptureSink) system).onStitchAccepted(new AtlasDescriptor(
            new AtlasId("blocks.png"), 4, 6, 1,
            List.of(SpriteDescriptor.staticSprite("stone", 0, 0, 2, 2))), true);
        var known = assertInstanceOf(AtlasSizeResult.Known.class, system.atlasSize());
        assertEquals(4, known.width());
        assertEquals(6, known.height());
        ((TextureCaptureSink) system).invalidateStitch(EPOCH + 1);
        assertTrue(system.atlasSize() instanceof AtlasSizeResult.Unknown);
    }

    @Test
    void candidates_companionDefaultNoiseRowsInCanonicalOrder() {
        var device = new RecordingGLDevice(profile(), new ScriptedResponses());
        var system = system(device);
        var ready = build(system, device);
        var table = ready.candidates();

        // Noise cell: exactly one candidate, owned handle, Noise origin, ordinal 0.
        var noiseCell = table.entry(StageId.GBUFFERS, FixedSamplerName.NOISETEX);
        var noiseRow = assertInstanceOf(TextureCandidateEntry.Candidates.class, noiseCell);
        assertEquals(1, noiseRow.candidates().size());
        var noiseCandidate = noiseRow.candidates().getFirst();
        assertInstanceOf(CandidateOrigin.Noise.class, noiseCandidate.origin());
        assertTrue(noiseCandidate.candidateOrdinal() >= 0);
        assertEquals(FixedSamplerName.NOISETEX.exactName(),
            noiseCandidate.exactSamplerName());

        // Texture cell: companion(default-filled) sprites per atlas then defaults —
        // one atlas × 2 sprites produce no extra candidates (discovery empty → the full
        // atlas carries them), then the two standalone default fills follow.
        var textureCell = table.entry(StageId.GBUFFERS, FixedSamplerName.TEXTURE);
        var textureRow = assertInstanceOf(TextureCandidateEntry.Candidates.class,
            textureCell);
        List<String> originKinds = textureRow.candidates().stream()
            .map(c -> c.origin() instanceof CandidateOrigin.Companion ? "companion"
                : c.origin() instanceof CandidateOrigin.DefaultFill ? "default"
                : "other")
            .toList();
        assertTrue(originKinds.contains("default"), "default fills must be present");
        assertTrue(originKinds.indexOf("default")
            <= originKinds.lastIndexOf("default"), "defaults contiguous");
        int previous = -1;
        for (var candidate : textureRow.candidates()) {
            assertTrue(candidate.candidateOrdinal() > previous,
                "cell ordinals are unique and increasing");
            previous = candidate.candidateOrdinal();
        }
        // Shadow-expanded stage sees its own dense ordering.
        var shadowCell = table.entry(StageId.SHADOW, FixedSamplerName.TEXTURE);
        var shadowRow = assertInstanceOf(TextureCandidateEntry.Candidates.class,
            shadowCell);
        assertEquals(textureRow.candidates().size(), shadowRow.candidates().size());

        // Absent cells classify rather than fabricate.
        assertTrue(table.entry(StageId.GBUFFERS, FixedSamplerName.LIGHTMAP)
            instanceof TextureCandidateEntry.Absent);
        // Companion plans exist for both enabled kinds over the catalogued atlas.
        assertEquals(2, ready.plan().companions().size());
    }

    @Test
    void build_backendFailureTearsDownAndFailsWithoutPublication() {
        var device = new RecordingGLDevice(profile(), new ScriptedResponses());
        GLDevice failing = failingAfter(device, 1);
        TextureSystem system = TextureSystemFactory.factory().create(failing, reporter())
            instanceof TextureSystemCreationResult.Created c ? c.system() : null;
        assertTrue(system != null);
        var result = build(system, failing);
        assertTrue(result == null, "build must fail");
        // All-or-nothing: whatever was created before the failure is deleted.
        int creations = device.log().callsMatching("textures.create").size();
        int deletions = device.log().callsMatching("textures.delete").size();
        assertEquals(creations, deletions);
        // atlasSize stays Unknown: no publication, no capture.
        assertTrue(system.atlasSize() instanceof AtlasSizeResult.Unknown);
    }

    @Test
    void plan_schemaGateRejectsForeignSchema() {
        var request = request(profile(), 1);
        var result = system(new RecordingGLDevice(profile(), new ScriptedResponses()))
            .plan(request);
        var invalid = assertInstanceOf(TexturePlanResult.Invalid.class, result);
        assertEquals(TextureFailureCode.IDENTITY_MISMATCH, invalid.failure().code());
    }

    // ------------------------------------------------------------------
    // fixtures
    // ------------------------------------------------------------------

    private static TextureSystem system(GLDevice device) {
        return assertInstanceOf(TextureSystemCreationResult.Created.class,
            TextureSystemFactory.factory().create(device, reporter())).system();
    }

    private static TexturePublication build(TextureSystem system, GLDevice device) {
        var result = system.build(new TextureBuildRequest(request(profile(),
            PackFrontEnd.CURRENT_SCHEMA_VERSION), new TextureBuildSources(EPOCH,
            List.of())));
        if (result instanceof TextureBuildResult.Ready ready) {
            return ready.publication();
        }
        return null;
    }

    private static TextureLeaseRejection rejection(TextureLeaseResult result) {
        return assertInstanceOf(TextureLeaseResult.Rejected.class, result).reason();
    }

    private record Evidence() implements AtlasBindingEvidence {
    }

    private static void installObserver(Object token, boolean latest) {
        AtlasBindingObservers.install(new AtlasBindingObserver() {
            @Override
            public AtlasBindingObservation authenticate(AtlasBindingEvidence evidence) {
                return new AtlasBindingObservation.Authenticated(
                    new com.schmaloogium.engine.buffers.BaseAtlasContext.NonAtlas(),
                    Optional.empty(), token);
            }

            @Override
            public boolean isLatest(Object currentness) {
                return latest && currentness == token;
            }
        });
    }

    private static com.schmaloogium.engine.registry.ProgramBindingSelection selection() {
        return selection(REGISTRY_FP, REGISTRY_GEN);
    }

    /**
     * {@link ProgramBindingSelection} is mint-only inside Phase 4 barriers, so tests
     * allocate an instance via Unsafe and stamp only the fields the lease preflight
     * dereferences (the same precedent as EstateFrameProtocolTest).
     */
    private static com.schmaloogium.engine.registry.ProgramBindingSelection selection(
            String fingerprint, long generation) {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Object unsafe = theUnsafe.get(null);
            Method allocateInstance = unsafeClass.getMethod("allocateInstance",
                Class.class);
            var minted = allocateInstance.invoke(unsafe,
                com.schmaloogium.engine.registry.ProgramBindingSelection.class);
            for (Field field : com.schmaloogium.engine.registry.ProgramBindingSelection.class
                    .getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType() == RegistryFingerprint.class) {
                    unsafeClass.getMethod("putObject", Object.class, long.class,
                        Object.class).invoke(unsafe, minted,
                        unsafeClass.getMethod("objectFieldOffset", Field.class)
                            .invoke(unsafe, field),
                        new RegistryFingerprint(fingerprint));
                } else if (field.getType() == long.class
                        && field.getName().equals("registryGeneration")) {
                    unsafeClass.getMethod("putLong", Object.class, long.class,
                        long.class).invoke(unsafe, minted,
                        unsafeClass.getMethod("objectFieldOffset", Field.class)
                            .invoke(unsafe, field), generation);
                }
            }
            return (com.schmaloogium.engine.registry.ProgramBindingSelection) minted;
        } catch (ReflectiveOperationException failure) {
            throw new AssertionError("cannot mint an inert selection", failure);
        }
    }

    private static TextureOverlayPublicationId id(long generation) {
        return new TextureOverlayPublicationId(generation,
            new com.schmaloogium.engine.buffers.TextureOverlayFingerprint("fp"));
    }

    private static GLDevice failingAfter(GLDevice delegate, int allowedCreates) {
        return new GLDevice() {
            private final GLDevice self = this;
            private int creates;

            @Override
            public TextureService textures() {
                var real = delegate.textures();
                return new TextureService() {
                    @Override
                    public TextureHandle create(String debugLabel) {
                        if (++creates > allowedCreates) {
                            throw new IllegalStateException("scripted backend failure");
                        }
                        return real.create(debugLabel);
                    }

                    @Override
                    public void prepareUnitBindings(int occupiedUnitMask) {
                        real.prepareUnitBindings(occupiedUnitMask);
                    }
                    @Override
                    public void allocate(TextureHandle t,
                            com.schmaloogium.engine.gl.TextureSpec spec) {
                        real.allocate(t, spec);
                    }

                    @Override
                    public void setParameters(TextureHandle t,
                            com.schmaloogium.engine.gl.TextureParameters p) {
                        real.setParameters(t, p);
                    }

                    @Override
                    public void upload(TextureHandle t,
                            com.schmaloogium.engine.gl.TextureData data) {
                        real.upload(t, data);
                    }

                    @Override
                    public void bindToUnit(int unit, TextureHandle t) {
                        real.bindToUnit(unit, t);
                    }

                    @Override
                    public void generateMipmap(TextureHandle t) {
                        real.generateMipmap(t);
                    }

                    @Override
                    public void delete(TextureHandle t) {
                        real.delete(t);
                    }
                };
            }

            @Override public com.schmaloogium.engine.gl.ShaderService shaders() {
                return delegate.shaders();
            }

            @Override public com.schmaloogium.engine.gl.UniformService uniforms() {
                return delegate.uniforms();
            }


            @Override public com.schmaloogium.engine.gl.VertexInputService
                    vertexInputs() {
                return delegate.vertexInputs();
            }
            @Override public GLCapabilityProfile capabilities() {
                return delegate.capabilities();
            }


            @Override public com.schmaloogium.engine.gl.FramebufferService
                    framebuffers() {
                return delegate.framebuffers();
            }
            @Override public com.schmaloogium.engine.gl.DrawService draw() {
                return delegate.draw();
            }

            @Override public com.schmaloogium.engine.gl.StateService state() {
                return delegate.state();
            }

            @Override public com.schmaloogium.engine.gl.DebugService debug() {
                return delegate.debug();
            }

            @Override public java.util.List<com.schmaloogium.engine.gl.GLError>
                    drainErrors() {
                return delegate.drainErrors();
            }
        };
    }

    private static GLCapabilityProfile profile() {
        return new GLCapabilityProfile(3, 2, "1.50", "vendor", "renderer", 8, 8, 16, 16,
            1024, 256, 256, Set.of());
    }

    private static DiagnosticReporter reporter() {
        return d -> System.out.println("DIAG " + d);
    }

    private static TexturePlanRequest request(GLCapabilityProfile capabilities,
            int schemaVersion) {
        int version = PackFrontEnd.CURRENT_SCHEMA_VERSION;
        if (schemaVersion != version) {
            version = schemaVersion;
        }
        PackIdentity pack = new PackIdentity(new NormalizedPackPath("shaders"), Map.of());
        var catalog = OptionCatalogs.create(List.of(), new Object(), false);
        ShaderPropertiesModel properties = new ShaderPropertiesModel(
            EngineFlags.allDefault(), List.of(), List.of(), List.of(),
            new NoiseTextureSpec.Generated(), List.of(),
            new ProgramStateModel(Map.of()), List.of());
        ResourceRequirements resources = new ResourceRequirements(null, Map.of(), null,
            null, Map.of(), null, null, new NoiseRequirement(true, 4));
        IdMappingFileInput absentBlock = new IdMappingFileInput(MappingKind.BLOCK,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("fp"));
        IdMappingFileInput absentItem = new IdMappingFileInput(MappingKind.ITEM,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("fp"));
        IdMappingFileInput absentEntity = new IdMappingFileInput(MappingKind.ENTITY,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("fp"));
        IdMappingFileInput absentLayer = new IdMappingFileInput(MappingKind.LAYER,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("fp"));
        var configuration = new PackConfiguration(version, pack,
            CompatibilityStatus.COMPATIBLE, Map.of(), new SourceCatalog() {
                @Override
                public List<com.schmaloogium.engine.preprocess.SourceDocument> sources() {
                    return List.of();
                }

                @Override
                public List<com.schmaloogium.engine.preprocess.SourceKey> roots() {
                    return List.of();
                }

                @Override
                public List<com.schmaloogium.engine.preprocess.IncludeEdge>
                        includeEdges() {
                    return List.of();
                }

                @Override
                public Optional<com.schmaloogium.engine.preprocess.SourceDocument>
                        source(com.schmaloogium.engine.preprocess.SourceId id) {
                    return Optional.empty();
                }

                @Override
                public Set<com.schmaloogium.engine.config.ProgramKey>
                        executablePrograms() {
                    return Set.of();
                }

                @Override
                public com.schmaloogium.engine.preprocess.SourceMaterializer
                        materializer() {
                    return null;
                }
            }, emptyAssets(pack),
            new OptionConfiguration(catalog, catalog.defaultState(), List.of(), null,
                Map.of(), null, Map.of()),
            new MacroConfiguration(MacroIdentityPolicy.OPTION_1, List.of(),
                List.of(), new CompanionOptionMacros(false, false), List.of(), List.of(),
                Map.of(), List.of()),
            properties, resources,
            new IdMappingInput(version, new IdMappingMacroEnvironment(11904, List.of()),
                absentBlock, absentItem, absentEntity, absentLayer),
            List.of(), new ConfigurationFingerprint("config-fp"));
        var atlases = new AtlasCatalog(List.of(new AtlasDescriptor(new AtlasId("b.png"),
            4, 6, 1, List.of(SpriteDescriptor.staticSprite("dirt", 2, 0, 2, 2),
                SpriteDescriptor.staticSprite("stone", 0, 0, 2, 2)))));
        return new TexturePlanRequest(configuration, registry(), atlases,
            TextureSourceCatalog.EMPTY, new CompanionPolicy(true, true,
                CompanionDemandSource.DECLARED_SAMPLERS), new CompanionMacroState(true,
                true), capabilities, new RegistryFingerprint(REGISTRY_FP), ESTATE,
            REGISTRY_GEN, EPOCH);
    }

    private static ProgramRegistryView registry() {
        return new ProgramRegistryView() {
            @Override
            public StageRegistry stages() {
                return new StageRegistry() {
                    @Override public List<StageStep> schedule() {
                        return List.of();
                    }

                    @Override public List<PassDescriptor> passes(StageStep step) {
                        return List.of();
                    }

                    @Override public Optional<PassDescriptor> named(StageStep step,
                            ProgramSlotId id) {
                        return Optional.empty();
                    }

                    @Override public Optional<PassDescriptor> indexed(StageStep step,
                            PassIndex index) {
                        return Optional.empty();
                    }

                    @Override public boolean stepExists(StageStep step) {
                        return false;
                    }
                };
            }

            @Override public Optional<ResolvedProgramDescriptor> resolve(
                    ProgramSlotId requested) {
                return Optional.empty();
            }

            @Override public List<com.schmaloogium.engine.registry.ProgramResolutionProjection>
                    resolutions() {
                return List.of();
            }

            @Override public RegistryFingerprint fingerprint() {
                return new RegistryFingerprint(REGISTRY_FP);
            }

            @Override public FixedSamplerPolicyFingerprint samplerPolicyFingerprint() {
                return new FixedSamplerPolicyFingerprint("policy-fp");
            }
        };
    }

    private static PackAssetSnapshot emptyAssets(PackIdentity pack) {
        try {
            Constructor<?> constructor = Class
                .forName("com.schmaloogium.engine.pack.PackAssetSnapshotImpl")
                .getDeclaredConstructor(PackIdentity.class, Map.class);
            constructor.setAccessible(true);
            return (PackAssetSnapshot) constructor.newInstance(pack, Map.of());
        } catch (ReflectiveOperationException failure) {
            throw new AssertionError("cannot mint an empty pack-asset snapshot", failure);
        }
    }
}
