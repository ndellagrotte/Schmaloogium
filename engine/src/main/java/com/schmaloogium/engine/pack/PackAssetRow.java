// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.NormalizedPackPath;

import java.util.Optional;
import java.util.OptionalInt;

final class PackAssetRow {

    private final PackAssetMetadata metadata;
    private final byte[] payload;   // null for missing/unreadable

    PackAssetRow(PackAssetMetadata metadata, byte[] payload) {
        this.metadata = java.util.Objects.requireNonNull(metadata, "metadata");
        this.payload = payload;
    }

    static PackAssetRow available(NormalizedPackPath path, byte[] payload) {
        return new PackAssetRow(new PackAssetMetadata(path, PackAssetAvailability.AVAILABLE,
            OptionalInt.of(payload.length), Optional.of(Sha256.hex(payload))), payload.clone());
    }

    static PackAssetRow missing(NormalizedPackPath path) {
        return new PackAssetRow(new PackAssetMetadata(path, PackAssetAvailability.MISSING,
            OptionalInt.empty(), Optional.empty()), null);
    }

    static PackAssetRow unreadable(NormalizedPackPath path) {
        return new PackAssetRow(new PackAssetMetadata(path, PackAssetAvailability.UNREADABLE,
            OptionalInt.empty(), Optional.empty()), null);
    }

    PackAssetMetadata metadata() {
        return metadata;
    }

    PackAssetAcquisition acquire(NormalizedPackPath path) {
        if (payload == null) {
            return metadata.availability() == PackAssetAvailability.MISSING
                ? new PackAssetAcquisition.Missing(path)
                : new PackAssetAcquisition.Unreadable(path);
        }
        return new PackAssetAcquisition.Acquired(new PackAssetBytesImpl(metadata, payload));
    }
}
