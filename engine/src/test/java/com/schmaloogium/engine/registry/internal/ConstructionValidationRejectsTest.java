// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.registry.ClassicProgramCatalog;
import com.schmaloogium.engine.registry.PassPopulation;
import com.schmaloogium.engine.registry.ProgramSlotDescriptor;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramSlotKind;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageRegistries;
import com.schmaloogium.engine.registry.StageStep;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * §4.1 construction validation rejects over {@link StageRegistries#compile}: duplicate
 * family index, prelude misuse, named-population catalog order, FINAL shape, and band
 * pairing. Every reject throws before any state exists.
 */
class ConstructionValidationRejectsTest {

    private static List<ProgramSlotDescriptor> catalog() {
        return ClassicProgramCatalog.rows();
    }

    private static List<ProgramSlotId> namedIds(StageId stage, StageBand band) {
        return catalog().stream()
            .filter(row -> row.stage() == stage)
            .filter(row -> row.kind() == ProgramSlotKind.RASTER)
            .filter(row -> row.permittedBands().contains(band))
            .map(ProgramSlotDescriptor::id)
            .toList();
    }

    private static PassPopulation.SparseArray sparse(StageId stage, StageBand band,
            Optional<ProgramSlotId> prelude) {
        return new PassPopulation.SparseArray(99, 15, prelude);
    }

    private static List<StageStep> classicSchedule() {
        return List.of(
            new StageStep(StageId.SHADOW, StageBand.SHADOW,
                new PassPopulation.NamedPrograms(
                    namedIds(StageId.SHADOW, StageBand.SHADOW))),
            new StageStep(StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE,
                new PassPopulation.NamedPrograms(
                    namedIds(StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE))),
            new StageStep(StageId.DEFERRED, StageBand.BETWEEN_GBUFFERS,
                sparse(StageId.DEFERRED, StageBand.BETWEEN_GBUFFERS,
                    Optional.of(new ProgramSlotId("deferred_pre")))),
            new StageStep(StageId.GBUFFERS, StageBand.GBUFFERS_TRANSLUCENT,
                new PassPopulation.NamedPrograms(
                    namedIds(StageId.GBUFFERS, StageBand.GBUFFERS_TRANSLUCENT))),
            new StageStep(StageId.FINAL, StageBand.SCREEN, new PassPopulation.Singleton()));
    }

    @Test
    void duplicateFamilyIndexIsRejected() {
        List<ProgramSlotDescriptor> rows = new ArrayList<>(catalog());
        ProgramSlotDescriptor duplicate = new ProgramSlotDescriptor(
            new ProgramSlotId("deferred05"), StageId.DEFERRED, ProgramSlotKind.RASTER,
            Optional.of("deferred"), Optional.of(new ProgramSlotId("deferred")),
            java.util.Set.of(StageBand.BETWEEN_GBUFFERS));
        rows.add(duplicate);
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
            () -> StageRegistries.compile(rows, classicSchedule()));
        assertTrue(failure.getMessage().toLowerCase().contains("index")
                || failure.getMessage().toLowerCase().contains("duplicate"),
            failure.getMessage());
    }

    @Test
    void wrongPreludeForSparseStepIsRejected() {
        List<StageStep> schedule = new ArrayList<>(classicSchedule());
        schedule.set(2, new StageStep(StageId.DEFERRED, StageBand.BETWEEN_GBUFFERS,
            sparse(StageId.DEFERRED, StageBand.BETWEEN_GBUFFERS,
                Optional.of(new ProgramSlotId("composite_pre")))));
        assertThrows(IllegalArgumentException.class,
            () -> StageRegistries.compile(catalog(), schedule));
    }

    @Test
    void missingRequiredPreludeIsRejected() {
        List<StageStep> schedule = new ArrayList<>(classicSchedule());
        schedule.set(2, new StageStep(StageId.DEFERRED, StageBand.BETWEEN_GBUFFERS,
            sparse(StageId.DEFERRED, StageBand.BETWEEN_GBUFFERS, Optional.empty())));
        assertThrows(IllegalArgumentException.class,
            () -> StageRegistries.compile(catalog(), schedule));
    }

    @Test
    void namedPopulationOutOfCatalogOrderIsRejected() {
        List<StageStep> schedule = new ArrayList<>(classicSchedule());
        List<ProgramSlotId> reversed =
            new ArrayList<>(namedIds(StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE));
        java.util.Collections.reverse(reversed);
        schedule.set(1, new StageStep(StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE,
            new PassPopulation.NamedPrograms(List.copyOf(reversed))));
        assertThrows(IllegalArgumentException.class,
            () -> StageRegistries.compile(catalog(), schedule));
    }

    @Test
    void foreignSlotInNamedPopulationIsRejected() {
        List<StageStep> schedule = new ArrayList<>(classicSchedule());
        List<ProgramSlotId> foreign = new ArrayList<>(
            namedIds(StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE));
        foreign.add(new ProgramSlotId("final"));
        schedule.set(1, new StageStep(StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE,
            new PassPopulation.NamedPrograms(List.copyOf(foreign))));
        assertThrows(IllegalArgumentException.class,
            () -> StageRegistries.compile(catalog(), schedule));
    }

    @Test
    void finalNotLastIsRejected() {
        List<StageStep> schedule = new ArrayList<>(classicSchedule());
        StageStep finalStep = schedule.remove(schedule.size() - 1);
        schedule.add(0, finalStep);
        assertThrows(IllegalArgumentException.class,
            () -> StageRegistries.compile(catalog(), schedule));
    }

    @Test
    void finalNonSingletonIsRejected() {
        List<StageStep> schedule = new ArrayList<>(classicSchedule());
        schedule.set(schedule.size() - 1,
            new StageStep(StageId.FINAL, StageBand.SCREEN,
                new PassPopulation.NamedPrograms(
                    namedIds(StageId.FINAL, StageBand.SCREEN))));
        assertThrows(IllegalArgumentException.class,
            () -> StageRegistries.compile(catalog(), schedule));
    }

    @Test
    void loadOrResizeBandOutsideSetupIsRejected() {
        List<StageStep> schedule = new ArrayList<>(classicSchedule());
        schedule.set(0, new StageStep(StageId.SHADOW, StageBand.LOAD_OR_RESIZE,
            schedule.get(0).population()));
        assertThrows(IllegalArgumentException.class,
            () -> StageRegistries.compile(catalog(), schedule));
    }

    @Test
    void wrongBandForStageIdentityIsRejected() {
        List<StageStep> schedule = new ArrayList<>(classicSchedule());
        schedule.set(3, new StageStep(StageId.GBUFFERS, StageBand.BETWEEN_GBUFFERS,
            schedule.get(3).population()));
        assertThrows(IllegalArgumentException.class,
            () -> StageRegistries.compile(catalog(), schedule));
    }

    @Test
    void duplicateScheduledIdentityBeyondGbuffersIsRejected() {
        List<StageStep> schedule = new ArrayList<>(classicSchedule());
        schedule.add(2, schedule.get(0)); // second SHADOW occurrence
        assertThrows(IllegalArgumentException.class,
            () -> StageRegistries.compile(catalog(), schedule));
    }

    @Test
    void legalClassicScheduleStillCompiles() {
        // Sanity guard: the reject fixtures differ from a legal build only in the
        // mutated aspect.
        assertTrue(StageRegistries.compile(catalog(), classicSchedule()) != null);
    }
}
