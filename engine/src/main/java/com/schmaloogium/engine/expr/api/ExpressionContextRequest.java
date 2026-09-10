// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** One loader-neutral biome/view snapshot request per refresh (§4.10). */
public record ExpressionContextRequest(String planFingerprint, long refreshEpoch, int frameCounter) {

    public ExpressionContextRequest {
        if (planFingerprint == null || planFingerprint.isEmpty()) {
            throw new IllegalArgumentException("planFingerprint must be non-empty");
        }
    }
}
