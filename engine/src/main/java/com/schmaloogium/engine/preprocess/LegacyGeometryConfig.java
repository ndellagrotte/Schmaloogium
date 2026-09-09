// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

public record LegacyGeometryConfig(
        SourceKey root,
        LegacyGeometryExtension extension,
        int maxVertices,
        LegacyGeometrySite site) {

    public LegacyGeometryConfig {
        java.util.Objects.requireNonNull(root, "root");
        java.util.Objects.requireNonNull(extension, "extension");
        java.util.Objects.requireNonNull(site, "site");
    }
}
