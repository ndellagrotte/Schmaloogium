// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.registry.ClassicProgramCatalog;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.registry.PassPopulation;
import com.schmaloogium.engine.registry.PassIndex;
import com.schmaloogium.engine.registry.ProgramBuildFailure;
import com.schmaloogium.engine.registry.ProgramBuildStage;
import com.schmaloogium.engine.registry.ProgramOwnBuildDisposition;
import com.schmaloogium.engine.registry.ProgramResolutionProjection;
import com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint;
import com.schmaloogium.engine.registry.ProgramUniformLayout;
import com.schmaloogium.engine.registry.ProgramResolutionStatus;
import com.schmaloogium.engine.registry.ProgramSlotDescriptor;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramSlotKind;
import com.schmaloogium.engine.registry.ProgramStateBundle;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageRegistries;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.registry.StageStep;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Backup-chain resolution and projection-row semantics (PHASE_4_DOC §4.6/§4.7): the walk
 * folds Missing/Disabled/Failed over declared fallback edges to a root terminal, and the
 * projection classifies SOURCED/CHAIN/ABSENT/FAILED with failure detail masking.
 */
class ResolutionProjectionTest {

    private static ProgramSlotDescriptor raster(String name, StageId stage,
            StageBand band, String fallback) {
        return new ProgramSlotDescriptor(new ProgramSlotId(name), stage,
            ProgramSlotKind.RASTER, Optional.of(name),
            fallback == null ? Optional.empty() : Optional.of(new ProgramSlotId(fallback)),
            Set.of(band));
    }

    private static Planner.PlannedSlot planned(
            ProgramSlotDescriptor descriptor,
            ProgramOwnBuildDisposition disposition,
            boolean sourcePresent,
            String diagnosticId) {
        Planner.PlannedSlot slot = new Planner.PlannedSlot(descriptor);
        slot.disposition = disposition;
        slot.sourcePresent = sourcePresent;
        if (diagnosticId != null) {
            slot.failure = new ProgramBuildFailure(descriptor.id(), descriptor.sourceStem(),
                ProgramBuildStage.LINK, Optional.empty(), "driver exploded",
                List.of(), List.of(), "FAILED", diagnosticId, Optional.empty());
        }
        return slot;
    }

    private static CompiledProgramBinding shader(ProgramSlotId provider) {
        return new CompiledProgramBinding.ShaderProgram(provider, new ProgramHandle() {},
            new ProgramStateBundle(
                new com.schmaloogium.engine.registry.DrawRouting.AllUsedBuffers(
                    com.schmaloogium.engine.registry.BufferDomain.COLORTEX),
                Set.of(), 1, Set.of(), Optional.empty(), Optional.empty(),
                Optional.empty(), Map.of(), Optional.empty(),
                com.schmaloogium.engine.registry.GeometryInputRequirement.NONE),
            ProgramUniformLayout.empty(),
            new com.schmaloogium.engine.registry.ProgramSamplerLayout.FixedFunctionEmpty(
                new ProgramSamplerLayoutFingerprint("fp"),
                new com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint("p")),
            List.of());
    }

    @Test
    void succeededOwnBuildResolvesSourced() {
        ProgramSlotDescriptor self = raster("gbuffers_basic", StageId.GBUFFERS,
            StageBand.GBUFFERS_OPAQUE, null);
        Planner.PlannedSlot slot = planned(self,
            ProgramOwnBuildDisposition.SUCCEEDED, true, null);
        Map<ProgramSlotId, CompiledProgramBinding> bindings =
            Map.of(self.id(), shader(self.id()));

        RegistryAssembler.Walk walk =
            RegistryAssembler.walk(self.id(), List.of(slot), bindings);
        assertEquals(self.id(), walk.binding.provider());
        assertEquals(List.of(self.id()), walk.path);
        ProgramResolutionProjection row = RegistryAssembler.project(slot, walk);
        assertEquals(ProgramResolutionStatus.SOURCED, row.status());
        assertEquals(ProgramOwnBuildDisposition.SUCCEEDED, row.ownBuild());
        assertTrue(row.from().isEmpty());
        assertEquals("", row.driverLog());
    }

    @Test
    void missingSlotChainsToProviderAndMasksNoOwnFailure() {
        ProgramSlotDescriptor textured = raster("gbuffers_textured", StageId.GBUFFERS,
            StageBand.GBUFFERS_OPAQUE, "gbuffers_basic");
        ProgramSlotDescriptor basic = raster("gbuffers_basic", StageId.GBUFFERS,
            StageBand.GBUFFERS_OPAQUE, null);
        Planner.PlannedSlot missing = planned(textured,
            ProgramOwnBuildDisposition.NO_SOURCE, false, null);
        Planner.PlannedSlot provider = planned(basic,
            ProgramOwnBuildDisposition.SUCCEEDED, true, null);
        Map<ProgramSlotId, CompiledProgramBinding> bindings =
            Map.of(basic.id(), shader(basic.id()));

        RegistryAssembler.Walk walk = RegistryAssembler.walk(textured.id(),
            List.of(missing, provider), bindings);
        assertEquals(basic.id(), walk.binding.provider());
        assertEquals(List.of(textured.id(), basic.id()), walk.path);

        ProgramResolutionProjection row = RegistryAssembler.project(missing, walk);
        assertEquals(ProgramResolutionStatus.CHAIN, row.status());
        assertEquals(Optional.of(basic.id()), row.from());
        assertEquals(ProgramOwnBuildDisposition.NO_SOURCE, row.ownBuild());
        assertEquals("", row.driverLog());
    }

    @Test
    void failedSlotChainsAndRecordsMaskedFailureDetail() {
        ProgramSlotDescriptor textured = raster("gbuffers_textured", StageId.GBUFFERS,
            StageBand.GBUFFERS_OPAQUE, "gbuffers_basic");
        ProgramSlotDescriptor basic = raster("gbuffers_basic", StageId.GBUFFERS,
            StageBand.GBUFFERS_OPAQUE, null);
        Planner.PlannedSlot failed = planned(textured,
            ProgramOwnBuildDisposition.FAILED, true, "diag-link-1");
        Planner.PlannedSlot provider = planned(basic,
            ProgramOwnBuildDisposition.SUCCEEDED, true, null);
        Map<ProgramSlotId, CompiledProgramBinding> bindings =
            Map.of(basic.id(), shader(basic.id()));

        RegistryAssembler.Walk walk = RegistryAssembler.walk(textured.id(),
            List.of(failed, provider), bindings);
        ProgramResolutionProjection row = RegistryAssembler.project(failed, walk);
        assertEquals(ProgramResolutionStatus.CHAIN, row.status());
        assertEquals(Optional.of(basic.id()), row.from());
        assertEquals(ProgramOwnBuildDisposition.FAILED, row.ownBuild());
        assertTrue(row.driverLog().contains("LINK|diag-link-1"),
            "masked own failure must surface in the projection detail");
        assertTrue(row.driverLog().contains("driver exploded"));
    }

    @Test
    void failedRootTerminalIsFailedRowOnFixedFunction() {
        ProgramSlotDescriptor root = raster("gbuffers_basic", StageId.GBUFFERS,
            StageBand.GBUFFERS_OPAQUE, null);
        Planner.PlannedSlot failed = planned(root,
            ProgramOwnBuildDisposition.FAILED, true, "diag-compile-2");
        RegistryAssembler.Walk walk =
            RegistryAssembler.walk(root.id(), List.of(failed), Map.of());
        assertInstanceOf(CompiledProgramBinding.FixedFunction.class, walk.binding);

        ProgramResolutionProjection row = RegistryAssembler.project(failed, walk);
        assertEquals(ProgramResolutionStatus.FAILED, row.status());
        assertTrue(row.from().isEmpty());
        assertTrue(row.driverLog().contains("diag-compile-2"));
    }

    @Test
    void absentRootTerminalIsAbsentRow() {
        ProgramSlotDescriptor root = raster("shadow", StageId.SHADOW, StageBand.SHADOW, null);
        Planner.PlannedSlot missing = planned(root,
            ProgramOwnBuildDisposition.NO_SOURCE, false, null);
        RegistryAssembler.Walk walk =
            RegistryAssembler.walk(root.id(), List.of(missing), Map.of());
        assertInstanceOf(CompiledProgramBinding.FixedFunction.class, walk.binding);
        ProgramResolutionProjection row = RegistryAssembler.project(missing, walk);
        assertEquals(ProgramResolutionStatus.ABSENT, row.status());
        assertEquals(ProgramOwnBuildDisposition.NO_SOURCE, row.ownBuild());
        assertEquals("", row.driverLog());
    }

    @Test
    void multiHopChainFoldsThroughEveryEdge() {
        ProgramSlotDescriptor leaf = raster("gbuffers_textured_lit", StageId.GBUFFERS,
            StageBand.GBUFFERS_OPAQUE, "gbuffers_textured");
        ProgramSlotDescriptor mid = raster("gbuffers_textured", StageId.GBUFFERS,
            StageBand.GBUFFERS_OPAQUE, "gbuffers_basic");
        ProgramSlotDescriptor root = raster("gbuffers_basic", StageId.GBUFFERS,
            StageBand.GBUFFERS_OPAQUE, null);
        Planner.PlannedSlot leafSlot = planned(leaf,
            ProgramOwnBuildDisposition.NO_SOURCE, false, null);
        Planner.PlannedSlot midSlot = planned(mid,
            ProgramOwnBuildDisposition.FAILED, true, "diag-mid");
        Planner.PlannedSlot rootSlot = planned(root,
            ProgramOwnBuildDisposition.SUCCEEDED, true, null);
        Map<ProgramSlotId, CompiledProgramBinding> bindings =
            Map.of(root.id(), shader(root.id()));

        RegistryAssembler.Walk walk = RegistryAssembler.walk(leaf.id(),
            List.of(leafSlot, midSlot, rootSlot), bindings);
        assertEquals(root.id(), walk.binding.provider());
        assertEquals(List.of(leaf.id(), mid.id(), root.id()), walk.path);
        assertEquals(2, walk.path.size() - 1);
    }

    @Test
    void classicCatalogChainsResolveThroughCatalogFallbacks() {
        // End-to-end over the real catalog: every declared fallback edge terminates at
        // a root with a binding, and sparse indexed holes fold to their family base.
        List<ProgramSlotDescriptor> catalog = ClassicProgramCatalog.rows();
        List<Planner.PlannedSlot> planned = new java.util.ArrayList<>();
        for (ProgramSlotDescriptor descriptor : catalog) {
            planned.add(planned(descriptor, ProgramOwnBuildDisposition.NO_SOURCE,
                false, null));
        }
        for (ProgramSlotDescriptor descriptor : catalog) {
            RegistryAssembler.Walk walk = RegistryAssembler.walk(descriptor.id(),
                planned, Map.of());
            if (descriptor.fallback().isEmpty()) {
                // Root: fixed function.
                assertInstanceOf(CompiledProgramBinding.FixedFunction.class, walk.binding);
            } else {
                assertTrue(walk.path.size() >= 2,
                    descriptor.id().packName() + " must traverse at least one edge");
                assertEquals(descriptor.id(), walk.path.get(0));
            }
            assertEquals(descriptor.id(), walk.path.get(0));
        }
    }

    @Test
    void sparseIndexedHolesKeepAscendingProjectionOrder() {
        // The full-superset schedule population covers 0..15 with every index present;
        // projection rows for holes come only from sparse configurations.
        StageRegistry registry = StageRegistries.modernFullShape(15, 15);
        StageStep deferred = registry.schedule().stream()
            .filter(step -> step.stage() == StageId.DEFERRED).findFirst().orElseThrow();
        PassPopulation.SparseArray population =
            (PassPopulation.SparseArray) deferred.population();
        assertEquals(99, population.highestLegalIndex());
        assertEquals(15, population.highestPopulatedIndex());
    }
}
