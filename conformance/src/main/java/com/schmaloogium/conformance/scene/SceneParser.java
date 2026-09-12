// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.scene;

import com.schmaloogium.conformance.wire.SectionedText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Parses the §4.3.1 grammar into a {@link SceneSpec}. Every unknown key is an error; every
 * missing required key is an error naming the section and key; the only defaults are the
 * ones §4.3.2 states ({@code hideGui = true}, the seven window/motion booleans {@code false},
 * {@code captureFrames = 1}, empty held items and notes). Sections must appear at most once
 * except {@code [shot]}/{@code [path]}; capture blocks keep file order.
 */
public final class SceneParser {

    private static final Set<String> SCENE_KEYS = Set.of("schema", "id", "description", "family",
        "minMilestone");
    private static final Set<String> WORLD_KEYS = Set.of("seed", "worldType", "generateStructures",
        "dimension", "time", "weather", "weatherTicks", "difficulty", "gamemode", "prepTicks");
    private static final Set<String> CLIENT_KEYS = Set.of("width", "height", "fov", "gamma",
        "renderDistance", "guiScale", "mipmapLevels", "particles", "fancyGraphics", "clouds", "ao",
        "hideGui", "viewBobbing", "entityShadows", "smoothCamera", "anaglyph", "vsync", "fullscreen",
        "pauseOnLostFocus");
    private static final Set<String> CLIENT_OPTIONAL = Set.of("hideGui", "viewBobbing",
        "entityShadows", "smoothCamera", "anaglyph", "vsync", "fullscreen", "pauseOnLostFocus");
    private static final Set<String> SHOT_KEYS = Set.of("pos", "look", "heldMain", "heldOff",
        "warmupFrames", "captureFrames", "note");
    private static final Set<String> PATH_KEYS = Set.of("heldMain", "heldOff", "warmupFrames",
        "samples.count", "captureStartSample", "captureSampleCount", "note");

    private SceneParser() {
    }

    public static SceneSpec parse(String text) {
        List<SectionedText.Section> sections = SectionedText.parse(text);
        Map<String, SectionedText.Section> singles = new TreeMap<>();
        List<SectionedText.Section> captures = new ArrayList<>();
        for (SectionedText.Section section : sections) {
            switch (section.kind()) {
                case "scene", "world", "client", "pack" -> {
                    if (section.header().size() != 1) {
                        throw error(section, "section takes no name");
                    }
                    if (singles.putIfAbsent(section.kind(), section) != null) {
                        throw error(section, "duplicate section");
                    }
                }
                case "shot", "path" -> {
                    if (section.header().size() != 2) {
                        throw error(section, "capture section needs a name");
                    }
                    captures.add(section);
                }
                default -> throw error(section, "unknown section");
            }
        }
        for (String required : new String[] {"scene", "world", "client", "pack"}) {
            if (!singles.containsKey(required)) {
                throw new IllegalArgumentException("missing required section [" + required + "]");
            }
        }
        SectionedText.Section scene = singles.get("scene");
        requireKeys(scene, SCENE_KEYS, SCENE_KEYS);
        if (!SceneSpec.SCHEMA.equals(scene.entries().get("schema"))) {
            throw error(scene, "schema must be exactly " + SceneSpec.SCHEMA);
        }
        List<SceneSpec.Capture> parsed = new ArrayList<>();
        for (SectionedText.Section section : captures) {
            parsed.add(section.kind().equals("shot") ? shot(section) : path(section));
        }
        return new SceneSpec(
            scene.entries().get("id"),
            scene.entries().get("description"),
            scene.entries().get("family"),
            scene.entries().get("minMilestone"),
            world(singles.get("world")),
            client(singles.get("client")),
            pack(singles.get("pack")),
            parsed);
    }

    private static SceneSpec.World world(SectionedText.Section s) {
        TreeMap<String, String> gamerules = new TreeMap<>();
        TreeMap<Integer, Map<String, String>> entities = new TreeMap<>();
        Set<String> seenFixed = new HashSet<>();
        for (Map.Entry<String, String> e : s.entries().entrySet()) {
            String key = e.getKey();
            if (key.startsWith("gamerule.")) {
                String name = key.substring("gamerule.".length());
                if (name.isEmpty()) {
                    throw error(s, "empty gamerule name");
                }
                gamerules.put(name, e.getValue());
            } else if (key.startsWith("entity.")) {
                String[] parts = key.split("\\.");
                if (parts.length != 3) {
                    throw error(s, "entity keys are entity.<n>.{type,pos,nbt}: " + key);
                }
                int index = parseIndex(s, parts[1]);
                if (!Set.of("type", "pos", "nbt").contains(parts[2])) {
                    throw error(s, "unknown key " + key);
                }
                entities.computeIfAbsent(index, k -> new TreeMap<>()).put(parts[2], e.getValue());
            } else if (WORLD_KEYS.contains(key)) {
                seenFixed.add(key);
            } else {
                throw error(s, "unknown key " + key);
            }
        }
        for (String key : WORLD_KEYS) {
            if (!seenFixed.contains(key)) {
                throw error(s, "missing required key " + key);
            }
        }
        List<SceneSpec.Entity> entityList = new ArrayList<>();
        int expected = 0;
        for (Map.Entry<Integer, Map<String, String>> e : entities.entrySet()) {
            if (e.getKey() != expected++) {
                throw error(s, "entity indices must be dense from 0");
            }
            Map<String, String> fields = e.getValue();
            for (String f : new String[] {"type", "pos", "nbt"}) {
                if (!fields.containsKey(f)) {
                    throw error(s, "entity." + e.getKey() + " missing " + f);
                }
            }
            entityList.add(new SceneSpec.Entity(fields.get("type"), fields.get("pos"), fields.get("nbt")));
        }
        Map<String, String> v = s.entries();
        return new SceneSpec.World(
            parseLong(s, "seed", v.get("seed")),
            v.get("worldType"),
            parseBool(s, "generateStructures", v.get("generateStructures")),
            (int) parseLong(s, "dimension", v.get("dimension")),
            parseLong(s, "time", v.get("time")),
            v.get("weather"),
            (int) parseLong(s, "weatherTicks", v.get("weatherTicks")),
            v.get("difficulty"),
            v.get("gamemode"),
            gamerules,
            entityList,
            (int) parseLong(s, "prepTicks", v.get("prepTicks")));
    }

    private static SceneSpec.Client client(SectionedText.Section s) {
        Set<String> required = new HashSet<>(CLIENT_KEYS);
        required.removeAll(CLIENT_OPTIONAL);
        requireKeys(s, CLIENT_KEYS, required);
        Map<String, String> v = s.entries();
        return new SceneSpec.Client(
            (int) parseLong(s, "width", v.get("width")),
            (int) parseLong(s, "height", v.get("height")),
            parseDouble(s, "fov", v.get("fov")),
            parseDouble(s, "gamma", v.get("gamma")),
            (int) parseLong(s, "renderDistance", v.get("renderDistance")),
            (int) parseLong(s, "guiScale", v.get("guiScale")),
            (int) parseLong(s, "mipmapLevels", v.get("mipmapLevels")),
            (int) parseLong(s, "particles", v.get("particles")),
            parseBool(s, "fancyGraphics", v.get("fancyGraphics")),
            (int) parseLong(s, "clouds", v.get("clouds")),
            (int) parseLong(s, "ao", v.get("ao")),
            optionalBool(s, v, "hideGui", true),
            optionalBool(s, v, "viewBobbing", false),
            optionalBool(s, v, "entityShadows", false),
            optionalBool(s, v, "smoothCamera", false),
            optionalBool(s, v, "anaglyph", false),
            optionalBool(s, v, "vsync", false),
            optionalBool(s, v, "fullscreen", false),
            optionalBool(s, v, "pauseOnLostFocus", false));
    }

    private static SceneSpec.Pack pack(SectionedText.Section s) {
        TreeMap<String, String> options = new TreeMap<>();
        TreeMap<String, String> engine = new TreeMap<>();
        String shaderpack = null;
        for (Map.Entry<String, String> e : s.entries().entrySet()) {
            String key = e.getKey();
            if (key.equals("shaderpack")) {
                shaderpack = e.getValue();
            } else if (key.startsWith("option.") && key.length() > 7) {
                options.put(key.substring(7), e.getValue());
            } else if (key.startsWith("engine.") && key.length() > 7) {
                engine.put(key.substring(7), e.getValue());
            } else {
                throw error(s, "unknown key " + key);
            }
        }
        if (shaderpack == null) {
            throw error(s, "missing required key shaderpack");
        }
        return new SceneSpec.Pack(shaderpack, options, engine);
    }

    private static SceneSpec.Shot shot(SectionedText.Section s) {
        requireKeys(s, SHOT_KEYS, Set.of("pos", "look", "warmupFrames"));
        Map<String, String> v = s.entries();
        return new SceneSpec.Shot(
            s.name(),
            pose(s, v.get("pos"), v.get("look")),
            v.getOrDefault("heldMain", ""),
            v.getOrDefault("heldOff", ""),
            (int) parseLong(s, "warmupFrames", v.get("warmupFrames")),
            v.containsKey("captureFrames") ? (int) parseLong(s, "captureFrames", v.get("captureFrames")) : 1,
            v.getOrDefault("note", ""));
    }

    private static SceneSpec.Path path(SectionedText.Section s) {
        TreeMap<Integer, String[]> samples = new TreeMap<>();
        for (String key : s.entries().keySet()) {
            if (key.startsWith("sample.")) {
                String[] parts = key.split("\\.");
                if (parts.length != 3 || !(parts[2].equals("pos") || parts[2].equals("look"))) {
                    throw error(s, "sample keys are sample.<n>.{pos,look}: " + key);
                }
                int index = parseIndex(s, parts[1]);
                String[] pair = samples.computeIfAbsent(index, k -> new String[2]);
                pair[parts[2].equals("pos") ? 0 : 1] = s.entries().get(key);
            } else if (!PATH_KEYS.contains(key)) {
                throw error(s, "unknown key " + key);
            }
        }
        for (String required : new String[] {"warmupFrames", "samples.count", "captureStartSample",
            "captureSampleCount"}) {
            if (!s.entries().containsKey(required)) {
                throw error(s, "missing required key " + required);
            }
        }
        Map<String, String> v = s.entries();
        int count = (int) parseLong(s, "samples.count", v.get("samples.count"));
        List<SceneSpec.Pose> poses = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String[] pair = samples.get(i);
            if (pair == null || pair[0] == null || pair[1] == null) {
                throw error(s, "sample." + i + " needs both pos and look (dense from 0)");
            }
            poses.add(pose(s, pair[0], pair[1]));
        }
        if (samples.size() != count) {
            throw error(s, "sample indices beyond samples.count");
        }
        return new SceneSpec.Path(
            s.name(),
            v.getOrDefault("heldMain", ""),
            v.getOrDefault("heldOff", ""),
            (int) parseLong(s, "warmupFrames", v.get("warmupFrames")),
            poses,
            (int) parseLong(s, "captureStartSample", v.get("captureStartSample")),
            (int) parseLong(s, "captureSampleCount", v.get("captureSampleCount")),
            v.getOrDefault("note", ""));
    }

    // ------------------------------------------------------------------

    private static void requireKeys(SectionedText.Section s, Set<String> known, Set<String> required) {
        for (String key : s.entries().keySet()) {
            if (!known.contains(key)) {
                throw error(s, "unknown key " + key);
            }
        }
        for (String key : required) {
            if (!s.entries().containsKey(key)) {
                throw error(s, "missing required key " + key);
            }
        }
    }

    private static SceneSpec.Pose pose(SectionedText.Section s, String pos, String look) {
        String[] p = pos.trim().split("\\s+");
        String[] l = look.trim().split("\\s+");
        if (p.length != 3 || l.length != 2) {
            throw error(s, "pos needs 'x y z' and look needs 'yaw pitch'");
        }
        try {
            return new SceneSpec.Pose(Double.parseDouble(p[0]), Double.parseDouble(p[1]),
                Double.parseDouble(p[2]), Double.parseDouble(l[0]), Double.parseDouble(l[1]));
        } catch (IllegalArgumentException e) {
            throw error(s, "pose components must be finite doubles: " + e.getMessage());
        }
    }

    private static int parseIndex(SectionedText.Section s, String raw) {
        try {
            int i = Integer.parseInt(raw);
            if (i < 0) {
                throw new NumberFormatException();
            }
            return i;
        } catch (NumberFormatException e) {
            throw error(s, "index must be a non-negative integer: " + raw);
        }
    }

    private static long parseLong(SectionedText.Section s, String key, String raw) {
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            throw error(s, key + " must be an integer: " + raw);
        }
    }

    private static double parseDouble(SectionedText.Section s, String key, String raw) {
        try {
            double d = Double.parseDouble(raw);
            if (!Double.isFinite(d)) {
                throw new NumberFormatException();
            }
            return d;
        } catch (NumberFormatException e) {
            throw error(s, key + " must be a finite number: " + raw);
        }
    }

    private static boolean parseBool(SectionedText.Section s, String key, String raw) {
        if ("true".equals(raw)) {
            return true;
        }
        if ("false".equals(raw)) {
            return false;
        }
        throw error(s, key + " must be true or false: " + raw);
    }

    private static boolean optionalBool(SectionedText.Section s, Map<String, String> v, String key,
            boolean fallback) {
        return v.containsKey(key) ? parseBool(s, key, v.get(key)) : fallback;
    }

    private static IllegalArgumentException error(SectionedText.Section s, String message) {
        return new IllegalArgumentException("[" + String.join(" ", s.header()) + "] (line "
            + s.line() + "): " + message);
    }
}
