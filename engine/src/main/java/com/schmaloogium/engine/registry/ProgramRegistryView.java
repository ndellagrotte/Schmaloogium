// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The non-owning registry view (PHASE_4_DOC §2.2): inspection metadata only, never authority
 * to select or bind. Contains no GL handle or publication generation.
 */
public interface ProgramRegistryView {

    StageRegistry stages();

    /** Projects the private compiled binding; empty for ABSENT and FAILED rows. */
    java.util.Optional<ResolvedProgramDescriptor> resolve(ProgramSlotId requested);

    /** One immutable row per descriptor in complete catalog order, virtuals included. */
    java.util.List<ProgramResolutionProjection> resolutions();

    RegistryFingerprint fingerprint();

    FixedSamplerPolicyFingerprint samplerPolicyFingerprint();
}
