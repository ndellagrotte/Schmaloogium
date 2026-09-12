// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.baseline;

import com.schmaloogium.conformance.wire.FlatDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

/**
 * The committed per-scene baseline manifest
 * {@code conformance/baselines/<packId>@<version>/<sceneId>.baseline}: a canonical flat
 * document, schema {@code schmaloogium.baseline/1}, with the pack/scene identity and dense
 * {@code records.<n>.*} rows sorted uniquely by the full record key. Manifests only — the
 * rasters live in the cache ([D-P2-6]).
 */
public record BaselineManifest(String packId, String packVersion, String sceneId,
        List<BaselineRecord> records) {

    public static final String SCHEMA_LINE = "schema = schmaloogium.baseline/1";

    public BaselineManifest {
        TreeSet<BaselineRecord> sorted = new TreeSet<>(records);
        if (sorted.size() != records.size()) {
            throw new IllegalArgumentException("duplicate baseline record keys");
        }
        records = List.copyOf(sorted);
    }

    public Optional<BaselineRecord> find(String kind, String id, long ordinal, String optionState,
            String profile, String machine) {
        return records.stream()
            .filter(r -> r.sameKey(kind, id, ordinal, optionState, profile, machine))
            .findFirst();
    }

    /** Replaces the record with the same full key, keeps every other record. */
    public BaselineManifest withRecord(BaselineRecord record) {
        List<BaselineRecord> out = new ArrayList<>();
        for (BaselineRecord r : records) {
            if (!r.sameKey(record.captureKind(), record.captureId(), record.sampleOrdinal(),
                    record.optionStateSha256(), record.toleranceProfile(), record.machineClass())) {
                out.add(r);
            }
        }
        out.add(record);
        return new BaselineManifest(packId, packVersion, sceneId, out);
    }

    public FlatDocument document() {
        FlatDocument.Builder b = FlatDocument.builder(SCHEMA_LINE);
        b.text("pack.id", packId).text("pack.version", packVersion).token("scene.id", sceneId);
        b.integer("records.count", records.size());
        for (int i = 0; i < records.size(); i++) {
            BaselineRecord r = records.get(i);
            String p = "records." + i + ".";
            b.token(p + "captureKind", r.captureKind());
            b.text(p + "captureId", r.captureId());
            b.integer(p + "sampleOrdinal", r.sampleOrdinal());
            b.token(p + "optionStateSha256", r.optionStateSha256());
            b.token(p + "pixelSha256", r.pixelSha256());
            b.integer(p + "width", r.width());
            b.integer(p + "height", r.height());
            b.token(p + "toleranceProfile", r.toleranceProfile());
            b.text(p + "machineClass", r.machineClass());
            b.text(p + "approvedBy", r.approvedBy());
            b.text(p + "approvedOn", r.approvedOn());
            b.token(p + "runId", r.runId());
            b.token(p + "runManifestSha", r.runManifestSha());
            b.token(p + "sceneSha256", r.sceneSha256());
            b.token(p + "worldGenerationSha256", r.worldGenerationSha256());
            b.token(p + "worldSha256", r.worldSha256());
            b.token(p + "externalModSetSha256", r.externalModSetSha256());
            b.token(p + "subjectModId", r.subjectModId());
            b.token(p + "subjectJarSha256", r.subjectJarSha256());
            b.token(p + "modSetSha256", r.modSetSha256());
        }
        return b.build();
    }

    public String render() {
        return document().render();
    }

    public static BaselineManifest parse(String text) {
        FlatDocument d = FlatDocument.parse(text, SCHEMA_LINE);
        int count = d.count("records");
        List<BaselineRecord> records = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String p = "records." + i + ".";
            records.add(new BaselineRecord(
                d.token(p + "captureKind"), d.text(p + "captureId"), d.integer(p + "sampleOrdinal"),
                d.token(p + "optionStateSha256"), d.token(p + "pixelSha256"),
                (int) d.integer(p + "width"), (int) d.integer(p + "height"),
                d.token(p + "toleranceProfile"), d.text(p + "machineClass"),
                d.text(p + "approvedBy"), d.text(p + "approvedOn"), d.token(p + "runId"),
                d.token(p + "runManifestSha"), d.token(p + "sceneSha256"),
                d.token(p + "worldGenerationSha256"), d.token(p + "worldSha256"),
                d.token(p + "externalModSetSha256"), d.token(p + "subjectModId"),
                d.token(p + "subjectJarSha256"), d.token(p + "modSetSha256")));
        }
        int expectedKeys = 4 + count * 20;
        if (d.entries().size() != expectedKeys) {
            throw new IllegalArgumentException("baseline manifest has unknown or missing keys");
        }
        BaselineManifest manifest = new BaselineManifest(d.text("pack.id"), d.text("pack.version"),
            d.token("scene.id"), records);
        if (!manifest.render().equals(text)) {
            throw new IllegalArgumentException("baseline manifest is not canonical (records must be"
                + " sorted by the full record key)");
        }
        return manifest;
    }
}
