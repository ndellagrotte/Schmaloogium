// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * The balanced per-entity and per-block-entity uniform-value state machine
 * (PHASE_9_DOC §4.12). Entry authenticates a main-or-shadow admission, resolves the
 * mapped-or-zero ID, pushes the prior value and updates the Phase 6 sink; only the exact
 * LIFO token restores the immediately preceding ID. Unknown ordinals use 0, while stale
 * generations reject before lookup. Overflow, wrong order, underflow or a post-drain
 * token neutralizes the affected cells, disables that producer for the frame, reports
 * once and leaves the drain to Phase 7's frame boundary.
 *
 * <p>All operations are render-thread confined through the admission's owner thread.
 * Mixins only forward object ordinals and call-local tokens; every policy decision lives
 * here. Phase 7's H-FRAME finally/abort path calls {@link #resetFrame()}, which clears
 * both stacks, sends neutral zero and re-arms both producers before any later draw.
 */
public interface PerDrawDynamics {

    /** Opens one balanced entity-ID scope under an authenticated admission. */
    IdScopeResult enterEntity(IdScopeAdmission admission, int entityOrdinal);

    /** Opens one balanced block-entity-ID scope for a state ordinal under an admission. */
    IdScopeResult enterBlockEntity(IdScopeAdmission admission, int stateOrdinal);

    /** Closes the scope of the exact LIFO token and restores the preceding ID. */
    void leave(IdScopeToken token);

    /** Frame abort/drain backstop: clears both stacks and writes neutral zero. */
    void resetFrame();
}
