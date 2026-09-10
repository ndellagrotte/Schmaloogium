// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.shadow.internal.CanonicalSha256;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The immutable Phase-8 hook report projection (PHASE_8_DOC §2.2/§4.13.1). Owned by the
 * application health audit and borrowed immutably by plans. Construction accepts only the
 * complete canonical flattened-v3 catalogue: exactly the sixty-six known hook ids in
 * ascending ASCII order, {@code expected=1} per row, non-negative actual counts, and the
 * disposition implied by {@code actual == expected}. Malformed evidence — missing,
 * duplicate, unknown or group ids, reordering, wrong expected counts, negative counts,
 * or a non-canonical fingerprint — is rejected, never repaired, sorted, or filled.
 *
 * <p>{@code shadowEnabled} is true exactly when all sixty-five non-CLOUD rows are HEALTHY;
 * the CLOUD row gates clouds-in-shadow only. The fingerprint preimage is UTF-8 LF-terminated
 * text beginning {@code ShadowHookHealth/flattened-v3\n}, one
 * {@code hookId|expected|actual|disposition\n} line per catalogue row (base-10, no leading
 * zeros, exact enum names), then {@code shadowEnabled=true\n} or {@code shadowEnabled=false\n}.
 * Older flattened-v1/v2 domains are rejected, not migrated.
 */
public record ShadowHookHealth(
        List<ShadowHookRow> rows, boolean shadowEnabled, ShadowHookFingerprint fingerprint) {

    /** The canonical catalogue domain; any other fingerprint domain is malformed input. */
    public static final String FLATTENED_V3 = "ShadowHookHealth/flattened-v3";

    private static final List<String> CATALOGUE = List.of(
            "H-SHADOW-OUTLINE-01",
            "H8-BLOB-01-REDIRECT",
            "H8-CLOUD-01-RESOLVE",
            "H8-ENTITY-01-FIELD-72740-G-GET",
            "H8-ENTITY-01-FIELD-72740-G-RESOLVE",
            "H8-ENTITY-01-FIELD-72740-G-SET",
            "H8-ENTITY-01-FIELD-72748-H-GET",
            "H8-ENTITY-01-FIELD-72748-H-RESOLVE",
            "H8-ENTITY-01-FIELD-72748-H-SET",
            "H8-ENTITY-01-FIELD-72749-I-GET",
            "H8-ENTITY-01-FIELD-72749-I-RESOLVE",
            "H8-ENTITY-01-FIELD-72749-I-SET",
            "H8-ENTITY-01-FIELD-72750-J-GET",
            "H8-ENTITY-01-FIELD-72750-J-RESOLVE",
            "H8-ENTITY-01-FIELD-72750-J-SET",
            "H8-ENTITY-01-METHOD-RESOLVE",
            "H8-FORGE-01-GET-RESOLVE",
            "H8-FORGE-01-SET-RESOLVE",
            "H8-REBUILD-01-ENTRY",
            "H8-REBUILD-01-SETTER-RESOLVE",
            "H8-RESTORE-01-FIELD-147595-R-GET",
            "H8-RESTORE-01-FIELD-147595-R-RESOLVE",
            "H8-RESTORE-01-FIELD-147595-R-SET",
            "H8-RESTORE-01-FIELD-147596-F-GET",
            "H8-RESTORE-01-FIELD-147596-F-SET",
            "H8-RESTORE-01-FIELD-147597-G-GET",
            "H8-RESTORE-01-FIELD-147597-G-SET",
            "H8-RESTORE-01-FIELD-147602-H-GET",
            "H8-RESTORE-01-FIELD-147602-H-SET",
            "H8-RESTORE-01-FIELD-174994-L-GET",
            "H8-RESTORE-01-FIELD-174994-L-RESOLVE",
            "H8-RESTORE-01-FIELD-174994-L-SET",
            "H8-RESTORE-01-FIELD-174997-H-GET",
            "H8-RESTORE-01-FIELD-174997-H-RESOLVE",
            "H8-RESTORE-01-FIELD-174997-H-SET",
            "H8-RESTORE-01-FIELD-174998-I-GET",
            "H8-RESTORE-01-FIELD-174998-I-RESOLVE",
            "H8-RESTORE-01-FIELD-174998-I-SET",
            "H8-RESTORE-01-FIELD-174999-J-GET",
            "H8-RESTORE-01-FIELD-174999-J-RESOLVE",
            "H8-RESTORE-01-FIELD-174999-J-SET",
            "H8-RESTORE-01-FIELD-175000-K-GET",
            "H8-RESTORE-01-FIELD-175000-K-RESOLVE",
            "H8-RESTORE-01-FIELD-175000-K-SET",
            "H8-RESTORE-01-FIELD-175001-U-GET",
            "H8-RESTORE-01-FIELD-175001-U-RESOLVE",
            "H8-RESTORE-01-FIELD-175001-U-SET",
            "H8-RESTORE-01-FIELD-175002-T-GET",
            "H8-RESTORE-01-FIELD-175002-T-RESOLVE",
            "H8-RESTORE-01-FIELD-175002-T-SET",
            "H8-RESTORE-01-FIELD-175008-N-GET",
            "H8-RESTORE-01-FIELD-175008-N-RESOLVE",
            "H8-RESTORE-01-FIELD-175009-L-GET",
            "H8-RESTORE-01-FIELD-175009-L-RESOLVE",
            "H8-RESTORE-01-FIELD-72755-R-GET",
            "H8-RESTORE-01-FIELD-72755-R-RESOLVE",
            "H8-RESTORE-01-FIELD-72755-R-SET",
            "H8-SLOT-01-FRAME-05",
            "H8-TERRAIN-01-RESOLVE",
            "H8-TRAVERSE-01-COMPILED-VISIBILITY",
            "H8-TRAVERSE-01-NEIGHBOR",
            "H8-TRAVERSE-01-RENDER-CHUNKS-MANY",
            "H8-TRAVERSE-01-SEED-DIRECTIONS",
            "H8-TRAVERSE-01-VISIT-FALLBACK-SEED",
            "H8-TRAVERSE-01-VISIT-NEIGHBOR",
            "H8-TRAVERSE-01-VISIT-SEED");

    private static final String CLOUD_ROW = "H8-CLOUD-01-RESOLVE";

    public ShadowHookHealth {
        rows = validate(rows);
        shadowEnabled = computeShadowEnabled(rows);
        String preimage = fingerprintPreimage(rows, shadowEnabled);
        String computed = CanonicalSha256.of(preimage);
        if (!computed.equals(fingerprint.canonicalSha256())) {
            throw new IllegalArgumentException(
                    "hook-health fingerprint does not match the flattened-v3 preimage");
        }
        fingerprint = new ShadowHookFingerprint(computed);
    }

    /** The complete canonical hook-id catalogue in ascending ASCII order. */
    public static List<String> catalogue() {
        return CATALOGUE;
    }

    /** Whether the named catalogue row gates real shadows (everything except CLOUD). */
    public static boolean gatesShadow(String hookId) {
        return !CLOUD_ROW.equals(hookId);
    }

    /**
     * Freezes audit observations into the canonical projection: rows must be exactly the
     * catalogue (id, order, count), dispositions follow the observed counts, and the
     * aggregate/fingerprint are computed here — never supplied by the audit.
     */
    public static ShadowHookHealth of(List<ShadowHookRow> observed) {
        List<ShadowHookRow> rows = validate(observed);
        boolean enabled = computeShadowEnabled(rows);
        return new ShadowHookHealth(rows, enabled,
                new ShadowHookFingerprint(CanonicalSha256.of(fingerprintPreimage(rows, enabled))));
    }

    private static List<ShadowHookRow> validate(List<ShadowHookRow> rows) {
        Objects.requireNonNull(rows, "rows");
        if (rows.size() != CATALOGUE.size()) {
            throw new IllegalArgumentException(
                    "hook health must carry exactly " + CATALOGUE.size() + " rows, got "
                            + rows.size());
        }
        for (int i = 0; i < CATALOGUE.size(); i++) {
            ShadowHookRow row = Objects.requireNonNull(rows.get(i), "row " + i);
            if (!CATALOGUE.get(i).equals(row.hookId())) {
                throw new IllegalArgumentException("hook row " + i + " is '"
                        + row.hookId() + "' but the catalogue requires '"
                        + CATALOGUE.get(i) + "'");
            }
            if (row.expected() != 1) {
                throw new IllegalArgumentException(
                        "hook row " + row.hookId() + " must expect 1");
            }
            HookDisposition implied = row.actual() == row.expected()
                    ? HookDisposition.HEALTHY
                    : HookDisposition.FEATURE_DISABLED;
            if (row.disposition() != implied) {
                throw new IllegalArgumentException("hook row " + row.hookId()
                        + " disposition " + row.disposition() + " contradicts actual="
                        + row.actual());
            }
        }
        return List.copyOf(rows);
    }

    private static boolean computeShadowEnabled(List<ShadowHookRow> rows) {
        for (ShadowHookRow row : rows) {
            if (gatesShadow(row.hookId()) && row.disposition() != HookDisposition.HEALTHY) {
                return false;
            }
        }
        return true;
    }

    private static String fingerprintPreimage(List<ShadowHookRow> rows, boolean enabled) {
        StringBuilder preimage = new StringBuilder(FLATTENED_V3).append('\n');
        for (ShadowHookRow row : rows) {
            preimage.append(row.hookId()).append('|')
                    .append(row.expected()).append('|')
                    .append(row.actual()).append('|')
                    .append(row.disposition().name()).append('\n');
        }
        preimage.append("shadowEnabled=").append(enabled).append('\n');
        return preimage.toString();
    }

    /** The observation row for a hook id; empty for ids outside the catalogue. */
    public Optional<ShadowHookRow> row(String hookId) {
        for (ShadowHookRow row : rows) {
            if (row.hookId().equals(hookId)) {
                return Optional.of(row);
            }
        }
        return Optional.empty();
    }
}
