// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

public record MaterializationFingerprint(String value) {

    public MaterializationFingerprint {
        java.util.Objects.requireNonNull(value, "value");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("fingerprint must be non-empty");
        }
    }
}
