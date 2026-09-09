// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.Optional;
import java.util.Set;

/**
 * One contained pass descriptor (PHASE_4_DOC §2.2). {@code step()} is the identical
 * contained schedule value; virtual prelude descriptors carry empty {@code index},
 * {@code computeSlots}, {@code readable}, {@code writes} and {@code mipmappedBeforeRead},
 * retaining only Phase-3-derived {@code explicitFlips}. Immutable.
 */
public record PassDescriptor(
        StageStep step,
        ProgramSlotId slot,
        Optional<PassIndex> index,
        PassResourceAccess resources,
        Set<ComputeDispatchSlot> computeSlots) {

    public PassDescriptor {
        java.util.Objects.requireNonNull(step, "step");
        java.util.Objects.requireNonNull(slot, "slot");
        index = index == null ? Optional.empty() : index;
        java.util.Objects.requireNonNull(resources, "resources");
        computeSlots = Set.copyOf(computeSlots);
    }
}
