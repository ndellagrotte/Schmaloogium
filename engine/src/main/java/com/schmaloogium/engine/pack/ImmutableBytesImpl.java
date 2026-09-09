// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.ImmutableBytes;

final class ImmutableBytesImpl implements ImmutableBytes {

    private final byte[] bytes;

    private ImmutableBytesImpl(byte[] bytes) {
        this.bytes = bytes;
    }

    static ImmutableBytes of(byte[] bytes) {
        java.util.Objects.requireNonNull(bytes, "bytes");
        return new ImmutableBytesImpl(bytes.clone());
    }

    @Override
    public int size() {
        return bytes.length;
    }

    @Override
    public byte[] copy() {
        return bytes.clone();
    }
}
