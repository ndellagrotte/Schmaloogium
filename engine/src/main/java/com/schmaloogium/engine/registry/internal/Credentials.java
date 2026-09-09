// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

/**
 * Opaque minted credentials. Only Phase-4 internals can create or compare them; no public
 * operation accepts a caller-supplied frame number, source identity, epoch, or kind.
 */
final class Credentials {

    private Credentials() {
    }

    /** Identity of one persistent context source owned by the publisher. */
    record SourceIdentity(long id) {
    }

    /** Proof that a selection was minted by the barrier core of one publication. */
    record Selection(BarrierCore issuer) {
    }

    /** Proof that a participant bundle was minted by the production assembler. */
    record Production() {
    }
}
