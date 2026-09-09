// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The barrier factory (PHASE_4_DOC §4.10). Package-private implementation; checks both
 * private credentials and creates a private barrier that invokes the positions in
 * sampler/built-in/custom order. Null inputs, a closed or non-compiler-issued registry
 * product, or an internal missing member yield {@code Invalid} without GL work or retained
 * participant references.
 */
public interface ProgramStateBarrierFactory {

    BarrierConstructionResult create(
            CompiledRegistryCandidate registry,
            ProductionBarrierParticipants participants);
}
