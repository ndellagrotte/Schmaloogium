// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.buffers.internal.CandidateImpl;

import java.util.Objects;

/**
 * An owned, not-yet-published buffer estate (PHASE_5_DOC §2.2/§4.11): handle-bearing,
 * caller-owned until acceptance. {@code close()} is idempotent while caller-owned and
 * deletes only owned handles; acceptance transfers ownership to the publisher.
 */
public final class BufferEstateCandidate implements AutoCloseable {

    private CandidateImpl impl;

    /** Wraps an internal candidate; internal construction seam. */
    public static BufferEstateCandidate wrap(
            com.schmaloogium.engine.buffers.internal.CandidateImpl impl) {
        return new BufferEstateCandidate(impl);
    }

    BufferEstateCandidate(CandidateImpl impl) {
        this.impl = Objects.requireNonNull(impl, "impl");
    }

    /** Handle-free inspection metadata (§2.2). */
    public BufferEstateInspection inspection() {
        CandidateImpl current = impl;
        if (current == null) {
            throw new IllegalStateException("candidate already closed");
        }
        return current;
    }

    /** Internal construction seam for the publisher; not API. */
    public CandidateImpl internal() {
        CandidateImpl current = impl;
        if (current == null) {
            throw new IllegalStateException("candidate already closed");
        }
        return current;
    }


    @Override
    public void close() {
        CandidateImpl current = impl;
        impl = null;
        if (current != null) {
            current.closeOwned();
        }
    }
}
