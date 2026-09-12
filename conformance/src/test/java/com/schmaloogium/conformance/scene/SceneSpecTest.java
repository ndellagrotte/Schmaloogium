// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.scene;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

/** SceneParserTest + SceneParserRejectionTest + SceneRoundTripTest + SceneCorpusTest (§8.1). */
public class SceneSpecTest {

    static final Path SCENES = Path.of("..", "conformance", "scenes").toAbsolutePath().normalize();

    public static String terrainDay() throws IOException {
        return Files.readString(SCENES.resolve("terrain-day.scene"), StandardCharsets.UTF_8);
    }

    @Test
    void everyFieldParsesToItsType() throws IOException {
        SceneSpec s = SceneParser.parse(terrainDay());
        assertEquals("terrain-day", s.id());
        assertEquals("terrain", s.family());
        assertEquals(20260912L, s.world().seed());
        assertEquals("default", s.world().worldType());
        assertTrue(s.world().generateStructures());
        assertEquals(6000L, s.world().time());
        assertEquals("false", s.world().gamerules().get("doDaylightCycle"));
        assertEquals(854, s.client().width());
        assertEquals(70.0, s.client().fov());
        assertTrue(s.client().hideGui());
        assertFalse(s.client().viewBobbing());
        assertEquals("mp-pingpong@1.0.0", s.pack().shaderpack());
        assertEquals(2, s.captures().size());
        assertTrue(s.captures().get(0) instanceof SceneSpec.Shot);
        assertTrue(s.captures().get(1) instanceof SceneSpec.Path);
        SceneSpec.Shot shot = (SceneSpec.Shot) s.captures().get(0);
        assertEquals(2, shot.captureFrames());
        assertEquals(2, shot.samples().size());
        assertEquals(new SceneSpec.Pose(-211.5, 75.0, 240.5, 45.0, 15.0), shot.pose());
        SceneSpec.Path path = (SceneSpec.Path) s.captures().get(1);
        assertEquals(4, path.samples().size());
        assertEquals(1, path.captureStartSample());
        assertEquals(2, path.captureSampleCount());
    }

    @Test
    void rejectionsNameTheRule() throws IOException {
        String base = terrainDay();
        assertMessage(base.replace("prepTicks = 200", "prepTicks = 200\nwheather = rain"), "unknown key wheather");
        assertMessage(base.replace("seed = 20260912\n", ""), "missing required key seed");
        assertMessage(base.replace("[shot main]", "[shot terrain-pan]"), "duplicate capture name");
        assertMessage(base.replace("pos = -211.5 75.0 240.5\nlook = 45.0 15.0\nwarmupFrames = 4800\ncaptureFrames = 2",
            "warmupFrames = 4800\ncaptureFrames = 2"), "missing required key");
        assertMessage(base.replace("captureStartSample = 1\ncaptureSampleCount = 2",
            "captureStartSample = 3\ncaptureSampleCount = 2"), "capture window");
        assertMessage(base.replace("gamerule.doFireTick = false\n", ""), "mandatory gamerule unset");
        assertThrows(IllegalArgumentException.class, () -> SceneValidator.validate(SceneParser.parse(base),
            "other-name"));
        // warm-up below the family floor
        assertThrows(IllegalArgumentException.class, () -> SceneValidator.validate(SceneParser.parse(base),
            "terrain-day", 5000, null));
        // unknown engine.* key without an inventory is refused, not accepted silently
        assertMessage(base.replace("shaderpack = mp-pingpong@1.0.0", "shaderpack = mp-pingpong@1.0.0\nengine.foo = 1"),
            "unvalidated");
        // a path whose window has no motion
        assertMessage(base.replace("sample.2.look = 50.0 15.0", "sample.2.look = 45.0 15.0"), "non-zero pose delta");
        // wrong schema
        assertThrows(IllegalArgumentException.class, () -> SceneParser.parse(base.replace("scene/2", "scene/1")));
    }

    private static void assertMessage(String text, String fragment) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            SceneSpec scene = SceneParser.parse(text);
            SceneValidator.validate(scene, "terrain-day");
        });
        assertTrue(e.getMessage().contains(fragment), "expected '" + fragment + "' in: " + e.getMessage());
    }

    @Test
    void writeThenParseIsIdentityAndByteStable() throws IOException {
        SceneSpec parsed = SceneParser.parse(terrainDay());
        String written = SceneWriter.render(parsed);
        SceneSpec again = SceneParser.parse(written);
        assertEquals(parsed, again);
        assertEquals(written, SceneWriter.render(again));
        assertFalse(written.contains("\r"));
    }

    @Test
    void everyCommittedSceneParsesValidatesAndHasAPath() throws IOException {
        List<String> ids = List.of("terrain-day", "water-translucent", "night-shadows", "weather-rain",
            "hand-item", "entities-blocks");
        for (String id : ids) {
            Path file = SCENES.resolve(id + ".scene");
            assertTrue(Files.isRegularFile(file), "missing scene " + id);
            SceneSpec scene = SceneParser.parse(Files.readString(file, StandardCharsets.UTF_8));
            SceneValidator.validate(scene, id);
            assertTrue(scene.captures().stream().anyMatch(c -> c instanceof SceneSpec.Path),
                id + " needs at least one valid [path] (§4.3.3)");
        }
    }

    @Test
    void warmupFloorFollowsTheHalflifeRule() {
        assertEquals(60, SceneValidator.warmupFloor(1.0, 1));
        assertEquals(4800, SceneValidator.warmupFloor(600.0, 1));
        assertEquals(2400, SceneValidator.warmupFloor(600.0, 2));
    }
}
