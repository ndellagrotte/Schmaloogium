// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.NormalizedPackPath;

import java.util.Optional;
import java.util.OptionalInt;

public record PackAssetMetadata(
        NormalizedPackPath path, PackAssetAvailability availability,
        OptionalInt byteCount, Optional<String> sha256) {

    public PackAssetMetadata {
        java.util.Objects.requireNonNull(path, "path");
        java.util.Objects.requireNonNull(availability, "availability");
        java.util.Objects.requireNonNull(sha256, "sha256");
        if (availability == PackAssetAvailability.AVAILABLE) {
            if (byteCount.isEmpty() || byteCount.getAsInt() < 0 || sha256.isEmpty()) {
                throw new IllegalArgumentException("AVAILABLE requires byteCount and sha256");
            }
            if (!sha256.get().matches("[0-9a-f]{64}")) {
                throw new IllegalArgumentException("sha256 must be 64 lowercase hex digits");
            }
        } else {
            if (byteCount.isPresent() || sha256.isPresent()) {
                throw new IllegalArgumentException("absent/unreadable rows carry empty optionals");
            }
        }
    }
}
