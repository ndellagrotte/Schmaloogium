// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Half-open [startOffset, endOffset) character range inside one declaration's raw
 * expression text (§2.3). */
public record SourceSpan(int declarationOrdinal, int startOffset, int endOffset) {

    public SourceSpan {
        if (declarationOrdinal < 0) {
            throw new IllegalArgumentException("declarationOrdinal must be >= 0");
        }
        if (startOffset < 0 || endOffset < startOffset) {
            throw new IllegalArgumentException("invalid span range");
        }
    }

    public static SourceSpan whole(int declarationOrdinal, String rawExpression) {
        return new SourceSpan(declarationOrdinal, 0, rawExpression.length());
    }
}
