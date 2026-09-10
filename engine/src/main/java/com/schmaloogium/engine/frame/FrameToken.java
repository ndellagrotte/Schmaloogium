// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * An opaque engine-issued per-frame credential (PHASE_7_DOC §5.1). Minted only by
 * {@link FrameDriver#open}; deliberately no value equality — identity alone authenticates,
 * so a copied field set can never forge a live frame. Not closeable.
 */
public final class FrameToken {

    private static final AtomicIssuer ISSUER = new AtomicIssuer();

    private final long serial;
    private final long frameId;

    private FrameToken(long serial, long frameId) {
        this.serial = serial;
        this.frameId = frameId;
    }

    public static FrameToken mint(long frameId) {
        return new FrameToken(ISSUER.next(), frameId);
    }

    /** The driver-assigned frame id this token was opened with. */
    public long frameId() {
        return frameId;
    }

    long serial() {
        return serial;
    }

    @Override
    public String toString() {
        return "FrameToken[" + frameId + "#" + serial + "]";
    }

    private static final class AtomicIssuer {
        private final java.util.concurrent.atomic.AtomicLong current =
                new java.util.concurrent.atomic.AtomicLong();

        long next() {
            return current.incrementAndGet();
        }
    }
}
