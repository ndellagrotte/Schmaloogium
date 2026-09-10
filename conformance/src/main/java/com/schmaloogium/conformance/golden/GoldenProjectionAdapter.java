// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.golden;

import com.schmaloogium.conformance.wire.CanonicalText;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.DecisionDiagnostic;
import com.schmaloogium.engine.pack.DecisionSource;
import com.schmaloogium.engine.pack.DecisionValue;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.pack.PackInspectionResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The one place a front-end inspection becomes a golden document (§4.11.4). The adapter
 * consumes the closed {@code PackInspectionResult.Inspected} boundary only: it maps the
 * snapshot's {@code sources} and {@code diagnostics} lists and the nine
 * {@code DecisionValue} trees through the §4.11.4 table, and it never opens
 * parser-private code, original logical lines, source dumps, diagnostic args or driver
 * logs. [D-P2-5] therefore holds by construction — arbitrary text arrives inside
 * {@code DecisionValue.TextHash} or not at all.
 *
 * <p>Schema discipline is enforced here before any projection: the returned
 * configuration must carry {@link PackFrontEnd#CURRENT_SCHEMA_VERSION}, the snapshot's
 * schema must match it, {@code projectionVersion} must be 1, and the snapshot
 * fingerprint must equal the configuration fingerprint.
 */
public final class GoldenProjectionAdapter {

    /** Inputs the runner owns (§4.11.4's "archive identity remains separate"). */
    public record GoldenInputs(String packId, String packVersion, Optional<String> archiveSha512,
        String profileId, GLCapabilityProfile profile, String engineVersion) {

        public GoldenInputs {
            java.util.Objects.requireNonNull(packId, "packId");
            java.util.Objects.requireNonNull(packVersion, "packVersion");
            archiveSha512 = archiveSha512 == null ? Optional.empty() : archiveSha512;
            java.util.Objects.requireNonNull(profileId, "profileId");
            java.util.Objects.requireNonNull(profile, "profile");
            java.util.Objects.requireNonNull(engineVersion, "engineVersion");
        }

        public String packIdentity() {
            return packId + "@" + packVersion;
        }
    }

    public GoldenProjectionAdapter() {
    }

    public GoldenDocument project(PackInspectionResult.Inspected inspected, GoldenInputs inputs) {
        PackConfiguration configuration = inspected.configuration();
        var snapshot = inspected.snapshot();
        if (configuration.schemaVersion() != PackFrontEnd.CURRENT_SCHEMA_VERSION) {
            throw new IllegalStateException("configuration schema "
                + configuration.schemaVersion() + " is not the current "
                + PackFrontEnd.CURRENT_SCHEMA_VERSION);
        }
        if (snapshot.schemaVersion() != configuration.schemaVersion()) {
            throw new IllegalStateException("snapshot schema " + snapshot.schemaVersion()
                + " does not match configuration schema " + configuration.schemaVersion());
        }
        if (snapshot.projectionVersion() != 1) {
            throw new IllegalStateException("snapshot projectionVersion "
                + snapshot.projectionVersion() + " is not 1");
        }
        if (!snapshot.configurationFingerprint().equals(configuration.fingerprint())) {
            throw new IllegalStateException("snapshot fingerprint does not match configuration");
        }

        Map<String, String> header = new LinkedHashMap<>();
        header.put("engine.version", inputs.engineVersion());
        header.put("input.configurationFingerprint", configuration.fingerprint().value());
        header.put("input.configurationSchema", Integer.toString(configuration.schemaVersion()));
        header.put("input.pack", inputs.packIdentity());
        header.put("input.packSha512", inputs.archiveSha512().orElse(""));
        header.put("input.profile", inputs.profileId());
        header.put("input.projectionVersion", Integer.toString(snapshot.projectionVersion()));
        GoldenDocument document = GoldenDocument.create(header);

        document.section("sources", sourcesSection(snapshot.sources()));
        // Several P3 section keys project into the SAME golden section (§4.11.4's
        // table; e.g. "properties" receives pack/dimensions/properties/idMappings/
        // assets). GoldenDocument.section replaces wholesale, so the contributions
        // must be merged BEFORE the single per-section call, and the walk order must
        // not depend on Map.of iteration order (which varies per JVM): OWNER_SECTIONS
        // is therefore walked in its key-sorted order and every row key carries its
        // owner prefix, making the merge lossless and the render deterministic.
        Map<String, Map<String, String>> merged = new java.util.TreeMap<>();
        for (String sectionKey : OWNER_SECTIONS.keySet().stream().sorted().toList()) {
            DecisionValue tree = snapshot.sections().get(sectionKey);
            if (tree == null) {
                continue;
            }
            List<String> target = OWNER_SECTIONS.get(sectionKey);
            Map<String, String> rows = merged.computeIfAbsent(target.get(0), k -> new LinkedHashMap<>());
            Map<String, String> treeRows = new LinkedHashMap<>();
            flatten(target.get(1), tree, treeRows);
            if (treeRows.isEmpty()) {
                // An owner tree that exists but declares nothing is still a decision.
                treeRows.put("present", "true");
            }
            rows.putAll(treeRows);
        }
        for (Map.Entry<String, Map<String, String>> section : merged.entrySet()) {
            document.section(section.getKey(), section.getValue());
        }
        document.section("diagnostics", diagnosticsSection(snapshot.diagnostics()));
        return document;
    }

    private static Map<String, String> sourcesSection(java.util.List<DecisionSource> sources) {
        Map<String, String> rows = new LinkedHashMap<>();
        rows.put("count", Integer.toString(sources.size()));
        for (DecisionSource source : sources) {
            rows.put(source.path().canonicalString(),
                "lines=" + source.logicalLineCount() + " sha256=" + source.sha256());
        }
        return rows;
    }


    private static Map<String, String> diagnosticsSection(List<DecisionDiagnostic> diagnostics) {
        Map<String, String> rows = new LinkedHashMap<>();
        rows.put("count", Integer.toString(diagnostics.size()));
        int index = 0;
        for (DecisionDiagnostic diagnostic : diagnostics) {
            String file = diagnostic.location()
                .map(location -> location.source().canonicalString())
                .orElse("");
            int line = diagnostic.location().map(location -> location.physicalLine()).orElse(0);
            rows.put(Integer.toString(index), "code=" + diagnostic.code()
                + " severity=" + diagnostic.severity()
                + " channel=" + diagnostic.channel()
                + " file=" + file
                + " line=" + line);
            index++;
        }
        return rows;
    }

    /** §4.11.4's lossless mapping: P3 {@code sections} key → golden section. */
    private static final Map<String, List<String>> OWNER_SECTIONS = Map.of(
        "pack", List.of("properties", "owner.pack"),
        "dimensions", List.of("properties", "owner.dimensions"),
        "options", List.of("options", "owner.options"),
        "properties", List.of("properties", "owner.properties"),
        "programStates", List.of("programs", "owner.programStates"),
        "resources", List.of("sizing", "owner.resources"),
        "macros", List.of("macros", "owner.macros"),
        "idMappings", List.of("properties", "owner.idMappings"),
        "assets", List.of("properties", "owner.assets"));

    private static void flatten(String prefix, DecisionValue value, Map<String, String> rows) {
        if (value instanceof DecisionValue.Fields fields) {
            List<String> keys = new java.util.ArrayList<>(fields.values().keySet());
            keys.sort(CanonicalText.ORDER);
            for (String key : keys) {
                flatten(prefix + "." + key, fields.values().get(key), rows);
            }
            return;
        }
        if (value instanceof DecisionValue.Sequence sequence) {
            for (int i = 0; i < sequence.values().size(); i++) {
                flatten(prefix + "." + i, sequence.values().get(i), rows);
            }
            return;
        }
        rows.put(prefix, renderLeaf(value));
    }

    private static String renderLeaf(DecisionValue value) {
        if (value instanceof DecisionValue.Bool bool) {
            return CanonicalText.formatBoolean(bool.value());
        }
        if (value instanceof DecisionValue.IntegerValue integer) {
            return CanonicalText.formatInt(integer.value());
        }
        if (value instanceof DecisionValue.FloatBits bits) {
            return CanonicalText.formatFloatBits(bits.bits());
        }
        if (value instanceof DecisionValue.Token token) {
            return token.value();
        }
        if (value instanceof DecisionValue.TextHash hash) {
            return "sha256:" + hash.sha256();
        }
        if (value instanceof DecisionValue.Absent) {
            return "absent";
        }
        throw new IllegalStateException("unrenderable DecisionValue: " + value.getClass());
    }
}
