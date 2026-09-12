// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.baseline;

import com.schmaloogium.conformance.wire.Hashes;

import java.util.Objects;

/**
 * One approved baseline (§4.7.2): the full record key
 * {@code (captureKind, captureId, sampleOrdinal, optionStateSha256, toleranceProfile, machineClass)},
 * the raster identity and dimensions, approval provenance and the environment identities
 * that §4.7.4 compares for invalidation.
 */
public record BaselineRecord(
        String captureKind,
        String captureId,
        long sampleOrdinal,
        String optionStateSha256,
        String pixelSha256,
        int width,
        int height,
        String toleranceProfile,
        String machineClass,
        String approvedBy,
        String approvedOn,
        String runId,
        String runManifestSha,
        String sceneSha256,
        String worldGenerationSha256,
        String worldSha256,
        String externalModSetSha256,
        String subjectModId,
        String subjectJarSha256,
        String modSetSha256) implements Comparable<BaselineRecord> {

    public BaselineRecord {
        Objects.requireNonNull(captureKind, "captureKind");
        Objects.requireNonNull(captureId, "captureId");
        for (String h : new String[] {optionStateSha256, pixelSha256, runManifestSha, sceneSha256,
            worldGenerationSha256, worldSha256, externalModSetSha256, subjectJarSha256, modSetSha256}) {
            if (!Hashes.isHex(h, 64)) {
                throw new IllegalArgumentException("baseline hashes must be 64 lowercase hex: " + h);
            }
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("baseline dimensions must be positive");
        }
        if (sampleOrdinal < 0) {
            throw new IllegalArgumentException("sampleOrdinal must be non-negative");
        }
        Objects.requireNonNull(toleranceProfile, "toleranceProfile");
        Objects.requireNonNull(machineClass, "machineClass");
        Objects.requireNonNull(approvedBy, "approvedBy");
        Objects.requireNonNull(approvedOn, "approvedOn");
        Objects.requireNonNull(runId, "runId");
        Objects.requireNonNull(subjectModId, "subjectModId");
    }

    /** The canonical JSON-array record key of §4.2.6. */
    public String recordKey() {
        return "[" + json(captureKind) + "," + json(captureId) + "," + sampleOrdinal + ","
            + json(optionStateSha256) + "," + json(toleranceProfile) + "," + json(machineClass) + "]";
    }

    private static String json(String s) {
        return com.schmaloogium.conformance.wire.CanonicalText.encodeJson(s);
    }

    public boolean sameKey(String kind, String id, long ordinal, String optionState, String profile,
            String machine) {
        return captureKind.equals(kind) && captureId.equals(id) && sampleOrdinal == ordinal
            && optionStateSha256.equals(optionState) && toleranceProfile.equals(profile)
            && machineClass.equals(machine);
    }

    @Override
    public int compareTo(BaselineRecord o) {
        int c = captureKind.compareTo(o.captureKind);
        if (c == 0) {
            c = captureId.compareTo(o.captureId);
        }
        if (c == 0) {
            c = Long.compare(sampleOrdinal, o.sampleOrdinal);
        }
        if (c == 0) {
            c = optionStateSha256.compareTo(o.optionStateSha256);
        }
        if (c == 0) {
            c = toleranceProfile.compareTo(o.toleranceProfile);
        }
        if (c == 0) {
            c = machineClass.compareTo(o.machineClass);
        }
        return c;
    }
}
