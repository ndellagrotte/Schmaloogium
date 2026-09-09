// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Inclusive integer range 0..2147483647 with lo <= hi. */
public record IntegerRange(int lowerInclusive, int upperInclusive) {

    public static final int MAX = 2147483647;

    public IntegerRange {
        if (lowerInclusive < 0 || upperInclusive > MAX || lowerInclusive > upperInclusive) {
            throw new IllegalArgumentException("invalid integer range");
        }
    }
}
