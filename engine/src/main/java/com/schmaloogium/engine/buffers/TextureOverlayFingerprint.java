// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Opaque Phase-5 texture overlay content fingerprint. */
public record TextureOverlayFingerprint(String value) {

    public TextureOverlayFingerprint {
        java.util.Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must be non-blank");
        }
    }
}
