// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.buffers.BindingPurpose;
import com.schmaloogium.engine.buffers.CompanionKind;
import com.schmaloogium.engine.buffers.BaseAtlasContext;
import com.schmaloogium.engine.buffers.BindingOriginKind;
import com.schmaloogium.engine.buffers.CandidateOrigin;
import com.schmaloogium.engine.buffers.FixedSamplerName;
import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.buffers.FrameBeginResult;
import com.schmaloogium.engine.buffers.PassBufferSnapshot;
import com.schmaloogium.engine.buffers.PassSnapshotResult;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.buffers.TextureBindingCandidate;
import com.schmaloogium.engine.buffers.TextureBindingDiagnostic;
import com.schmaloogium.engine.buffers.TextureBindingDiagnosticCode;
import com.schmaloogium.engine.buffers.TextureBindingOutcome;
import com.schmaloogium.engine.buffers.TextureBindingRejection;
import com.schmaloogium.engine.buffers.TextureBindingResult;
import com.schmaloogium.engine.buffers.TextureCandidateEntry;
import com.schmaloogium.engine.buffers.TextureCandidateTable;
import com.schmaloogium.engine.buffers.TextureHandleRef;
import com.schmaloogium.engine.buffers.TextureOverlayAbsence;
import com.schmaloogium.engine.buffers.TextureOverlayFingerprint;
import com.schmaloogium.engine.buffers.TextureOverlayLease;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.buffers.TextureParameterFingerprint;
import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.buffers.TextureSourceIdentity;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.config.TextureBindingKey;
import com.schmaloogium.engine.config.TexturePropertyStage;
import com.schmaloogium.engine.config.TextureTarget;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureExtent;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureSpec;
import com.schmaloogium.engine.gl.TextureWrap;
import com.schmaloogium.engine.gl.record.GLCall;
import com.schmaloogium.engine.gl.record.ScriptedResponses;
import com.schmaloogium.engine.pack.ConfigurationFingerprint;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
import com.schmaloogium.engine.registry.BarrierConstructionResult;
import com.schmaloogium.engine.registry.BarrierParticipantResult;
import com.schmaloogium.engine.registry.BarrierPublicationCandidate;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.BufferRef;
import com.schmaloogium.engine.registry.ClassicProgramCatalog;
import com.schmaloogium.engine.registry.CompiledRegistryCandidate;
import com.schmaloogium.engine.registry.DrawRouting;
import com.schmaloogium.engine.registry.DrawRoutingSlot;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.GeometryInputRequirement;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.PassResourceAccess;
import com.schmaloogium.engine.registry.ProgramBindingParticipant;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramOwnBuildDisposition;
import com.schmaloogium.engine.registry.ProgramRegistries;
import com.schmaloogium.engine.registry.ProgramRegistryPublisher;
import com.schmaloogium.engine.registry.ProgramResolutionProjection;
import com.schmaloogium.engine.registry.ProgramResolutionStatus;
import com.schmaloogium.engine.registry.ProgramSamplerDeclaration;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint;
import com.schmaloogium.engine.registry.ProgramSelectionResult;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramStateBundle;
import com.schmaloogium.engine.registry.ProgramUniformLayout;
import com.schmaloogium.engine.registry.PublicationResult;
import com.schmaloogium.engine.registry.PublishedRegistry;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.RegistryPublication;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageRegistries;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.registry.StageStep;
import com.schmaloogium.engine.registry.internal.CompiledProgramBinding;
import com.schmaloogium.engine.registry.internal.CompiledProgramRegistryImpl;
import com.schmaloogium.engine.registry.internal.RegistryContexts;

/** Physical fixed-unit selection and lease ownership under PHASE_5_DOC §§4.12/5. */
class TextureBinderDomainTest {
    private static final ProgramSlotId COMPOSITE = new ProgramSlotId("composite");
    private static final BufferRef COLORTEX0 = new BufferRef(BufferDomain.COLORTEX, 0);
    private static final DeclaredGlslType.Sampler SAMPLER_2D = new DeclaredGlslType.Sampler(
        SampledKind.FLOAT, TextureDimension.D2, false, false, false);

    private record Fixture(BuffersEstateFixture estate, EstateViewImpl view,
            PassBufferSnapshot pass, ScriptedResponses responses) {
    }

    private static Fixture fixture(String... names) {
        return fixture(SAMPLER_2D, names);
    }

    private static Fixture fixture(DeclaredGlslType.Sampler shape, String... names) {
        return fixture(COMPOSITE, StageId.COMPOSITE, StageBand.FRAME_END,
            layout(StageId.COMPOSITE, StageBand.FRAME_END, shape, names));
    }

    private static Fixture fixture(ProgramSlotId slot, StageId stage, StageBand band,
            ProgramSamplerLayout layout) {
        ScriptedResponses responses = new ScriptedResponses();
        PlanningArtifacts.PlannedRoute route = new PlanningArtifacts.PlannedRoute(slot,
            List.of(new DrawRoutingSlot.Attachment(COLORTEX0)),
            List.of(BuffersEstateFixture.colorRow(0)));
        BuffersEstateFixture estate = BuffersEstateFixture.createWithRoutes(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8, ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {new ResourceClearPolicy.FogRgbAlphaOne(),
                new ResourceClearPolicy.FogRgbAlphaOne()},
            new boolean[] {true, true}, 1, Map.of(slot, route), responses);
        String key = CandidateBuilder.passKey(route);
        FramebufferHandle fbo = estate.device.framebuffers().create(key);
        TextureHandle color = estate.core.colorPairs.get(0).sideA;
        estate.device.framebuffers().attachColor(fbo, 0, color);
        estate.core.passFbos.put(key, fbo);
        estate.core.attachedColor.put(fbo, new java.util.LinkedHashMap<>(Map.of(0, color)));
        ProgramBindingSelection selection = selection(estate, slot, stage, band, layout);
        StageStep step = StageRegistries.modernFullShape(15, 15).schedule().stream()
            .filter(value -> value.stage() == stage && value.band() == band).findFirst().orElseThrow();
        PassDescriptor descriptor = new PassDescriptor(step, slot, Optional.empty(),
            new PassResourceAccess(Set.of(), Set.of(COLORTEX0), Map.of(), Set.of()), Set.of());
        EstateViewImpl view = new EstateViewImpl(estate.core, new ClearExecutor(),
            new TextureBinder(), new ShadowOperator());
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(1));
        PassBufferSnapshot pass = assertInstanceOf(PassSnapshotResult.Acquired.class,
            view.snapshot(descriptor, selection)).snapshot();
        return new Fixture(estate, view, pass, responses);
    }

    static ProgramSamplerLayout.Shader layout(StageId stage, StageBand band,
            DeclaredGlslType.Sampler shape, String... names) {
        List<ProgramSamplerDeclaration> declarations = new ArrayList<>();
        for (int index = 0; index < names.length; index++) {
            declarations.add(new ProgramSamplerDeclaration(names[index], shape,
                index, List.of()));
        }
        return new ProgramSamplerLayout.Shader(
            new ProgramSamplerLayoutFingerprint("binder-layout"),
            FixedSamplerPolicies.appB3Fingerprint(), stage,
            Set.of(band), declarations, new SamplerLayoutValidation.Valid());
    }

    static ProgramBindingSelection selection(BuffersEstateFixture estate, ProgramSlotId slot,
            StageId stage, StageBand band, ProgramSamplerLayout layout) {
        ProgramStateBundle state = new ProgramStateBundle(
            new DrawRouting.AllUsedBuffers(BufferDomain.COLORTEX), Set.of(), 1, Set.of(),
            Optional.empty(), Optional.empty(), Optional.empty(), Map.of(), Optional.empty(),
            GeometryInputRequirement.NONE);
        var program = estate.device.shaders().createProgram();
        CompiledProgramBinding binding = layout instanceof ProgramSamplerLayout.FixedFunctionEmpty
            ? new CompiledProgramBinding.FixedFunction(slot, state)
            : new CompiledProgramBinding.ShaderProgram(
                slot, program, state, ProgramUniformLayout.empty(), layout, List.of());
        StageRegistry stages = StageRegistries.modernFullShape(15, 15);
        CompiledProgramRegistryImpl registry;
        try {
            // Assemble the same registry fixture as RegistryBarrierPublicationTest. Only
            // assembly crosses package access; publication and selector issuance are real.
            var constructor = CompiledProgramRegistryImpl.class.getDeclaredConstructor(
                StageRegistry.class, List.class, Map.class, List.class,
                RegistryFingerprint.class, FixedSamplerPolicyFingerprint.class, GLDevice.class,
                List.class, RegistryContexts.class, ProgramSlotId.class);
            constructor.setAccessible(true);
            var resolvedConstructor = Class.forName(
                "com.schmaloogium.engine.registry.internal.ResolvedCompiledProgramBinding")
                .getDeclaredConstructor(ProgramSlotId.class, CompiledProgramBinding.class, List.class);
            resolvedConstructor.setAccessible(true);
            registry = constructor.newInstance(stages, ClassicProgramCatalog.rows(),
                Map.of(slot, resolvedConstructor.newInstance(slot, binding, List.of(slot))),
                List.of(new ProgramResolutionProjection(slot,
                    ProgramResolutionStatus.SOURCED, Optional.empty(), true,
                    ProgramOwnBuildDisposition.SUCCEEDED, "")),
                estate.core.registryFingerprint, FixedSamplerPolicies.appB3Fingerprint(),
                estate.device, List.of(program), new RegistryContexts(), ClassicProgramCatalog.SHADOW);
        } catch (ReflectiveOperationException failure) {
            throw new AssertionError("cannot assemble registry fixture", failure);
        }
        CompiledRegistryCandidate candidate = new CompiledRegistryCandidate(registry);
        ProgramBindingParticipant participant = (bindingView, context, uniforms) ->
            new BarrierParticipantResult.Continue();
        BarrierPublicationCandidate barrier = assertInstanceOf(BarrierConstructionResult.Ready.class,
            ProgramRegistries.productionComposer().compose(candidate, participant, participant,
                participant)).candidate();
        ProgramRegistryPublisher publisher = ProgramRegistries.publisher();
        PublishedRegistry published = assertInstanceOf(PublicationResult.Accepted.class,
            publisher.publish(new RegistryPublication.Ready(candidate, barrier),
                ((PublishedRegistry) publisher.current()).contexts().beginFrame().release())).published();
        StageStep step = stages.schedule().stream()
            .filter(value -> value.stage() == stage && value.band() == band).findFirst().orElseThrow();
        return assertInstanceOf(ProgramSelectionResult.Selected.class,
            published.barrier().orElseThrow().select(slot,
                published.contexts().beginFrame().activation(step, stage == StageId.SHADOW))).selection();
    }

    static final class Overlay implements TextureOverlayLease {
        private final BuffersEstateFixture estate;
        private final ProgramBindingSelection selection;
        private final List<TextureBindingCandidate> entries;
        private final TextureOverlayPublicationId id;
        private boolean current = true;
        private int closes;
        private Map<TextureHandleRef, BaseAtlasContext> atlases = Map.of();

        Overlay(Fixture fixture, TextureBindingCandidate... entries) {
            this(fixture.estate, fixture.pass.selection(), entries);
        }

        Overlay(BuffersEstateFixture estate, ProgramBindingSelection selection,
                TextureBindingCandidate... entries) {
            this.estate = estate;
            this.selection = selection;
            this.entries = List.of(entries);
            id = new TextureOverlayPublicationId(estate.core.generation,
                new TextureOverlayFingerprint("overlay"));
        }

        @Override public TextureOverlayPublicationId id() { return id; }
        @Override public RegistryFingerprint registryFingerprint() {
            return selection.registryFingerprint();
        }
        @Override public long registryGeneration() {
            return selection.registryGeneration();
        }
        @Override public long resourceReloadEpoch() { return 1; }
        @Override public ConfigurationFingerprint configurationFingerprint() {
            return estate.core.configurationFingerprint;
        }
        @Override public FixedSamplerPolicyFingerprint policyFingerprint() {
            return FixedSamplerPolicies.appB3Fingerprint();
        }
        @Override public TextureCandidateTable candidates() {
            return (stage, name) -> {
                List<TextureBindingCandidate> cell = entries.stream()
                    .filter(entry -> entry.expandedStage() == stage && entry.name() == name).toList();
                return cell.isEmpty() ? new TextureCandidateEntry.Absent(TextureOverlayAbsence.NOT_CONFIGURED)
                    : new TextureCandidateEntry.Candidates(cell);
            };
        }
        @Override public BaseAtlasContext baseAtlasContext() { return new BaseAtlasContext.NonAtlas(); }
        @Override public Optional<TextureHandleRef> baseTexture() { return Optional.empty(); }
        @Override public BaseAtlasContext atlasContext(TextureHandleRef base) {
            return atlases.getOrDefault(base, new BaseAtlasContext.NonAtlas());
        }
        @Override public boolean isCurrent() { return current && closes == 0; }
        @Override public void close() { closes++; }
    }

    private static TextureBindingCandidate custom(Fixture fixture, FixedSamplerName name,
            int ordinal, DeclaredGlslType.Sampler shape) {
        TextureHandle texture = fixture.estate.device.textures().create("custom-" + name + ordinal);
        var format = com.schmaloogium.engine.gl.ColorInternalFormat.RGBA8;
        fixture.estate.device.textures().allocate(texture, new TextureSpec.ColorTextureSpec(
            TextureAllocationTarget.TEXTURE_2D, format, FormatTable.row(format).allocationLayout(),
            new TextureExtent(8, 8, 1), 1));
        return new TextureBindingCandidate(new CandidateOrigin.Custom(
            new TextureBindingKey(TexturePropertyStage.valueOf(fixture.pass.pass().step().stage().name()),
                name.exactName(), OptionalInt.of(ordinal)), ordinal),
            fixture.pass.pass().step().stage(), name.exactName(), name,
            shape, TextureTarget.TEXTURE_2D, new TextureHandleRef.Owned(texture),
            new TextureSourceIdentity("source-" + name + ordinal),
            new TextureParameterSpec(TextureMinFilter.NEAREST, TextureMagFilter.NEAREST,
                TextureWrap.REPEAT), new TextureParameterFingerprint("nearest-repeat"), ordinal);
    }

    private static TextureBindingCandidate companion(Fixture fixture, FixedSamplerName name,
            int ordinal, CandidateOrigin origin) {
        TextureBindingCandidate backing = custom(fixture, name, ordinal, SAMPLER_2D);
        return new TextureBindingCandidate(origin, backing.expandedStage(), name.exactName(),
            name, backing.shape(), backing.target(), backing.handle(), backing.source(),
            backing.parameters(), backing.parameterizationFingerprint(), ordinal);
    }

    @Test
    void customColortex1ReplacesEstateAtFixedUnit() {
        Fixture fixture = fixture("colortex1");
        TextureBindingCandidate first = custom(fixture, FixedSamplerName.COLORTEX1, 1, SAMPLER_2D);
        TextureBindingCandidate winner = custom(fixture, FixedSamplerName.COLORTEX1, 2, SAMPLER_2D);
        TextureBindingCandidate incompatible = custom(fixture, FixedSamplerName.COLORTEX1, 3,
            new DeclaredGlslType.Sampler(SampledKind.SIGNED_INT, TextureDimension.D2,
                false, false, false));
        Overlay overlay = new Overlay(fixture, first, winner, incompatible);
        int before = fixture.estate.device.log().calls().size();
        TextureBindingResult.Bound bound = assertInstanceOf(TextureBindingResult.Bound.class,
            fixture.view.textureBindings(fixture.pass, overlay, overlay.id()));
        TextureBindingOutcome.BoundObject row = assertInstanceOf(TextureBindingOutcome.BoundObject.class,
            bound.snapshot().outcome(1));
        assertEquals(BindingOriginKind.CUSTOM, row.origin().kind());
        assertSame(winner.handle(), row.handle());
        List<GLCall> binds = fixture.estate.device.log().calls().subList(before,
            fixture.estate.device.log().calls().size()).stream()
            .filter(call -> call.op().equals("textures.bindToUnit")).toList();
        assertEquals(1, binds.size());
        assertEquals(1, binds.get(0).args().get(0));
        assertSame(((TextureHandleRef.Owned) winner.handle()).handle(), binds.get(0).args().get(1));
        bound.snapshot().close();
    }

    @Test
    void fullscreenProgramsStillReadColortexOnUnitsZeroToThree() {
        Fixture fixture = fixture("colortex0", "colortex1");
        Overlay overlay = new Overlay(fixture);
        TextureBindingResult.Bound bound = assertInstanceOf(TextureBindingResult.Bound.class,
            fixture.view.textureBindings(fixture.pass, overlay, overlay.id()));
        for (int unit = 0; unit < 2; unit++) {
            var row = assertInstanceOf(TextureBindingOutcome.BoundObject.class, bound.snapshot().outcome(unit));
            assertEquals(BindingOriginKind.ESTATE, row.origin().kind());
            assertSame(fixture.estate.core.colorPairs.get(unit).readSide(),
                ((TextureHandleRef.Borrowed) row.handle()).handle());
        }
        bound.snapshot().close();
    }

    @Test
    void fullyResolvedPassRecordsNoDiagnostics() {
        Fixture fixture = fixture("colortex0", "colortex1");
        Overlay overlay = new Overlay(fixture, custom(fixture, FixedSamplerName.COLORTEX1, 1, SAMPLER_2D));
        TextureBindingResult.Bound bound = assertInstanceOf(TextureBindingResult.Bound.class,
            fixture.view.textureBindings(fixture.pass, overlay, overlay.id()));
        assertEquals(BindingOriginKind.ESTATE, assertInstanceOf(TextureBindingOutcome.BoundObject.class,
            bound.snapshot().outcome(0)).origin().kind());
        assertEquals(BindingOriginKind.CUSTOM, assertInstanceOf(TextureBindingOutcome.BoundObject.class,
            bound.snapshot().outcome(1)).origin().kind());
        assertEquals(List.of(), bound.snapshot().diagnostics());
        bound.snapshot().close();
    }

    @Test
    void degradedPassReportsOnlyTheUnbackedUnit() {
        Fixture fixture = fixture("colortex0", "colortex1", "colortex2");
        Overlay overlay = new Overlay(fixture, custom(fixture, FixedSamplerName.COLORTEX1, 1, SAMPLER_2D));
        int before = fixture.estate.device.log().calls().size();
        TextureBindingResult.Degraded result = assertInstanceOf(TextureBindingResult.Degraded.class,
            fixture.view.textureBindings(fixture.pass, overlay, overlay.id()));
        assertEquals(List.of(new TextureBindingDiagnostic(TextureBindingDiagnosticCode.NOT_CONFIGURED,
            "colortex2", OptionalInt.of(2))), result.degradation().diagnostics());
        assertEquals(before, fixture.estate.device.log().calls().size());
        overlay.close();
    }

    @Test
    void stalePublicationRejectsWithoutGlAndKeepsCallerLease() {
        Fixture fixture = fixture("colortex1");
        Overlay overlay = new Overlay(fixture);
        int before = fixture.estate.device.log().calls().size();
        TextureBindingResult.Rejected result = assertInstanceOf(TextureBindingResult.Rejected.class,
            fixture.view.textureBindings(fixture.pass, overlay, new TextureOverlayPublicationId(
                overlay.id().generation(), new TextureOverlayFingerprint("retired-publication"))));
        assertEquals(TextureBindingRejection.OVERLAY_PUBLICATION_ID_MISMATCH, result.reason());
        assertEquals(before, fixture.estate.device.log().calls().size());
        assertEquals(0, overlay.closes);
        overlay.close();
        assertEquals(1, overlay.closes);
    }

    @Test
    void retiredOverlayWithMatchingIdentityRejectsWithoutGl() {
        Fixture fixture = fixture("colortex1");
        Overlay overlay = new Overlay(fixture);
        overlay.current = false;
        int before = fixture.estate.device.log().calls().size();
        TextureBindingResult.Rejected result = assertInstanceOf(TextureBindingResult.Rejected.class,
            fixture.view.textureBindings(fixture.pass, overlay, overlay.id()));
        assertEquals(TextureBindingRejection.CLOSED_OVERLAY_LEASE, result.reason());
        assertEquals(before, fixture.estate.device.log().calls().size());
        assertEquals(0, overlay.closes);
        overlay.close();
        assertEquals(1, overlay.closes);
    }

    @Test
    void incompatibleCandidateShapeDegradesWithoutGlAndKeepsCallerLease() {
        var integerShape = new DeclaredGlslType.Sampler(SampledKind.SIGNED_INT,
            TextureDimension.D2, false, false, false);
        Fixture fixture = fixture(integerShape, "colortex1");
        Overlay overlay = new Overlay(fixture, custom(fixture, FixedSamplerName.COLORTEX1, 1, SAMPLER_2D));
        int before = fixture.estate.device.log().calls().size();
        TextureBindingResult.Degraded result = assertInstanceOf(TextureBindingResult.Degraded.class,
            fixture.view.textureBindings(fixture.pass, overlay, overlay.id()));
        assertTrue(result.degradation().diagnostics().stream()
            .anyMatch(diagnostic -> diagnostic.code() == TextureBindingDiagnosticCode.INCOMPATIBLE_CANDIDATE));
        assertEquals(before, fixture.estate.device.log().calls().size());
        assertEquals(0, overlay.closes);
        overlay.close();
        assertEquals(1, overlay.closes);
    }

    @Test
    void distinctAliasSourcesConflictWithoutGl() {
        Fixture fixture = fixture("colortex1", "gdepth");
        Overlay overlay = new Overlay(fixture,
            custom(fixture, FixedSamplerName.COLORTEX1, 1, SAMPLER_2D),
            custom(fixture, FixedSamplerName.GDEPTH, 2, SAMPLER_2D));
        int before = fixture.estate.device.log().calls().size();
        TextureBindingResult.Degraded result = assertInstanceOf(TextureBindingResult.Degraded.class,
            fixture.view.textureBindings(fixture.pass, overlay, overlay.id()));
        assertTrue(result.degradation().diagnostics().stream()
            .anyMatch(diagnostic -> diagnostic.code() == TextureBindingDiagnosticCode.CONFLICTING_CANDIDATES));
        assertEquals(before, fixture.estate.device.log().calls().size());
        assertEquals(0, overlay.closes);
        overlay.close();
    }

    @Test
    void boundSnapshotClosesLeaseExactlyOnce() {
        Fixture fixture = fixture("colortex1");
        Overlay overlay = new Overlay(fixture, custom(fixture, FixedSamplerName.COLORTEX1, 1, SAMPLER_2D));
        TextureBindingResult.Bound bound = assertInstanceOf(TextureBindingResult.Bound.class,
            fixture.view.textureBindings(fixture.pass, overlay, overlay.id()));
        assertEquals(0, overlay.closes);
        assertTrue(bound.snapshot().isCurrent());
        int before = fixture.estate.device.log().calls().size();
        bound.snapshot().close();
        bound.snapshot().close();
        assertEquals(1, overlay.closes);
        assertFalse(bound.snapshot().isCurrent());
        assertEquals(before, fixture.estate.device.log().calls().size());
    }

    @Test
    void backendFailureKeepsCallerLease() {
        Fixture fixture = fixture("colortex1");
        Overlay overlay = new Overlay(fixture, custom(fixture, FixedSamplerName.COLORTEX1, 1, SAMPLER_2D));
        fixture.responses.glError("textures.bindToUnit", "custom", GLErrorKind.INVALID_OPERATION);
        assertInstanceOf(TextureBindingResult.BackendFailed.class,
            fixture.view.textureBindings(fixture.pass, overlay, overlay.id()));
        assertEquals(0, overlay.closes);
        overlay.close();
        assertEquals(1, overlay.closes);
    }

    @Test
    void worldCompanionsFollowCustomBaseAtlasAndUseOnlyMatchingDefaults() {
        Fixture fixture = fixture(new ProgramSlotId("gbuffers_terrain"), StageId.GBUFFERS,
            StageBand.GBUFFERS_OPAQUE, layout(StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE,
                SAMPLER_2D, "normals", "specular", "texture"));
        AtlasId atlas = new AtlasId("accepted-base");
        AtlasId unrelated = new AtlasId("unrelated-atlas");
        TextureBindingCandidate base = custom(fixture, FixedSamplerName.TEXTURE, 0, SAMPLER_2D);
        TextureBindingCandidate normals = companion(fixture, FixedSamplerName.NORMALS, 1,
            new CandidateOrigin.Companion(atlas, CompanionKind.NORMALS));
        TextureBindingCandidate otherNormals = companion(fixture, FixedSamplerName.NORMALS, 2,
            new CandidateOrigin.Companion(unrelated, CompanionKind.NORMALS));
        TextureBindingCandidate otherSpecular = companion(fixture, FixedSamplerName.SPECULAR, 3,
            new CandidateOrigin.Companion(unrelated, CompanionKind.SPECULAR));
        TextureBindingCandidate defaultSpecular = companion(fixture, FixedSamplerName.SPECULAR, 4,
            new CandidateOrigin.DefaultFill(CompanionKind.SPECULAR));
        Overlay overlay = new Overlay(fixture, base, normals, otherNormals, otherSpecular, defaultSpecular);
        overlay.atlases = Map.of(base.handle(), new BaseAtlasContext.Atlas(atlas));
        int before = fixture.estate.device.log().calls().size();
        TextureBindingResult.Bound bound = assertInstanceOf(TextureBindingResult.Bound.class,
            fixture.view.textureBindings(fixture.pass, overlay, overlay.id()));
        List<GLCall> binds = fixture.estate.device.log().calls().subList(before,
            fixture.estate.device.log().calls().size()).stream()
            .filter(call -> call.op().equals("textures.bindToUnit")).toList();
        assertEquals(List.of(0, 2, 3), binds.stream().map(call -> call.args().get(0)).toList());
        assertSame(((TextureHandleRef.Owned) base.handle()).handle(), binds.get(0).args().get(1));
        assertSame(((TextureHandleRef.Owned) normals.handle()).handle(), binds.get(1).args().get(1));
        assertSame(((TextureHandleRef.Owned) defaultSpecular.handle()).handle(), binds.get(2).args().get(1));
        bound.snapshot().close();
    }

    @Test
    void fixedFunctionGbuffersProgramsBindNothing() {
        Fixture fixture = fixture(new ProgramSlotId("gbuffers_terrain"), StageId.GBUFFERS,
            StageBand.GBUFFERS_OPAQUE, new ProgramSamplerLayout.FixedFunctionEmpty(
                new ProgramSamplerLayoutFingerprint("fixed"), FixedSamplerPolicies.appB3Fingerprint()));
        Overlay overlay = new Overlay(fixture);
        int before = fixture.estate.device.log().calls().size();
        TextureBindingResult.Bound bound = assertInstanceOf(TextureBindingResult.Bound.class,
            fixture.view.textureBindings(fixture.pass, overlay, overlay.id()));
        assertEquals(BindingPurpose.NONE, bound.snapshot().purpose());
        assertFalse(fixture.estate.device.log().calls().subList(before,
            fixture.estate.device.log().calls().size()).stream()
            .anyMatch(call -> call.op().equals("textures.bindToUnit")));
        bound.snapshot().close();
    }
}
