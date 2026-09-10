// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.pack.ImmutableBytes;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** D-P3-73 selector grammar, forced-11300 dual run, and minimum-edition ordering. */
class IdMappingAndEditionTest {

    private static IdMappingFileInput parse(MappingKind kind, String content) {
        IdMappingMacroEnvironment env = new IdMappingMacroEnvironment(11202, List.of(
            new MacroDefinition("MC_VERSION", "11202")));
        com.schmaloogium.engine.diag.DiagnosticReporter noop = d -> { };
        IdMappingParseRequest request = new IdMappingParseRequest(kind,
            content == null ? Optional.empty()
                : Optional.of(ImmutableBytes.of(content.getBytes(StandardCharsets.ISO_8859_1))),
            new ModMappingOrigin("testmod", 0, "fixture"), env, noop);
        return new IdMappingParserImpl().parse(request);
    }

    @Test
    void absentFileYieldsAbsentStateWithFingerprint() {
        IdMappingFileInput result = parse(MappingKind.BLOCK, null);
        assertEquals(MappingFileState.ABSENT, result.state());
        assertFalse(result.fingerprint().value().isEmpty());
    }

    @Test
    void entriesWithMetadataAndPropertiesParse() {
        IdMappingFileInput result = parse(MappingKind.BLOCK,
            "1001=minecraft:stone\n"
            + "1002=minecraft:repeater:2-4 minecraft:comparator minecraft:oak_door:half=upper:facing=east\n"
            + "1003=%minecraft:logs\n");
        assertEquals(MappingFileState.PRESENT_RULES, result.state());
        assertEquals(5, result.ordinaryRules().size());
        // metadata selector
        IdRule repeater = result.ordinaryRules().stream()
            .filter(IdRule.class::isInstance).map(IdRule.class::cast)
            .filter(r -> r.shaderId() == 1002 && r.selectorToken().contains("repeater"))
            .findFirst().orElseThrow();
        assertTrue(repeater.legacyMetadata().isPresent());
        assertEquals(SelectorKind.ENTRY, repeater.selectorKind());
        // tag selector with implicit namespace
        IdRule tag = result.ordinaryRules().stream()
            .filter(IdRule.class::isInstance).map(IdRule.class::cast)
            .filter(r -> r.shaderId() == 1003).findFirst().orElseThrow();
        assertEquals(SelectorKind.TAG, tag.selectorKind());
        assertEquals("minecraft:logs", tag.selectorToken());
        // property predicates split into one rule per alternative
        List<IdRule> door = result.ordinaryRules().stream()
            .filter(IdRule.class::isInstance).map(IdRule.class::cast)
            .filter(r -> r.selectorToken().contains("oak_door")).toList();
        assertEquals(1, door.size());
        assertEquals(2, door.get(0).propertyPredicates().size());
        PropertyPredicate facing = door.get(0).propertyPredicates().get(1);
        assertEquals("facing", facing.propertyName());
    }

    @Test
    void numericIdentityIsLegacyNotNamespace() {
        IdMappingFileInput result = parse(MappingKind.BLOCK, "1004=1:0 2\n");
        assertEquals(2, result.ordinaryRules().size());
        assertTrue(result.ordinaryRules().stream()
            .allMatch(r -> r.selectorKind() == SelectorKind.ENTRY));
    }

    @Test
    void blockAndEntityGetForcedModernRun() {
        IdMappingFileInput blocks = parse(MappingKind.BLOCK, "1001=minecraft:stone\n");
        assertEquals(1, blocks.forced11300Rules().size());
        assertEquals(MappingEra.MODERN, blocks.forced11300Rules().get(0).era());
        assertEquals(MappingEra.CLASSIC, blocks.ordinaryRules().get(0).era());
        IdMappingFileInput layers = parse(MappingKind.LAYER,
            "layer.translucent=minecraft:water\n");
        assertTrue(layers.forced11300Rules().isEmpty());
        assertEquals(1, layers.ordinaryRules().size());
        assertEquals(RequestedRenderLayer.TRANSLUCENT,
            ((LayerRule) layers.ordinaryRules().get(0)).layer());
    }

    @Test
    void invalidSelectorsAreSkippedLineLocally() {
        IdMappingFileInput result = parse(MappingKind.BLOCK,
            "1005=minecraft:%bad minecraft:stone\n");
        assertEquals(1, result.ordinaryRules().size());
        assertEquals("minecraft:stone", result.ordinaryRules().get(0).selectorToken());
    }

    @Test
    void editionsCanonicalizeAndOrder() {
        assertEquals("1.20.1", MinimumEditionRules.canonicalEdition("1.20.1"));
        assertEquals("1.20.1", MinimumEditionRules.canonicalEdition("01.20.01"));
        assertTrue(MinimumEditionRules.compareEditions("1.7.10", "1.20.1") < 0);
        assertEquals(0, MinimumEditionRules.compareEditions("01.020", "1.20"));
        assertTrue(MinimumEditionRules.compareEditions("1.20", "1.20.1") < 0);
        assertNotNull(MinimumEditionRules.parse("version.1.20.1", "sodium-1.20"));
        assertNull(MinimumEditionRules.parse("version.1.20.x", "nope"));
        assertNull(MinimumEditionRules.parse("version.1..2", "nope"));
    }
    @Test
    void inputConstructsWithBlockForcedRulesAndRejectsItemLayerForced() {
        IdMappingMacroEnvironment env = new IdMappingMacroEnvironment(11202, List.of(
            new MacroDefinition("MC_VERSION", "11202")));
        IdMappingFileInput blocks = parse(MappingKind.BLOCK, "1001=minecraft:stone\n");
        assertFalse(blocks.forced11300Rules().isEmpty(),
            "a parsed block.properties must carry its D-P3-72 forced run");
        IdMappingFileInput items = parse(MappingKind.ITEM, "1001=minecraft:stone\n");
        IdMappingFileInput entities = parse(MappingKind.ENTITY, "1001=minecraft:cow\n");
        IdMappingFileInput layers = parse(MappingKind.LAYER, null);

        // a real parsed block.properties with non-empty forced rules must construct
        IdMappingInput input = new IdMappingInput(23, env, blocks, items, entities, layers);
        assertEquals(1, input.blocks().forced11300Rules().size());

        // ITEM/LAYER alternates stay empty per D-P3-72; a forced item rule is rejected
        IdMappingFileInput itemWithForced = new IdMappingFileInput(MappingKind.ITEM,
            MappingFileState.PRESENT_RULES, items.ordinaryRules(), blocks.forced11300Rules(),
            items.fingerprint());
        assertThrows(IllegalArgumentException.class,
            () -> new IdMappingInput(23, env, blocks, itemWithForced, entities, layers));
        IdMappingFileInput layerWithForced = new IdMappingFileInput(MappingKind.LAYER,
            MappingFileState.PRESENT_RULES, layers.ordinaryRules(), blocks.forced11300Rules(),
            layers.fingerprint());
        assertThrows(IllegalArgumentException.class,
            () -> new IdMappingInput(23, env, blocks, items, entities, layerWithForced));
    }
}
