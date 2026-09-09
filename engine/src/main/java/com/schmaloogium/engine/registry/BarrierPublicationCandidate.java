// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The factory's opaque, final proof (PHASE_4_DOC §4.10) that the private barrier was created
 * from an authenticated production bundle for that exact compiler product and registry
 * identity. Inactive and caller-owned until supplied with that same compiler product in
 * {@link RegistryPublication.Ready}; rejection leaves both caller-owned, acceptance transfers
 * both to the publisher. An unpublished barrier has acquired no GL handle, lock, or
 * participant-owned resource; {@code close()} marks it closed and drops its private barrier
 * and participant references without GL work. Close is idempotent before transfer; after
 * successful transfer caller close is an idempotent no-op that cannot affect the publication.
 */
public final class BarrierPublicationCandidate implements AutoCloseable {

    private final com.schmaloogium.engine.registry.internal.BarrierBundle bundle;
    private volatile boolean closed;
    private volatile boolean ownershipTransferred;

    public BarrierPublicationCandidate(com.schmaloogium.engine.registry.internal.BarrierBundle bundle) {
        this.bundle = java.util.Objects.requireNonNull(bundle, "bundle");
    }

    public void close() {
        if (closed || ownershipTransferred) {
            closed = true;
            return;
        }
        closed = true;
        bundle.release();
    }

    public com.schmaloogium.engine.registry.internal.BarrierBundle bundle() {
        return bundle;
    }

    public boolean isClosed() {
        return closed;
    }

    public void markTransferred() {
        ownershipTransferred = true;
        closed = true;
    }

    @Override
    public String toString() {
        return "BarrierPublicationCandidate[closed=" + closed + "]";
    }
}
