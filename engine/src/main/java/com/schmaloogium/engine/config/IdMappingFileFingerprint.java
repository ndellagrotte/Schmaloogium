// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Non-empty opaque canonical ID-mapping file fingerprint. */
public record IdMappingFileFingerprint(String value) {

    public IdMappingFileFingerprint {
        java.util.Objects.requireNonNull(value, "value");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("fingerprint must be non-empty");
        }
    }
}
