// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The two required schedule configurations and §4.1 construction validation
 * (PHASE_4_DOC §4.1/§4.2): schedule is data, gbuffers occurs twice, FINAL is last,
 * SETUP lives outside the per-frame schedule.
 */
class StageRegistryConfigurationsTest {

    @Test
    void classicG6HasSixStepsWithGbuffersTwiceAndFinalLast() {
        StageRegistry registry = StageRegistries.classicG6();
        List<StageStep> schedule = registry.schedule();
        assertEquals(6, schedule.size());
        assertEquals(StageId.SHADOW, schedule.get(0).stage());
        assertEquals(StageBand.SHADOW, schedule.get(0).band());
        StageStep last = schedule.get(schedule.size() - 1);
        assertEquals(StageId.FINAL, last.stage());
        assertEquals(StageBand.SCREEN, last.band());
        long gbuffers = schedule.stream()
            .filter(step -> step.stage() == StageId.GBUFFERS).count();
        assertEquals(2, gbuffers);
        for (StageStep step : schedule) {
            assertTrue(step.band() != StageBand.LOAD_OR_RESIZE);
        }
    }

    @Test
    void fullShapeScheduleIncludesAllNineIdentitiesOnceExceptGbuffers() {
        StageRegistry registry = StageRegistries.modernFullShape(15, 15);
        List<StageStep> schedule = registry.schedule();
        assertEquals(StageId.SETUP, schedule.get(0).stage());
        assertEquals(StageBand.LOAD_OR_RESIZE, schedule.get(0).band());
        StageStep last = schedule.get(schedule.size() - 1);
        assertEquals(StageId.FINAL, last.stage());
        for (StageId id : StageId.values()) {
            long occurrences = schedule.stream()
                .filter(step -> step.stage() == id).count();
            if (id == StageId.GBUFFERS) {
                assertEquals(2, occurrences);
            } else {
                assertEquals(1, occurrences, id.name());
            }
        }
    }

    @Test
    void sparsePopulationOrderSkipsHolesAndKeepsPreludeFirst() {
        StageRegistry registry = StageRegistries.modernFullShape(15, 15);
        StageStep deferred = registry.schedule().stream()
            .filter(step -> step.stage() == StageId.DEFERRED).findFirst().orElseThrow();
        List<PassDescriptor> passes = registry.passes(deferred);
        assertFalse(passes.isEmpty());
        assertEquals("deferred_pre", passes.get(0).slot().packName());
        int previous = -1;
        int populated = 0;
        for (PassDescriptor pass : passes) {
            if (pass.index().isPresent()) {
                int value = pass.index().get().value();
                assertTrue(value > previous, "indexes must ascend: " + value);
                previous = value;
                populated++;
            }
        }
        assertEquals(16, populated);
    }

    @Test
    void namedLookupRejectsWrongKindAndForeignStep() {
        StageRegistry registry = StageRegistries.classicG6();
        StageStep opaque = registry.schedule().get(0);
        StageStep deferred = registry.schedule().stream()
            .filter(step -> step.stage() == StageId.DEFERRED).findFirst().orElseThrow();
        List<PassDescriptor> namedPasses = registry.passes(opaque);
        assertFalse(namedPasses.isEmpty());
        assertTrue(registry.named(opaque, namedPasses.get(0).slot()).isPresent());
        // A deferred-indexed slot name is not a named key: sparse steps accept only
        // the exact prelude key and reject everything else.
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
            () -> registry.named(deferred, new ProgramSlotId("deferred5")));
        // Indexed lookup on a NamedPrograms step rejects.
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
            () -> registry.indexed(opaque, new PassIndex(5)));
    }

    @Test
    void finalStepIsSingletonPopulation() {
        StageRegistry registry = StageRegistries.classicG6();
        StageStep finalStep = registry.schedule().get(registry.schedule().size() - 1);
        List<PassDescriptor> passes = registry.passes(finalStep);
        assertEquals(1, passes.size());
        assertEquals("final", passes.get(0).slot().packName());
        assertEquals(StageId.FINAL, passes.get(0).step().stage());
    }
}
