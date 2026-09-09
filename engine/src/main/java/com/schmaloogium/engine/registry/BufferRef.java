// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * A logical buffer reference (PHASE_4_DOC §2.2): domain plus index. The registry never
 * stores a "main" or "alt" texture side — that snapshot belongs to Phase 5 (§4.4).
 */
public record BufferRef(BufferDomain domain, int index) {

    public BufferRef {
        java.util.Objects.requireNonNull(domain, "domain");
    }
}
