// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.dispatch;

import com.schmaloogium.engine.registry.ProgramSlotId;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The immutable section → slot routing table (PHASE_7_DOC §5.1): every catalogued vanilla
 * moment maps onto exactly one requested program slot (the classic program name) plus its
 * legal frame window. Missing legal slots are never removed here — Phase 4's fixed/skip
 * terminal answers them at selection time. No hidden string fallback: the table is
 * constructed from the closed section set, and lookups of mapped sections always yield a
 * slot.
 */
public final class PhaseDispatchTable {

    private final Map<RenderSection, Routing> routes;

    private PhaseDispatchTable(Map<RenderSection, Routing> routes) {
        this.routes = Map.copyOf(routes);
    }

    /** The classic-shader routing table for the whole §4.10 catalog. */
    public static PhaseDispatchTable classic() {
        Map<RenderSection, Routing> routes = new EnumMap<>(RenderSection.class);
        routes.put(RenderSection.SKY_BASIC, routing("gbuffers_skybasic", SectionWindow.ESTATE_CLEARED));
        routes.put(RenderSection.SKY_TEXTURED, routing("gbuffers_skytextured", SectionWindow.ESTATE_CLEARED));
        routes.put(RenderSection.CLOUDS, routing("gbuffers_clouds", SectionWindow.ESTATE_CLEARED));
        routes.put(RenderSection.TERRAIN_SOLID, routing("gbuffers_terrain_solid", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.TERRAIN_CUTOUT_MIPPED, routing("gbuffers_terrain_cutout_mip", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.TERRAIN_CUTOUT, routing("gbuffers_terrain_cutout", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.DAMAGED_BLOCK, routing("gbuffers_damagedblock", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.ENTITIES, routing("gbuffers_entities", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.ENTITIES_GLOWING, routing("gbuffers_entities_glowing", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.BLOCK_ENTITIES, routing("gbuffers_block", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.BEACON_BEAM, routing("gbuffers_beaconbeam", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.ARMOR_GLINT, routing("gbuffers_armor_glint", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.SPIDER_EYES, routing("gbuffers_spidereyes", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.LEASH, routing("gbuffers_leash", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.PARTICLES_UNLIT, routing("gbuffers_textured", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.PARTICLES_LIT, routing("gbuffers_textured_lit", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.WORLD_BORDER, routing("gbuffers_textured_lit", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.WEATHER, routing("gbuffers_weather", SectionWindow.GBUFFERS_OPAQUE));
        routes.put(RenderSection.TERRAIN_TRANSLUCENT, routing("gbuffers_water", SectionWindow.TRANSLUCENT_TRIGGER));
        routes.put(RenderSection.HAND_SOLID, routing("gbuffers_hand", SectionWindow.GBUFFERS_TRANSLUCENT));
        routes.put(RenderSection.HAND_TRANSLUCENT, routing("gbuffers_water", SectionWindow.GBUFFERS_TRANSLUCENT));
        routes.put(RenderSection.FIRST_PERSON_OVERLAY, routing("gbuffers_textured_lit", SectionWindow.GBUFFERS_TRANSLUCENT));
        return new PhaseDispatchTable(routes);
    }

    private static Routing routing(String slot, SectionWindow window) {
        return new Routing(new ProgramSlotId(slot), window);
    }

    /** The route for a catalogued section; total over {@link RenderSection}. */
    public Optional<Routing> route(RenderSection section) {
        Objects.requireNonNull(section, "section");
        return Optional.ofNullable(routes.get(section));
    }

    /** One section's routing: requested slot plus legal window. Immutable. */
    public record Routing(ProgramSlotId requested, SectionWindow window) {

        public Routing {
            Objects.requireNonNull(requested, "requested");
            Objects.requireNonNull(window, "window");
        }
    }
}
