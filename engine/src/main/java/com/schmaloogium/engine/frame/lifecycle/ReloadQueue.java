// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.lifecycle;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;

/**
 * The P7-owned reload queue (PHASE_7_DOC §4.8). One pending slot on the client thread:
 * submit copies a valid immutable request in, effects merge by max lifecycle and OR flags,
 * the latest cause is diagnostics-only, and submissions during a drain form the next slot.
 * Coalescing promises one drained request and one final composition outcome — not one
 * Phase-4 bump. Malformed or null requests are rejected with IllegalArgumentException
 * before any state change; off-thread submission with IllegalStateException.
 */
public final class ReloadQueue {

    /** Confinement predicate; the queue never guesses a thread by itself. */
    private final BooleanSupplier clientThread;

    private ReloadRequest pending;
    private boolean draining;
    private ReloadRequest nextSlot;

    public ReloadQueue(BooleanSupplier clientThread) {
        this.clientThread = Objects.requireNonNull(clientThread, "clientThread");
    }

    /**
     * Submits one request into the pending slot, merging with any pending effects.
     * Returns the merged pending request after this submission.
     */
    public ReloadRequest submit(ReloadRequest request) {
        Objects.requireNonNull(request, "request");
        requireClientThread();
        if (draining) {
            nextSlot = nextSlot == null ? request : nextSlot.merge(request);
            return pending;
        }
        pending = pending == null ? request : pending.merge(request);
        return pending;
    }

    /**
     * Begins a drain and returns the merged request exactly once. The next slot (forms
     * during the drain) is returned as the new pending slot at {@link #endDrain()}.
     */
    public Optional<ReloadRequest> beginDrain() {
        requireClientThread();
        if (draining) {
            throw new IllegalStateException("a drain is already in progress");
        }
        if (pending == null) {
            return Optional.empty();
        }
        draining = true;
        return Optional.of(pending);
    }

    /** Ends the drain; the queue's pending slot becomes the next-slot content, if any. */
    public void endDrain() {
        requireClientThread();
        if (!draining) {
            throw new IllegalStateException("no drain in progress");
        }
        draining = false;
        pending = nextSlot;
        nextSlot = null;
    }

    /** True when a drain holds the pending slot. */
    public boolean draining() {
        return draining;
    }

    private void requireClientThread() {
        if (!clientThread.getAsBoolean()) {
            throw new IllegalStateException("reload queue is client-thread confined");
        }
    }
}
