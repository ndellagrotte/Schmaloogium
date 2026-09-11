// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.registry.BarrierConstructionResult;
import com.schmaloogium.engine.registry.ProgramUniformLayout;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.StageRegistries;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.ClassicProgramCatalog;
import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierPublicationCandidate;
import com.schmaloogium.engine.registry.BarrierResult;
import com.schmaloogium.engine.registry.BarrierParticipantResult;
import com.schmaloogium.engine.registry.CompiledRegistryCandidate;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.ProgramBindingParticipant;
import com.schmaloogium.engine.registry.ProgramBuildFailure;
import com.schmaloogium.engine.registry.ProgramOwnBuildDisposition;
import com.schmaloogium.engine.registry.ProgramResolutionProjection;
import com.schmaloogium.engine.registry.ProgramResolutionStatus;
import com.schmaloogium.engine.registry.ProgramSelectionResult;
import com.schmaloogium.engine.registry.ProgramSlotDescriptor;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramStateBundle;
import com.schmaloogium.engine.registry.PublishedRegistry;
import com.schmaloogium.engine.registry.PublicationResult;
import com.schmaloogium.engine.registry.RegistryBuildFailure;
import com.schmaloogium.engine.registry.RegistryFailureKind;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.RegistryPublication;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.registry.StageStep;
import com.schmaloogium.engine.registry.support.ScriptedGLDevice;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Headless barrier/publication semantics over the scripted device (PHASE_4_DOC §4.10/§4.11):
 * publish accounting, select/activate/release, Skipped for absent indexed passes, fixed
 * terminals, stale views after replacement, and participant ordering.
 */
class RegistryBarrierPublicationTest {

    private static final FixedSamplerPolicyFingerprint POLICY =
        new FixedSamplerPolicyFingerprint("policy/v1");

    private static ProgramStateBundle defaults() {
        return new ProgramStateBundle(
            new com.schmaloogium.engine.registry.DrawRouting.AllUsedBuffers(
                com.schmaloogium.engine.registry.BufferDomain.COLORTEX),
            java.util.Set.of(), 1, java.util.Set.of(),
            Optional.empty(), Optional.empty(), Optional.empty(),
            Map.of(), Optional.empty(),
            com.schmaloogium.engine.registry.GeometryInputRequirement.NONE);
    }

    private record Fixture(
            ScriptedGLDevice device,
            CompiledProgramRegistryImpl registry,
            CompiledRegistryCandidate candidate,
            List<ProgramResolutionProjection> projections) {
    }

    private static Fixture fixture(ScriptedGLDevice device) {
        StageRegistry stages = StageRegistries.modernFullShape(15, 15);
        List<ProgramSlotDescriptor> catalog = ClassicProgramCatalog.rows();
        com.schmaloogium.engine.gl.ProgramHandle providerHandle =
            device.shaders().createProgram();
        CompiledProgramBinding provider = new CompiledProgramBinding.ShaderProgram(
            slot(catalog, "gbuffers_basic"), providerHandle, defaults(),
            ProgramUniformLayout.empty(),
            new com.schmaloogium.engine.registry.ProgramSamplerLayout.FixedFunctionEmpty(
                new com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint("x"),
                POLICY),
            List.of());
        Map<ProgramSlotId, ResolvedCompiledProgramBinding> resolutions = Map.of(
            slot(catalog, "gbuffers_basic"),
                new ResolvedCompiledProgramBinding(slot(catalog, "gbuffers_basic"),
                    provider, List.of(slot(catalog, "gbuffers_basic"))),
            slot(catalog, "gbuffers_textured"),
                new ResolvedCompiledProgramBinding(slot(catalog, "gbuffers_textured"),
                    provider, List.of(
                        slot(catalog, "gbuffers_textured"), slot(catalog, "gbuffers_basic"))),
            ClassicProgramCatalog.SHADOW,
                new ResolvedCompiledProgramBinding(ClassicProgramCatalog.SHADOW,
                    new CompiledProgramBinding.FixedFunction(
                        ClassicProgramCatalog.SHADOW, defaults()),
                    List.of(ClassicProgramCatalog.SHADOW)));
        List<ProgramResolutionProjection> projections = new ArrayList<>();
        for (ProgramSlotDescriptor descriptor : catalog) {
            projections.add(new ProgramResolutionProjection(
                descriptor.id(), ProgramResolutionStatus.ABSENT,
                Optional.empty(), false, ProgramOwnBuildDisposition.NO_SOURCE, ""));
        }
        replace(projections, slot(catalog, "gbuffers_textured"),
            new ProgramResolutionProjection(slot(catalog, "gbuffers_textured"),
                ProgramResolutionStatus.CHAIN, Optional.of(slot(catalog, "gbuffers_basic")),
                true, ProgramOwnBuildDisposition.SUCCEEDED, ""));
        replace(projections, slot(catalog, "gbuffers_basic"),
            new ProgramResolutionProjection(slot(catalog, "gbuffers_basic"),
                ProgramResolutionStatus.SOURCED, Optional.empty(), true,
                ProgramOwnBuildDisposition.SUCCEEDED, ""));
        replace(projections, slot(catalog, "deferred5"),
            new ProgramResolutionProjection(slot(catalog, "deferred5"),
                ProgramResolutionStatus.ABSENT, Optional.empty(), false,
                ProgramOwnBuildDisposition.NO_SOURCE, ""));
        CompiledProgramRegistryImpl registry = new CompiledProgramRegistryImpl(
            stages, catalog, resolutions, projections,
            new RegistryFingerprint("registry-fp"), POLICY, device,
            List.of(providerHandle), new RegistryContexts(), ClassicProgramCatalog.SHADOW);
        return new Fixture(device, registry,
            new CompiledRegistryCandidate(registry), projections);
    }

    private static ProgramSlotId slot(List<ProgramSlotDescriptor> catalog, String name) {
        return catalog.stream().filter(d -> d.id().packName().equals(name))
            .findFirst().orElseThrow().id();
    }

    private static void replace(
            List<ProgramResolutionProjection> projections,
            ProgramSlotId slot,
            ProgramResolutionProjection row) {
        projections.removeIf(existing -> existing.slot().equals(slot));
        projections.add(row);
    }

    private static ProgramBindingParticipant continuing() {
        return (binding, context, uniforms) -> new BarrierParticipantResult.Continue();
    }

    private static BarrierPublicationCandidate compose(
            CompiledRegistryCandidate candidate,
            List<BarrierParticipantResult> seen) {
        BarrierConstructionResult result = ProductionAssembler.create().compose(
            candidate,
            (binding, context, uniforms) -> {
                seen.add(new BarrierParticipantResult.Continue());
                return new BarrierParticipantResult.Continue();
            },
            (binding, context, uniforms) -> {
                seen.add(new BarrierParticipantResult.Continue());
                return new BarrierParticipantResult.Continue();
            },
            (binding, context, uniforms) -> {
                seen.add(new BarrierParticipantResult.Continue());
                return new BarrierParticipantResult.Continue();
            });
        return assertInstanceOf(BarrierConstructionResult.Ready.class, result).candidate();
    }

    private static PublishedRegistry publish(
            com.schmaloogium.engine.registry.ProgramRegistryPublisher publisher,
            CompiledRegistryCandidate candidate,
            BarrierPublicationCandidate barrier) {
        RegistryContexts contexts = candidate.registry().contexts();
        BarrierContext release = contexts.beginFrame().release();
        PublicationResult result =
            publisher.publish(new RegistryPublication.Ready(candidate, barrier), release);
        return assertInstanceOf(PublicationResult.Accepted.class, result).published();
    }

    @Test
    void acceptedPublicationCountsExactlyOneGeneration() {
        ScriptedGLDevice device = new ScriptedGLDevice();
        Fixture fixture = fixture(device);
        var publisher = com.schmaloogium.engine.registry.ProgramRegistries.publisher();
        assertEquals(0L, publisher.current().generation());
        PublishedRegistry published = publish(
            publisher, fixture.candidate(), compose(fixture.candidate(), new ArrayList<>()));
        assertEquals(1L, published.generation());
        assertTrue(published.registry().isPresent());
        assertTrue(published.barrier().isPresent());
        // Rejection does not mutate the generation.
        PublicationResult rejected = publisher.publish(
            new RegistryPublication.Ready(null, null), mintedRelease(publisher));
        assertInstanceOf(PublicationResult.Rejected.class, rejected);
        assertEquals(1L, publisher.current().generation());
    }

    @Test
    void readyPublicationAcceptsReleaseContextFromTheOldPublicationSource() {
        // §4.10: the release context is "issued by the old publication's
        // BarrierContextSource" — publisher.current().contexts() — the only mint a caller
        // outside this package (the composition root) can reach.
        ScriptedGLDevice device = new ScriptedGLDevice();
        Fixture fixture = fixture(device);
        var publisher = com.schmaloogium.engine.registry.ProgramRegistries.publisher();
        BarrierContext release = mintedRelease(publisher);
        PublicationResult result = publisher.publish(new RegistryPublication.Ready(
            fixture.candidate(), compose(fixture.candidate(), new ArrayList<>())), release);
        PublishedRegistry published =
            assertInstanceOf(PublicationResult.Accepted.class, result).published();
        assertEquals(1L, published.generation());
        // A second replacement mints from the now-current (migrated) source.
        Fixture second = fixture(device);
        PublicationResult replaced = publisher.publish(new RegistryPublication.Ready(
            second.candidate(), compose(second.candidate(), new ArrayList<>())),
            mintedRelease(publisher));
        assertEquals(2L, assertInstanceOf(PublicationResult.Accepted.class, replaced)
            .published().generation());
        // A stale context (older epoch) is still rejected by epoch, not source.
        BarrierContext stale = mintedRelease(publisher);
        publisher.current().contexts().beginFrame();
        PublicationResult rejected = publisher.publish(
            new RegistryPublication.ShadersOff(new RegistryBuildFailure(
                RegistryFailureKind.UNEXPECTED_BACKEND, java.util.List.of(), "t", "t")), stale);
        assertEquals(com.schmaloogium.engine.registry.PublicationFailureKind.CONTEXT_EPOCH,
            assertInstanceOf(PublicationResult.Rejected.class, rejected).cause().kind());
    }

    private static BarrierContext mintedRelease(
            com.schmaloogium.engine.registry.ProgramRegistryPublisher publisher) {
        return ((PublishedRegistry) publisher.current()).contexts().beginFrame().release();
    }

    @Test
    void selectActivateRunsPositionsAndSharesProviderCacheKey() {
        ScriptedGLDevice device = new ScriptedGLDevice();
        Fixture fixture = fixture(device);
        List<BarrierParticipantResult> seen = new ArrayList<>();
        BarrierPublicationCandidate barrier = compose(fixture.candidate(), seen);
        var publisher = com.schmaloogium.engine.registry.ProgramRegistries.publisher();
        PublishedRegistry published = publish(publisher, fixture.candidate(), barrier);
        var barrierView = published.barrier().orElseThrow();

        StageStep opaque = published.registry().orElseThrow().stages().schedule().stream()
            .filter(step -> step.stage() == StageId.GBUFFERS
                && step.band() == StageBand.GBUFFERS_OPAQUE)
            .findFirst().orElseThrow();
        BarrierContext activation = published.contexts().beginFrame().activation(opaque, false);

        ProgramSelectionResult selection = barrierView.select(
            slot(ClassicProgramCatalog.rows(), "gbuffers_textured"), activation);
        ProgramBindingSelection chosen =
            assertInstanceOf(ProgramSelectionResult.Selected.class, selection).selection();
        assertEquals("gbuffers_basic", chosen.effectiveDescriptor().effective().packName());
        assertEquals("gbuffers_textured", chosen.requested().packName());

        BarrierResult activated = barrierView.activate(
            new com.schmaloogium.engine.registry.UseProgramRequest(chosen, activation));
        BarrierResult.Activated done = assertInstanceOf(BarrierResult.Activated.class, activated);
        assertTrue(done.binding().fallbackPath()
            .contains(slot(ClassicProgramCatalog.rows(), "gbuffers_basic")));
        assertTrue(device.calls.stream().anyMatch(call -> call.startsWith("use:")));
        assertTrue(device.calls.stream().anyMatch(call -> call.startsWith("lockAlphaBlend")));
        assertTrue(device.calls.stream().anyMatch(call -> call.equals("effectiveBlend")));
        assertEquals(3, seen.size());

        // Fixed terminal: shadow select/activate runs on the shadow step's own
        // activation context; the terminal resolves with an empty sampler layout.
        StageStep shadowStep = published.registry().orElseThrow().stages().schedule()
            .stream().filter(step -> step.stage() == StageId.SHADOW).findFirst()
            .orElseThrow();
        BarrierContext shadowActivation =
            published.contexts().beginFrame().activation(shadowStep, true);
        ProgramSelectionResult fixedSelection =
            barrierView.select(ClassicProgramCatalog.SHADOW, shadowActivation);
        ProgramBindingSelection fixed = assertInstanceOf(
            ProgramSelectionResult.Selected.class, fixedSelection).selection();
        assertInstanceOf(
            com.schmaloogium.engine.registry.ProgramSamplerLayout.FixedFunctionEmpty.class,
            fixed.effectiveDescriptor().samplerLayout());
        BarrierResult fixedActivated = barrierView.activate(
            new com.schmaloogium.engine.registry.UseProgramRequest(fixed, shadowActivation));
        assertInstanceOf(BarrierResult.FixedFunction.class, fixedActivated);
        assertTrue(device.usedFixedFunctionLast());

        // Release restores fixed function and closes the lease.
        BarrierContext release = published.contexts().beginFrame().release();
        BarrierResult released = barrierView.releaseToFixedFunction(release);
        assertInstanceOf(BarrierResult.FixedFunction.class, released);
        assertTrue(device.calls.stream().anyMatch(call -> call.equals("closeOverride")));
    }

    @Test
    void absentIndexedPassSelectsSkipped() {
        ScriptedGLDevice device = new ScriptedGLDevice();
        Fixture fixture = fixture(device);
        var publisher = com.schmaloogium.engine.registry.ProgramRegistries.publisher();
        PublishedRegistry published = publish(
            publisher, fixture.candidate(), compose(fixture.candidate(), new ArrayList<>()));
        var barrierView = published.barrier().orElseThrow();
        StageStep deferred = published.registry().orElseThrow().stages().schedule().stream()
            .filter(step -> step.stage() == StageId.DEFERRED).findFirst().orElseThrow();
        BarrierContext activation = published.contexts().beginFrame().activation(deferred, false);
        ProgramSelectionResult result = barrierView.select(
            slot(ClassicProgramCatalog.rows(), "deferred5"), activation);
        assertEquals("deferred5",
            assertInstanceOf(ProgramSelectionResult.Skipped.class, result).requested()
                .packName());
    }

    @Test
    void replacedBarrierReturnsStalePublicationAndGenerationAdvances() {
        ScriptedGLDevice device = new ScriptedGLDevice();
        Fixture first = fixture(device);
        var publisher = com.schmaloogium.engine.registry.ProgramRegistries.publisher();
        PublishedRegistry published = publish(
            publisher, first.candidate(), compose(first.candidate(), new ArrayList<>()));
        var oldView = published.barrier().orElseThrow();

        // Replacement with a second registry product.
        ScriptedGLDevice secondDevice = new ScriptedGLDevice();
        Fixture second = fixture(secondDevice);
        PublishedRegistry replacement = publish(
            publisher, second.candidate(), compose(second.candidate(), new ArrayList<>()));
        assertEquals(2L, replacement.generation());
        assertNotSame(published, replacement);

        // The retained old view cannot act on the replacement generation.
        StageStep opaque = second.registry().stages().schedule().stream()
            .filter(step -> step.stage() == StageId.GBUFFERS
                && step.band() == StageBand.GBUFFERS_OPAQUE)
            .findFirst().orElseThrow();
        BarrierContext activation = replacement.contexts().beginFrame().activation(opaque, false);
        ProgramSelectionResult staleSelection =
            oldView.select(slot(ClassicProgramCatalog.rows(), "gbuffers_textured"), activation);
        assertInstanceOf(ProgramSelectionResult.StalePublication.class, staleSelection);
        BarrierResult staleActivate = oldView.activate(
            new com.schmaloogium.engine.registry.UseProgramRequest(
                assertInstanceOf(ProgramSelectionResult.Selected.class,
                    barrierStillWorks(replacement, activation)).selection(),
                activation));
        assertInstanceOf(BarrierResult.StalePublication.class, staleActivate);
        // The replacement view itself remains fully operational.
        BarrierResult freshActivate = replacement.barrier().orElseThrow().activate(
            new com.schmaloogium.engine.registry.UseProgramRequest(
                assertInstanceOf(ProgramSelectionResult.Selected.class,
                    barrierStillWorks(replacement, activation)).selection(),
                activation));
        assertInstanceOf(BarrierResult.Activated.class, freshActivate);
    }

    private static ProgramSelectionResult barrierStillWorks(
            PublishedRegistry published, BarrierContext activation) {
        return published.barrier().orElseThrow().select(
            slot(ClassicProgramCatalog.rows(), "gbuffers_textured"), activation);
    }

    @Test
    void shadersOffPublicationAdvancesGenerationWithoutViews() {
        ScriptedGLDevice device = new ScriptedGLDevice();
        Fixture fixture = fixture(device);
        var publisher = com.schmaloogium.engine.registry.ProgramRegistries.publisher();
        BarrierContext release =
            publisher.current().contexts().beginFrame().release();
        PublicationResult result = publisher.publish(
            new RegistryPublication.ShadersOff(new RegistryBuildFailure(
                RegistryFailureKind.INVALID_SAMPLER_POLICY, List.of(),
                "diag-shaders-off", "sampler policy rejected")),
            release);
        PublicationResult.Accepted accepted = assertInstanceOf(PublicationResult.Accepted.class,
            result);
        assertEquals(1L, accepted.published().generation());
        assertTrue(accepted.published().registry().isEmpty());
        assertTrue(accepted.published().barrier().isEmpty());
    }
}
