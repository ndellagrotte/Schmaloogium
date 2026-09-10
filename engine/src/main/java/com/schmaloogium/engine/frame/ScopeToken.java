// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * An opaque engine-issued nested-scope credential (PHASE_7_DOC §5.1). Minted only by
 * {@link FrameDriver#enter}; consumed exactly once by a matching {@link FrameDriver#exit}.
 * Identity alone authenticates. Not closeable.
 */
public final class ScopeToken {

    private static final java.util.concurrent.atomic.AtomicLong ISSUER =
            new java.util.concurrent.atomic.AtomicLong();

    private final long serial;

    private ScopeToken(long serial) {
        this.serial = serial;
    }

    public static ScopeToken mint() {
        return new ScopeToken(ISSUER.incrementAndGet());
    }

    long serial() {
        return serial;
    }

    @Override
    public String toString() {
        return "ScopeToken[" + serial + "]";
    }
}
