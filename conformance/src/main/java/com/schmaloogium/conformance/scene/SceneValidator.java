// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.scene;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The §4.3.3 rules, each failure naming its rule. The warm-up floor is the §4.4 convergence
 * rule {@code max(60, ceil(8 · largestHalflifeTicks / ticksPerFrame))}, supplied by the caller
 * because the pack constants come from the front end; the corpus test uses the bare floor.
 * {@code [pack] engine.*} keys are checked against P3's executable engine settings when the
 * caller supplies that inventory; otherwise they are reported as unvalidated, never accepted.
 */
public final class SceneValidator {

    public static final int WARMUP_FLOOR = 60;
    public static final int PATH_MIN_SAMPLES = 2;
    public static final int PATH_MAX_SAMPLES = 240;

    /** §4.4's mandatory suppressions, written explicitly in every scene. */
    public static final Map<String, String> MANDATORY_GAMERULES = Map.of(
        "doDaylightCycle", "false",
        "doWeatherCycle", "false",
        "doMobSpawning", "false",
        "randomTickSpeed", "0",
        "doFireTick", "false",
        "mobGriefing", "false",
        "doTileDrops", "false",
        "doEntityDrops", "false");

    public static final Set<String> FAMILIES = Set.of("terrain", "water", "shadows", "weather",
        "hand", "entities");
    public static final Set<String> MILESTONES = Set.of("v0.1", "v0.2", "v0.3", "v0.4", "v0.5",
        "post-v0.5");
    public static final Set<String> WEATHER = Set.of("clear", "rain", "thunder");
    public static final Set<String> DIFFICULTY = Set.of("peaceful", "easy", "normal", "hard");
    public static final Set<String> GAMEMODE = Set.of("survival", "creative", "adventure", "spectator");
    public static final Set<String> WORLD_TYPES = Set.of("default", "flat", "largeBiomes",
        "amplified");

    /** Validated engine setting inventory: key → allowed values (null = any). */
    public record EngineSettingDomain(Set<String> keys, Map<String, Set<String>> domains) {
    }

    private SceneValidator() {
    }

    public static int warmupFloor(double largestHalflifeTicks, int ticksPerFrame) {
        if (ticksPerFrame <= 0) {
            throw new IllegalArgumentException("ticksPerFrame must be positive");
        }
        return Math.max(WARMUP_FLOOR, (int) Math.ceil(8.0 * largestHalflifeTicks / ticksPerFrame));
    }

    /** Validates against the bare floor with no engine-setting inventory. */
    public static void validate(SceneSpec scene, String fileBaseName) {
        validate(scene, fileBaseName, WARMUP_FLOOR, null);
    }

    public static void validate(SceneSpec scene, String fileBaseName, int warmupFloor,
            EngineSettingDomain engineSettings) {
        List<String> failures = new ArrayList<>();
        if (!scene.id().equals(fileBaseName)) {
            failures.add("id must equal the file base name: id=" + scene.id() + " file=" + fileBaseName);
        }
        if (!FAMILIES.contains(scene.family())) {
            failures.add("family must be one of " + FAMILIES + ": " + scene.family());
        }
        if (!MILESTONES.contains(scene.minMilestone())) {
            failures.add("minMilestone must be one of " + MILESTONES + ": " + scene.minMilestone());
        }
        SceneSpec.World w = scene.world();
        if (!WORLD_TYPES.contains(w.worldType())) {
            failures.add("worldType must be one of " + WORLD_TYPES + ": " + w.worldType());
        }
        if (w.dimension() != 0 && w.dimension() != -1 && w.dimension() != 1) {
            failures.add("dimension must be 0, -1 or 1");
        }
        if (!WEATHER.contains(w.weather())) {
            failures.add("weather must be one of " + WEATHER);
        }
        if (!DIFFICULTY.contains(w.difficulty())) {
            failures.add("difficulty must be one of " + DIFFICULTY);
        }
        if (!GAMEMODE.contains(w.gamemode())) {
            failures.add("gamemode must be one of " + GAMEMODE);
        }
        if (w.time() < 0 || w.weatherTicks() < 0 || w.prepTicks() < 0) {
            failures.add("time, weatherTicks and prepTicks must be non-negative");
        }
        for (Map.Entry<String, String> rule : MANDATORY_GAMERULES.entrySet()) {
            String set = w.gamerules().get(rule.getKey());
            if (set == null) {
                failures.add("mandatory gamerule unset (§4.4 ledger): " + rule.getKey());
            } else if (!set.equals(rule.getValue())) {
                failures.add("mandatory gamerule " + rule.getKey() + " must be " + rule.getValue()
                    + ", is " + set);
            }
        }
        SceneSpec.Client c = scene.client();
        if (c.width() <= 0 || c.height() <= 0) {
            failures.add("width and height must be positive");
        }
        if (c.renderDistance() < 2 || c.renderDistance() > 32) {
            failures.add("renderDistance must be in 2..32");
        }
        if (scene.captures().isEmpty()) {
            failures.add("a scene needs at least one [shot] or [path]");
        }
        Set<String> names = new HashSet<>();
        for (SceneSpec.Capture capture : scene.captures()) {
            String where = capture.kind() + " " + capture.id();
            if (capture.id().isEmpty() || !capture.id().chars().allMatch(ch ->
                    (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '-')) {
                failures.add(where + ": capture name must be lowercase kebab");
            }
            if (!names.add(capture.id())) {
                failures.add(where + ": duplicate capture name across shots and paths");
            }
            if (capture.warmupFrames() < warmupFloor) {
                failures.add(where + ": warmupFrames " + capture.warmupFrames()
                    + " below the family floor " + warmupFloor);
            }
            if (capture instanceof SceneSpec.Shot shot) {
                if (shot.captureFrames() < 1) {
                    failures.add(where + ": captureFrames must be at least 1");
                }
            } else if (capture instanceof SceneSpec.Path path) {
                validatePath(path, where, failures);
            }
        }
        for (String key : scene.pack().engine().keySet()) {
            if (engineSettings == null) {
                failures.add("[pack] engine." + key + " is unvalidated: no engine-setting inventory"
                    + " was supplied (§5.4); refusing rather than accepting silently");
            } else if (!engineSettings.keys().contains(key)) {
                failures.add("[pack] engine." + key + " is not an executable engine setting");
            } else {
                Set<String> domain = engineSettings.domains().get(key);
                if (domain != null && !domain.contains(scene.pack().engine().get(key))) {
                    failures.add("[pack] engine." + key + " value outside its domain " + domain);
                }
            }
        }
        if (!failures.isEmpty()) {
            throw new IllegalArgumentException("scene " + scene.id() + " invalid:\n  "
                + String.join("\n  ", failures));
        }
    }

    private static void validatePath(SceneSpec.Path path, String where, List<String> failures) {
        int n = path.samples().size();
        if (n < PATH_MIN_SAMPLES || n > PATH_MAX_SAMPLES) {
            failures.add(where + ": samples.count must be in " + PATH_MIN_SAMPLES + ".."
                + PATH_MAX_SAMPLES);
            return;
        }
        int start = path.captureStartSample();
        int count = path.captureSampleCount();
        if (start < 0 || count < 2 || start + count > n) {
            failures.add(where + ": capture window must be inside the sample range and at least two");
            return;
        }
        boolean delta = false;
        for (int i = start + 1; i < start + count; i++) {
            if (!path.samples().get(i).equals(path.samples().get(i - 1))) {
                delta = true;
                break;
            }
        }
        if (!delta) {
            failures.add(where + ": captured window has no non-zero pose delta between"
                + " consecutive samples");
        }
    }
}
