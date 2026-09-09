// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.List;

/**
 * Per-program draw routing (PHASE_4_DOC §4.9): the lossless Phase-4 mirror of the Phase 3
 * {@code DRAWBUFFERS}/{@code RENDERTARGETS} adaptation. {@link AllUsedBuffers} stays
 * symbolic for Phase 5, which resolves it against its estate. {@link Explicit} is non-empty
 * and immutable, preserves leading/middle/trailing/repeated/all-none positions without
 * deduplication, and {@code N} = {@code [None]} — never empty and never AllUsedBuffers.
 */
public sealed interface DrawRouting {

    /** Writes every color buffer the stage uses; the domain is symbolic for Phase 5. */
    record AllUsedBuffers(BufferDomain domain) implements DrawRouting {

        public AllUsedBuffers {
            java.util.Objects.requireNonNull(domain, "domain");
        }
    }

    /** Exact positional slots, holes ({@code None}) included, order preserved. */
    record Explicit(List<DrawRoutingSlot> slots) implements DrawRouting {

        public Explicit {
            slots = List.copyOf(slots);
            if (slots.isEmpty()) {
                throw new IllegalArgumentException("explicit routing must be non-empty");
            }
            for (DrawRoutingSlot slot : slots) {
                if (slot == null) {
                    throw new IllegalArgumentException("null routing slot");
                }
            }
        }
    }
}
