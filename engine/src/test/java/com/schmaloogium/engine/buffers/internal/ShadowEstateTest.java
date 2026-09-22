// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.buffers.ShadowAbortResult;
import com.schmaloogium.engine.buffers.ShadowBeginResult;
import com.schmaloogium.engine.buffers.ShadowCompletionResult;
import com.schmaloogium.engine.buffers.ShadowDepthCopyPoint;
import com.schmaloogium.engine.buffers.ShadowEstateAvailable;
import com.schmaloogium.engine.buffers.ShadowEstateNotRequested;
import com.schmaloogium.engine.buffers.ShadowEstateResult;
import com.schmaloogium.engine.buffers.ShadowEstateUnavailable;
import com.schmaloogium.engine.buffers.ShadowMipmapOutcome;
import com.schmaloogium.engine.buffers.ShadowMipmapPolicy;
import com.schmaloogium.engine.buffers.ShadowMipmapResult;
import com.schmaloogium.engine.buffers.ShadowNeutralReason;
import com.schmaloogium.engine.buffers.ShadowNeutralizationResult;
import com.schmaloogium.engine.buffers.ShadowOperationResult;
import com.schmaloogium.engine.buffers.ShadowPassSnapshot;
import com.schmaloogium.engine.buffers.ShadowProtocolRejection;
import com.schmaloogium.engine.buffers.ShadowResourceProjection;
import com.schmaloogium.engine.buffers.ShadowTextureResource;
import com.schmaloogium.engine.buffers.TextureBindingOutcome;
import com.schmaloogium.engine.buffers.TextureBindingRejection;
import com.schmaloogium.engine.buffers.TextureBindingResult;
import com.schmaloogium.engine.buffers.TextureHandleRef;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.gl.ColorClearValue;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.DepthTransferLayout;
import com.schmaloogium.engine.gl.FramebufferTarget;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureParameters;
import com.schmaloogium.engine.gl.TextureRegion;
import com.schmaloogium.engine.gl.record.GLCall;
import com.schmaloogium.engine.gl.record.RecordingGLDevice;
import com.schmaloogium.engine.gl.record.ScriptedResponses;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.PassPopulation;
import com.schmaloogium.engine.registry.PassResourceAccess;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageStep;

/**
 * The §4.10 shadow estate over a scripted recorder: planned-estate detection and operator
 * results, snapshot borrowing, the sixteen-row shadow bind units (4/5/13/14), typed
 * single-sided shadowcolor clears, the shadowtex1 translucent copy tier, mipmap generation
 * with fail-closed degradation, the bounded neutral cache policy keys, and atomic
 * degrade-to-neutral with unit re-backing and idempotent repetition.
 */
class ShadowEstateTest {

    private static final ColorInternalFormat[] FORMATS =
        {ColorInternalFormat.RGBA8, ColorInternalFormat.RGBA8};
    private static final ResourceClearPolicy[] POLICIES =
        {new ResourceClearPolicy.FogRgbAlphaOne(), new ResourceClearPolicy.FogRgbAlphaOne()};
    private static final boolean[] CLEARS = {true, true};

    /** One depth (hardware compare, mipmapped, nearest) plus one color (linear). */
    private static ShadowResourceProjection shadowProjection(int depths, int colors) {
        List<ShadowTextureResource> depth = new java.util.ArrayList<>();
        for (int index = 0; index < depths; index++) {
            depth.add(new ShadowTextureResource(index == 0, true, true));
        }
        List<ShadowTextureResource> color = new java.util.ArrayList<>();
        for (int index = 0; index < colors; index++) {
            color.add(new ShadowTextureResource(false, false, false));
        }
        return new ShadowResourceProjection(depths, colors, 1024,
            List.copyOf(depth), List.copyOf(color));
    }

    private static BuffersEstateFixture shadowEstate() {
        return BuffersEstateFixture.createWithShadow(FORMATS, POLICIES, CLEARS, 1,
            shadowProjection(1, 1), 16, new ScriptedResponses());
    }

    private static ShadowEstateImpl view(BuffersEstateFixture fixture) {
        return (ShadowEstateImpl) fixture.core.shadowView;
    }

    private static PassDescriptor shadowPass() {
        return new PassDescriptor(
            new StageStep(StageId.SHADOW, StageBand.SHADOW, new PassPopulation.Singleton()),
            new ProgramSlotId("shadow"), Optional.empty(), PassResourceAccess.empty(),
            Set.of());
    }

    private static ShadowPassSnapshot acquire(BuffersEstateFixture fixture) {
        ShadowBeginResult.Acquired acquired = assertInstanceOf(
            ShadowBeginResult.Acquired.class,
            view(fixture).beginPass(7, shadowPass(), shadowSelection(fixture)));
        return acquired.snapshot();
    }

    private static ClearRequest clearRequest() {
        return new ClearRequest(7, 0.25f, 0.5f, 0.75f, false);
    }

    private static ProgramBindingSelection shadowSelection(BuffersEstateFixture fixture) {
        DeclaredGlslType.Sampler sampler2d = new DeclaredGlslType.Sampler(SampledKind.FLOAT,
            TextureDimension.D2, false, false, false);
        ProgramSamplerLayout layout = TextureBinderDomainTest.layout(StageId.SHADOW,
            StageBand.SHADOW, sampler2d, "shadowcolor0");
        return TextureBinderDomainTest.selection(fixture, new ProgramSlotId("shadow"),
            StageId.SHADOW, StageBand.SHADOW, layout);
    }

    private static List<GLCall> calls(BuffersEstateFixture fixture, String op) {
        return fixture.device.log().calls().stream()
            .filter(call -> call.op().equals(op))
            .toList();
    }

    // ------------------------------------------------------------------ operator + snapshot

    @Test
    void operatorReportsNotRequestedWithoutPlannedEstateAndAvailableWithOne() {
        BuffersEstateFixture plain = BuffersEstateFixture.create(FORMATS, POLICIES, CLEARS, 1);
        ShadowEstateResult notRequested = new ShadowOperator().shadow(plain.core);
        assertEquals(new ShadowEstateNotRequested(1), notRequested);

        BuffersEstateFixture fixture = shadowEstate();
        ShadowEstateResult result = new ShadowOperator().shadow(fixture.core);
        ShadowEstateAvailable available = assertInstanceOf(ShadowEstateAvailable.class, result);
        assertSame(fixture.core.shadowView, available.view());
        assertEquals(1, available.view().estateGeneration());
    }

    @Test
    void beginPassAcquiresTheSoleSnapshotWithBorrowedSides() {
        BuffersEstateFixture fixture = shadowEstate();
        fixture.core.openFrameId = 7;
        ShadowEstateImpl view = view(fixture);

        ShadowPassSnapshot snapshot = acquire(fixture);
        assertEquals(1, snapshot.estateGeneration());
        assertEquals(7, snapshot.frameId());
        assertSame(fixture.core.shadowFbo, snapshot.framebuffer());
        assertSame(fixture.core.shadowDepths.get(0),
            snapshot.readableTextures().get(new LogicalBuffer(BufferDomain.SHADOWTEX,
                new BufferIndex(0))));
        assertEquals(fixture.core.shadowColorPairs.get(0).readSide(),
            snapshot.colorAttachments().get(0).physicalTexture());
        assertEquals(0, snapshot.colorAttachments().get(0).outputOrdinal());

        assertEquals(ShadowBeginResult.Rejected.class,
            view.beginPass(7, shadowPass(), shadowSelection(fixture)).getClass(),
            "a second beginPass while one is open returns PASS_ALREADY_OPEN before GL");
    }

    @Test
    void beginPassRejectsForeignFramesAndStaleEstatesBeforeGL() {
        BuffersEstateFixture fixture = shadowEstate();
        ShadowEstateImpl view = view(fixture);
        assertEquals(ShadowProtocolRejection.WRONG_FRAME_ID,
            assertInstanceOf(ShadowBeginResult.Rejected.class,
                view.beginPass(7, shadowPass(), shadowSelection(fixture))).reason(),
            "no open estate frame");

        fixture.core.openFrameId = 7;
        fixture.core.stale = true;
        assertEquals(ShadowProtocolRejection.STALE_GENERATION,
            assertInstanceOf(ShadowBeginResult.Rejected.class,
                view.beginPass(7, shadowPass(), shadowSelection(fixture))).reason());
        fixture.core.stale = false;

        long bindsBefore = fixture.exactOpCount("framebuffers.bind");
        ShadowPassSnapshot snapshot = acquire(fixture);
        assertEquals(ShadowProtocolRejection.WRONG_FRAME_ID,
            assertInstanceOf(ShadowOperationResult.Rejected.class,
                view.bind(new ShadowPassSnapshot(1, 0, 8, snapshot.pass(),
                    snapshot.selection(), snapshot.framebuffer(),
                    snapshot.colorAttachments(), snapshot.readableTextures(),
                    snapshot.flipAfterPass()))).reason(),
            "a forged same-generation snapshot is foreign");
        assertEquals(bindsBefore, fixture.exactOpCount("framebuffers.bind"),
            "protocol rejections append no GL");
    }

    // ------------------------------------------------------------------ bind + bindings

    @Test
    void bindOnlySelectsTheShadowFramebufferWithoutTextureMutation() {
        BuffersEstateFixture fixture = shadowEstate();
        fixture.core.openFrameId = 7;
        ShadowEstateImpl view = view(fixture);
        ShadowPassSnapshot snapshot = acquire(fixture);

        assertEquals(ShadowOperationResult.Applied.class, view.bind(snapshot).getClass());
        assertTrue(calls(fixture, "textures.prepareUnitBindings").isEmpty());
        assertTrue(calls(fixture, "textures.bindToUnit").isEmpty(),
            "framebuffer binding cannot bypass authenticated overlay binding");
        GLCall fboBind = calls(fixture, "framebuffers.bind").get(0);
        assertEquals(List.of(FramebufferTarget.DRAW, fixture.core.shadowFbo),
            fboBind.args());
    }

    @Test
    void shadowBindingsReturnsALeasedSnapshotAndExpiresItAtCompletion() {
        BuffersEstateFixture fixture = shadowEstate();
        fixture.core.openFrameId = 7;
        ShadowEstateImpl view = view(fixture);
        ShadowPassSnapshot snapshot = acquire(fixture);

        TextureBinderDomainTest.Overlay overlay =
            new TextureBinderDomainTest.Overlay(fixture, snapshot.selection());
        assertEquals(TextureBindingRejection.INVALID_INPUT,
            assertInstanceOf(TextureBindingResult.Rejected.class,
                view.shadowBindings(1, 7, snapshot, null, overlay.id())).reason());
        assertEquals(TextureBindingRejection.STALE_ESTATE_GENERATION,
            assertInstanceOf(TextureBindingResult.Rejected.class,
                view.shadowBindings(2, 8, snapshot, overlay, overlay.id())).reason());
        assertEquals(TextureBindingRejection.WRONG_FRAME_ID,
            assertInstanceOf(TextureBindingResult.Rejected.class,
                view.shadowBindings(1, 8, snapshot, overlay, overlay.id())).reason());
        ShadowPassSnapshot forged = new ShadowPassSnapshot(snapshot.estateGeneration(),
            snapshot.depthAttachmentEpoch(), snapshot.frameId(), snapshot.pass(),
            snapshot.selection(), snapshot.framebuffer(), snapshot.colorAttachments(),
            snapshot.readableTextures(), snapshot.flipAfterPass());
        assertEquals(TextureBindingRejection.INVALID_PASS_SNAPSHOT,
            assertInstanceOf(TextureBindingResult.Rejected.class,
                view.shadowBindings(1, 7, forged, overlay, overlay.id())).reason());
        assertTrue(calls(fixture, "textures.prepareUnitBindings").isEmpty());
        assertTrue(calls(fixture, "textures.bindToUnit").isEmpty());
        assertTrue(overlay.isCurrent());

        TextureHandle frozenColor = fixture.core.shadowColorPairs.get(0).readSide();
        fixture.core.shadowColorPairs.get(0).flipped = true;
        TextureBindingResult.Bound bound = assertInstanceOf(TextureBindingResult.Bound.class,
            view.shadowBindings(1, 7, snapshot, overlay, overlay.id()));
        try {
            TextureBindingOutcome.BoundObject color = assertInstanceOf(
                TextureBindingOutcome.BoundObject.class, bound.snapshot().outcome(13));
            assertSame(frozenColor,
                assertInstanceOf(TextureHandleRef.Borrowed.class, color.handle()).handle());
            assertEquals(List.of(13, frozenColor),
                calls(fixture, "textures.bindToUnit").get(0).args());
            assertTrue(bound.snapshot().isCurrent());
            assertEquals(ShadowCompletionResult.Completed.class,
                view.completePass(snapshot).getClass());
            assertFalse(bound.snapshot().isCurrent());
            assertTrue(overlay.isCurrent(),
                "completion invalidates use but does not release the owned lease");
            assertEquals(TextureBindingRejection.INVALID_PASS_SNAPSHOT,
                assertInstanceOf(TextureBindingResult.Rejected.class,
                    view.shadowBindings(1, 7, snapshot, overlay, overlay.id())).reason());
        } finally {
            bound.snapshot().close();
        }
        assertFalse(overlay.isCurrent());
    }

    // ------------------------------------------------------------------ clear + copy

    @Test
    void clearIsTypedSingleSidedThroughTheFacade() {
        BuffersEstateFixture fixture = shadowEstate();
        fixture.core.openFrameId = 7;
        ShadowEstateImpl view = view(fixture);
        ShadowPassSnapshot snapshot = acquire(fixture);
        assertEquals(ShadowOperationResult.Applied.class,
            view.clear(snapshot, clearRequest()).getClass());
        GLCall clear = calls(fixture, "framebuffers.clearColorAttachment").get(0);
        assertEquals(new ColorClearValue.Floating(0.0f, 0.0f, 0.0f, 0.0f), clear.args().get(2),
            "the undeclared pre-shadowcomp policy converts to transparent floating black");
        assertEquals(0, clear.args().get(1), "the pair's read side clears at ordinal 0");
        assertEquals(1, calls(fixture, "framebuffers.clearColorAttachment").size(),
            "single-sided: only the current read side of the one pair clears");
        GLCall attach = calls(fixture, "framebuffers.attachColor").stream()
            .filter(call -> call.args().get(0).equals(clear.args().get(0)))
            .findFirst().orElseThrow();
        assertEquals(List.of(clear.args().get(0), 0,
            fixture.core.shadowColorPairs.get(0).readSide()), attach.args(),
            "the clear FBO carries the pair's read side, not the write side");
    }

    @Test
    void copyDepthTiersOntoShadowtex1AndTreatsTheMissingTierAsANoOp() {
        BuffersEstateFixture single = shadowEstate();
        single.core.openFrameId = 7;
        ShadowPassSnapshot singleSnapshot = acquire(single);
        int copiesBefore = calls(single, "framebuffers.copyDepthToTexture").size();
        assertInstanceOf(ShadowOperationResult.Applied.class,
            view(single).copyDepth(singleSnapshot, ShadowDepthCopyPoint.SHADOW_PRE_TRANSLUCENT),
            "PHASE_8_DOC §4.8.3: a one-depth estate treats the split copy as a successful no-op");
        assertEquals(copiesBefore, calls(single, "framebuffers.copyDepthToTexture").size(),
            "no second planned depth: nothing is copied");

        BuffersEstateFixture fixture = BuffersEstateFixture.createWithShadow(
            FORMATS, POLICIES, CLEARS, 1, shadowProjection(2, 1), 16,
            new ScriptedResponses());
        fixture.core.openFrameId = 7;
        ShadowPassSnapshot snapshot = acquire(fixture);
        assertEquals(ShadowOperationResult.Applied.class,
            view(fixture).copyDepth(snapshot, ShadowDepthCopyPoint.SHADOW_PRE_TRANSLUCENT)
                .getClass());
        GLCall initialize = calls(fixture, "framebuffers.initializeDepthTextureFromFramebuffer")
            .get(0);
        assertEquals(fixture.core.shadowDepths.get(1), initialize.args().get(1));
        assertEquals(new TextureRegion(0, 0, 0, 16, 16, 1), initialize.args().get(2),
            "the copy region is the §4.11 shadow extent");
        assertEquals(ShadowOperationResult.Applied.class,
            view(fixture).copyDepth(snapshot, ShadowDepthCopyPoint.SHADOW_PRE_TRANSLUCENT)
                .getClass());
        assertEquals("framebuffers.copyDepthToTexture",
            calls(fixture, "framebuffers.copyDepthToTexture").get(0).op(),
            "the second copy is the steady verb, never a storage redefinition");
    }

    // ------------------------------------------------------------------ mipmaps

    @Test
    void mipmapsGenerateDegradeAndReportUnallocatedChains() {
        BuffersEstateFixture fixture = shadowEstate();
        fixture.core.openFrameId = 7;
        ShadowEstateImpl view = view(fixture);
        ShadowPassSnapshot snapshot = acquire(fixture);
        ShadowMipmapPolicy policy = new ShadowMipmapPolicy(List.of(
            new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(0)),
            new LogicalBuffer(BufferDomain.SHADOWCOLOR, new BufferIndex(0)),
            new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(1))));

        ShadowMipmapResult.Generated generated = assertInstanceOf(
            ShadowMipmapResult.Generated.class,
            view.generateShadowMipmaps(1, 7, snapshot, policy));
        assertInstanceOf(ShadowMipmapOutcome.Generated.class,
            outcomeFor(generated, BufferDomain.SHADOWTEX, 0));
        assertInstanceOf(ShadowMipmapOutcome.NotAllocated.class,
            outcomeFor(generated, BufferDomain.SHADOWCOLOR, 0),
            "the planned non-mipmapped shadowcolor has no chain");
        assertInstanceOf(ShadowMipmapOutcome.NotAllocated.class,
            outcomeFor(generated, BufferDomain.SHADOWTEX, 1),
            "shadowtex1 is not allocated");
        GLCall mipmap = calls(fixture, "textures.generateMipmap").get(0);
        assertEquals(fixture.core.shadowDepths.get(0), mipmap.args().get(0));
        TextureParameters applied = (TextureParameters) calls(fixture, "textures.setParameters")
            .get(calls(fixture, "textures.setParameters").size() - 1).args().get(1);
        assertEquals(TextureMinFilter.NEAREST_MIPMAP_NEAREST, applied.minFilter(),
            "the nearest depth class gains its mipmap min filter");
    }

    /** Finds one buffer's outcome: the policy reports in canonical domain order. */
    private static ShadowMipmapOutcome outcomeFor(ShadowMipmapResult.Generated generated,
            BufferDomain domain, int index) {
        for (ShadowMipmapOutcome outcome : generated.outcomes()) {
            LogicalBuffer logical = outcome instanceof ShadowMipmapOutcome.Generated generated1
                ? generated1.buffer()
                : outcome instanceof ShadowMipmapOutcome.Degraded degraded ? degraded.buffer()
                : ((ShadowMipmapOutcome.NotAllocated) outcome).buffer();
            if (logical.domain() == domain && logical.index().value() == index) {
                return outcome;
            }
        }
        throw new AssertionError("no outcome reported for " + domain + " " + index);
    }

    @Test
    void mipmapGenerationFailureDegradesWithoutStoppingLaterBuffers() {
        ScriptedResponses responses = new ScriptedResponses()
            .glError("textures.generateMipmap", "shadowtex0", GLErrorKind.INVALID_OPERATION);
        BuffersEstateFixture fixture = BuffersEstateFixture.createWithShadow(
            FORMATS, POLICIES, CLEARS, 1, shadowProjection(1, 1), 16, responses);
        fixture.core.openFrameId = 7;
        ShadowEstateImpl view = view(fixture);
        ShadowPassSnapshot snapshot = acquire(fixture);
        ShadowMipmapPolicy policy = new ShadowMipmapPolicy(List.of(
            new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(0)),
            new LogicalBuffer(BufferDomain.SHADOWCOLOR, new BufferIndex(0))));

        ShadowMipmapResult.Generated generated = assertInstanceOf(
            ShadowMipmapResult.Generated.class,
            view.generateShadowMipmaps(1, 7, snapshot, policy));
        ShadowMipmapOutcome.Degraded degraded = assertInstanceOf(
            ShadowMipmapOutcome.Degraded.class,
            outcomeFor(generated, BufferDomain.SHADOWTEX, 0));
        assertEquals(BufferFailureCode.UNEXPECTED_BACKEND, degraded.failure().code());
        assertTrue(degraded.diagnosticId().startsWith("schmaloogium.buffers.shadow.diagnostic."),
            "the degraded buffer records its stable diagnostic id");
        TextureParameters restored =
            (TextureParameters) calls(fixture, "textures.setParameters").get(
                calls(fixture, "textures.setParameters").size() - 1).args().get(1);
        assertEquals(TextureMinFilter.NEAREST, restored.minFilter(),
            "the configured non-mipmap min filter is restored before reporting Degraded");
        assertInstanceOf(ShadowMipmapOutcome.NotAllocated.class,
            outcomeFor(generated, BufferDomain.SHADOWCOLOR, 0),
            "a degraded buffer does not stop later requested buffers");
        assertFalse(fixture.core.shadowNeutralBacked(), "degradation keeps the real estate");
    }

    // ------------------------------------------------------------------ neutralization

    @Test
    void degradeToNeutralDisablesShadowAndRepeatsIdempotently() {
        BuffersEstateFixture fixture = shadowEstate();
        fixture.core.openFrameId = 7;
        ShadowEstateImpl view = view(fixture);
        ShadowPassSnapshot snapshot = acquire(fixture);

        ShadowNeutralizationResult.Neutralized neutralized = assertInstanceOf(
            ShadowNeutralizationResult.Neutralized.class,
            view.degradeToNeutral(1, ShadowNeutralReason.EXPLICIT_FEATURE_DISABLE));
        assertEquals(1, neutralized.generation());
        assertTrue(neutralized.openSnapshotAborted());
        assertTrue(neutralized.diagnosticId().startsWith("schmaloogium.buffers.shadow.neutral."));

        ShadowNeutralizationResult.AlreadyNeutral repeated = assertInstanceOf(
            ShadowNeutralizationResult.AlreadyNeutral.class,
            view.degradeToNeutral(1, ShadowNeutralReason.BIND_BACKEND_FAILURE));
        assertEquals(neutralized.diagnosticId(), repeated.diagnosticId(),
            "repetition answers the same stable diagnostic");
        long callsAfterFirst = fixture.device.log().calls().size();
        view.degradeToNeutral(1, ShadowNeutralReason.DEPTH_COPY_BACKEND_FAILURE);
        assertEquals(callsAfterFirst, fixture.device.log().calls().size(),
            "repetition appends no GL");

        ShadowEstateUnavailable unavailable = assertInstanceOf(ShadowEstateUnavailable.class,
            new ShadowOperator().shadow(fixture.core));
        assertEquals(BufferFailureCode.CAPABILITY_LIMIT, unavailable.reason().code(),
            "explicit feature disable reports the capability limit class");

        assertEquals(ShadowCompletionResult.Rejected.class,
            view.completePass(snapshot).getClass(),
            "the consumed token answers CLOSED, a never-issued one FOREIGN");
        assertEquals(ShadowAbortResult.Rejected.class,
            view.abortPass(new ShadowPassSnapshot(1, 0, 7, shadowPass(), snapshot.selection(),
                fixture.core.shadowFbo, List.of(), Map.of(), Set.of()), "d").getClass());
    }


    @Test
    void staleGenerationRejectsNeutralizationWithoutMutation() {
        BuffersEstateFixture fixture = shadowEstate();
        ShadowEstateImpl view = view(fixture);
        ShadowNeutralizationResult.Rejected rejected = assertInstanceOf(
            ShadowNeutralizationResult.Rejected.class,
            view.degradeToNeutral(2, ShadowNeutralReason.BIND_BACKEND_FAILURE));
        assertEquals(ShadowProtocolRejection.STALE_GENERATION, rejected.reason());
        assertFalse(fixture.core.shadowNeutralBacked());
    }

    // ------------------------------------------------------------------ neutral cache

    @Test
    void neutralCacheSharesOnlyObjectsWithIdenticalPolicies() {
        GLCapabilityProfile profile = new GLCapabilityProfile(3, 3, "3.30 cache profile",
            "vendor", "renderer", 8, 8, 16, 16, 4096, 256, 0, Set.of());
        RecordingGLDevice device = new RecordingGLDevice(profile, new ScriptedResponses());
        ShadowNeutralCache cache = new ShadowNeutralCache(device, handle -> {
        });
        TextureParameters compare = new TextureParameters(TextureMinFilter.NEAREST,
            TextureMagFilter.NEAREST, com.schmaloogium.engine.gl.TextureWrap.CLAMP_TO_EDGE,
            com.schmaloogium.engine.gl.TextureWrap.CLAMP_TO_EDGE,
            com.schmaloogium.engine.gl.TextureWrap.CLAMP_TO_EDGE,
            com.schmaloogium.engine.gl.TextureCompareMode.REF_TO_TEXTURE,
            com.schmaloogium.engine.gl.TextureCompareFunction.LEQUAL,
            new com.schmaloogium.engine.gl.TextureBorderColor(0.0f, 0.0f, 0.0f, 0.0f),
            0.0f, 0.0f, 0.0f, 1.0f, 0, 0,
            com.schmaloogium.engine.gl.TextureSwizzle.LEGACY_DEPTH_LUMINANCE);
        TextureParameters plain = new TextureParameters(TextureMinFilter.NEAREST,
            TextureMagFilter.NEAREST, com.schmaloogium.engine.gl.TextureWrap.CLAMP_TO_EDGE,
            com.schmaloogium.engine.gl.TextureWrap.CLAMP_TO_EDGE,
            com.schmaloogium.engine.gl.TextureWrap.CLAMP_TO_EDGE,
            com.schmaloogium.engine.gl.TextureCompareMode.NONE,
            com.schmaloogium.engine.gl.TextureCompareFunction.LEQUAL,
            new com.schmaloogium.engine.gl.TextureBorderColor(0.0f, 0.0f, 0.0f, 0.0f),
            0.0f, 0.0f, 0.0f, 1.0f, 0, 0,
            com.schmaloogium.engine.gl.TextureSwizzle.LEGACY_DEPTH_LUMINANCE);

        cache.requireDepthUnit(DepthAttachmentFormat.DEPTH_COMPONENT,
            DepthTransferLayout.DEPTH_COMPONENT_FLOAT, compare);
        cache.requireDepthUnit(DepthAttachmentFormat.DEPTH_COMPONENT,
            DepthTransferLayout.DEPTH_COMPONENT_FLOAT, compare);
        cache.requireDepthUnit(DepthAttachmentFormat.DEPTH_COMPONENT,
            DepthTransferLayout.DEPTH_COMPONENT_FLOAT, plain);
        cache.requireColorUnit(compare);
        cache.requireColorUnit(compare);

        assertSame(cache.depthByUnit(0), cache.depthByUnit(1),
            "identical shadowtex policies share one 1x1 fully-far object");
        assertNotSame(cache.depthByUnit(0), cache.depthByUnit(2),
            "comparison NONE and REF_TO_TEXTURE can never share");
        assertSame(cache.colorByUnit(0), cache.colorByUnit(1),
            "identical shadowcolor policies share one opaque-white object");
        assertEquals(3, cache.ownedObjects().size());
        assertEquals(3, device.log().calls().stream()
                .filter(call -> call.op().equals("textures.upload")).count(),
            "each distinct object initializes exactly once (depth=1, color=white)");
    }

    // ------------------------------------------------------------------ build failure

    @Test
    void realEstateFailureDisablesTheFeatureWithoutPartialFramebuffer() {
        ScriptedResponses responses = new ScriptedResponses()
            .framebufferStatus("shadow:sfb",
                com.schmaloogium.engine.gl.FramebufferStatus.INCOMPLETE_MISSING_ATTACHMENT);
        BuffersEstateFixture fixture = BuffersEstateFixture.createWithShadow(
            FORMATS, POLICIES, CLEARS, 1, shadowProjection(1, 1), 16, responses);
        assertNull(fixture.core.shadowView, "no partial sfb is ever handed out");
        assertNull(fixture.core.shadowFbo);
        ShadowEstateUnavailable unavailable = assertInstanceOf(ShadowEstateUnavailable.class,
            new ShadowOperator().shadow(fixture.core));
        assertEquals(BufferFailureCode.FRAMEBUFFER_INCOMPLETE, unavailable.reason().code());

    }
}
