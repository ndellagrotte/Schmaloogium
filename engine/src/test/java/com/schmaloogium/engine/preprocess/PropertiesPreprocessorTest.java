// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Properties-safe preprocessing vectors: joining, hash protection, directives. */
class PropertiesPreprocessorTest {

    @Test
    void plainLinesPassThroughUnchanged() {
        String in = "screen=<empty>\nsliders=\n";
        String out = PropertiesPreprocessor.preprocess(in, Map.of());
        assertTrue(out.startsWith(in));
        assertTrue(out.lines().toList().size() >= 2);
    }

    @Test
    void backslashJoinsPhysicalLines() {
        String out = PropertiesPreprocessor.preprocess("alpha=one \\\ntwo\n", Map.of());
        assertFalse(out.contains("\\\n"));
        assertTrue(out.contains("one two"));
    }

    @Test
    void dataLineHashSurvivesPreprocessing() {
        String in = "key=value # keep me\n";
        String out = PropertiesPreprocessor.preprocess(in, Map.of());
        assertTrue(out.contains("# keep me"));
        assertTrue(out.contains("key=value"));
    }

    @Test
    void ifDefDirectiveFiltersLines() {
        String in = String.join("\n",
            "#ifdef FANCY",
            "fancyOption=true",
            "#endif",
            "#ifndef FANCY",
            "fancyOption=false",
            "#endif",
            "");
        String with = PropertiesPreprocessor.preprocess(in, Map.of("FANCY", "1"));
        assertTrue(with.contains("fancyOption=true"));
        assertFalse(with.contains("fancyOption=false"));
        String without = PropertiesPreprocessor.preprocess(in, Map.of());
        assertTrue(without.contains("fancyOption=false"));
    }

    @Test
    void mcVersionMacroComparesNumerically() {
        String in = String.join("\n",
            "#if MC_VERSION >= 11800",
            "newBehavior=true",
            "#else",
            "newBehavior=false",
            "#endif",
            "");
        assertTrue(PropertiesPreprocessor.preprocess(in, Map.of("MC_VERSION", "11904"))
            .contains("newBehavior=true"));
        assertTrue(PropertiesPreprocessor.preprocess(in, Map.of("MC_VERSION", "11202"))
            .contains("newBehavior=false"));
    }

    @Test
    void crlfNumericGuardsMatchLfWithUndefinedAndSelectedMacros() {
        String lf = "header=value\n"
            + "    #if QUALITY_LEVEL == -1\n"
            + "program.composite.enabled=false\n"
            + "    #endif\n"
            + "    #if SECOND_LEVEL == -1 || THIRD_LEVEL == -1\n"
            + "program.composite1.enabled=false\n"
            + "    #endif\n"
            + "survivor=value # retained\n";
        String in = lf.replace("\n", "\r\n");
        String undefined = PropertiesPreprocessor.preprocess(in, Map.of());
        assertFalse(undefined.contains("program.composite.enabled"));
        assertFalse(undefined.contains("program.composite1.enabled"));
        assertTrue(undefined.contains("survivor=value # retained"));
        assertEquals(PropertiesPreprocessor.preprocess(lf, Map.of()), undefined);
        String selected = PropertiesPreprocessor.preprocess(in, Map.of("QUALITY_LEVEL", "-1"));
        assertTrue(selected.contains("program.composite.enabled=false"));
        assertFalse(selected.contains("program.composite1.enabled"));
        assertEquals(PropertiesPreprocessor.preprocess(lf, Map.of("QUALITY_LEVEL", "-1")), selected);
    }

    @Test
    void malformedDirectiveIsIgnoredLineLocally() {
        String in = "key=value\n#if(((broken\nother=1\n";
        String out = PropertiesPreprocessor.preprocess(in, Map.of());
        assertTrue(out.contains("key=value"));
        assertTrue(out.contains("other=1"));
    }
}
