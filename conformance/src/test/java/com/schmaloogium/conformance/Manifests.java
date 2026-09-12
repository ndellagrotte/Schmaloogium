// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance;

import com.schmaloogium.conformance.capture.RunManifest;
import com.schmaloogium.conformance.wire.CanonicalText;

import java.util.function.UnaryOperator;

/**
 * Hand-built {@code schmaloogium.run-manifest/4} fixtures for tests (§8.2: hand-built
 * manifests are canonical harness fixtures). {@link #valid()} assembles the smallest
 * complete document that passes both {@code RunManifestReader} and {@code T0Evaluator};
 * mutators derive the failure scenarios from it.
 */
public final class Manifests {

    /** A complete, valid, T0-passing manifest. */
    public static RunManifest valid() {
        RunManifest.Builder b = RunManifest.builder();
        // run
        b.token("run.id", "RUN-T0")
            .token("run.sceneId", "synthetic-scene")
            .token("run.sceneHash", hex64())
            .token("run.planHash", hex64())
            .token("run.exitStatus", "COMPLETE")
            .text("run.failureReason", "")
            .text("run.uncaughtException", "")
            .token("run.compatVerdict", "Continue")
            .bool("run.shadersActiveThroughout", true)
            .integer("run.hangCeilingMillis", 1_000)
            .bool("run.timedOut", false)
            .text("run.startedAt", "\"2026-09-09T00:00:00Z\"")
            .text("run.endedAt", "\"2026-09-09T00:01:00Z\"");
        // front end
        b.bool("frontEnd.completed", true)
            .bool("frontEnd.packConfigurationProduced", true);
        b.token("environment.os", "linux")
            .token("environment.jvm", "25")
            .token("environment.minecraftVersion", "1.12.2")
            .token("environment.cleanroomVersion", "v0.1.0-dev")
            .token("environment.worldSha256", hex64())
            .token("environment.modSetSha256", hex64())
            .token("environment.subjectModId", "schmaloogium")
            .token("environment.subjectJarSha256", hex64())
            .token("environment.externalModSetSha256", hex64())
            .token("environment.worldGenerationSha256", hex64());
        b.row("environment.mods", 0, modsRow("synthetic-lib"));
        // clock
        b.integer("clock.frameTimeNanos", 50_000_000L)
            .integer("clock.ticksPerFrame", 1)
            .decimal("clock.partialTicks", 0.0);
        // gl
        b.bool("gl.available", true)
            .text("gl.profile_text", "\"synthetic gl 3.3 core\"");
        // pack
        b.text("pack.id", "\"internal-default\"")
            .text("pack.version", "\"1.0.0\"")
            .token("pack.acquisitionMode", "MANUAL")
            .token("pack.archiveSha512", hex128())
            .text("pack.licence", "\"GPL-3.0-or-later\"")
            .token("pack.optionStateSha256", hex64());
        b.row("pack.options", 0, optionsRow("SHADOWS", "true"));
        b.row("pack.engineOptions", 0, optionsRow("mipmap_level", "4"));
        // availability + the REALIZED resources block (§4.5.4, P5 §4.1.1)
        b.bool("resources.available", true)
            .bool("hooks.available", true);
        b.token("resources.evidence_stage", "REALIZED")
            .integer("resources.depthTextures.count", 2)
            .integer("resources.shadow.depthTextures", 0)
            .integer("resources.shadow.colorTextures", 0)
            .integer("resources.shadow.resolution", 0)
            .bool("resources.centerDepthSmooth.enabled", false)
            .integer("resources.noise.resolution", 256)
            .token("resources.capabilityGate", "OK");
        b.row("resources.colorBuffers", 0, colorRow("DEFAULT_RGBA", true, "FOG_RGB_ALPHA_ONE", null,
            "RGBA", "REQUESTED"));
        b.row("resources.colorBuffers", 1, colorRow("RGBA8", true, "CONSTANT",
            new double[] {1.0, 1.0, 1.0, 1.0}, "RGBA8", "REQUESTED"));
        b.count("resources.shadow.depth", 0).count("resources.shadow.color", 0)
            .count("resources.vertexAttributes", 0).count("resources.instances", 0)
            .count("resources.capabilityShortfalls", 0);
        b.count("hooks.rows", 0).count("hooks.subreports", 0);
        // timing
        b.bool("timing.available", true)
            .bool("timing.complete", true)
            .text("timing.failureReason", "")
            .token("timing.restoration", "RESTORED")
            .bool("timing.identitiesUnchanged", true);
        b.integer("timing.origin.clockStep", 0)
            .integer("timing.origin.worldTick", 100)
            .integer("timing.origin.logicalTick", 0)
            .integer("timing.origin.animationTick", 0)
            .integer("timing.origin.clientTicks", 0)
            .integer("timing.origin.serverTicks", 0)
            .integer("timing.origin.acceptedFrames", 0)
            .integer("timing.origin.finalizedFrames", 0)
            .integer("timing.origin.frameCounter", 0)
            .decimal("timing.origin.frameTimeCounter", 0.0)
            .bool("timing.origin.quiescent", true)
            .bool("timing.origin.freshRuntime", true)
            .bool("timing.origin.frameTimingAbsent", false);
        b.text("timing.origin.checkpointId", "\"checkpoint-0\"");
        // captures: one SHOT capture, 2 warm-up frames + 3 samples, window [1,3)
        b.row("captures", 0, captureRow("SHOT", "shot-a", 2, 3, 1, 2));
        // images: one per captured sample
        b.row("images", 0, imageRow("SHOT", "shot-a", 1, 0));
        b.row("images", 1, imageRow("SHOT", "shot-a", 2, 1));
        // programs: terrain SOURCED, water CHAIN from terrain, hand ABSENT
        b.row("programs", 0, programRow("gbuffers_terrain", "SOURCED", "", true, "SUCCEEDED", "", 0));
        b.row("programs", 1, programRow("gbuffers_water", "CHAIN", "gbuffers_terrain", true,
            "FAILED", "", 1));
        b.row("programs", 2, programRow("gbuffers_hand", "ABSENT", "", false, "NO_SOURCE", "", 2));
        // diagnostics, gl_errors: none
        b.count("diagnostics", 0);
        b.count("gl_errors", 0);
        for (int ordinal = 0; ordinal < 3; ordinal++) {
            b.row("frames", ordinal, frameRow("SHOT", "shot-a", ordinal, ordinal >= 1,
                5 + ordinal));
        }
        b.integer("timing.steps.count", 2 + 2 + 3);
        for (int prep = 0; prep < 2; prep++) {
            b.row("timing.steps", prep, stepRow("PREPARATION", -1, prep, prep + 1));
        }
        int position = 2;
        for (int warmup = 0; warmup < 2; warmup++) {
            b.row("timing.steps", position, stepRow("WARMUP", 0, warmup, position + 1));
            position++;
        }
        for (int sample = 0; sample < 3; sample++) {
            b.row("timing.steps", position, stepRow("SAMPLE", 0, sample, position + 1));
            position++;
        }
        return b.build();
    }

    /** Derives a manifest: existing keys upserted, mutator adds or overrides. */
    public static RunManifest with(RunManifest base, UnaryOperator<RunManifest.Builder> mutator) {
        RunManifest.Builder b = RunManifest.builder();
        base.entries().forEach(b::set);
        return mutator.apply(b).build();
    }
    static java.util.SortedMap<String, RunManifest.Value> colorRow(String requested, boolean clear,
            String policy, double[] constant, String allocated, String origin) {
        java.util.SortedMap<String, RunManifest.Value> row = new java.util.TreeMap<>();
        row.put("requested_format", new RunManifest.Value.Text(requested));
        row.put("clear", new RunManifest.Value.Bool(clear));
        row.put("clear_policy", new RunManifest.Value.Token(policy));
        if (constant != null) {
            row.put("clear_color_r", new RunManifest.Value.Dec(constant[0]));
            row.put("clear_color_g", new RunManifest.Value.Dec(constant[1]));
            row.put("clear_color_b", new RunManifest.Value.Dec(constant[2]));
            row.put("clear_color_a", new RunManifest.Value.Dec(constant[3]));
        }
        if (allocated != null) {
            row.put("allocated_format", new RunManifest.Value.Text(allocated));
            row.put("allocation_origin", new RunManifest.Value.Token(origin));
        }
        return row;
    }

    private static java.util.SortedMap<String, RunManifest.Value> modsRow(String id) {
        java.util.SortedMap<String, RunManifest.Value> row = new java.util.TreeMap<>();
        row.put("id", new RunManifest.Value.Text(CanonicalText.encodeJson(id)));
        row.put("sha256", new RunManifest.Value.Token(hex64()));
        return row;
    }

    private static java.util.SortedMap<String, RunManifest.Value> optionsRow(String name,
            String value) {
        java.util.SortedMap<String, RunManifest.Value> row = new java.util.TreeMap<>();
        row.put("name", new RunManifest.Value.Text(CanonicalText.encodeJson(name)));
        row.put("value", new RunManifest.Value.Text(CanonicalText.encodeJson(value)));
        return row;
    }

    private static java.util.SortedMap<String, RunManifest.Value> captureRow(String kind,
            String id, int warmup, int samples, int windowStart, int windowCount) {
        java.util.SortedMap<String, RunManifest.Value> row = new java.util.TreeMap<>();
        row.put("kind", new RunManifest.Value.Token(kind));
        row.put("id", new RunManifest.Value.Text(CanonicalText.encodeJson(id)));
        row.put("plannedWarmupFrames", new RunManifest.Value.Int(warmup));
        row.put("actualWarmupFrames", new RunManifest.Value.Int(warmup));
        row.put("plannedSamples", new RunManifest.Value.Int(samples));
        row.put("actualSamples", new RunManifest.Value.Int(samples));
        row.put("captureStartSample", new RunManifest.Value.Int(windowStart));
        row.put("captureSampleCount", new RunManifest.Value.Int(windowCount));
        return row;
    }

    private static java.util.SortedMap<String, RunManifest.Value> frameRow(String kind,
            String id, int ordinal, boolean captured, long clockStep) {
        java.util.SortedMap<String, RunManifest.Value> row = new java.util.TreeMap<>();
        row.put("captureKind", new RunManifest.Value.Token(kind));
        row.put("captureId", new RunManifest.Value.Text(CanonicalText.encodeJson(id)));
        row.put("sampleOrdinal", new RunManifest.Value.Int(ordinal));
        row.put("captured", new RunManifest.Value.Bool(captured));
        for (String role : new String[] {"plannedCurrent", "plannedPrevious", "actualCurrent",
            "actualPrevious"}) {
            row.put(role + ".posX", new RunManifest.Value.Dec(0.5 + ordinal * 0.25));
            row.put(role + ".posY", new RunManifest.Value.Dec(64.0));
            row.put(role + ".posZ", new RunManifest.Value.Dec(-12.5));
            row.put(role + ".yaw", new RunManifest.Value.Dec(90.0));
            row.put(role + ".pitch", new RunManifest.Value.Dec(0.0));
        }
        row.put("worldTick", new RunManifest.Value.Int(100 + clockStep));
        row.put("partialTicks", new RunManifest.Value.Dec(0.0));
        row.put("frameCounter", new RunManifest.Value.Int(clockStep));
        row.put("logicalTick", new RunManifest.Value.Int(clockStep));
        row.put("animationTick", new RunManifest.Value.Int(clockStep));
        row.put("smoothingTimeTicks", new RunManifest.Value.Dec(0.0));
        row.put("frameTimeSeconds", new RunManifest.Value.Dec(0.05));
        row.put("frameTimeCounter", new RunManifest.Value.Dec(clockStep * 0.05));
        row.put("clockStep", new RunManifest.Value.Int(clockStep));
        row.put("entityCount", new RunManifest.Value.Int(0));
        row.put("durationMillis", new RunManifest.Value.Int(5));
        return row;
    }

    private static java.util.Map<String, RunManifest.Value> imageRow(String kind, String id,
            int ordinal, int index) {
        java.util.SortedMap<String, RunManifest.Value> row = new java.util.TreeMap<>();
        row.put("captureKind", new RunManifest.Value.Token(kind));
        row.put("captureId", new RunManifest.Value.Text(CanonicalText.encodeJson(id)));
        row.put("sampleOrdinal", new RunManifest.Value.Int(ordinal));
        row.put("path", new RunManifest.Value.Text(
            CanonicalText.encodeJson("runs/RUN-T0/shot-a-" + ordinal + ".png")));
        row.put("width", new RunManifest.Value.Int(128));
        row.put("height", new RunManifest.Value.Int(128));
        row.put("pixelSha256", new RunManifest.Value.Token(hex64()));
        return row;
    }

    private static java.util.Map<String, RunManifest.Value> programRow(String slot, String status,
            String from, boolean sourcePresent, String ownBuild, String driverLog, int index) {
        java.util.SortedMap<String, RunManifest.Value> row = new java.util.TreeMap<>();
        row.put("slot", new RunManifest.Value.Token(slot));
        row.put("status", new RunManifest.Value.Token(status));
        row.put("from", new RunManifest.Value.Text(from.isEmpty() ? "" : from));
        row.put("sourcePresent", new RunManifest.Value.Bool(sourcePresent));
        row.put("ownBuild", new RunManifest.Value.Token(ownBuild));
        row.put("driverLog", new RunManifest.Value.Text(
            driverLog.isEmpty() ? "" : CanonicalText.encodeJson(driverLog)));
        return row;
    }

    private static java.util.Map<String, RunManifest.Value> stepRow(String phase, int captureIndex,
            int ordinal, long clockStep) {
        java.util.SortedMap<String, RunManifest.Value> row = new java.util.TreeMap<>();
        row.put("phase", new RunManifest.Value.Token(phase));
        row.put("captureIndex", new RunManifest.Value.Int(captureIndex));
        row.put("ordinal", new RunManifest.Value.Int(ordinal));
        row.put("validation", new RunManifest.Value.Token("VALID"));
        row.put("registryGeneration", new RunManifest.Value.Int(0));
        row.put("frameId", new RunManifest.Value.Int(clockStep));
        row.put("worldEpoch", new RunManifest.Value.Int(0));
        row.put("logicalTick", new RunManifest.Value.Int(clockStep));
        row.put("smoothingTimeTicks", new RunManifest.Value.Dec(0.0));
        row.put("frameTimeSeconds", new RunManifest.Value.Dec(0.05));
        row.put("frameCounter", new RunManifest.Value.Int(clockStep));
        row.put("frameTimeCounter", new RunManifest.Value.Dec(clockStep * 0.05));
        row.put("worldTick", new RunManifest.Value.Int(100 + clockStep));
        row.put("animationTick", new RunManifest.Value.Int(clockStep));
        row.put("partialTicks", new RunManifest.Value.Dec(0.0));
        row.put("clockStep", new RunManifest.Value.Int(clockStep));
        row.put("clientTicks", new RunManifest.Value.Int(clockStep));
        row.put("serverTicks", new RunManifest.Value.Int(clockStep));
        row.put("acceptedFrames", new RunManifest.Value.Int(clockStep));
        row.put("finalizedFrames", new RunManifest.Value.Int(clockStep));
        return row;
    }

    private static String hex64() {
        return "3f7a1c9e2b8d4f60a5c3e71d9b0f4a2c6e8d13579bdf2468ace013579bdf2468";
    }

    private static String hex128() {
        return hex64() + hex64();
    }

    private Manifests() {
    }
}
