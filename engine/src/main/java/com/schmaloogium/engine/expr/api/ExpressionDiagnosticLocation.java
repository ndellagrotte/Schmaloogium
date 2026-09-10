// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import com.schmaloogium.engine.config.SourceAttribution;
import java.util.Objects;

/** Closed attribution domain: a real declaration location or an explicit source-less marker —
 * never invented paths or zero coordinates (§4.9, D-P11-24). */
public sealed interface ExpressionDiagnosticLocation {

    record Declaration(String declarationName, SourceAttribution attribution, SourceSpan span)
            implements ExpressionDiagnosticLocation {
        public Declaration {
            Objects.requireNonNull(declarationName, "declarationName");
            Objects.requireNonNull(attribution, "attribution");
            Objects.requireNonNull(span, "span");
        }
    }

    record SourceLess() implements ExpressionDiagnosticLocation {}
}
