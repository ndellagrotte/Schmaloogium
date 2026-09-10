// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.IdMappingFileInput;
import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.config.MappingEra;
import com.schmaloogium.engine.config.MappingFileState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** §8.1 tests 4, 5, 6: modern alias catalog, ambiguous-era provenance, 11300 retry. */
class IdEraAliasTest {

    @Test
    void modernAliasCatalog_liveTargetsAndFluidPairs() {
        // Every §4.6 seed against the scripted live registry, plus one fluid group:
        // a rule naming the still token assigns both live still and flowing entries.
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings(
                        "100=grass_block\n101=short_grass\n102=tall_grass\n103=dead_bush\n"
                                + "104=sugar_cane\n105=lily_pad\n106=cobweb\n107=water\n",
                        "", "", "", IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                CompatibilityAliasCatalog.v0_3(), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(100, f.stateId(f.stateOrdinal("grass", 0)), "grass_block alias");
            assertEquals(101, f.stateId(f.stateOrdinal("tallgrass", 0)), "short_grass alias");
            assertEquals(101, f.stateId(f.stateOrdinal("tallgrass", 2)), "short_grass alias");
            assertEquals(102, f.stateId(f.stateOrdinal("double_plant", 0)), "tall_grass alias");
            assertEquals(102, f.stateId(f.stateOrdinal("double_plant", 1)),
                    "no metadata means all states");
            assertEquals(103, f.stateId(f.stateOrdinal("deadbush", 0)), "dead_bush alias");
            assertEquals(104, f.stateId(f.stateOrdinal("reeds", 3)), "sugar_cane alias");
            assertEquals(105, f.stateId(f.stateOrdinal("waterlily", 0)), "lily_pad alias");
            assertEquals(106, f.stateId(f.stateOrdinal("web", 0)), "cobweb alias");
            assertEquals(107, f.stateId(f.stateOrdinal("water", 0)), "fluid still token");
            assertEquals(107, f.stateId(f.stateOrdinal("flowing_water", 0)),
                    "fluid counterpart");
        }
    }

    @Test
    void modernAliasCatalog_missingTargetDisablesOnlyThatPair() {
        // waterlily is absent from this registry; grass_block still expands while the
        // lily_pad pair is disabled with exactly one diagnostic.
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings("100=grass_block\n105=lily_pad\n", "", "", "",
                        IdFixtures.packOrigin()),
                IdFixtures.vanilla(false), ModIdSourceSnapshot.empty(),
                CompatibilityAliasCatalog.v0_3(), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(100, f.stateId(f.stateOrdinal("grass", 0)));
            assertEquals(1, f.reporter().withKey(
                    "schmaloogium.warn.ids.missing_alias_target").size());
            assertEquals(1, f.view().blockAliasAssignments());
        }
    }

    @Test
    void ambiguousGrassAndLamp_requireModernEraProvenance() {
        // Real P3 bytes: rules guarded for modern versions only — the isolated MODERN
        // alternate is selected, so grass means tallgrass and the lamp means lit_lamp.
        String guarded = "#if MC_VERSION >= 11300\n"
                + "100=minecraft:grass\n101=minecraft:redstone_lamp\n"
                + "#endif\n";
        assertEquals(MappingFileState.PRESENT_EMPTY,
                IdFixtures.parsePack(MappingKind.BLOCK, guarded, IdFixtures.packOrigin())
                        .state());
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                packWithBlock(guarded), IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                CompatibilityAliasCatalog.v0_3(), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(100, f.stateId(f.stateOrdinal("tallgrass", 0)));
            assertEquals(101, f.stateId(f.stateOrdinal("lit_redstone_lamp", 0)));
        }
        // Unguarded classic bytes keep their 1.12 exact meanings — the ambiguous
        // modern names never rewrite a CLASSIC-provenance rule.
        String classic = "100=minecraft:grass\n101=minecraft:redstone_lamp\n"
                + "102=minecraft:water\n";
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                packWithBlock(classic), IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                CompatibilityAliasCatalog.v0_3(), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(100, f.stateId(f.stateOrdinal("grass", 0)));
            assertEquals(101, f.stateId(f.stateOrdinal("redstone_lamp", 0)));
            assertEquals(102, f.stateId(f.stateOrdinal("water", 0)));
        }
    }

    @Test
    void blockAndEntity11300Retry_presentEmptyOnly() {
        // Ordinary nonempty + alternate nonempty: ordinary only, never merged.
        String mixed = "30=creeper\n#if MC_VERSION >= 11300\n31=minecraft:sheep\n#endif\n";
        IdMappingFileInput file = IdFixtures.parsePack(MappingKind.ENTITY, mixed,
                IdFixtures.packOrigin());
        assertEquals(MappingFileState.PRESENT_RULES, file.state());
        assertEquals(1, file.ordinaryRules().size());
        // The alternate is the WHOLE file re-interpreted under MC_VERSION=11300.
        assertEquals(2, file.forced11300Rules().size());
        assertEquals(MappingEra.MODERN, file.forced11300Rules().get(0).era());
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                packWithEntity(mixed), IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(30, f.entityId("creeper"));
            assertEquals(1, f.view().entityAliasAssignments(),
                    "the alternate matching more must never leak in");
        }
        // PRESENT_EMPTY with empty alternate contributes nothing.
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                packWithEntity("# nothing here\n"), IdFixtures.vanilla(),
                ModIdSourceSnapshot.empty(), new CompatibilityAliasCatalog(1, List.of()),
                LegacyTagCatalog.empty(), HandLightPolicy.allDefault())) {
            assertEquals(0, f.view().entityAliasAssignments());
        }
        // PRESENT_EMPTY with a nonempty alternate selects the alternate.
        String guarded = "#if MC_VERSION >= 11300\n31=minecraft:sheep\n#endif\n";
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                packWithEntity(guarded), IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(31, f.entityId("sheep"));
        }
        // The fingerprint discriminates ordinary-selected from forced-selected builds.
        IdRuntimeCandidate ordinary = PublishedHarness.buildOnly(packWithEntity(mixed),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault(), new IdFixtures.RecordingReporter());
        IdRuntimeCandidate forced = PublishedHarness.buildOnly(packWithEntity(guarded),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault(), new IdFixtures.RecordingReporter());
        assertFalse(ordinary.view().sourceFingerprint().value()
                .equals(forced.view().sourceFingerprint().value()),
                "selection discriminator must differ");
        ordinary.close();
        forced.close();
    }

    // ------------------------------------------------------------------ helpers

    private static IdMappingInput packWithBlock(String blocks) {
        return IdFixtures.packMappings(blocks, "", "", "", IdFixtures.packOrigin());
    }

    private static IdMappingInput packWithEntity(String entities) {
        return IdFixtures.packMappings("", "", entities, "", IdFixtures.packOrigin());
    }
}
