// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.oracle;

import com.schmaloogium.conformance.wire.Hashes;

import java.util.Objects;

/**
 * One manually captured OptiFine G6_pre1 oracle image (PHASE_2_DOC §4.8.2 step 6): the static
 * SHOT sample it stands for, its raster identity, and the timing/comparability evidence the
 * operator recorded. {@code timing.control} is always {@code UNCONTROLLED} for this protocol;
 * only {@code ESTABLISHED} comparability admits a T2 verdict, anything else is
 * {@code SKIPPED("timing not comparable")}.
 */
public record OracleRecord(
        String captureKind,
        String captureId,
        long sampleOrdinal,
        String optionStateSha256,
        String pixelSha256,
        int width,
        int height,
        String timingEvidence,
        String timingComparability,
        String timingComparabilityReason) implements Comparable<OracleRecord> {

    public static final String CONTROL = "UNCONTROLLED";

    public OracleRecord {
        Objects.requireNonNull(captureKind, "captureKind");
        Objects.requireNonNull(captureId, "captureId");
        if (!captureKind.equals("SHOT")) {
            throw new IllegalArgumentException("oracle records are static SHOT samples only (§4.8.3)");
        }
        for (String h : new String[] {optionStateSha256, pixelSha256}) {
            if (!Hashes.isHex(h, 64)) {
                throw new IllegalArgumentException("oracle hashes must be 64 lowercase hex: " + h);
            }
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("oracle dimensions must be positive");
        }
        if (sampleOrdinal < 0) {
            throw new IllegalArgumentException("sampleOrdinal must be non-negative");
        }
        Objects.requireNonNull(timingEvidence, "timingEvidence");
        if (timingEvidence.isBlank()) {
            throw new IllegalArgumentException("timing.evidence must be an observation or \"UNAVAILABLE\"");
        }
        if (!timingComparability.equals("ESTABLISHED") && !timingComparability.equals("UNAVAILABLE")) {
            throw new IllegalArgumentException("timing.comparability must be ESTABLISHED or UNAVAILABLE");
        }
        Objects.requireNonNull(timingComparabilityReason, "timingComparabilityReason");
        if (timingComparabilityReason.isBlank()) {
            throw new IllegalArgumentException("timing.comparabilityReason is required");
        }
    }

    public String recordKey() {
        return captureKind + "/" + captureId + "/" + sampleOrdinal + "@" + optionStateSha256;
    }

    public boolean sameKey(String kind, String id, long ordinal, String optionState) {
        return captureKind.equals(kind) && captureId.equals(id) && sampleOrdinal == ordinal
            && optionStateSha256.equals(optionState);
    }

    @Override
    public int compareTo(OracleRecord o) {
        return recordKey().compareTo(o.recordKey());
    }
}
