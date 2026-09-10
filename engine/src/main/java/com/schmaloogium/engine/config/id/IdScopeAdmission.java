// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * Opaque main-or-shadow admission capability issued and validated by Phase 7 glue
 * (PHASE_9_DOC §4.12). Main admission is issued only from the accepted entity/block
 * scope; shadow admission only after the shadow execution validates. Implementations
 * carry exactly the authenticated identity Phase 9 re-checks — frame, generation, owner
 * thread and liveness — and never expose frame internals or stack data.
 */
public interface IdScopeAdmission {

    /** The ID runtime generation this admission was issued for. */
    long generation();

    /** The frame identity this admission belongs to. */
    long frameId();

    /** The thread that may enter scopes under this admission. */
    Thread ownerThread();

    /** Whether the admission is still live (false once Phase 7 revokes it). */
    boolean active();

    /** Whether this admission authenticates shadow execution (never main activation). */
    boolean shadow();
}
