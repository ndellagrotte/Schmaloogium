// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.diff.PngRaster;
import com.schmaloogium.conformance.scene.CapturePlan;
import com.schmaloogium.conformance.scene.OptionStateDigest;
import com.schmaloogium.conformance.wire.FlatDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * §4.5.1 step 9 / §5.1.1: before atomic publication the runner compares the agent's
 * temporary manifest against the authoritative plan — pack facts, every clock and
 * environment field, the full mod inventory, world identities, both option maps and the
 * recomputed option-state digest, the plan hash — and verifies every image record against
 * the file on disk (decodes, dimensions, pixel SHA-256). Missing or unequal values reject.
 */
public final class ManifestValidator {

    private ManifestValidator() {
    }

    /** Returns the list of mismatches; empty means the manifest may be published. */
    public static List<String> validate(RunManifest m, CapturePlan plan, Path runDir) throws IOException {
        List<String> problems = new ArrayList<>();
        FlatDocument p = plan.document();
        eq(problems, "run.id", m.token("run.id"), p.token("run.id"));
        eq(problems, "run.sceneId", m.token("run.sceneId"), p.token("scene.id"));
        eq(problems, "run.sceneHash", m.token("run.sceneHash"), p.token("scene.hash"));
        eq(problems, "run.planHash", m.token("run.planHash"), plan.planHash());
        for (String k : new String[] {"pack.id", "pack.version", "pack.licence"}) {
            eq(problems, k, m.text(k), p.text(k));
        }
        for (String k : new String[] {"pack.acquisitionMode", "pack.archiveSha512", "pack.optionStateSha256"}) {
            eq(problems, k, m.token(k), p.token(k));
        }
        Map<String, String> packOptions = rows(m, "pack.options");
        Map<String, String> engineOptions = rows(m, "pack.engineOptions");
        eq(problems, "pack.options", packOptions.toString(),
            plan.familyMap("pack.options", "name", "value").toString());
        eq(problems, "pack.engineOptions", engineOptions.toString(),
            plan.familyMap("pack.engineOptions", "name", "value").toString());
        eq(problems, "pack.optionStateSha256 (recomputed)", m.token("pack.optionStateSha256"),
            OptionStateDigest.sha256(packOptions, engineOptions));
        eq(problems, "clock.frameTimeNanos", m.integer("clock.frameTimeNanos"), p.integer("clock.frameTimeNanos"));
        eq(problems, "clock.ticksPerFrame", m.integer("clock.ticksPerFrame"), p.integer("clock.ticksPerFrame"));
        eq(problems, "clock.partialTicks", m.decimal("clock.partialTicks"), p.decimal("clock.partialTicks"));
        for (String k : new String[] {"environment.subjectModId", "environment.subjectJarSha256",
            "environment.modSetSha256", "environment.externalModSetSha256", "environment.minecraftVersion",
            "environment.worldGenerationSha256", "environment.worldSha256"}) {
            eq(problems, k, m.token(k), p.token(k));
        }
        Map<String, String> mods = new java.util.TreeMap<>();
        for (RunManifest.Row row : m.family("environment.mods")) {
            mods.put(row.text("id"), row.token("sha256"));
        }
        eq(problems, "environment.mods", mods.toString(),
            plan.familyMap("environment.mods", "id", "sha256").toString());
        eq(problems, "environment.mods (recomputed modSetSha256)", m.token("environment.modSetSha256"),
            LaunchInventory.modSetHash(mods, false));
        eq(problems, "environment.mods (recomputed externalModSetSha256)",
            m.token("environment.externalModSetSha256"), LaunchInventory.modSetHash(mods, true));
        // captures must mirror the plan's kind/id/warm-up/sample domain in plan order
        List<RunManifest.Row> captures = m.family("captures");
        if (m.token("run.exitStatus").equals("COMPLETE")) {
            if (captures.size() != plan.captureCount()) {
                problems.add("captures.count " + captures.size() + " != plan " + plan.captureCount());
            } else {
                for (int i = 0; i < captures.size(); i++) {
                    RunManifest.Row c = captures.get(i);
                    eq(problems, "captures." + i + ".kind", c.token("kind"), plan.captureKind(i));
                    eq(problems, "captures." + i + ".id", c.text("id"), plan.captureId(i));
                    eq(problems, "captures." + i + ".plannedWarmupFrames", c.integer("plannedWarmupFrames"),
                        (long) plan.warmupFrames(i));
                    eq(problems, "captures." + i + ".plannedSamples", c.integer("plannedSamples"),
                        (long) plan.sampleCount(i));
                    eq(problems, "captures." + i + ".captureStartSample", c.integer("captureStartSample"),
                        (long) plan.captureStartSample(i));
                    eq(problems, "captures." + i + ".captureSampleCount", c.integer("captureSampleCount"),
                        (long) plan.captureSampleCount(i));
                }
                for (RunManifest.Row f : m.family("frames")) {
                    int ci = indexOf(plan, f.token("captureKind"), f.text("captureId"));
                    if (ci < 0) {
                        problems.add("frames." + f.index() + " names a capture outside the plan");
                        continue;
                    }
                    int ordinal = (int) f.integer("sampleOrdinal");
                    if (ordinal >= plan.sampleCount(ci)) {
                        problems.add("frames." + f.index() + " ordinal beyond the plan");
                        continue;
                    }
                    var cur = plan.sample(ci, ordinal);
                    var prev = plan.sample(ci, Math.max(0, ordinal - 1));
                    pose(problems, "frames." + f.index() + ".plannedCurrent", f, "plannedCurrent", cur);
                    pose(problems, "frames." + f.index() + ".plannedPrevious", f, "plannedPrevious", prev);
                }
            }
        }
        for (RunManifest.Row image : m.family("images")) {
            String rel = image.text("path");
            if (rel.startsWith("/") || rel.contains("..")) {
                problems.add("images." + image.index() + ".path must be run-relative: " + rel);
                continue;
            }
            Path file = runDir.resolve(rel);
            if (!Files.isRegularFile(file)) {
                problems.add("images." + image.index() + " file absent: " + rel);
                continue;
            }
            PngRaster raster = PngRaster.read(file);
            if (raster.width() != image.integer("width") || raster.height() != image.integer("height")) {
                problems.add("images." + image.index() + " dimensions differ from the file");
            }
            if (!raster.pixelSha256().equals(image.token("pixelSha256"))) {
                problems.add("images." + image.index() + " pixelSha256 differs from the decoded raster");
            }
        }
        return problems;
    }

    private static int indexOf(CapturePlan plan, String kind, String id) {
        for (int i = 0; i < plan.captureCount(); i++) {
            if (plan.captureKind(i).equals(kind) && plan.captureId(i).equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private static void pose(List<String> problems, String where, RunManifest.Row f, String role,
            com.schmaloogium.conformance.scene.SceneSpec.Pose pose) {
        eq(problems, where + ".posX", f.decimal(role + ".posX"), pose.x());
        eq(problems, where + ".posY", f.decimal(role + ".posY"), pose.y());
        eq(problems, where + ".posZ", f.decimal(role + ".posZ"), pose.z());
        eq(problems, where + ".yaw", f.decimal(role + ".yaw"), pose.yaw());
        eq(problems, where + ".pitch", f.decimal(role + ".pitch"), pose.pitch());
    }

    private static Map<String, String> rows(RunManifest m, String family) {
        Map<String, String> out = new java.util.TreeMap<>();
        for (RunManifest.Row row : m.family(family)) {
            out.put(row.text("name"), row.text("value"));
        }
        return out;
    }

    private static void eq(List<String> problems, String what, Object manifest, Object plan) {
        if (!manifest.equals(plan)) {
            problems.add(what + ": manifest " + manifest + " != plan " + plan);
        }
    }
}
