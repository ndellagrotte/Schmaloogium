// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.scene;

import com.schmaloogium.conformance.wire.FlatDocument;
import com.schmaloogium.conformance.wire.Hashes;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Serialises one complete resolved value into the {@code schmaloogium.capture-plan/4} wire.
 * It performs no defaulting: the scene has already been parsed and validated, the fixture
 * facts come from the runner's resolution ([D-P2-23]), the option maps are the complete
 * effective state, the environment identities are authenticated launch facts (§5.1.1) and
 * the world identities come from the world cache (§4.5.5).
 */
public final class CapturePlanWriter {

    /** Runner-owned fixture facts: acquisition mode, licence, verified archive SHA-512. */
    public record FixtureFacts(String packId, String packVersion, String acquisitionMode,
            String archiveSha512, String licence) {
        public FixtureFacts {
            Objects.requireNonNull(packId, "packId");
            Objects.requireNonNull(packVersion, "packVersion");
            if (!CapturePlan.ACQUISITION_MODES.contains(acquisitionMode)) {
                throw new IllegalArgumentException("acquisitionMode must be MODRINTH|MANUAL");
            }
            if (!Hashes.isHex(archiveSha512, 128)) {
                throw new IllegalArgumentException("archiveSha512 must be 128 lowercase hex digits");
            }
            if (licence == null || licence.isEmpty()) {
                throw new IllegalArgumentException("licence must be non-empty");
            }
            if (packId.isEmpty() || packVersion.isEmpty()) {
                throw new IllegalArgumentException("pack id and version must be non-empty");
            }
        }
    }

    /** Authenticated launch inventory (§5.1.1): subject and complete mod list. */
    public record Environment(String subjectModId, String subjectJarSha256, String modSetSha256,
            String externalModSetSha256, String minecraftVersion,
            SortedMap<String, String> modJarHashes) {
        public Environment {
            Objects.requireNonNull(subjectModId, "subjectModId");
            for (String h : new String[] {subjectJarSha256, modSetSha256, externalModSetSha256}) {
                if (!Hashes.isHex(h, 64)) {
                    throw new IllegalArgumentException("environment hashes must be 64 lowercase hex");
                }
            }
            Objects.requireNonNull(minecraftVersion, "minecraftVersion");
            modJarHashes = new TreeMap<>(modJarHashes);
        }
    }

    /** World cache facts (§4.5.5): run-relative save path and both identities. */
    public record WorldFacts(String path, String worldGenerationSha256, String worldSha256) {
        public WorldFacts {
            Objects.requireNonNull(path, "path");
            if (!Hashes.isHex(worldGenerationSha256, 64) || !Hashes.isHex(worldSha256, 64)) {
                throw new IllegalArgumentException("world hashes must be 64 lowercase hex");
            }
        }
    }

    private CapturePlanWriter() {
    }

    public static CapturePlan write(String runId, SceneSpec scene, String sceneSha256,
            List<SceneSpec.Capture> selectedCaptures, FixtureFacts fixture,
            Map<String, String> packOptions, Map<String, String> engineOptions,
            ClockProfile clock, Environment environment, WorldFacts world) {
        if (!Hashes.isHex(sceneSha256, 64)) {
            throw new IllegalArgumentException("sceneSha256 must be 64 lowercase hex");
        }
        if (selectedCaptures.isEmpty()) {
            throw new IllegalArgumentException("a plan needs at least one selected capture");
        }
        if (scene.world().prepTicks() % clock.ticksPerFrame() != 0) {
            throw new IllegalArgumentException("prepTicks " + scene.world().prepTicks()
                + " must be divisible by ticksPerFrame " + clock.ticksPerFrame());
        }
        FlatDocument.Builder b = FlatDocument.builder(CapturePlan.SCHEMA_LINE);
        b.token("run.id", runId);
        b.token("scene.id", scene.id());
        b.token("scene.hash", sceneSha256);
        b.text("pack.id", fixture.packId());
        b.text("pack.version", fixture.packVersion());
        b.token("pack.acquisitionMode", fixture.acquisitionMode());
        b.token("pack.archiveSha512", fixture.archiveSha512());
        b.text("pack.licence", fixture.licence());
        b.token("pack.optionStateSha256", OptionStateDigest.sha256(packOptions, engineOptions));
        namedMap(b, "pack.options", packOptions);
        namedMap(b, "pack.engineOptions", engineOptions);
        SceneSpec.World w = scene.world();
        b.text("world.path", world.path());
        b.integer("world.seed", w.seed());
        b.token("world.worldType", w.worldType());
        b.bool("world.generateStructures", w.generateStructures());
        b.integer("world.dimension", w.dimension());
        b.integer("world.time", w.time());
        b.token("world.weather", w.weather());
        b.integer("world.weatherTicks", w.weatherTicks());
        b.token("world.difficulty", w.difficulty());
        b.token("world.gamemode", w.gamemode());
        b.integer("world.prepTicks", w.prepTicks());
        namedMap(b, "world.gamerules", w.gamerules());
        b.integer("world.entities.count", w.entities().size());
        for (int i = 0; i < w.entities().size(); i++) {
            SceneSpec.Entity e = w.entities().get(i);
            b.text("world.entities." + i + ".type", e.type());
            b.text("world.entities." + i + ".pos", e.pos());
            b.text("world.entities." + i + ".nbt", e.nbt());
        }
        SceneSpec.Client c = scene.client();
        b.integer("client.width", c.width());
        b.integer("client.height", c.height());
        b.decimal("client.fov", c.fov());
        b.decimal("client.gamma", c.gamma());
        b.integer("client.renderDistance", c.renderDistance());
        b.integer("client.guiScale", c.guiScale());
        b.integer("client.mipmapLevels", c.mipmapLevels());
        b.integer("client.particles", c.particles());
        b.bool("client.fancyGraphics", c.fancyGraphics());
        b.integer("client.clouds", c.clouds());
        b.integer("client.ao", c.ao());
        b.bool("client.hideGui", c.hideGui());
        b.bool("client.viewBobbing", c.viewBobbing());
        b.bool("client.entityShadows", c.entityShadows());
        b.bool("client.smoothCamera", c.smoothCamera());
        b.bool("client.anaglyph", c.anaglyph());
        b.bool("client.vsync", c.vsync());
        b.bool("client.fullscreen", c.fullscreen());
        b.bool("client.pauseOnLostFocus", c.pauseOnLostFocus());
        b.integer("clock.frameTimeNanos", clock.frameTimeNanos());
        b.integer("clock.ticksPerFrame", clock.ticksPerFrame());
        b.decimal("clock.partialTicks", clock.partialTicks());
        b.token("environment.subjectModId", environment.subjectModId());
        b.token("environment.subjectJarSha256", environment.subjectJarSha256());
        b.token("environment.modSetSha256", environment.modSetSha256());
        b.token("environment.externalModSetSha256", environment.externalModSetSha256());
        b.token("environment.minecraftVersion", environment.minecraftVersion());
        b.token("environment.worldGenerationSha256", world.worldGenerationSha256());
        b.token("environment.worldSha256", world.worldSha256());
        b.integer("environment.mods.count", environment.modJarHashes().size());
        int m = 0;
        for (Map.Entry<String, String> e : environment.modJarHashes().entrySet()) {
            b.text("environment.mods." + m + ".id", e.getKey());
            b.token("environment.mods." + m + ".sha256", e.getValue());
            m++;
        }
        b.integer("captures.count", selectedCaptures.size());
        for (int i = 0; i < selectedCaptures.size(); i++) {
            SceneSpec.Capture capture = selectedCaptures.get(i);
            String p = "captures." + i + ".";
            b.token(p + "kind", capture.kind());
            b.text(p + "id", capture.id());
            b.text(p + "heldMain", capture.heldMain());
            b.text(p + "heldOff", capture.heldOff());
            b.text(p + "note", capture.note());
            b.integer(p + "warmupFrames", capture.warmupFrames());
            List<SceneSpec.Pose> samples = capture.samples();
            b.integer(p + "samples.count", samples.size());
            b.integer(p + "captureStartSample", capture.captureStartSample());
            b.integer(p + "captureSampleCount", capture.captureSampleCount());
            for (int s = 0; s < samples.size(); s++) {
                SceneSpec.Pose pose = samples.get(s);
                String sp = p + "samples." + s + ".";
                b.decimal(sp + "pos.x", pose.x());
                b.decimal(sp + "pos.y", pose.y());
                b.decimal(sp + "pos.z", pose.z());
                b.decimal(sp + "look.yaw", pose.yaw());
                b.decimal(sp + "look.pitch", pose.pitch());
            }
        }
        CapturePlan plan = new CapturePlan(b.build());
        plan.totalSteps(); // checked arithmetic before launch (§5.1.1)
        return plan;
    }

    private static void namedMap(FlatDocument.Builder b, String family, Map<String, String> map) {
        SortedMap<String, String> sorted = new TreeMap<>(map);
        b.integer(family + ".count", sorted.size());
        int i = 0;
        for (Map.Entry<String, String> e : sorted.entrySet()) {
            b.text(family + "." + i + ".name", e.getKey());
            b.text(family + "." + i + ".value", e.getValue());
            i++;
        }
    }
}
