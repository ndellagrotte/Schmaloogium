// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.ProgramSlotId;

import java.util.List;

/**
 * Phase-4-private resolved binding (PHASE_4_DOC §2.2): the requested hook slot, the effective
 * provider binding reached through the sole backup chain, and the root-to-provider walk.
 * The requested identity is preserved separately from the forced root/provider (§4.10).
 */
record ResolvedCompiledProgramBinding(
        ProgramSlotId requested,
        CompiledProgramBinding effective,
        List<ProgramSlotId> fallbackPath) {

    ResolvedCompiledProgramBinding {
        java.util.Objects.requireNonNull(requested, "requested");
        java.util.Objects.requireNonNull(effective, "effective");
        fallbackPath = List.copyOf(fallbackPath);
    }
}
