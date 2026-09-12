// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.scene;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The authored, human-edited scene (PHASE_2_DOC §4.3.2, schema {@code schmaloogium.scene/2}):
 * world + client + pack blocks and the ordered captures, each a static {@link Shot} or a
 * dense-sample {@link Path}. Values are exactly what the file said (defaults applied by the
 * parser where §4.3.2 names one); nothing here is resolved against a fixture or a clock.
 */
public record SceneSpec(
        String id,
        String description,
        String family,
        String minMilestone,
        World world,
        Client client,
        Pack pack,
        List<Capture> captures) {

    public static final String SCHEMA = "schmaloogium.scene/2";

    public SceneSpec {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(family, "family");
        Objects.requireNonNull(minMilestone, "minMilestone");
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(client, "client");
        Objects.requireNonNull(pack, "pack");
        captures = List.copyOf(captures);
    }

    public record Pose(double x, double y, double z, double yaw, double pitch) {
        public Pose {
            for (double d : new double[] {x, y, z, yaw, pitch}) {
                if (!Double.isFinite(d)) {
                    throw new IllegalArgumentException("non-finite pose component");
                }
            }
        }
    }

    public record Entity(String type, String pos, String nbt) {
        public Entity {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(pos, "pos");
            Objects.requireNonNull(nbt, "nbt");
        }
    }

    public record World(
            long seed,
            String worldType,
            boolean generateStructures,
            int dimension,
            long time,
            String weather,
            int weatherTicks,
            String difficulty,
            String gamemode,
            SortedMap<String, String> gamerules,
            List<Entity> entities,
            int prepTicks) {
        public World {
            Objects.requireNonNull(worldType, "worldType");
            Objects.requireNonNull(weather, "weather");
            Objects.requireNonNull(difficulty, "difficulty");
            Objects.requireNonNull(gamemode, "gamemode");
            gamerules = Collections.unmodifiableSortedMap(new TreeMap<>(gamerules));
            entities = List.copyOf(entities);
        }
    }

    public record Client(
            int width,
            int height,
            double fov,
            double gamma,
            int renderDistance,
            int guiScale,
            int mipmapLevels,
            int particles,
            boolean fancyGraphics,
            int clouds,
            int ao,
            boolean hideGui,
            boolean viewBobbing,
            boolean entityShadows,
            boolean smoothCamera,
            boolean anaglyph,
            boolean vsync,
            boolean fullscreen,
            boolean pauseOnLostFocus) {
    }

    public record Pack(String shaderpack, SortedMap<String, String> options,
            SortedMap<String, String> engine) {
        public Pack {
            Objects.requireNonNull(shaderpack, "shaderpack");
            options = Collections.unmodifiableSortedMap(new TreeMap<>(options));
            engine = Collections.unmodifiableSortedMap(new TreeMap<>(engine));
        }
    }

    /** A capture block: the closed {@code SHOT|PATH} kinds of §4.3.2. */
    public sealed interface Capture permits Shot, Path {
        String id();
        String heldMain();
        String heldOff();
        int warmupFrames();
        String note();
        String kind();
        /** The dense sample sequence the plan expands to (§4.3.4). */
        List<Pose> samples();
        int captureStartSample();
        int captureSampleCount();
    }

    public record Shot(String id, Pose pose, String heldMain, String heldOff, int warmupFrames,
            int captureFrames, String note) implements Capture {
        public Shot {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(pose, "pose");
            Objects.requireNonNull(heldMain, "heldMain");
            Objects.requireNonNull(heldOff, "heldOff");
            Objects.requireNonNull(note, "note");
        }

        @Override
        public String kind() {
            return "SHOT";
        }

        @Override
        public List<Pose> samples() {
            return Collections.nCopies(captureFrames, pose);
        }

        @Override
        public int captureStartSample() {
            return 0;
        }

        @Override
        public int captureSampleCount() {
            return captureFrames;
        }
    }

    public record Path(String id, String heldMain, String heldOff, int warmupFrames,
            List<Pose> samples, int captureStartSample, int captureSampleCount, String note)
            implements Capture {
        public Path {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(heldMain, "heldMain");
            Objects.requireNonNull(heldOff, "heldOff");
            Objects.requireNonNull(note, "note");
            samples = List.copyOf(samples);
        }

        @Override
        public String kind() {
            return "PATH";
        }
    }
}
