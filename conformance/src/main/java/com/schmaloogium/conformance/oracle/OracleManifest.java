// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.oracle;

import com.schmaloogium.conformance.wire.FlatDocument;
import com.schmaloogium.conformance.wire.Hashes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;

/**
 * The committed oracle manifest {@code conformance/oracle/<packId>@<version>/<sceneId>.oracle}
 * (PHASE_2_DOC §4.8.2 step 6, D-P2-6): provenance of the manual OptiFine capture (build, GPU,
 * driver, date, operator, the same world identities the candidate carries) and one
 * {@link OracleRecord} per static shot. Images never enter the repository; they live at
 * {@code <cache>/oracle/<packId>@<version>/<sceneId>/<optionStateSha256>/SHOT/<shotId>/<ordinal>/<pixelSha256>.png}.
 */
public record OracleManifest(
        String packId,
        String packVersion,
        String sceneId,
        String optifineBuild,
        String gpu,
        String driver,
        String capturedOn,
        String operator,
        String worldGenerationSha256,
        String worldSha256,
        String externalModSetSha256,
        List<OracleRecord> records) {

    public static final String SCHEMA_LINE = "schema = schmaloogium.oracle/1";
    private static final int HEADER_KEYS = 12;
    private static final int RECORD_KEYS = 11;

    public OracleManifest {
        Objects.requireNonNull(packId, "packId");
        Objects.requireNonNull(packVersion, "packVersion");
        Objects.requireNonNull(sceneId, "sceneId");
        for (String s : new String[] {optifineBuild, gpu, driver, capturedOn, operator}) {
            if (s == null || s.isBlank()) {
                throw new IllegalArgumentException("oracle provenance fields must all be recorded");
            }
        }
        for (String h : new String[] {worldGenerationSha256, worldSha256, externalModSetSha256}) {
            if (!Hashes.isHex(h, 64)) {
                throw new IllegalArgumentException("oracle world identities must be 64 lowercase hex");
            }
        }
        TreeSet<OracleRecord> sorted = new TreeSet<>(records);
        if (sorted.size() != records.size()) {
            throw new IllegalArgumentException("duplicate oracle record keys");
        }
        records = List.copyOf(sorted);
    }

    public Optional<OracleRecord> find(String kind, String id, long ordinal, String optionState) {
        return records.stream().filter(r -> r.sameKey(kind, id, ordinal, optionState)).findFirst();
    }

    /** The provenance line every T2 verdict prints next to the candidate's (§4.8.3). */
    public String provenance() {
        return "OptiFine " + optifineBuild + ", " + gpu + ", " + driver + ", " + capturedOn + ", " + operator;
    }

    public FlatDocument document() {
        FlatDocument.Builder b = FlatDocument.builder(SCHEMA_LINE);
        b.text("pack.id", packId).text("pack.version", packVersion).token("scene.id", sceneId);
        b.text("oracle.optifineBuild", optifineBuild).text("oracle.gpu", gpu).text("oracle.driver", driver);
        b.text("oracle.capturedOn", capturedOn).text("oracle.operator", operator);
        b.token("oracle.worldGenerationSha256", worldGenerationSha256);
        b.token("oracle.worldSha256", worldSha256);
        b.token("oracle.externalModSetSha256", externalModSetSha256);
        b.integer("records.count", records.size());
        for (int i = 0; i < records.size(); i++) {
            OracleRecord r = records.get(i);
            String p = "records." + i + ".";
            b.token(p + "captureKind", r.captureKind());
            b.text(p + "captureId", r.captureId());
            b.integer(p + "sampleOrdinal", r.sampleOrdinal());
            b.token(p + "optionStateSha256", r.optionStateSha256());
            b.token(p + "pixelSha256", r.pixelSha256());
            b.integer(p + "width", r.width());
            b.integer(p + "height", r.height());
            b.token(p + "timing.control", OracleRecord.CONTROL);
            b.text(p + "timing.evidence", r.timingEvidence());
            b.token(p + "timing.comparability", r.timingComparability());
            b.text(p + "timing.comparabilityReason", r.timingComparabilityReason());
        }
        return b.build();
    }

    public String render() {
        return document().render();
    }

    public static OracleManifest parse(String text) {
        FlatDocument d = FlatDocument.parse(text, SCHEMA_LINE);
        int count = d.count("records");
        List<OracleRecord> records = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String p = "records." + i + ".";
            if (!d.token(p + "timing.control").equals(OracleRecord.CONTROL)) {
                throw new IllegalArgumentException("oracle timing.control must be " + OracleRecord.CONTROL);
            }
            records.add(new OracleRecord(d.token(p + "captureKind"), d.text(p + "captureId"),
                d.integer(p + "sampleOrdinal"), d.token(p + "optionStateSha256"), d.token(p + "pixelSha256"),
                (int) d.integer(p + "width"), (int) d.integer(p + "height"), d.text(p + "timing.evidence"),
                d.token(p + "timing.comparability"), d.text(p + "timing.comparabilityReason")));
        }
        if (d.entries().size() != HEADER_KEYS + count * RECORD_KEYS) {
            throw new IllegalArgumentException("oracle manifest has unknown or missing keys");
        }
        OracleManifest manifest = new OracleManifest(d.text("pack.id"), d.text("pack.version"),
            d.token("scene.id"), d.text("oracle.optifineBuild"), d.text("oracle.gpu"), d.text("oracle.driver"),
            d.text("oracle.capturedOn"), d.text("oracle.operator"), d.token("oracle.worldGenerationSha256"),
            d.token("oracle.worldSha256"), d.token("oracle.externalModSetSha256"), records);
        if (!manifest.render().equals(text)) {
            throw new IllegalArgumentException("oracle manifest is not canonical (records sorted by key)");
        }
        return manifest;
    }
}
