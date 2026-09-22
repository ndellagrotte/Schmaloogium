// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.NormalizedPackPath;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OptionCatalogBuilderTest {

    private static final SourceAttribution FIRST = new SourceAttribution(
        new NormalizedPackPath("shaders/lib/options.glsl"), 1, 1);
    private static final SourceAttribution SECOND = new SourceAttribution(
        new NormalizedPackPath("shaders/lib/other.glsl"), 1, 1);

    private static OptionCatalogBuilder.Raw raw() {
        return new OptionCatalogBuilder.Raw(new LinkedHashMap<>(), new LinkedHashMap<>(),
            new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    private static void scan(OptionCatalogBuilder.Raw raw, String text,
            SourceAttribution at, Set<String> confirmed) {
        OptionCatalogBuilder.scanSwitches(text, at, raw.switchOccurrences(), raw.switchDefaults(),
            raw.switchValues(), raw.tooltips(), confirmed);
        OptionCatalogBuilder.scanConsts(text, at, raw.constOccurrences(), raw.constValues());
    }

    @Test
    void numericDefaultsRetainSpellingAndBracketValuesWithoutPropertyMentions() {
        var raw = raw();
        scan(raw, """
            #define QUALITY -2 // Quality! [-4 0 2]
            #define EXPOSURE .5f // [.25f .5f 1.0f]
            #define SCALE +1e-2
            """, FIRST, Set.of("SCALE"));
        var definitions = OptionCatalogBuilder.build(raw);
        assertEquals(List.of("QUALITY", "EXPOSURE", "SCALE"),
            definitions.stream().map(OptionDefinition::name).toList());
        var quality = definitions.getFirst();
        assertEquals(OptionKind.VARIABLE, quality.kind());
        assertEquals(new TextOptionValue("-2"), quality.defaultValue());
        assertEquals(List.of(new TextOptionValue("-2"), new TextOptionValue("-4"),
            new TextOptionValue("0"), new TextOptionValue("2")), quality.allowedValues());
        assertEquals(new TextOptionValue(".5f"), definitions.get(1).defaultValue());
        assertEquals(List.of(new TextOptionValue(".25f"), new TextOptionValue(".5f"),
            new TextOptionValue("1.0f")), definitions.get(1).allowedValues());
        assertEquals(new TextOptionValue("+1e-2"), definitions.get(2).defaultValue());
    }

    @Test
    void nonOptionMacrosAreRejectedEvenWhenNamedByProperties() {
        var raw = raw();
        scan(raw, """
            #define INTERNAL 42
            #define ALIAS OTHER // [0 1]
            #define EXPRESSION (1 + 2) // [1 2]
            #define FUNCTION(x) x // [0 1]
            #define MALFORMED 1 // [0 other]
            // #define COMMENTED 2 // [1 2]
            #ifdef MISSING
            #endif
            """, FIRST, Set.of("ALIAS", "EXPRESSION", "FUNCTION", "MISSING"));
        assertEquals(List.of(), OptionCatalogBuilder.build(raw));
    }

    @Test
    void duplicateNumericDeclarationsRetainLocationsAndFirstDefault() {
        var raw = raw();
        scan(raw, "#define QUALITY -2 // [-4 -2 0]\n", FIRST, Set.of());
        scan(raw, "#define QUALITY 0 // [0 2]\n", SECOND, Set.of());
        var definitions = OptionCatalogBuilder.build(raw);
        assertEquals(1, definitions.size());
        var quality = definitions.getFirst();
        assertEquals(new TextOptionValue("-2"), quality.defaultValue());
        assertEquals(List.of(FIRST, SECOND), quality.occurrences());
        assertEquals(List.of(new TextOptionValue("-4"), new TextOptionValue("-2"),
            new TextOptionValue("0")), quality.allowedValues());
    }

    @Test
    void switchesAndWhitelistedConstantsRemainDistinctFromVariables() {
        var raw = raw();
        scan(raw, """
            #define ENABLED
            #ifdef ENABLED
            #endif
            // #define DISABLED // Disabled switch
            #ifndef DISABLED
            #endif
            #define EXPLICIT false
            const int shadowMapResolution = 1024;
            const int ordinaryConstant = 7;
            """, FIRST, Set.of("EXPLICIT"));
        var definitions = OptionCatalogBuilder.build(raw);
        assertEquals(List.of("ENABLED", "DISABLED", "EXPLICIT", "shadowMapResolution"),
            definitions.stream().map(OptionDefinition::name).toList());
        assertEquals(OptionKind.SWITCH, definitions.getFirst().kind());
        assertEquals(new BooleanOptionValue(true), definitions.getFirst().defaultValue());
        assertEquals(new BooleanOptionValue(false), definitions.get(1).defaultValue());
        assertEquals(new BooleanOptionValue(false), definitions.get(2).defaultValue());
        assertEquals(OptionKind.CONSTANT, definitions.get(3).kind());
        assertEquals(new TextOptionValue("1024"), definitions.get(3).defaultValue());
    }
}
