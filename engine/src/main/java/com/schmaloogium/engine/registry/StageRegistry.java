// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.List;
import java.util.Optional;

/**
 * The immutable stage/pass traversal of one registry (PHASE_4_DOC §2.2/§4.1).
 * {@link #schedule()} is the sole deterministic traversal: configuration order, including
 * both distinct gbuffers occurrences; each DEFERRED and COMPOSITE appears exactly once; a
 * prelude never creates another occurrence. {@link #passes} returns an immutable list:
 * NamedPrograms in declared catalog order, Singleton once, SparseArray's present virtual
 * prelude first followed by populated indexed members ascending with holes omitted.
 * Lookups of wrong keys/kinds reject rather than convert into absence. Every returned
 * descriptor is the identical contained value.
 */
public interface StageRegistry {

    /** The frame schedule in execution order; immutable. */
    List<StageStep> schedule();

    /** The contained descriptors of one schedule step, in population order. */
    List<PassDescriptor> passes(StageStep step);

    /** Named lookup; on SparseArray only the exact stage-specific prelude key is legal. */
    Optional<PassDescriptor> named(StageStep step, ProgramSlotId id);

    /** Indexed lookup; only on SparseArray, addressing indexed members only. */
    Optional<PassDescriptor> indexed(StageStep step, PassIndex index);

    /** Whether {@code step} is one of this registry's schedule steps (identity match). */
    boolean stepExists(StageStep step);
}
