// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.fixture;

import java.util.Objects;

/**
 * One registry row (§4.10.1): identity, tier, acquisition mode, pin, licence line and source.
 * {@code sha512} and {@code modrinthVersion} may be empty ([D-P2-20] — unpopulated pins), in
 * which case the resolver refuses the row rather than resolving "latest".
 */
public record PackFixture(String id, String displayName, String tier, AcquisitionMode mode,
        String modrinthProject, String modrinthVersion, String version, String sha512,
        String archiveName, String licence, String sourceUrl) {

    public PackFixture {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(tier, "tier");
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(modrinthProject, "modrinthProject");
        Objects.requireNonNull(modrinthVersion, "modrinthVersion");
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(sha512, "sha512");
        Objects.requireNonNull(archiveName, "archiveName");
        Objects.requireNonNull(licence, "licence");
        Objects.requireNonNull(sourceUrl, "sourceUrl");
    }

    public boolean pinned() {
        return !sha512.isEmpty() && (mode == AcquisitionMode.MANUAL || !modrinthVersion.isEmpty());
    }

    public String key() {
        return id + "@" + version;
    }
}
