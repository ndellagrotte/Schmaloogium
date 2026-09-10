// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import com.schmaloogium.engine.config.SourceAttribution;
import java.util.Objects;

/** One ordered custom declaration offered to the compiler (§4.1). */
public record CustomExpressionSource(
        int sourceOrdinal,
        DeclarationKind kind,
        ExpressionType declaredType,
        String name,
        String rawExpression,
        SourceAttribution attribution) {

    public CustomExpressionSource {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(declaredType, "declaredType");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(rawExpression, "rawExpression");
        Objects.requireNonNull(attribution, "attribution");
        if (sourceOrdinal < 0) {
            throw new IllegalArgumentException("sourceOrdinal must be >= 0");
        }
    }
}
