// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import com.schmaloogium.engine.registry.internal.StageRegistryDefinition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The two required schedule configurations (PHASE_4_DOC §4.2) plus the schedule shape
 * helper. G6: five stage identities in six steps — shadow named, gbuffers opaque named,
 * deferred sparse 0…15 with its exact prelude, gbuffers translucent named, composite sparse
 * 0…15 with its exact prelude, final singleton. Full shape: all nine identities including
 * the dormant SETUP/BEGIN/SHADOWCOMP/PREPARE sparse families. Construction validates the
 * complete §4.1 rule set before any publication.
 */
public final class StageRegistries {

    private StageRegistries() {
    }

    /** The classic G6 configuration: five identities, six steps, populations through 15. */
    public static StageRegistry classicG6() {
        return build(classicSchedule(15, 15));
    }

    /**
     * The full-superset configuration: all nine identities; the dormant families carry no
     * populated members at v0.1 ({@code highestPopulatedIndex = -1}); deferred and
     * composite populate through {@code h} (greatest populated member, or {@code -1}).
     */
    public static StageRegistry modernFullShape(int deferredPopulatedThrough,
            int compositePopulatedThrough) {
        return build(fullShapeSchedule(deferredPopulatedThrough, compositePopulatedThrough));
    }

    /**
     * Compiles a custom definition over the given slot rows; exposed for G8/S1 population
     * wiring and tests. All §4.1 validation applies.
     */
    public static StageRegistry compile(List<ProgramSlotDescriptor> slots,
            List<StageStep> schedule) {
        return StageRegistryDefinition.compile(
            slots,
            schedule,
            StageRegistryDefinition.ComputePolicy.DORMANT_SLOTS_ONLY,
            slot -> Map.of(),
            slot -> Set.of());
    }

    private static StageRegistry build(List<StageStep> schedule) {
        return StageRegistryDefinition.compile(
            ClassicProgramCatalog.rows(),
            schedule,
            StageRegistryDefinition.ComputePolicy.DORMANT_SLOTS_ONLY,
            slot -> Map.of(),
            slot -> Set.of());
    }

    private static List<StageStep> classicSchedule(int deferredThrough, int compositeThrough) {
        return List.of(
            named(StageId.SHADOW, StageBand.SHADOW,
                "shadow", "shadow_solid", "shadow_cutout"),
            named(StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE,
                "gbuffers_basic", "gbuffers_textured", "gbuffers_textured_lit",
                "gbuffers_skybasic", "gbuffers_skytextured", "gbuffers_clouds",
                "gbuffers_terrain", "gbuffers_terrain_solid", "gbuffers_terrain_cutout_mip",
                "gbuffers_terrain_cutout", "gbuffers_damagedblock", "gbuffers_block",
                "gbuffers_beaconbeam", "gbuffers_item", "gbuffers_entities",
                "gbuffers_entities_glowing", "gbuffers_armor_glint", "gbuffers_spidereyes"),
            sparse(StageId.DEFERRED, StageBand.BETWEEN_GBUFFERS, deferredThrough,
                ClassicProgramCatalog.DEFERRED_PRE),
            named(StageId.GBUFFERS, StageBand.GBUFFERS_TRANSLUCENT,
                "gbuffers_basic", "gbuffers_textured", "gbuffers_textured_lit",
                "gbuffers_clouds", "gbuffers_block", "gbuffers_beaconbeam", "gbuffers_item",
                "gbuffers_entities", "gbuffers_entities_glowing", "gbuffers_armor_glint",
                "gbuffers_spidereyes", "gbuffers_hand", "gbuffers_weather", "gbuffers_water",
                "gbuffers_hand_water"),
            sparse(StageId.COMPOSITE, StageBand.FRAME_END, compositeThrough,
                ClassicProgramCatalog.COMPOSITE_PRE),
            new StageStep(StageId.FINAL, StageBand.SCREEN, new PassPopulation.Singleton()));
    }

    private static List<StageStep> fullShapeSchedule(int deferredThrough,
            int compositeThrough) {
        return List.of(
            sparse(StageId.SETUP, StageBand.LOAD_OR_RESIZE, -1, null),
            sparse(StageId.BEGIN, StageBand.FRAME_BEGIN, -1, null),
            named(StageId.SHADOW, StageBand.SHADOW,
                "shadow", "shadow_solid", "shadow_cutout"),
            sparse(StageId.SHADOWCOMP, StageBand.AFTER_SHADOW, -1, null),
            sparse(StageId.PREPARE, StageBand.BEFORE_GBUFFERS, -1, null),
            named(StageId.GBUFFERS, StageBand.GBUFFERS_OPAQUE,
                "gbuffers_basic", "gbuffers_textured", "gbuffers_textured_lit",
                "gbuffers_skybasic", "gbuffers_skytextured", "gbuffers_clouds",
                "gbuffers_terrain", "gbuffers_terrain_solid", "gbuffers_terrain_cutout_mip",
                "gbuffers_terrain_cutout", "gbuffers_damagedblock", "gbuffers_block",
                "gbuffers_beaconbeam", "gbuffers_item", "gbuffers_entities",
                "gbuffers_entities_glowing", "gbuffers_armor_glint", "gbuffers_spidereyes"),
            sparse(StageId.DEFERRED, StageBand.BETWEEN_GBUFFERS, deferredThrough,
                ClassicProgramCatalog.DEFERRED_PRE),
            named(StageId.GBUFFERS, StageBand.GBUFFERS_TRANSLUCENT,
                "gbuffers_basic", "gbuffers_textured", "gbuffers_textured_lit",
                "gbuffers_clouds", "gbuffers_block", "gbuffers_beaconbeam", "gbuffers_item",
                "gbuffers_entities", "gbuffers_entities_glowing", "gbuffers_armor_glint",
                "gbuffers_spidereyes", "gbuffers_hand", "gbuffers_weather", "gbuffers_water",
                "gbuffers_hand_water"),
            sparse(StageId.COMPOSITE, StageBand.FRAME_END, compositeThrough,
                ClassicProgramCatalog.COMPOSITE_PRE),
            new StageStep(StageId.FINAL, StageBand.SCREEN, new PassPopulation.Singleton()));
    }

    private static StageStep named(StageId stage, StageBand band, String... names) {
        List<ProgramSlotId> slots = java.util.Arrays.stream(names)
            .map(ProgramSlotId::new)
            .toList();
        return new StageStep(stage, band, new PassPopulation.NamedPrograms(slots));
    }

    private static StageStep sparse(StageId stage, StageBand band, int populatedThrough,
            ProgramSlotId prelude) {
        return new StageStep(stage, band, new PassPopulation.SparseArray(99,
            populatedThrough,
            prelude == null ? java.util.Optional.empty() : java.util.Optional.of(prelude)));
    }
}
