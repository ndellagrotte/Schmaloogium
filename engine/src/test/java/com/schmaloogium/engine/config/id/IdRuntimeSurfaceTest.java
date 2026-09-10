// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.pack.ImmutableBytes;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** §8.1 tests 10, 11, 14: AliasLookup semantics, publication lifecycle, warn-once scope. */
class IdRuntimeSurfaceTest {

    private static ImmutableBytes bytes(String text) {
        return ImmutableBytes.of(text.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Test
    void aliasLookup_exactWordSemantics_fullIntVsMcEntity() {
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings("0=stone\n-5=tallgrass:0\n70000=water\n", "", "", "",
                        IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            AliasLookup lookup = f.runtime().aliases();
            assertEquals(f.runtime().generation(), lookup.generation());
            int stone = f.stateOrdinal("stone", 0);
            // Explicit shader ID zero stays explicit: present with id 0.
            AliasValue zero = lookup.blockId(stone);
            assertTrue(zero.present());
            assertEquals(0, zero.shaderId());
            BlockStampResult stamp = lookup.mcEntity(stone);
            assertTrue(stamp instanceof BlockStampResult.Present);
            assertEquals(0, ((BlockStampResult.Present) stamp).packedRenderTypeAndId() & 0xffff,
                    "explicit zero clears the low word");
            // Negative id is representable: low word is the unsigned pattern.
            AliasValue negative = lookup.blockId(f.stateOrdinal("tallgrass", 0));
            assertTrue(negative.present());
            assertEquals(-5, negative.shaderId());
            assertEquals(0xFFFF & -5,
                    ((BlockStampResult.Present) lookup.mcEntity(
                            f.stateOrdinal("tallgrass", 0))).packedRenderTypeAndId() & 0xffff);
            // Outside the representable low word: full int keeps the value, mc_Entity
            // reports unrepresentable with a zero low word and one diagnostic.
            AliasValue big = lookup.blockId(f.stateOrdinal("water", 0));
            assertTrue(big.present());
            assertEquals(70000, big.shaderId());
            BlockStampResult unrepresentable = lookup.mcEntity(f.stateOrdinal("water", 0));
            assertTrue(unrepresentable instanceof BlockStampResult.Unrepresentable);
            assertEquals(0, ((BlockStampResult.Unrepresentable) unrepresentable)
                    .packedRenderTypeAndZero() & 0xffff);
            assertEquals(1, f.reporter().withKey(
                    "schmaloogium.warn.ids.unrepresentable_alias").size());
            // Out-of-range ordinals never index an array.
            int stateCount = f.view().blockStateCount();
            assertThrows(IndexOutOfBoundsException.class, () -> lookup.blockId(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> lookup.mcEntity(stateCount));
        }
    }

    @Test
    void publicationLifecycle_atomicReplacementRetirementRejection() {
        IdFixtures.RecordingReporter reporter = new IdFixtures.RecordingReporter();
        IdRuntimePublisher publisher = IdRuntimePublisher.create(
                PublishedHarness.NO_SINK, d -> { });
        IdRuntimeCandidate first = PublishedHarness.buildOnly(
                IdFixtures.packMappings("100=stone\n", "", "", "", IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault(), reporter);
        IdPublishResult r1 = publisher.publish(first, new IdPublishContext("stage-1"));
        assertTrue(r1 instanceof IdPublishResult.Published);
        long gen1 = ((IdPublishResult.Published) r1).runtime().generation();
        assertEquals(1, gen1);
        // Double publish of the same (now transferred) candidate is rejected.
        IdPublishResult r1b = publisher.publish(first, new IdPublishContext("stage-1b"));
        assertEquals(IdPublishResult.Rejection.CANDIDATE_ALREADY_PUBLISHED,
                ((IdPublishResult.Rejected) r1b).reason());
        // A closed candidate is rejected and leaves the current publication intact.
        IdRuntimeCandidate closed = PublishedHarness.buildOnly(
                IdFixtures.packMappings("101=stone\n", "", "", "", IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault(), reporter);
        closed.close();
        IdPublishResult r2 = publisher.publish(closed, new IdPublishContext("stage-2"));
        assertEquals(IdPublishResult.Rejection.CANDIDATE_CLOSED,
                ((IdPublishResult.Rejected) r2).reason());
        assertEquals(gen1, publisher.current().orElseThrow().generation());
        // Replacement retires the old generation and installs the new one atomically.
        IdRuntimeCandidate second = PublishedHarness.buildOnly(
                IdFixtures.packMappings("102=stone\n", "", "", "", IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault(), reporter);
        IdPublishResult r3 = publisher.publish(second, new IdPublishContext("stage-3"));
        long gen2 = ((IdPublishResult.Published) r3).runtime().generation();
        assertEquals(gen1 + 1, gen2);
        assertEquals(gen2, publisher.current().orElseThrow().generation());
        // Deactivation retires; a second deactivation is already-inactive.
        assertEquals(IdDeactivateResult.DEACTIVATED,
                publisher.deactivate(new IdPublishContext("stage-4")));
        assertEquals(IdDeactivateResult.ALREADY_INACTIVE,
                publisher.deactivate(new IdPublishContext("stage-5")));
        assertTrue(publisher.current().isEmpty());
    }

    @Test
    void warnOnce_unknownName_repeatsOnlyWhenInputsChange() {
        IdFixtures.RecordingReporter reporter = new IdFixtures.RecordingReporter();
        IdRuntimeBuilder builder = new IdRuntimeBuilderImpl(
                new com.schmaloogium.engine.config.IdMappingParserImpl());
        IdBuildRequest request = new IdBuildRequest(
                IdFixtures.packMappings("100=minecraft:nonexistent\n", "", "", "",
                        IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault(), reporter);
        builder.build(request);
        builder.build(request);
        assertEquals(1, reporter.withKey("schmaloogium.warn.ids.unknown_name").size(),
                "same inputs warn exactly once across rebuilds");
        // A changed registry fingerprint re-opens the warn scope.
        // A changed registry fingerprint re-opens the warn scope: the same unknown
        // name warns again against vanillaWithModBlock (different fp and blocks).
        IdBuildRequest changed = new IdBuildRequest(request.mappings(),
                IdFixtures.vanillaWithModBlock(),
                ModIdSourceSnapshot.empty(), new CompatibilityAliasCatalog(1, List.of()),
                LegacyTagCatalog.empty(), HandLightPolicy.allDefault(), reporter);
        builder.build(changed);
        assertEquals(2, reporter.withKey("schmaloogium.warn.ids.unknown_name").size(),
                "changed inputs may warn again");
    }

    @Test
    void modSource_snapshotCanonicalOrderDuplicateAndKindRules() {
        ModIdSourceSnapshot a = new ModIdSourceSnapshot(List.of(
                new ModSourceEntry("zzz.mod", MappingKind.BLOCK, "b.properties",
                        bytes("100=stone\n"), "fp-z"),
                new ModSourceEntry("aaa.mod", MappingKind.ENTITY, "e.properties",
                        bytes("30=creeper\n"), "fp-a")));
        assertEquals("aaa.mod", a.sources().get(0).modId(), "canonical modid order");
        assertThrows(IllegalArgumentException.class, () -> new ModIdSourceSnapshot(List.of(
                new ModSourceEntry("dup.mod", MappingKind.BLOCK, "b.properties",
                        bytes(""), "fp-1"),
                new ModSourceEntry("dup.mod", MappingKind.BLOCK, "b2.properties",
                        bytes(""), "fp-2"))));
        assertThrows(IllegalArgumentException.class, () -> new ModSourceEntry(
                "dup.mod", MappingKind.LAYER, "layer.properties", bytes(""), "fp"));
        assertThrows(IllegalArgumentException.class, () -> new ModSourceEntry(
                "Bad_Upper", MappingKind.BLOCK, "b.properties", bytes(""), "fp"));
    }
}
