// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.scene;

import com.schmaloogium.conformance.wire.FlatDocument;
import com.schmaloogium.conformance.wire.Hashes;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * The derived, flat, machine-read form of a scene handed across the process boundary
 * (PHASE_2_DOC §4.5.2, schema {@code schmaloogium.capture-plan/4}): every value resolved,
 * captures and their samples as dense indexed blocks, and the runner-owned pack, world,
 * clock and environment facts the agent transports verbatim ([D-P2-23], §5.1.1).
 *
 * <p>This type wraps the canonical document and offers typed reads; {@link CapturePlanWriter}
 * is the only producer, {@link CapturePlanReader} the strict conformance-side reader.
 */
public final class CapturePlan {

    public static final String SCHEMA_LINE = "schema = schmaloogium.capture-plan/4";

    /** Required scalar keys and their wire type (T = JSON text, K = token, B, I, D, H64, H128). */
    static final Map<String, String> SCALARS = scalars();

    private static Map<String, String> scalars() {
        Map<String, String> m = new TreeMap<>();
        m.put("run.id", "K");
        m.put("scene.id", "K");
        m.put("scene.hash", "H64");
        m.put("pack.id", "T");
        m.put("pack.version", "T");
        m.put("pack.acquisitionMode", "K");
        m.put("pack.archiveSha512", "H128");
        m.put("pack.licence", "T");
        m.put("pack.optionStateSha256", "H64");
        m.put("pack.options.count", "I");
        m.put("pack.engineOptions.count", "I");
        m.put("world.path", "T");
        m.put("world.seed", "I");
        m.put("world.worldType", "K");
        m.put("world.generateStructures", "B");
        m.put("world.dimension", "I");
        m.put("world.time", "I");
        m.put("world.weather", "K");
        m.put("world.weatherTicks", "I");
        m.put("world.difficulty", "K");
        m.put("world.gamemode", "K");
        m.put("world.prepTicks", "I");
        m.put("world.gamerules.count", "I");
        m.put("world.entities.count", "I");
        m.put("client.width", "I");
        m.put("client.height", "I");
        m.put("client.fov", "D");
        m.put("client.gamma", "D");
        m.put("client.renderDistance", "I");
        m.put("client.guiScale", "I");
        m.put("client.mipmapLevels", "I");
        m.put("client.particles", "I");
        m.put("client.fancyGraphics", "B");
        m.put("client.clouds", "I");
        m.put("client.ao", "I");
        m.put("client.hideGui", "B");
        m.put("client.viewBobbing", "B");
        m.put("client.entityShadows", "B");
        m.put("client.smoothCamera", "B");
        m.put("client.anaglyph", "B");
        m.put("client.vsync", "B");
        m.put("client.fullscreen", "B");
        m.put("client.pauseOnLostFocus", "B");
        m.put("clock.frameTimeNanos", "I");
        m.put("clock.ticksPerFrame", "I");
        m.put("clock.partialTicks", "D");
        m.put("environment.subjectModId", "K");
        m.put("environment.subjectJarSha256", "H64");
        m.put("environment.modSetSha256", "H64");
        m.put("environment.externalModSetSha256", "H64");
        m.put("environment.minecraftVersion", "K");
        m.put("environment.worldGenerationSha256", "H64");
        m.put("environment.worldSha256", "H64");
        m.put("environment.mods.count", "I");
        m.put("captures.count", "I");
        return Map.copyOf(m);
    }

    /** Dense family member fields → type. */
    static final Map<String, Map<String, String>> FAMILIES = Map.of(
        "pack.options", Map.of("name", "T", "value", "T"),
        "pack.engineOptions", Map.of("name", "T", "value", "T"),
        "world.gamerules", Map.of("name", "T", "value", "T"),
        "world.entities", Map.of("type", "T", "pos", "T", "nbt", "T"),
        "environment.mods", Map.of("id", "T", "sha256", "H64"),
        "captures", Map.of("kind", "K", "id", "T", "heldMain", "T", "heldOff", "T", "note", "T",
            "warmupFrames", "I", "samples.count", "I", "captureStartSample", "I",
            "captureSampleCount", "I"));

    static final Map<String, String> SAMPLE_FIELDS = Map.of("pos.x", "D", "pos.y", "D", "pos.z", "D",
        "look.yaw", "D", "look.pitch", "D");

    static final Set<String> KINDS = Set.of("SHOT", "PATH");
    static final Set<String> ACQUISITION_MODES = Set.of("MODRINTH", "MANUAL");

    private final FlatDocument document;

    CapturePlan(FlatDocument document) {
        this.document = Objects.requireNonNull(document, "document");
    }

    public FlatDocument document() {
        return document;
    }

    public String render() {
        return document.render();
    }

    /** SHA-256 over the exact plan bytes; the manifest's {@code run.planHash}. */
    public String planHash() {
        return Hashes.sha256Hex(document.bytes());
    }

    public String runId() {
        return document.token("run.id");
    }

    public String sceneId() {
        return document.token("scene.id");
    }

    public int captureCount() {
        return document.count("captures");
    }

    public String captureKind(int i) {
        return document.token("captures." + i + ".kind");
    }

    public String captureId(int i) {
        return document.text("captures." + i + ".id");
    }

    public int warmupFrames(int i) {
        return (int) document.integer("captures." + i + ".warmupFrames");
    }

    public int sampleCount(int i) {
        return (int) document.integer("captures." + i + ".samples.count");
    }

    public int captureStartSample(int i) {
        return (int) document.integer("captures." + i + ".captureStartSample");
    }

    public int captureSampleCount(int i) {
        return (int) document.integer("captures." + i + ".captureSampleCount");
    }

    public SceneSpec.Pose sample(int capture, int ordinal) {
        String p = "captures." + capture + ".samples." + ordinal + ".";
        return new SceneSpec.Pose(document.decimal(p + "pos.x"), document.decimal(p + "pos.y"),
            document.decimal(p + "pos.z"), document.decimal(p + "look.yaw"),
            document.decimal(p + "look.pitch"));
    }

    public long prepTicks() {
        return document.integer("world.prepTicks");
    }

    public int ticksPerFrame() {
        return (int) document.integer("clock.ticksPerFrame");
    }

    public long frameTimeNanos() {
        return document.integer("clock.frameTimeNanos");
    }

    public double partialTicks() {
        return document.decimal("clock.partialTicks");
    }

    /** Preparation steps = prepTicks / ticksPerFrame (must divide exactly, §5.1.1). */
    public long preparationSteps() {
        return prepTicks() / ticksPerFrame();
    }

    /** Total controlled steps: preparation + every capture's warm-up + samples. */
    public long totalSteps() {
        long total = preparationSteps();
        for (int i = 0; i < captureCount(); i++) {
            total = Math.addExact(total, warmupFrames(i));
            total = Math.addExact(total, sampleCount(i));
        }
        return total;
    }

    /** A JSON-string field decoded, or a token field verbatim. */
    private String plain(String key) {
        String raw = document.raw(key);
        return raw.startsWith("\"") ? document.text(key) : raw;
    }

    public Map<String, String> familyMap(String family, String keyField, String valueField) {
        Map<String, String> out = new TreeMap<>();
        int count = document.count(family);
        for (int i = 0; i < count; i++) {
            out.put(plain(family + "." + i + "." + keyField), plain(family + "." + i + "." + valueField));
        }
        return out;
    }
}
