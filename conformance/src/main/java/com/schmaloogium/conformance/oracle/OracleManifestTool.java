// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.oracle;

import com.schmaloogium.conformance.diff.PngRaster;
import com.schmaloogium.conformance.wire.Hashes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * PHASE_2_DOC §4.8.2 step 6: walks the canonical oracle image tree
 * {@code <cache>/oracle/<packId>@<version>/<sceneId>/<optionStateSha256>/SHOT/<shotId>/<ordinal>/<pixelSha256>.png},
 * hashes every image's pixel raster (the file name must already carry that hash: a mismatch is
 * a misfiled screenshot, refused), and writes the manifest from the operator's provenance.
 * Timing evidence is recorded per manifest here (one manual session per scene); the operator
 * states {@code ESTABLISHED} only with independent evidence (§4.8.2), otherwise
 * {@code UNAVAILABLE} and T2 reports {@code SKIPPED}.
 */
public final class OracleManifestTool {

    /** Everything the operator supplies; world identities come from the candidate run manifest. */
    public record Provenance(String optifineBuild, String gpu, String driver, String capturedOn,
            String operator, String timingEvidence, String timingComparability,
            String timingComparabilityReason, String worldGenerationSha256, String worldSha256,
            String externalModSetSha256) {
    }

    private OracleManifestTool() {
    }

    /** Canonical image path for one record. */
    public static Path imagePath(Path oracleRoot, String packId, String packVersion, String sceneId,
            OracleRecord r) {
        return sceneRoot(oracleRoot, packId, packVersion, sceneId).resolve(r.optionStateSha256())
            .resolve(r.captureKind()).resolve(r.captureId()).resolve(Long.toString(r.sampleOrdinal()))
            .resolve(r.pixelSha256() + ".png");
    }

    public static Path sceneRoot(Path oracleRoot, String packId, String packVersion, String sceneId) {
        return oracleRoot.resolve(packId + "@" + packVersion).resolve(sceneId);
    }

    public static OracleManifest build(Path oracleRoot, String packId, String packVersion, String sceneId,
            Provenance provenance) throws IOException {
        Objects.requireNonNull(provenance, "provenance");
        Path root = sceneRoot(oracleRoot, packId, packVersion, sceneId);
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("no oracle images under " + root + " (§4.8.2 step 5)");
        }
        List<OracleRecord> records = new ArrayList<>();
        try (Stream<Path> files = Files.walk(root)) {
            for (Path png : files.filter(p -> p.toString().endsWith(".png")).sorted().toList()) {
                Path rel = root.relativize(png);
                if (rel.getNameCount() != 5) {
                    throw new IllegalArgumentException("misfiled oracle image (expected"
                        + " <optionState>/SHOT/<shot>/<ordinal>/<pixelSha256>.png): " + rel);
                }
                String optionState = rel.getName(0).toString();
                String kind = rel.getName(1).toString();
                String shot = rel.getName(2).toString();
                long ordinal = Long.parseLong(rel.getName(3).toString());
                String named = rel.getName(4).toString().replaceAll("\\.png$", "");
                PngRaster raster = PngRaster.read(png);
                if (!raster.pixelSha256().equals(named)) {
                    throw new IllegalArgumentException("oracle image " + rel + " is named " + named
                        + " but its pixel raster hashes to " + raster.pixelSha256());
                }
                if (!Hashes.isHex(optionState, 64)) {
                    throw new IllegalArgumentException("oracle option-state directory is not a sha256: " + optionState);
                }
                records.add(new OracleRecord(kind, shot, ordinal, optionState, raster.pixelSha256(),
                    raster.width(), raster.height(), provenance.timingEvidence(),
                    provenance.timingComparability(), provenance.timingComparabilityReason()));
            }
        }
        if (records.isEmpty()) {
            throw new IllegalArgumentException("no oracle images under " + root);
        }
        return new OracleManifest(packId, packVersion, sceneId, provenance.optifineBuild(), provenance.gpu(),
            provenance.driver(), provenance.capturedOn(), provenance.operator(),
            provenance.worldGenerationSha256(), provenance.worldSha256(), provenance.externalModSetSha256(),
            records);
    }

    /** The committed manifest path. */
    public static Path manifestPath(Path repoRoot, String packId, String packVersion, String sceneId) {
        return repoRoot.resolve("conformance").resolve("oracle").resolve(packId + "@" + packVersion)
            .resolve(sceneId + ".oracle");
    }
}
