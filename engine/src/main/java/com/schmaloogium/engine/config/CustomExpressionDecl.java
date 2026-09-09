// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.SourceAttribution;

/** One lossless custom uniform/variable declaration (D-P3-25). */
public record CustomExpressionDecl(
        CustomExpressionKind kind,
        CustomExpressionType type,
        String name,
        String rawExpression,
        int sourceOrdinal,
        SourceAttribution attribution) {

    public CustomExpressionDecl {
        java.util.Objects.requireNonNull(kind, "kind");
        java.util.Objects.requireNonNull(type, "type");
        java.util.Objects.requireNonNull(name, "name");
        java.util.Objects.requireNonNull(rawExpression, "rawExpression");
        if (sourceOrdinal < 0) {
            throw new IllegalArgumentException("sourceOrdinal must be >= 0");
        }
        java.util.Objects.requireNonNull(attribution, "attribution");
    }
}
