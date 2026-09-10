// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.dispatch;

/**
 * Engine vocabulary for the catalogued vanilla render moments (PHASE_7_DOC §5.1) — not
 * pack-facing program names. {@link PhaseDispatchTable} maps each value onto the exact
 * requested program slot and its legal frame window.
 */
public enum RenderSection {
    SKY_BASIC,
    SKY_TEXTURED,
    TERRAIN_SOLID,
    TERRAIN_CUTOUT_MIPPED,
    TERRAIN_CUTOUT,
    DAMAGED_BLOCK,
    ENTITIES,
    ENTITIES_GLOWING,
    BLOCK_ENTITIES,
    BEACON_BEAM,
    ARMOR_GLINT,
    SPIDER_EYES,
    LEASH,
    PARTICLES_LIT,
    PARTICLES_UNLIT,
    WORLD_BORDER,
    CLOUDS,
    WEATHER,
    TERRAIN_TRANSLUCENT,
    HAND_SOLID,
    HAND_TRANSLUCENT,
    FIRST_PERSON_OVERLAY
}
