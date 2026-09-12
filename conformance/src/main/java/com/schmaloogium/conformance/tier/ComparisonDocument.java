// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.tier;

import com.schmaloogium.conformance.diff.DiffResult;
import com.schmaloogium.conformance.diff.IgnoreMask;
import com.schmaloogium.conformance.diff.TolerancePolicy;
import com.schmaloogium.conformance.wire.FlatDocument;
import com.schmaloogium.conformance.wire.Hashes;

import java.util.List;
import java.util.Objects;

/**
 * [D-P2-42] authenticated comparison evidence: one immutable canonical artifact per image
 * decision at {@code <run>/comparisons/<sha256>.comparison}, schema
 * {@code schmaloogium.comparison/1}, whose SHA-256 covers its exact bytes. The policy and
 * comparability blocks embed complete canonical inner documents as JSON strings rather than
 * paths, so a decision is re-derivable from the artifact alone.
 */
public final class ComparisonDocument {

    public static final String SCHEMA_LINE = "schema = schmaloogium.comparison/1";
    public static final String POLICY_SCHEMA_LINE = "schema = schmaloogium.effective-comparison-policy/1";
    public static final String COMPARABILITY_SCHEMA_LINE = "schema = schmaloogium.comparability-evidence/1";

    public record Identity(String kind, String packId, String packVersion, String sceneId,
            String captureKind, String captureId, long sampleOrdinal, String featureId) {
    }

    public record Candidate(String runId, String manifestSha256, String optionStateSha256,
            String pixelSha256, int width, int height) {
    }

    /** Reference; when unavailable every hash/key is empty and dimensions are zero. */
    public record Reference(String kind, String manifestSha256, String recordKey,
            String optionStateSha256, String pixelSha256, int width, int height) {
        public static Reference unavailable(String kind) {
            return new Reference(kind, "", "", "", "", 0, 0);
        }
    }

    public record Comparability(String status, String reason, String candidateTiming,
            String referenceTiming, String checks) {
    }

    public record Result(TierOutcome outcome, String reason, DiffResult metrics) {
    }

    private ComparisonDocument() {
    }

    /** The canonical effective policy text (§4.2.6). */
    public static String policyText(TolerancePolicy policy, IgnoreMask mask, String escalationReason) {
        FlatDocument.Builder b = FlatDocument.builder(POLICY_SCHEMA_LINE);
        b.token("profile", policy.name());
        b.integer("channelTolerance", policy.channelTolerance());
        b.decimal("maxDifferingFraction", policy.maxDifferingFraction());
        b.integer("maxDelta", policy.maxDelta());
        b.decimal("maxRmse", policy.maxRmse());
        b.integer("maxClusterArea", policy.maxClusterArea());
        b.integer("maxClusters", policy.maxClusters());
        b.text("calibratedOn", policy.calibratedOn());
        b.text("calibrationEvidence", "");
        b.text("escalationReason", escalationReason);
        b.token("colourModel", "ARGB8_SRGB");
        b.token("alphaRule", "EXACT");
        b.integer("ignore.count", mask.rects().size());
        for (int i = 0; i < mask.rects().size(); i++) {
            IgnoreMask.Rect r = mask.rects().get(i);
            String p = "ignore." + i + ".";
            b.integer(p + "x", r.x()).integer(p + "y", r.y()).integer(p + "width", r.width())
                .integer(p + "height", r.height()).text(p + "reason", r.reason());
        }
        return b.build().render();
    }

    public static String comparabilityText(Comparability c) {
        FlatDocument.Builder b = FlatDocument.builder(COMPARABILITY_SCHEMA_LINE);
        b.text("candidateTiming", c.candidateTiming());
        b.text("referenceTiming", c.referenceTiming());
        b.text("checks", c.checks());
        b.text("independentEvidence", "");
        b.text("operator", "");
        b.text("observationProvenance", "");
        return b.build().render();
    }

    public static FlatDocument build(Identity id, Candidate candidate, Reference reference,
            String policyText, Comparability comparability, Result result, List<String> children) {
        Objects.requireNonNull(result.outcome(), "outcome");
        if (result.reason().isEmpty()) {
            throw new IllegalArgumentException("result.reason must be non-empty");
        }
        if (result.outcome() == TierOutcome.PASS && reference.pixelSha256().isEmpty()) {
            throw new IllegalArgumentException("no valid image-tier PASS has an absent reference");
        }
        FlatDocument.Builder b = FlatDocument.builder(SCHEMA_LINE);
        b.token("kind", id.kind());
        b.text("packId", id.packId()).text("packVersion", id.packVersion());
        b.token("sceneId", id.sceneId()).token("captureKind", id.captureKind());
        b.text("captureId", id.captureId()).integer("sampleOrdinal", id.sampleOrdinal());
        b.text("featureId", id.featureId());
        b.token("candidate.runId", candidate.runId());
        b.token("candidate.manifestSha256", candidate.manifestSha256());
        b.token("candidate.optionStateSha256", candidate.optionStateSha256());
        b.token("candidate.pixelSha256", candidate.pixelSha256());
        b.integer("candidate.width", candidate.width()).integer("candidate.height", candidate.height());
        b.token("reference.kind", reference.kind());
        b.text("reference.manifestSha256", reference.manifestSha256());
        b.text("reference.recordKey", reference.recordKey());
        b.text("reference.optionStateSha256", reference.optionStateSha256());
        b.text("reference.pixelSha256", reference.pixelSha256());
        b.integer("reference.width", reference.width()).integer("reference.height", reference.height());
        FlatDocument policy = FlatDocument.parse(policyText, POLICY_SCHEMA_LINE);
        b.token("policy.profile", policy.token("profile"));
        b.token("policy.sha256", Hashes.sha256HexOf(policyText));
        b.text("policy.text", policyText);
        String compText = comparabilityText(comparability);
        b.token("comparability.status", comparability.status());
        b.text("comparability.reason", comparability.reason());
        b.token("comparability.evidenceSha256", Hashes.sha256HexOf(compText));
        b.text("comparability.text", compText);
        b.token("result.outcome", result.outcome().name());
        b.text("result.reason", result.reason());
        boolean metrics = result.metrics() != null;
        b.bool("result.metricsAvailable", metrics);
        if (metrics) {
            DiffResult m = result.metrics();
            b.decimal("result.differingFraction", m.differingFraction());
            b.integer("result.maxChannelDelta", m.maxChannelDelta());
            b.decimal("result.rmse", m.rmse());
            b.integer("result.largestClusterArea", m.largestClusterArea());
            b.integer("result.clusterCount", m.clusterCount());
            b.integer("result.unmaskedPixels", m.unmaskedPixels());
        }
        b.integer("children.count", children.size());
        for (int i = 0; i < children.size(); i++) {
            b.token("children." + i + ".comparisonSha256", children.get(i));
        }
        return b.build();
    }
}
