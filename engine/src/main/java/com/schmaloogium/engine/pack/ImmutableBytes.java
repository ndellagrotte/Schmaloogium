// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.ImmutableBytes;

/** Immutable byte payload with size plus a fresh defensive copy on every {@link #copy()}. */
public interface ImmutableBytes {

    int size();

    byte[] copy();

    /** Wraps a defensive copy; callers keep ownership of the supplied array. */
    static ImmutableBytes of(byte[] bytes) {
        return ImmutableBytesImpl.of(bytes);
    }
}
