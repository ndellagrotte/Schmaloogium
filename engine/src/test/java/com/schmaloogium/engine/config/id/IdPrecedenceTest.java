// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.pack.ImmutableBytes;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** §8.1 tests 2, 3, 7, 9: precedence tiers, mod order, legacy fallback, layer routing. */
class IdPrecedenceTest {

    private static ImmutableBytes bytes(String text) {
        return ImmutableBytes.of(text.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Test
    void precedence_packThenEachMod_entriesBeforeTags_thenFallback_firstWriter() {
        // Pack: entry 100=stone, tag rule 110=%plant_tags (tallgrass+web).
        // modA: entry 100=stone (loses to pack), entry 110=glass (fills an unclaimed cell).
        // modB: entry 100=stone (loses).
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings("100=stone\n110=%minecraft:plant_tags\n",
                        "", "", "", IdFixtures.packOrigin()),
                IdFixtures.vanilla(),
                new ModIdSourceSnapshot(List.of(
                        new ModSourceEntry("mod_a", MappingKind.BLOCK, "block.properties",
                                bytes("100=stone\n110=glass\n"), "fp-a"),
                        new ModSourceEntry("mod_b", MappingKind.BLOCK, "block.properties",
                                bytes("100=stone\n"), "fp-b"))),
                new CompatibilityAliasCatalog(1, List.of()),
                new LegacyTagCatalog(1, java.util.Map.of("minecraft:plant_tags",
                        List.of(new LegacyTagCatalog.Binding("fixture",
                                (key, registries) -> List.of(
                                        new RegistryName("minecraft", "tallgrass"),
                                        new RegistryName("minecraft", "web")))))),
                HandLightPolicy.allDefault())) {
            assertEquals(100, f.stateId(f.stateOrdinal("stone", 0)));
            assertEquals(110, f.stateId(f.stateOrdinal("tallgrass", 0)));
            assertEquals(110, f.stateId(f.stateOrdinal("web", 0)));
            // mod_a's 110 entry legally fills glass: the pack tag never wrote that cell.
            assertEquals(110, f.stateId(f.stateOrdinal("glass", 0)));
            // Two losing rules: mod_a's 100 entry and mod_b's 100 entry (both lose the
            // stone cells to the pack); the 110 cells were written by different rules
            // on different blocks, which is not a conflict.
            List<EngineDiagnostic> conflicts =
                    f.reporter().withKey("schmaloogium.warn.ids.conflict");
            assertEquals(2, conflicts.size(), "two attributed losers expected: " + conflicts);
        }
    }

    @Test
    void modOrder_unicodeModIdStableAcrossEnumerationOrder() {
        ModIdSourceSnapshot permuted = new ModIdSourceSnapshot(List.of(
                new ModSourceEntry("zzz.unicode-b", MappingKind.BLOCK, "b.properties",
                        bytes("100=stone\n"), "fp-z"),
                new ModSourceEntry("aaa.unicode-a", MappingKind.BLOCK, "a.properties",
                        bytes("100=stone\n"), "fp-a")));
        ModIdSourceSnapshot sorted = new ModIdSourceSnapshot(List.of(
                new ModSourceEntry("aaa.unicode-a", MappingKind.BLOCK, "a.properties",
                        bytes("100=stone\n"), "fp-a"),
                new ModSourceEntry("zzz.unicode-b", MappingKind.BLOCK, "b.properties",
                        bytes("100=stone\n"), "fp-z")));
        // Canonical mod order sorts by modid before any rule runs, so permuted loader
        // enumeration yields identical source fingerprints and tables.
        assertEquals(sorted.fingerprint(), permuted.fingerprint());
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings(null, "", "", "", IdFixtures.packOrigin()),
                IdFixtures.vanilla(), permuted, new CompatibilityAliasCatalog(1, List.of()),
                LegacyTagCatalog.empty(), HandLightPolicy.allDefault())) {
            // aaa's entry assigned 100; zzz's entry lost with one attributed conflict;
            // the absent pack file activated the numeric fallback for the remaining
            // vanilla states (35 total states in the fixture).
            assertEquals(100, f.stateId(f.stateOrdinal("stone", 0)));
            assertEquals(36, f.view().blockAliasAssignments());
            assertEquals(1, f.reporter().withKey("schmaloogium.warn.ids.conflict").size());
        }
    }

    @Test
    void legacyFallback_absentOnly_modBeforeFallback_vanillaOnly() {
        // No pack block file at all: vanilla blocks fall back to live numeric ids,
        // mod-owned blocks stay absent unless a mod rule assigns them.
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings(null, "10=stick\n", "", "", IdFixtures.packOrigin()),
                IdFixtures.vanillaWithModBlock(),
                new ModIdSourceSnapshot(List.of(new ModSourceEntry("modrand",
                        MappingKind.BLOCK, "block.properties",
                        bytes("700=modrand:custom_ore\n"), "fp-rand"))),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(1, f.stateId(f.stateOrdinal("stone", 0)));
            assertEquals(31, f.stateId(f.stateOrdinal("tallgrass", 0)));
            // Tier 3 mod entry beats the tier 4 numeric fallback for the mod block.
            assertEquals(700, f.stateId(f.stateOrdinal("custom_ore", 0)));
            assertEquals(10, f.itemId("stick"));
            assertEquals(1, f.view().itemAliasAssignments());
        }
        // PRESENT_EMPTY (comment-only file) does NOT activate the fallback.
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings("# empty\n", "10=stick\n", "", "",
                        IdFixtures.packOrigin()),
                IdFixtures.vanillaWithModBlock(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertThrows(IllegalStateException.class,
                    () -> f.stateId(f.stateOrdinal("stone", 0)));
        }
    }

    @Test
    void layerRules_allFourAndOpaqueSolidExcluded() {
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings("100=stone\n", "", "",
                        "layer.solid=stone\nlayer.cutout=glass\n"
                                + "layer.cutout_mipped=tallgrass\nlayer.translucent=water\n"
                                + "layer.cutout=minecraft:oak_door:0\n",
                        IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(Optional.empty(),
                    f.runtime().renderLayers().layer(f.stateOrdinal("stone", 0)),
                    "solid opaque cube excluded even from layer.solid");
            assertEquals(ResolvedRenderLayer.CUTOUT,
                    f.runtime().renderLayers().layer(f.stateOrdinal("glass", 0)).orElseThrow());
            assertEquals(ResolvedRenderLayer.CUTOUT_MIPPED,
                    f.runtime().renderLayers().layer(f.stateOrdinal("tallgrass", 0)).orElseThrow());
            assertEquals(ResolvedRenderLayer.TRANSLUCENT,
                    f.runtime().renderLayers().layer(f.stateOrdinal("water", 0)).orElseThrow());
            assertEquals(ResolvedRenderLayer.CUTOUT,
                    f.runtime().renderLayers().layer(f.stateOrdinal("oak_door", 0)).orElseThrow());
            assertEquals(6, f.view().layerAssignments()); // tallgrass contributes 3 states
            assertTrue(f.reporter().withKey("schmaloogium.warn.ids.layer_opaque_excluded").size()
                    >= 1, "opaque exclusion must warn");
        }
    }

    @Test
    void invalidPredicateMatchesNothing_andWarnsOncePerKey() {
        // Unknown literal in the alternatives: the whole selector assigns nothing.
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings("100=minecraft:tallgrass:type=fern,crystal\n", "", "", "",
                        IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(0, f.view().blockAliasAssignments());
            assertEquals(1, f.reporter().withKey("schmaloogium.warn.ids.invalid_predicate").size());
        }
        // Disjoint interval on a numeric property domain: nothing, one warning.
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings("100=minecraft:reeds:age=5-9\n", "", "", "",
                        IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(0, f.view().blockAliasAssignments());
            assertEquals(1, f.reporter().withKey("schmaloogium.warn.ids.invalid_predicate").size());
        }
        // Sparse intersecting interval is legal: cake only exposes bites 0 and 3.
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings("100=minecraft:cake:bites=0-3\n", "", "", "",
                        IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(2, f.view().blockAliasAssignments());
            assertEquals(0, f.reporter().withKey("schmaloogium.warn.ids.invalid_predicate").size());
        }
    }
}
