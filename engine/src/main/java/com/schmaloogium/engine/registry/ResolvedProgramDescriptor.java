// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import com.schmaloogium.engine.preprocess.MaterializationFingerprint;

import java.util.List;

/**
 * The handle-free projection of one resolved program binding (PHASE_4_DOC §2.2): requested
 * and effective identities, the provider's complete state, layouts, source fingerprints and
 * the requested-to-provider fallback path. Every field except {@code requested} and
 * {@code fallbackPath} is the effective provider's — a child never overlays any part of the
 * ancestor's configuration (§4.6, D-P4-4). Inspection only; never selection authority.
 * Immutable and safe to retain after close.
 */
public record ResolvedProgramDescriptor(
        ProgramSlotId requested,
        ProgramSlotId effective,
        ProgramStateBundle state,
        ProgramUniformLayout uniformLayout,
        ProgramSamplerLayout samplerLayout,
        List<MaterializationFingerprint> sources,
        List<ProgramSlotId> fallbackPath) {

    public ResolvedProgramDescriptor {
        java.util.Objects.requireNonNull(requested, "requested");
        java.util.Objects.requireNonNull(effective, "effective");
        java.util.Objects.requireNonNull(state, "state");
        java.util.Objects.requireNonNull(uniformLayout, "uniformLayout");
        java.util.Objects.requireNonNull(samplerLayout, "samplerLayout");
        sources = List.copyOf(sources);
        fallbackPath = List.copyOf(fallbackPath);
    }
}
