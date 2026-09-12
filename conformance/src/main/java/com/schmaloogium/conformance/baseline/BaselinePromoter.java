// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.baseline;

import com.schmaloogium.conformance.capture.RunManifest;
import com.schmaloogium.conformance.diff.PngRaster;
import com.schmaloogium.conformance.fixture.FixtureCache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * §4.7.3 step 3, invoked only after a human reviewed the contact sheet: promotes each
 * captured raster to its canonical content-addressed cache path and writes/merges the
 * committed baseline manifest with the run's environment identities, approver and date.
 * Never automatic; there is no auto-approve flag anywhere.
 */
public final class BaselinePromoter {

    private BaselinePromoter() {
    }

    public static Path rasterPath(FixtureCache cache, String packId, String packVersion, String sceneId,
            BaselineRecord r) {
        return cache.baselines().resolve(packId + "@" + packVersion).resolve(sceneId)
            .resolve(r.optionStateSha256()).resolve(r.captureKind()).resolve(r.captureId())
            .resolve(Long.toString(r.sampleOrdinal())).resolve(r.pixelSha256() + ".png");
    }

    public static Path manifestPath(Path repoRoot, String packId, String packVersion, String sceneId) {
        return repoRoot.resolve("conformance").resolve("baselines").resolve(packId + "@" + packVersion)
            .resolve(sceneId + ".baseline");
    }

    /** Promotes every image of a COMPLETE run; returns the written manifest path. */
    public static Path approve(FixtureCache cache, Path repoRoot, Path runDir, RunManifest manifest,
            String manifestSha256, String machineClass, String toleranceProfile, String approvedBy,
            String approvedOn) throws IOException {
        if (!manifest.token("run.exitStatus").equals("COMPLETE")) {
            throw new IllegalStateException("only a COMPLETE run can be approved");
        }
        if (approvedBy == null || approvedBy.isBlank()) {
            throw new IllegalArgumentException("approvedBy is required (a human, named)");
        }
        String packId = manifest.text("pack.id");
        String packVersion = manifest.text("pack.version");
        String sceneId = manifest.token("run.sceneId");
        Path manifestPath = manifestPath(repoRoot, packId, packVersion, sceneId);
        BaselineManifest existing = Files.exists(manifestPath)
            ? BaselineManifest.parse(Files.readString(manifestPath, StandardCharsets.UTF_8))
            : new BaselineManifest(packId, packVersion, sceneId, List.of());
        List<BaselineRecord> promoted = new ArrayList<>();
        for (RunManifest.Row image : manifest.family("images")) {
            Path source = runDir.resolve(image.text("path"));
            PngRaster raster = PngRaster.read(source);
            if (!raster.pixelSha256().equals(image.token("pixelSha256"))) {
                throw new IllegalStateException("image " + source + " does not match its manifest hash");
            }
            BaselineRecord record = new BaselineRecord(
                image.token("captureKind"), image.text("captureId"), image.integer("sampleOrdinal"),
                manifest.token("pack.optionStateSha256"), image.token("pixelSha256"),
                (int) image.integer("width"), (int) image.integer("height"), toleranceProfile,
                machineClass, approvedBy, approvedOn, manifest.token("run.id"), manifestSha256,
                manifest.token("run.sceneHash"), manifest.token("environment.worldGenerationSha256"),
                manifest.token("environment.worldSha256"),
                manifest.token("environment.externalModSetSha256"),
                manifest.token("environment.subjectModId"),
                manifest.token("environment.subjectJarSha256"),
                manifest.token("environment.modSetSha256"));
            Path target = rasterPath(cache, packId, packVersion, sceneId, record);
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            existing = existing.withRecord(record);
            promoted.add(record);
        }
        Files.createDirectories(manifestPath.getParent());
        Files.writeString(manifestPath, existing.render(), StandardCharsets.UTF_8);
        return manifestPath;
    }
}
