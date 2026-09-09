// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The production composition entry (PHASE_4_DOC §4.10). Phase 7's composition root calls
 * {@code compose} with the compiler-issued candidate and Phase 6's three implementations in
 * the fixed sampler/built-in/custom order. The facade delegates to the Phase-4-owned
 * package-private production assembler, which alone mints
 * {@link ProductionBarrierParticipants}, and then to the package-private factory
 * implementation. Returns only {@link BarrierConstructionResult}, never the bundle or its
 * credential. Exactly one successful production candidate may be composed per registry
 * product; a repeated call returns {@code Invalid}. Failure retains nothing.
 */
public interface ProductionBarrierComposer {

    BarrierConstructionResult compose(
            CompiledRegistryCandidate registry,
            ProgramBindingParticipant samplers,
            ProgramBindingParticipant builtIns,
            ProgramBindingParticipant customs);
}
