// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.Objects;
import java.util.Optional;

/**
 * One atomic publication snapshot (PHASE_4_DOC §2.2). A ready publication contains a
 * non-owning registry view and barrier view with the same generation; accepted shaders-off
 * and RecoveredOff contain neither. The snapshot's {@code contexts} source stays live so the
 * renderer can issue the next frame's contexts.
 */
public record PublishedRegistry(
        long generation,
        Optional<ProgramRegistryView> registry,
        Optional<PublishedProgramStateBarrier> barrier,
        BarrierContextSource contexts) {

    public PublishedRegistry {
        Objects.requireNonNull(registry, "registry");
        Objects.requireNonNull(barrier, "barrier");
        Objects.requireNonNull(contexts, "contexts");
    }

    /** The canonical shaders-off snapshot: no registry or barrier view. */
    public static PublishedRegistry off(long generation, BarrierContextSource contexts) {
        return new PublishedRegistry(generation, Optional.empty(), Optional.empty(), contexts);
    }
}
