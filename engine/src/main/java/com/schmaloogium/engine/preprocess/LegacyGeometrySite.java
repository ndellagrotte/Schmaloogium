// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

/** Provenance-only pair site; spans may live in different included files. */
public record LegacyGeometrySite(
        SourceKey root,
        SourceSpan extensionSpan,
        SourceSpan maxVerticesSpan) {

    public LegacyGeometrySite {
        java.util.Objects.requireNonNull(root, "root");
        java.util.Objects.requireNonNull(extensionSpan, "extensionSpan");
        java.util.Objects.requireNonNull(maxVerticesSpan, "maxVerticesSpan");
    }
}
