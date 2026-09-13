// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.BindingOriginKind;
import com.schmaloogium.engine.buffers.BindingPurpose;
import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.buffers.FrameBeginResult;
import com.schmaloogium.engine.buffers.PassBufferSnapshot;
import com.schmaloogium.engine.buffers.PassSnapshotResult;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.buffers.ShadowResourceProjection;
import com.schmaloogium.engine.buffers.ShadowTextureResource;
import com.schmaloogium.engine.buffers.TextureBindingOutcome;
import com.schmaloogium.engine.buffers.TextureBindingResult;
import com.schmaloogium.engine.buffers.TextureHandleRef;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.record.GLCall;
import com.schmaloogium.engine.gl.record.ScriptedResponses;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.BufferRef;
import com.schmaloogium.engine.registry.DrawRouting;
import com.schmaloogium.engine.registry.DrawRoutingSlot;
import com.schmaloogium.engine.registry.GeometryInputRequirement;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.PassPopulation;
import com.schmaloogium.engine.registry.PassResourceAccess;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramSamplerDeclaration;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramStateBundle;
import com.schmaloogium.engine.registry.ProgramUniformLayout;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageStep;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * PHASE_5_DOC §4.12.2 family rule for the sixteen-row bind: in the gbuffers family the
 * platform keeps units 0/1 ({@code texture}/{@code lightmap}: retained, never bound),
 * units 2/3 carry the companion defaults, and every other demanded estate unit binds;
 * the fullscreen family reads colortex0-3 on units 0-3 as before. This is the Task D fix
 * for classic packs rendering black (gbuffers programs received no estate units).
 */
class TextureBinderDomainTest {

    private static final ProgramSlotId TERRAIN = new ProgramSlotId("gbuffers_terrain");
    private static final ProgramSlotId COMPOSITE = new ProgramSlotId("composite");
    private static final BufferRef COLORTEX0 = new BufferRef(BufferDomain.COLORTEX, 0);
    private static final DeclaredGlslType.Sampler SAMPLER_2D = new DeclaredGlslType.Sampler(
        SampledKind.FLOAT, TextureDimension.D2, false, false, false);

    private static PlanningArtifacts.PlannedRoute route(ProgramSlotId slot) {
        return new PlanningArtifacts.PlannedRoute(slot,
            List.of(new DrawRoutingSlot.Attachment(COLORTEX0)),
            List.of(BuffersEstateFixture.colorRow(0)));
    }

    private static BuffersEstateFixture estate() {
        ShadowResourceProjection shadow = new ShadowResourceProjection(1, 1, 1024,
            List.of(new ShadowTextureResource(true, true, true)),
            List.of(new ShadowTextureResource(false, false, false)));
        BuffersEstateFixture fixture = BuffersEstateFixture.createWithShadowAndRoutes(
            new ColorInternalFormat[] {ColorInternalFormat.RGBA8, ColorInternalFormat.RGBA8},
            new ResourceClearPolicy[] {new ResourceClearPolicy.FogRgbAlphaOne(),
                new ResourceClearPolicy.FogRgbAlphaOne()},
            new boolean[] {true, true}, 1, shadow, 16,
            Map.of(TERRAIN, route(TERRAIN), COMPOSITE, route(COMPOSITE)),
            new ScriptedResponses());
        seedPassFbo(fixture, TERRAIN);
        seedPassFbo(fixture, COMPOSITE);
        return fixture;
    }

    private static void seedPassFbo(BuffersEstateFixture fixture, ProgramSlotId slot) {
        String key = CandidateBuilder.passKey(route(slot));
        FramebufferHandle fbo = fixture.device.framebuffers().create(key);
        EstateCore.ColorPair pair = fixture.core.colorPairs.get(0);
        fixture.device.framebuffers().attachColor(fbo, 0, pair.sideA);
        fixture.core.passFbos.put(key, fbo);
        fixture.core.attachedColor.put(fbo, new java.util.LinkedHashMap<>(Map.of(0, pair.sideA)));
    }

    private static EstateViewImpl view(BuffersEstateFixture fixture) {
        return new EstateViewImpl(fixture.core, new ClearExecutor(), new TextureBinder(),
            new ShadowOperator());
    }

    private static PassDescriptor descriptor(ProgramSlotId slot, StageId stage, StageBand band) {
        return new PassDescriptor(new StageStep(stage, band, new PassPopulation.Singleton()),
            slot, Optional.empty(),
            new PassResourceAccess(Set.of(), Set.of(COLORTEX0), Map.of(), Set.of()), Set.of());
    }

    private static ProgramSamplerLayout.Shader layout(StageId stage, StageBand band,
            String... names) {
        List<ProgramSamplerDeclaration> declarations = new ArrayList<>();
        for (int index = 0; index < names.length; index++) {
            declarations.add(new ProgramSamplerDeclaration(names[index], SAMPLER_2D, index,
                List.of()));
        }
        return new ProgramSamplerLayout.Shader(new ProgramSamplerLayoutFingerprint("layout-fp"),
            FixedSamplerPolicies.appB3Fingerprint(), stage, Set.of(band), declarations,
            new SamplerLayoutValidation.Valid());
    }

    /** A usable selection: mint-blocked fields stay null except the effective descriptor. */
    private static ProgramBindingSelection selection(ProgramSlotId slot,
            ProgramSamplerLayout layout) {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Object unsafe = theUnsafe.get(null);
            Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
            ProgramBindingSelection selection = (ProgramBindingSelection) allocateInstance
                .invoke(unsafe, ProgramBindingSelection.class);
            Field descriptorField = ProgramBindingSelection.class
                .getDeclaredField("effectiveDescriptor");
            descriptorField.setAccessible(true);
            long offset = (Long) unsafeClass.getMethod("objectFieldOffset", Field.class)
                .invoke(unsafe, descriptorField);
            ResolvedProgramDescriptor descriptor = new ResolvedProgramDescriptor(slot, slot,
                new ProgramStateBundle(new DrawRouting.AllUsedBuffers(BufferDomain.COLORTEX),
                    Set.of(), 1, Set.of(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Map.of(), Optional.empty(),
                    GeometryInputRequirement.NONE),
                ProgramUniformLayout.empty(), layout, List.of(), List.of());
            unsafeClass.getMethod("putObject", Object.class, long.class, Object.class)
                .invoke(unsafe, selection, offset, descriptor);
            return selection;
        } catch (ReflectiveOperationException failure) {
            throw new AssertionError("cannot assemble a selection", failure);
        }
    }

    private static PassBufferSnapshot acquired(PassSnapshotResult result) {
        return assertInstanceOf(PassSnapshotResult.Acquired.class, result).snapshot();
    }

    private static List<GLCall> calls(BuffersEstateFixture fixture, String op) {
        return fixture.device.log().calls().stream()
            .filter(call -> call.op().equals(op)).toList();
    }

    private static List<Integer> boundUnits(BuffersEstateFixture fixture) {
        return calls(fixture, "textures.bindToUnit").stream()
            .map(call -> (Integer) call.args().get(0)).toList();
    }

    @Test
    void gbuffersProgramsKeepThePlatformUnitsAndBindTheirEstateUnits() {
        BuffersEstateFixture fixture = estate();
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(1));
        PassBufferSnapshot snapshot = acquired(view.snapshot(
            descriptor(TERRAIN, StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE),
            selection(TERRAIN, layout(StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE,
                "texture", "lightmap", "normals", "specular", "shadowtex0", "depthtex0",
                "shadowcolor0", "noisetex"))));
        int before = calls(fixture, "textures.bindToUnit").size();

        TextureBindingResult.Bound bound = assertInstanceOf(TextureBindingResult.Bound.class,
            view.textureBindings(snapshot, null, null));

        assertEquals(BindingPurpose.SHADER, bound.snapshot().purpose());
        TextureBindingOutcome.ForeignRetained unit0 = assertInstanceOf(
            TextureBindingOutcome.ForeignRetained.class, bound.snapshot().outcome(0));
        assertEquals("texture", unit0.names().get(0).exactName());
        assertInstanceOf(TextureBindingOutcome.ForeignRetained.class, bound.snapshot().outcome(1));
        TextureBindingOutcome.BoundObject unit2 = assertInstanceOf(
            TextureBindingOutcome.BoundObject.class, bound.snapshot().outcome(2));
        assertSame(fixture.core.companionNormalsNeutral,
            ((TextureHandleRef.Borrowed) unit2.handle()).handle());
        assertEquals(BindingOriginKind.NEUTRAL, unit2.origin().kind());
        TextureBindingOutcome.BoundObject unit3 = assertInstanceOf(
            TextureBindingOutcome.BoundObject.class, bound.snapshot().outcome(3));
        assertSame(fixture.core.companionSpecularNeutral,
            ((TextureHandleRef.Borrowed) unit3.handle()).handle());
        TextureBindingOutcome.BoundObject unit4 = assertInstanceOf(
            TextureBindingOutcome.BoundObject.class, bound.snapshot().outcome(4));
        assertSame(fixture.core.shadowDepths.get(0),
            ((TextureHandleRef.Borrowed) unit4.handle()).handle());
        TextureBindingOutcome.BoundObject unit6 = assertInstanceOf(
            TextureBindingOutcome.BoundObject.class, bound.snapshot().outcome(6));
        assertSame(fixture.core.cachedDepth.texture(),
            ((TextureHandleRef.Borrowed) unit6.handle()).handle(),
            "depthtex0 is the borrowed vanilla depth");
        assertInstanceOf(TextureBindingOutcome.BoundObject.class, bound.snapshot().outcome(13));
        TextureBindingOutcome.BoundObject unit15 = assertInstanceOf(
            TextureBindingOutcome.BoundObject.class, bound.snapshot().outcome(15));
        assertSame(fixture.core.noiseTexture,
            ((TextureHandleRef.Borrowed) unit15.handle()).handle(),
            "noisetex is the estate's generated noise until Phase 13 publishes");
        assertEquals(BindingOriginKind.NOISE, unit15.origin().kind());

        GLCall prepare = calls(fixture, "textures.prepareUnitBindings").get(0);
        assertEquals((1 << 2) | (1 << 3) | (1 << 4) | (1 << 6) | (1 << 13) | (1 << 15),
            prepare.args().get(0), "units 0/1 never enter the mask");
        assertEquals(List.of(2, 3, 4, 6, 13, 15),
            boundUnits(fixture).subList(before, before + 6),
            "vanilla's atlas and lightmap objects stay bound on 0/1");
    }

    @Test
    void fullscreenProgramsStillReadColortexOnUnitsZeroToThree() {
        BuffersEstateFixture fixture = estate();
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(1));
        PassBufferSnapshot snapshot = acquired(view.snapshot(
            descriptor(COMPOSITE, StageId.COMPOSITE, StageBand.FRAME_END),
            selection(COMPOSITE, layout(StageId.COMPOSITE, StageBand.FRAME_END,
                "colortex0", "colortex1"))));
        int before = calls(fixture, "textures.bindToUnit").size();

        TextureBindingResult.Bound bound = assertInstanceOf(TextureBindingResult.Bound.class,
            view.textureBindings(snapshot, null, null));

        TextureBindingOutcome.BoundObject unit0 = assertInstanceOf(
            TextureBindingOutcome.BoundObject.class, bound.snapshot().outcome(0));
        assertSame(fixture.core.colorPairs.get(0).readSide(),
            ((TextureHandleRef.Borrowed) unit0.handle()).handle());
        assertEquals(BindingOriginKind.ESTATE, unit0.origin().kind());
        TextureBindingOutcome.BoundObject unit1 = assertInstanceOf(
            TextureBindingOutcome.BoundObject.class, bound.snapshot().outcome(1));
        assertSame(fixture.core.colorPairs.get(1).readSide(),
            ((TextureHandleRef.Borrowed) unit1.handle()).handle());
        assertEquals(List.of(0, 1), boundUnits(fixture).subList(before, before + 2));
    }

    @Test
    void fixedFunctionGbuffersProgramsBindNothing() {
        BuffersEstateFixture fixture = estate();
        EstateViewImpl view = view(fixture);
        assertInstanceOf(FrameBeginResult.Begun.class, view.beginFrame(1));
        ProgramSamplerLayout.FixedFunctionEmpty fixedFunction =
            new ProgramSamplerLayout.FixedFunctionEmpty(
                new ProgramSamplerLayoutFingerprint("ff"), FixedSamplerPolicies.appB3Fingerprint());
        PassBufferSnapshot snapshot = acquired(view.snapshot(
            descriptor(TERRAIN, StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE),
            selection(TERRAIN, fixedFunction)));
        int before = calls(fixture, "textures.bindToUnit").size();

        TextureBindingResult.Bound bound = assertInstanceOf(TextureBindingResult.Bound.class,
            view.textureBindings(snapshot, null, null));

        assertEquals(BindingPurpose.FIXED_FUNCTION_PASSTHROUGH, bound.snapshot().purpose());
        assertInstanceOf(TextureBindingOutcome.Unused.class, bound.snapshot().outcome(0),
            "no colortex0 passthrough row in the gbuffers family: vanilla samples its own atlas");
        assertTrue(boundUnits(fixture).size() == before, "zero binds");
    }
}
