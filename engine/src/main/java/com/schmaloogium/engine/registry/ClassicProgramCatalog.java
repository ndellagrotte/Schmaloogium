// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The declarative Appendix A.1 classic catalog (PHASE_4_DOC §3.2/§4.5): every named row
 * encoded exactly once, validated for unique names, legal stages, known fallback parents,
 * acyclic edges, virtual-slot isolation and source-stem uniqueness. The rows are
 * constructed first; the numeric total (60, excluding the external {@code <none>}
 * sentinel) is validation metadata only and never an allocation constant.
 */
public final class ClassicProgramCatalog {

    /** The external GUI/menu sentinel; fixed, never compiled, not one of the 60. */
    public static final ProgramSlotDescriptor NONE_SENTINEL = new ProgramSlotDescriptor(
        new ProgramSlotId("<none>"),
        StageId.SETUP,
        ProgramSlotKind.FIXED_FUNCTION_SENTINEL,
        Optional.empty(),
        Optional.empty(),
        Set.of());

    public static final ProgramSlotId SHADOW = new ProgramSlotId("shadow");
    private static final ProgramSlotId SHADOW_SOLID = new ProgramSlotId("shadow_solid");
    private static final ProgramSlotId SHADOW_CUTOUT = new ProgramSlotId("shadow_cutout");
    private static final ProgramSlotId GBUFFERS_BASIC = new ProgramSlotId("gbuffers_basic");
    private static final ProgramSlotId GBUFFERS_TEXTURED = new ProgramSlotId("gbuffers_textured");
    private static final ProgramSlotId GBUFFERS_TEXTURED_LIT =
        new ProgramSlotId("gbuffers_textured_lit");
    private static final ProgramSlotId GBUFFERS_SKYBASIC = new ProgramSlotId("gbuffers_skybasic");
    private static final ProgramSlotId GBUFFERS_SKYTEXTURED =
        new ProgramSlotId("gbuffers_skytextured");
    private static final ProgramSlotId GBUFFERS_CLOUDS = new ProgramSlotId("gbuffers_clouds");
    private static final ProgramSlotId GBUFFERS_TERRAIN = new ProgramSlotId("gbuffers_terrain");
    private static final ProgramSlotId GBUFFERS_TERRAIN_SOLID =
        new ProgramSlotId("gbuffers_terrain_solid");
    private static final ProgramSlotId GBUFFERS_TERRAIN_CUTOUT_MIP =
        new ProgramSlotId("gbuffers_terrain_cutout_mip");
    private static final ProgramSlotId GBUFFERS_TERRAIN_CUTOUT =
        new ProgramSlotId("gbuffers_terrain_cutout");
    private static final ProgramSlotId GBUFFERS_DAMAGEDBLOCK =
        new ProgramSlotId("gbuffers_damagedblock");
    private static final ProgramSlotId GBUFFERS_BLOCK = new ProgramSlotId("gbuffers_block");
    private static final ProgramSlotId GBUFFERS_BEACONBEAM = new ProgramSlotId("gbuffers_beaconbeam");
    private static final ProgramSlotId GBUFFERS_ITEM = new ProgramSlotId("gbuffers_item");
    private static final ProgramSlotId GBUFFERS_ENTITIES = new ProgramSlotId("gbuffers_entities");
    private static final ProgramSlotId GBUFFERS_ENTITIES_GLOWING =
        new ProgramSlotId("gbuffers_entities_glowing");
    private static final ProgramSlotId GBUFFERS_ARMOR_GLINT =
        new ProgramSlotId("gbuffers_armor_glint");
    private static final ProgramSlotId GBUFFERS_SPIDEREYES =
        new ProgramSlotId("gbuffers_spidereyes");
    private static final ProgramSlotId GBUFFERS_HAND = new ProgramSlotId("gbuffers_hand");
    private static final ProgramSlotId GBUFFERS_WEATHER = new ProgramSlotId("gbuffers_weather");
    private static final ProgramSlotId GBUFFERS_WATER = new ProgramSlotId("gbuffers_water");
    private static final ProgramSlotId GBUFFERS_HAND_WATER =
        new ProgramSlotId("gbuffers_hand_water");
    /** The deferred virtual prelude transition. */
    public static final ProgramSlotId DEFERRED_PRE = new ProgramSlotId("deferred_pre");
    /** The composite virtual prelude transition. */
    public static final ProgramSlotId COMPOSITE_PRE = new ProgramSlotId("composite_pre");
    private static final ProgramSlotId FINAL = new ProgramSlotId("final");

    private static final Set<StageBand> OPAQUE_ONLY = Set.of(StageBand.GBUFFERS_OPAQUE);
    private static final Set<StageBand> TRANSLUCENT_ONLY = Set.of(StageBand.GBUFFERS_TRANSLUCENT);
    private static final Set<StageBand> EXECUTOR_SELECTED =
        Set.of(StageBand.GBUFFERS_OPAQUE, StageBand.GBUFFERS_TRANSLUCENT);

    private static final List<ProgramSlotDescriptor> ROWS = buildRows();

    private static List<ProgramSlotDescriptor> buildRows() {
        java.util.ArrayList<ProgramSlotDescriptor> rows = new java.util.ArrayList<>();
        // --- shadow (3) ---
        rows.add(slot(SHADOW, StageId.SHADOW, ProgramSlotKind.RASTER, Optional.empty(),
            Set.of(StageBand.SHADOW)));
        rows.add(slot(SHADOW_SOLID, StageId.SHADOW, ProgramSlotKind.RASTER,
            Optional.of(SHADOW), Set.of(StageBand.SHADOW)));
        rows.add(slot(SHADOW_CUTOUT, StageId.SHADOW, ProgramSlotKind.RASTER,
            Optional.of(SHADOW), Set.of(StageBand.SHADOW)));
        // --- gbuffers (22, Appendix A.1 order) ---
        rows.add(slot(GBUFFERS_BASIC, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.empty(), EXECUTOR_SELECTED));
        rows.add(slot(GBUFFERS_TEXTURED, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_BASIC), EXECUTOR_SELECTED));
        rows.add(slot(GBUFFERS_TEXTURED_LIT, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TEXTURED), EXECUTOR_SELECTED));
        rows.add(slot(GBUFFERS_SKYBASIC, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_BASIC), OPAQUE_ONLY));
        rows.add(slot(GBUFFERS_SKYTEXTURED, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TEXTURED), OPAQUE_ONLY));
        rows.add(slot(GBUFFERS_CLOUDS, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TEXTURED), EXECUTOR_SELECTED));
        rows.add(slot(GBUFFERS_TERRAIN, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TEXTURED_LIT), OPAQUE_ONLY));
        rows.add(slot(GBUFFERS_TERRAIN_SOLID, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TERRAIN), OPAQUE_ONLY));
        rows.add(slot(GBUFFERS_TERRAIN_CUTOUT_MIP, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TERRAIN), OPAQUE_ONLY));
        rows.add(slot(GBUFFERS_TERRAIN_CUTOUT, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TERRAIN), OPAQUE_ONLY));
        rows.add(slot(GBUFFERS_DAMAGEDBLOCK, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TERRAIN), OPAQUE_ONLY));
        rows.add(slot(GBUFFERS_BLOCK, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TERRAIN), EXECUTOR_SELECTED));
        rows.add(slot(GBUFFERS_BEACONBEAM, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TEXTURED), EXECUTOR_SELECTED));
        rows.add(slot(GBUFFERS_ITEM, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TEXTURED_LIT), EXECUTOR_SELECTED));
        rows.add(slot(GBUFFERS_ENTITIES, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TEXTURED_LIT), EXECUTOR_SELECTED));
        rows.add(slot(GBUFFERS_ENTITIES_GLOWING, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_ENTITIES), EXECUTOR_SELECTED));
        rows.add(slot(GBUFFERS_ARMOR_GLINT, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TEXTURED), EXECUTOR_SELECTED));
        rows.add(slot(GBUFFERS_SPIDEREYES, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TEXTURED), EXECUTOR_SELECTED));
        rows.add(slot(GBUFFERS_HAND, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TEXTURED_LIT), TRANSLUCENT_ONLY));
        rows.add(slot(GBUFFERS_WEATHER, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TEXTURED_LIT), TRANSLUCENT_ONLY));
        rows.add(slot(GBUFFERS_WATER, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_TERRAIN), TRANSLUCENT_ONLY));
        rows.add(slot(GBUFFERS_HAND_WATER, StageId.GBUFFERS, ProgramSlotKind.RASTER,
            Optional.of(GBUFFERS_HAND), TRANSLUCENT_ONLY));
        // --- deferred (17, prelude first) ---
        rows.add(virtual(DEFERRED_PRE, StageId.DEFERRED, StageBand.BETWEEN_GBUFFERS));
        rows.addAll(sparseFamily(StageId.DEFERRED, "deferred", StageBand.BETWEEN_GBUFFERS));
        // --- composite (17, prelude first) ---
        rows.add(virtual(COMPOSITE_PRE, StageId.COMPOSITE, StageBand.FRAME_END));
        rows.addAll(sparseFamily(StageId.COMPOSITE, "composite", StageBand.FRAME_END));
        // --- final (1) ---
        rows.add(slot(FINAL, StageId.FINAL, ProgramSlotKind.RASTER, Optional.empty(),
            Set.of(StageBand.SCREEN)));
        return List.copyOf(rows);
    }

    private ClassicProgramCatalog() {
    }

    /** All 60 classic slot rows, Appendix A.1 order, immutable. */
    public static List<ProgramSlotDescriptor> rows() {
        return ROWS;
    }

    /** The declared row count: always 60, validation metadata only. */
    public static int declaredClassicCount() {
        return 60;
    }

    /** The enumerated row count: {@code rows().size()} — the contract test pins equality. */
    public static int enumeratedClassicCount() {
        return ROWS.size();
    }

    private static ProgramSlotDescriptor slot(ProgramSlotId id, StageId stage,
            ProgramSlotKind kind, Optional<ProgramSlotId> fallback, Set<StageBand> bands) {
        return new ProgramSlotDescriptor(id, stage, kind, Optional.of(id.packName()),
            fallback, bands);
    }

    private static ProgramSlotDescriptor virtual(ProgramSlotId id, StageId stage,
            StageBand band) {
        return new ProgramSlotDescriptor(id, stage, ProgramSlotKind.VIRTUAL_FLIP_CONTROL,
            Optional.empty(), Optional.empty(), Set.of(band));
    }

    /** The 16 indexed family rows 0…15; index 0 uses the unsuffixed stem (§4.3). */
    private static List<ProgramSlotDescriptor> sparseFamily(StageId stage, String stem,
            StageBand band) {
        ProgramSlotDescriptor[] members = new ProgramSlotDescriptor[16];
        for (int i = 0; i <= 15; i++) {
            members[i] = new ProgramSlotDescriptor(
                new ProgramSlotId(i == 0 ? stem : stem + i),
                stage,
                ProgramSlotKind.RASTER,
                Optional.of(i == 0 ? stem : stem + i),
                Optional.empty(),
                Set.of(band));
        }
        return List.of(members);
    }
}
