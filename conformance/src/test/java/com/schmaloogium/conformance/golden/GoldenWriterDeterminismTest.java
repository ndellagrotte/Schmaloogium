// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.golden;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The golden document format (§4.11.4 + D-P2-5): fixed header, fixed section order,
 * byte-deterministic render under locale variation, strict reparse.
 */
class GoldenWriterDeterminismTest {

    private static final String HEADER_LINE =
        "# schmaloogium golden \u00b7 kind=frontend \u00b7 schema=1";

    private static GoldenDocument sample() {
        GoldenDocument doc = GoldenDocument.create(Map.of(
            "pack.id", "\"mp-minimal\"",
            "pack.version", "\"1.0.0\"",
            "engine.version", "\"0.1.0-dev\"",
            "projection.version", "1",
            "pack.sha512", "\"" + "a".repeat(128) + "\"",
            "input.profile", "synthetic-gl33",
            "input.schema", "23"));
        doc.section("sources", Map.of(
            "shaders/gbuffers_terrain.fsh.sha256", "b".repeat(64),
            "shaders/gbuffers_terrain.vsh.sha256", "c".repeat(64)));
        doc.section("programs", Map.of(
            "gbuffers_terrain", "\"SOURCED\""));
        doc.section("sizing", Map.of(
            "shadowMapResolution", "1024"));
        doc.section("options", Map.of(
            "SHADOWS", "true"));
        doc.section("properties", Map.of(
            "clouds", "\"on\""));
        doc.section("macros", Map.of());
        doc.section("diagnostics", Map.of());
        return doc;
    }

    @Test
    void renderIsByteIdenticalAcrossLocales() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ITALY);
            String inItaly = sample().render();
            Locale.setDefault(Locale.US);
            String inUs = sample().render();
            assertEquals(inUs, inItaly);
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void renderHasNoTimestampsAndFixedSectionOrder() {
        String rendered = sample().render();
        assertTrue(rendered.startsWith(HEADER_LINE + "\n"), rendered);
        int sources = rendered.indexOf("[sources]");
        int programs = rendered.indexOf("[programs]");
        int sizing = rendered.indexOf("[sizing]");
        assertTrue(sources >= 0 && programs > sources && sizing > programs);
    }

    @Test
    void emptySectionsAreNotEmitted() {
        String rendered = sample().render();
        assertEquals(-1, rendered.indexOf("[macros]"));
        assertEquals(-1, rendered.indexOf("[diagnostics]"));
    }

    @Test
    void iterationOrderOfInputMapsDoesNotMatter() {
        Map<String, String> rows = new LinkedHashMap<>();
        rows.put("zeta", "1");
        rows.put("alpha", "2");
        Map<String, String> reversed = new LinkedHashMap<>();
        reversed.put("alpha", "2");
        reversed.put("zeta", "1");
        GoldenDocument a = GoldenDocument.create(Map.of("pack.id", "\"x\""));
        a.section("sources", rows);
        GoldenDocument b = GoldenDocument.create(Map.of("pack.id", "\"x\""));
        b.section("sources", reversed);
        assertEquals(a.render(), b.render());
    }

    @Test
    void parseRejectsBadHeaderUnknownSectionsAndDuplicates() {
        assertThrows(RuntimeException.class,
            () -> GoldenDocument.parse(new StringReader("# no header\n")));
        assertThrows(RuntimeException.class, () -> GoldenDocument.parse(new StringReader(
            HEADER_LINE + "\n\n[unknown]\n")));
        assertThrows(RuntimeException.class, () -> GoldenDocument.parse(new StringReader(
            HEADER_LINE + "\n\nduplicate = 1\nduplicate = 2\n")));
    }

    @Test
    void roundTripIsByteIdentical() throws java.io.IOException {
        String rendered = sample().render();
        GoldenDocument parsed = GoldenDocument.parse(new StringReader(rendered));
        assertEquals(rendered, parsed.render());
    }
}
