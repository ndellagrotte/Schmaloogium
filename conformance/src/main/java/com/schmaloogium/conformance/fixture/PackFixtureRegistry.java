// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.fixture;

import com.schmaloogium.conformance.wire.Hashes;
import com.schmaloogium.conformance.wire.SectionedText;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * {@code conformance/fixtures/packs.registry} (§4.10.1): one {@code [pack <id>]} block per
 * row. Every row carries a licence line and a source URL; {@code MODRINTH} rows carry a
 * project (and, once populated, a version id); {@code MANUAL} rows carry no fetch path. Pack
 * ids are lowercase kebab and unique.
 */
public final class PackFixtureRegistry {

    private static final Set<String> KEYS = Set.of("displayName", "tier", "mode", "modrinthProject",
        "modrinthVersion", "version", "sha512", "archiveName", "licence", "sourceUrl");
    private static final Set<String> TIERS = Set.of("classic", "dual-spec", "corpus");

    private final Map<String, PackFixture> byId;

    private PackFixtureRegistry(Map<String, PackFixture> byId) {
        this.byId = Map.copyOf(byId);
    }

    public static PackFixtureRegistry parse(String text) {
        Map<String, PackFixture> rows = new LinkedHashMap<>();
        for (SectionedText.Section s : SectionedText.parse(text)) {
            if (!s.kind().equals("pack") || s.header().size() != 2) {
                throw new IllegalArgumentException("line " + s.line() + ": expected [pack <id>]");
            }
            String id = s.name();
            if (!id.chars().allMatch(c -> (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-')) {
                throw new IllegalArgumentException("[pack " + id + "]: id must be lowercase kebab");
            }
            Map<String, String> e = s.entries();
            for (String key : e.keySet()) {
                if (!KEYS.contains(key)) {
                    throw new IllegalArgumentException("[pack " + id + "]: unknown key " + key);
                }
            }
            for (String required : new String[] {"displayName", "tier", "mode", "version", "licence",
                "sourceUrl", "archiveName"}) {
                if (!e.containsKey(required) || e.get(required).isEmpty()) {
                    throw new IllegalArgumentException("[pack " + id + "]: missing " + required);
                }
            }
            if (!TIERS.contains(e.get("tier"))) {
                throw new IllegalArgumentException("[pack " + id + "]: tier must be one of " + TIERS);
            }
            AcquisitionMode mode;
            try {
                mode = AcquisitionMode.valueOf(e.get("mode"));
            } catch (IllegalArgumentException bad) {
                throw new IllegalArgumentException("[pack " + id + "]: mode must be MODRINTH|MANUAL");
            }
            String project = e.getOrDefault("modrinthProject", "");
            String modrinthVersion = e.getOrDefault("modrinthVersion", "");
            if (mode == AcquisitionMode.MODRINTH && project.isEmpty()) {
                throw new IllegalArgumentException("[pack " + id + "]: MODRINTH rows need modrinthProject");
            }
            if (mode == AcquisitionMode.MANUAL && (!project.isEmpty() || !modrinthVersion.isEmpty())) {
                throw new IllegalArgumentException("[pack " + id + "]: MANUAL rows carry no fetch path");
            }
            String sha512 = e.getOrDefault("sha512", "");
            if (!sha512.isEmpty() && !Hashes.isHex(sha512, 128)) {
                throw new IllegalArgumentException("[pack " + id + "]: sha512 must be 128 lowercase hex");
            }
            PackFixture row = new PackFixture(id, e.get("displayName"), e.get("tier"), mode, project,
                modrinthVersion, e.get("version"), sha512, e.get("archiveName"), e.get("licence"),
                e.get("sourceUrl"));
            if (rows.putIfAbsent(id, row) != null) {
                throw new IllegalArgumentException("duplicate pack id " + id);
            }
        }
        return new PackFixtureRegistry(rows);
    }

    public Optional<PackFixture> find(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    /** Resolves {@code <id>@<version>}; the version must equal the registry's pin. */
    public PackFixture require(String idAtVersion) {
        int at = idAtVersion.indexOf('@');
        if (at <= 0) {
            throw new IllegalArgumentException("pack reference must be <id>@<version>: " + idAtVersion);
        }
        String id = idAtVersion.substring(0, at);
        String version = idAtVersion.substring(at + 1);
        PackFixture row = byId.get(id);
        if (row == null) {
            throw new IllegalArgumentException("unknown pack id in registry: " + id);
        }
        if (!row.version().equals(version)) {
            throw new IllegalArgumentException("registry pins " + id + " at version " + row.version()
                + ", not " + version);
        }
        return row;
    }

    public Map<String, PackFixture> rows() {
        return byId;
    }
}
