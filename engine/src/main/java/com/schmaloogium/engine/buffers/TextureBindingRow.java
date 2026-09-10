// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** One precomputed texture-unit row: unit 0..15 and its outcome. */
public record TextureBindingRow(int unit, TextureBindingOutcome outcome) {

    public TextureBindingRow {
        java.util.Objects.requireNonNull(outcome, "outcome");
        if (unit < 0 || unit > 15) {
            throw new IllegalArgumentException("unit must be 0..15: " + unit);
        }
    }
}
