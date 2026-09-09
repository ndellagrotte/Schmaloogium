// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

/**
 * The opaque bundle behind {@link com.schmaloogium.engine.registry.BarrierPublicationCandidate}
 * (PHASE_4_DOC §4.10): the private barrier plus registry identity and provenance. Holds no GL
 * handle, lock, or participant-owned resource; {@code release()} only drops references.
 */
public final class BarrierBundle {

    private final long registryIdentity;
    private final long generation;
    private final com.schmaloogium.engine.registry.RegistryFingerprint fingerprint;
    private final BarrierCore barrier;
    private final boolean bootstrapOnly;
    private boolean released;

    BarrierBundle(
            long registryIdentity,
            long generation,
            com.schmaloogium.engine.registry.RegistryFingerprint fingerprint,
            BarrierCore barrier,
            boolean bootstrapOnly) {
        this.registryIdentity = registryIdentity;
        this.generation = generation;
        this.fingerprint = java.util.Objects.requireNonNull(fingerprint, "fingerprint");
        this.barrier = java.util.Objects.requireNonNull(barrier, "barrier");
        this.bootstrapOnly = bootstrapOnly;
    }

    long registryIdentity() {
        return registryIdentity;
    }

    long generation() {
        return generation;
    }

    com.schmaloogium.engine.registry.RegistryFingerprint fingerprint() {
        return fingerprint;
    }

    BarrierCore barrier() {
        return barrier;
    }

    boolean isBootstrapOnly() {
        return bootstrapOnly;
    }

    synchronized boolean isReleased() {
        return released;
    }

    public synchronized void release() {
        if (released) {
            return;
        }
        released = true;
        barrier.retire();
    }
}
