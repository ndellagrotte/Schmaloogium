// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import com.schmaloogium.engine.buffers.FixedSamplerName;
import com.schmaloogium.engine.registry.StageId;

import java.util.EnumSet;
import java.util.Set;

/**
 * The stage-column legality table (§5.2's fixed-unit mirror): which fixed names are legal
 * column members in which expanded stage. {@code tex} is legal only in SHADOW; the generated
 * companions and fullscreen aliases keep their documented columns. This decides
 * STAGE_COLUMN diagnostics for custom keys; Phase 5 alone resolves units.
 */
public final class StageColumnPolicy {

    private static final Set<StageId> WORLD = EnumSet.of(StageId.GBUFFERS, StageId.SHADOW);
    private static final Set<StageId> POST =
        EnumSet.of(StageId.DEFERRED, StageId.COMPOSITE, StageId.FINAL);
    private static final Set<StageId> BOTH =
        EnumSet.of(StageId.GBUFFERS, StageId.SHADOW, StageId.DEFERRED, StageId.COMPOSITE,
            StageId.FINAL);

    private StageColumnPolicy() {
    }

    /** The stages whose column admits this fixed name; empty when none. */
    public static Set<StageId> legalStages(FixedSamplerName name) {
        return switch (name) {
            case TEXTURE -> WORLD;
            case TEX -> EnumSet.of(StageId.SHADOW);
            case LIGHTMAP -> WORLD;
            case NORMALS, SPECULAR -> WORLD;
            case SHADOWTEX0, WATERSHADOW, SHADOW -> BOTH;
            case SHADOWTEX1 -> BOTH;
            case DEPTHTEX0 -> BOTH;
            case GDEPTHTEX -> POST;
            case GAUX1, GAUX2, GAUX3, GAUX4 -> BOTH;
            case DEPTHTEX1 -> BOTH;
            case DEPTHTEX2 -> POST;
            case SHADOWCOLOR0, SHADOWCOLOR, SHADOWCOLOR1 -> BOTH;
            case NOISETEX -> BOTH;
            case COLORTEX0, COLORTEX1, COLORTEX2, COLORTEX3,
                 COLORTEX4, COLORTEX5, COLORTEX6, COLORTEX7,
                 GCOLOR, GDEPTH, GNORMAL, COMPOSITE -> POST;
        };
    }

    /** Whether the expanded stage is one whose column can carry custom candidates. */
    public static boolean isCustomStage(StageId stage) {
        return stage == StageId.GBUFFERS || stage == StageId.SHADOW
            || stage == StageId.DEFERRED || stage == StageId.COMPOSITE
            || stage == StageId.FINAL;
    }

    /** The five custom-capable expanded stages, in canonical stage order. */
    public static Set<StageId> customStages() {
        return EnumSet.of(StageId.GBUFFERS, StageId.SHADOW, StageId.DEFERRED,
            StageId.COMPOSITE, StageId.FINAL);
    }

    /** Exact stage expansion (§4.3.1): GBUFFERS→+SHADOW, DEFERRED, COMPOSITE→+FINAL. */
    public static Set<StageId> expand(StageId declared) {
        return switch (declared) {
            case GBUFFERS -> EnumSet.of(StageId.GBUFFERS, StageId.SHADOW);
            case DEFERRED -> EnumSet.of(StageId.DEFERRED);
            case COMPOSITE -> EnumSet.of(StageId.COMPOSITE, StageId.FINAL);
            default -> EnumSet.noneOf(StageId.class);
        };
    }
}
