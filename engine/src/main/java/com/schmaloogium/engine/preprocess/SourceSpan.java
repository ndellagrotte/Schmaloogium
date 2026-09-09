// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

/** Zero-based half-open UTF-16 span with one-based original line/column provenance. */
public record SourceSpan(
        SourceId source,
        int startOffset,
        int endOffset,
        int startLine,
        int startColumn) {

    public SourceSpan {
        java.util.Objects.requireNonNull(source, "source");
        if (startOffset < 0 || endOffset < startOffset) {
            throw new IllegalArgumentException("invalid source span");
        }
        if (startLine < 1 || startColumn < 1) {
            throw new IllegalArgumentException("span coordinates are one-based");
        }
    }
}
