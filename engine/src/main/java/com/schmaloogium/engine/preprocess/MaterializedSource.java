// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.List;

/** One exact final transformed GLSL source with its complete attributed snapshot. */
public record MaterializedSource(
        SourceKey root,
        String transformedText,
        SourceMap sourceMap,
        DeclaredUniformCatalog declaredUniforms,
        ShaderLanguage language,
        GeometrySourceForm geometry,
        List<EngineDiagnostic> diagnostics,
        MaterializationFingerprint fingerprint) {

    public MaterializedSource {
        java.util.Objects.requireNonNull(root, "root");
        java.util.Objects.requireNonNull(transformedText, "transformedText");
        java.util.Objects.requireNonNull(sourceMap, "sourceMap");
        java.util.Objects.requireNonNull(declaredUniforms, "declaredUniforms");
        java.util.Objects.requireNonNull(language, "language");
        java.util.Objects.requireNonNull(geometry, "geometry");
        diagnostics = List.copyOf(diagnostics);
        java.util.Objects.requireNonNull(fingerprint, "fingerprint");
    }
}
