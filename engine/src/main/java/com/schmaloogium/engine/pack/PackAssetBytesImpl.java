// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

final class PackAssetBytesImpl implements PackAssetBytes {

    private final PackAssetMetadata metadata;
    private final byte[] payload;

    PackAssetBytesImpl(PackAssetMetadata metadata, byte[] payload) {
        this.metadata = java.util.Objects.requireNonNull(metadata, "metadata");
        this.payload = payload;
    }

    @Override
    public PackAssetMetadata metadata() {
        return metadata;
    }

    @Override
    public java.nio.ByteBuffer openCursor() {
        return java.nio.ByteBuffer.wrap(payload).asReadOnlyBuffer()
            .order(java.nio.ByteOrder.BIG_ENDIAN);
    }
}
