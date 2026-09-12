// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.scene.CapturePlan;
import com.schmaloogium.conformance.wire.FlatDocument;

/**
 * The runner-synthesised failure manifest (§4.5.1 step 10, §4.5.4): schema-valid,
 * {@code exitStatus = FAILED|SKIPPED}, a serialized reason, authoritative plan facts only,
 * unknown client evidence as the false / {@code NOT_REACHED} values, zero counts for every
 * repeated block, and no fabricated actual counters. Such a manifest deterministically fails
 * T0 and can never be mistaken for a green run.
 */
public final class FailureManifests {

    private FailureManifests() {
    }

    public static RunManifest fromPlan(CapturePlan plan, String exitStatus, String reason,
            String uncaughtException, long hangCeilingMillis, boolean timedOut, String os, String jvm,
            String cleanroomVersion) {
        if (!exitStatus.equals("FAILED") && !exitStatus.equals("SKIPPED")) {
            throw new IllegalArgumentException("a synthesized manifest is FAILED or SKIPPED");
        }
        if (reason == null || reason.isEmpty()) {
            throw new IllegalArgumentException("failureReason must be non-empty");
        }
        FlatDocument p = plan.document();
        RunManifest.Builder b = RunManifest.builder();
        b.token("run.id", p.token("run.id"))
            .token("run.sceneId", p.token("scene.id"))
            .token("run.sceneHash", p.token("scene.hash"))
            .token("run.planHash", plan.planHash())
            .token("run.exitStatus", exitStatus)
            .text("run.failureReason", reason)
            .text("run.uncaughtException", uncaughtException == null ? "" : uncaughtException)
            .token("run.compatVerdict", "NOT_REACHED")
            .bool("run.shadersActiveThroughout", false)
            .integer("run.hangCeilingMillis", hangCeilingMillis)
            .bool("run.timedOut", timedOut);
        b.bool("frontEnd.completed", false).bool("frontEnd.packConfigurationProduced", false);
        b.token("environment.os", os).token("environment.jvm", jvm)
            .token("environment.minecraftVersion", p.token("environment.minecraftVersion"))
            .token("environment.cleanroomVersion", cleanroomVersion)
            .token("environment.worldSha256", p.token("environment.worldSha256"))
            .token("environment.modSetSha256", p.token("environment.modSetSha256"))
            .token("environment.subjectModId", p.token("environment.subjectModId"))
            .token("environment.subjectJarSha256", p.token("environment.subjectJarSha256"))
            .token("environment.externalModSetSha256", p.token("environment.externalModSetSha256"))
            .token("environment.worldGenerationSha256", p.token("environment.worldGenerationSha256"));
        int mods = p.count("environment.mods");
        for (int i = 0; i < mods; i++) {
            java.util.SortedMap<String, RunManifest.Value> row = new java.util.TreeMap<>();
            row.put("id", new RunManifest.Value.Text(p.text("environment.mods." + i + ".id")));
            row.put("sha256", new RunManifest.Value.Token(p.token("environment.mods." + i + ".sha256")));
            b.row("environment.mods", i, row);
        }
        b.set("environment.mods.count", new RunManifest.Value.Int(mods));
        b.count("environment.resourcePacks", 0);
        b.integer("clock.frameTimeNanos", p.integer("clock.frameTimeNanos"))
            .integer("clock.ticksPerFrame", p.integer("clock.ticksPerFrame"))
            .decimal("clock.partialTicks", p.decimal("clock.partialTicks"));
        b.bool("gl.available", false);
        b.text("pack.id", p.text("pack.id")).text("pack.version", p.text("pack.version"))
            .token("pack.acquisitionMode", p.token("pack.acquisitionMode"))
            .token("pack.archiveSha512", p.token("pack.archiveSha512"))
            .text("pack.licence", p.text("pack.licence"))
            .token("pack.optionStateSha256", p.token("pack.optionStateSha256"));
        copyNamed(b, p, "pack.options");
        copyNamed(b, p, "pack.engineOptions");
        b.bool("resources.available", false).bool("hooks.available", false);
        b.bool("timing.available", false).bool("timing.complete", false)
            .text("timing.failureReason", reason).token("timing.restoration", "NOT_REACHED")
            .bool("timing.identitiesUnchanged", false);
        b.count("timing.steps", 0);
        for (String family : new String[] {"programs", "captures", "frames", "gl_errors", "images",
            "diagnostics"}) {
            b.count(family, 0);
        }
        return b.build();
    }

    private static void copyNamed(RunManifest.Builder b, FlatDocument p, String family) {
        int count = p.count(family);
        for (int i = 0; i < count; i++) {
            java.util.SortedMap<String, RunManifest.Value> row = new java.util.TreeMap<>();
            row.put("name", new RunManifest.Value.Text(p.text(family + "." + i + ".name")));
            row.put("value", new RunManifest.Value.Text(p.text(family + "." + i + ".value")));
            b.row(family, i, row);
        }
        b.set(family + ".count", new RunManifest.Value.Int(count));
    }
}
