// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.scene;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.conformance.golden.GoldenUpdatePolicy;
import com.schmaloogium.conformance.wire.Hashes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

/** CapturePlanTest (§8.1): schema, defaults resolved, provenance byte-covered, fixture round trip. */
class CapturePlanTest {

    static final String H64 = "3f7a1c9e2b8d4f60a5c3e71d9b0f4a2c6e8d13579bdf2468ace013579bdf2468";
    static final Path FIXTURE = Path.of("src", "test", "resources", "wire", "capture-plan-v4.plan");

    static CapturePlan plan(String archiveSha512, String licence) throws IOException {
        SceneSpec scene = SceneParser.parse(SceneSpecTest.terrainDay());
        TreeMap<String, String> mods = new TreeMap<>();
        mods.put("schmaloogium", H64);
        mods.put("forge", H64);
        return CapturePlanWriter.write("RUN-T0-terrain-day-fixture", scene, H64, scene.captures(),
            new CapturePlanWriter.FixtureFacts("mp-pingpong", "1.0.0", "MANUAL", archiveSha512, licence),
            Map.of("SHADOWS", "true"), Map.of("renderResMul", "1.0"),
            new ClockProfile("default", 50_000_000L, 1, 0.0),
            new CapturePlanWriter.Environment("schmaloogium", H64, H64, H64, "1.12.2", mods),
            new CapturePlanWriter.WorldFacts("saves/RUN-T0-terrain-day-fixture", H64, H64));
    }

    @Test
    void producesTheSchemaWithEverythingResolved() throws IOException {
        CapturePlan plan = plan(H64 + H64, "GPL-3.0-or-later");
        String text = plan.render();
        assertTrue(text.startsWith(CapturePlan.SCHEMA_LINE + "\n"));
        assertTrue(text.contains("client.hideGui = true\n"));
        assertTrue(text.contains("captures.0.kind = SHOT\n"));
        assertTrue(text.contains("captures.0.samples.count = 2\n"));
        assertTrue(text.contains("captures.1.kind = PATH\n"));
        assertTrue(text.contains("captures.1.samples.3.look.yaw = 55.0\n"));
        assertTrue(text.contains("pack.optionStateSha256 = "
            + OptionStateDigest.sha256(Map.of("SHADOWS", "true"), Map.of("renderResMul", "1.0")) + "\n"));
        assertEquals(200 + 4800 + 2 + 4800 + 4, plan.totalSteps());
        CapturePlan reparsed = CapturePlanReader.parse(text);
        assertEquals(text, reparsed.render());
        assertEquals(plan.planHash(), reparsed.planHash());
    }

    @Test
    void anyProvenanceByteChangesThePlanHash() throws IOException {
        String a = plan(H64 + H64, "GPL-3.0-or-later").planHash();
        String b = plan(H64 + H64.replace('3', '4'), "GPL-3.0-or-later").planHash();
        String c = plan(H64 + H64, "GPL-3.0-only").planHash();
        assertNotEquals(a, b);
        assertNotEquals(a, c);
    }

    @Test
    void malformedInputsAbort() throws IOException {
        String text = plan(H64 + H64, "GPL-3.0-or-later").render();
        assertThrows(IllegalArgumentException.class, () -> CapturePlanReader.parse(text.replace(
            "captures.1.samples.2.pos.x = -211.5\n", "")));
        assertThrows(IllegalArgumentException.class, () -> CapturePlanReader.parse(text.replace(
            "clock.ticksPerFrame = 1", "clock.ticksPerFrame = 0")));
        assertThrows(IllegalArgumentException.class, () -> CapturePlanReader.parse(text.replace(
            "capture-plan/4", "capture-plan/3")));
        assertThrows(IllegalArgumentException.class, () -> CapturePlanReader.parse(text
            + "zzz.unknown = 1\n"));
        assertThrows(IllegalArgumentException.class, () -> CapturePlanReader.parse(text.replace(
            "pack.acquisitionMode = MANUAL", "pack.acquisitionMode = LATEST")));
        assertThrows(IllegalArgumentException.class, () -> CapturePlanReader.parse(text.replace(
            "captures.0.captureSampleCount = 2", "captures.0.captureSampleCount = 3")));
        assertThrows(IllegalArgumentException.class, () -> new CapturePlanWriter.FixtureFacts("x", "1",
            "MANUAL", "abc", "lic"));
    }

    @Test
    void committedFixtureRoundTripsByteIdentically() throws IOException {
        String produced = plan(H64 + H64, "GPL-3.0-or-later").render();
        if (GoldenUpdatePolicy.fromSystemProperty() == GoldenUpdatePolicy.UPDATE_AND_FAIL) {
            Files.createDirectories(FIXTURE.getParent());
            Files.writeString(FIXTURE, produced, StandardCharsets.UTF_8);
        }
        assertTrue(Files.isRegularFile(FIXTURE), "missing " + FIXTURE + " (generate with -PupdateGoldens)");
        String committed = Files.readString(FIXTURE, StandardCharsets.UTF_8);
        assertEquals(committed, CapturePlanReader.parse(committed).render());
        assertEquals(committed, produced, "the committed capture-plan fixture drifted from the writer");
        assertEquals(Hashes.sha256HexOf(committed), CapturePlanReader.parse(committed).planHash());
    }

    @Test
    void optionStateDigestIsOrderAndEncodingStable() {
        TreeMap<String, String> a = new TreeMap<>(Map.of("b", "2", "a", "1"));
        Map<String, String> b = new java.util.LinkedHashMap<>();
        b.put("b", "2");
        b.put("a", "1");
        assertEquals(OptionStateDigest.sha256(a, Map.of()), OptionStateDigest.sha256(b, Map.of()));
        assertNotEquals(OptionStateDigest.sha256(a, Map.of()), OptionStateDigest.sha256(a, Map.of("x", "y")));
        assertTrue(OptionStateDigest.canonicalText(a, Map.of()).startsWith(OptionStateDigest.SCHEMA_LINE + "\n"));
    }
}
