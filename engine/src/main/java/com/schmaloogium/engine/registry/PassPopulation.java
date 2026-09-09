// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.List;
import java.util.Optional;

/**
 * How one {@link StageStep} is populated (PHASE_4_DOC §2.2). All components, optionals and
 * members are non-null; collections are defensively copied and immutable.
 *
 * <p>Sparse invariants: {@code highestLegalIndex} is 99 for modern array families.
 * {@code highestPopulatedIndex} describes indexed descriptor membership only — the greatest
 * present index, or {@code -1} iff none exists; never a count and never a prelude index.
 * {@code virtualPrelude} names membership: present requires exactly one matching contained
 * descriptor ({@code deferred_pre} only for {@code DEFERRED/BETWEEN_GBUFFERS},
 * {@code composite_pre} only for {@code COMPOSITE/FRAME_END}); empty forbids a virtual
 * member (§2.2, D-P4-40).
 */
public sealed interface PassPopulation {

    /** Exactly one descriptor: the {@code final} screen binding. */
    record Singleton() implements PassPopulation {
    }

    /** Declared named slots; the schedule stores declared catalog order. */
    record NamedPrograms(List<ProgramSlotId> slots) implements PassPopulation {

        public NamedPrograms {
            slots = List.copyOf(slots);
            for (ProgramSlotId slot : slots) {
                if (slot == null) {
                    throw new IllegalArgumentException("null named slot");
                }
            }
            java.util.Set<ProgramSlotId> distinct = new java.util.LinkedHashSet<>(slots);
            if (distinct.size() != slots.size()) {
                throw new IllegalArgumentException("duplicate named slot");
            }
        }
    }

    /**
     * A 0…99 sparse pass family. {@code highestPopulatedIndex} is the greatest present
     * indexed member or {@code -1}; require {@code -1 <= highestPopulatedIndex <= 99}.
     */
    record SparseArray(int highestLegalIndex, int highestPopulatedIndex,
            Optional<ProgramSlotId> virtualPrelude) implements PassPopulation {

        public SparseArray {
            virtualPrelude = virtualPrelude == null ? Optional.empty() : virtualPrelude;
            if (highestLegalIndex < 0 || highestLegalIndex > 99) {
                throw new IllegalArgumentException(
                    "highestLegalIndex outside 0..99: " + highestLegalIndex);
            }
            if (highestPopulatedIndex < -1 || highestPopulatedIndex > highestLegalIndex) {
                throw new IllegalArgumentException("highestPopulatedIndex inconsistent: "
                    + highestPopulatedIndex);
            }
        }
    }
}
