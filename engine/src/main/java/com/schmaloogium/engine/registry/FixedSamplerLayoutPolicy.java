// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import com.schmaloogium.engine.gl.SamplerUnitAssignment;

import java.util.List;

/**
 * The Phase-5-implemented fixed sampler policy (PHASE_4_DOC §2.2/§4.7, D-P4-36). Phase 4
 * owns only this interface and the immutable derived metadata — never the policy map. The
 * policy comes from Phase 5's pure {@code FixedSamplerPolicies.appB3()} before any estate or
 * GL object exists; a null policy, changing fingerprint, thrown callback or malformed output
 * is a typed registry-wide {@code INVALID_SAMPLER_POLICY} failure with zero GL calls.
 */
public interface FixedSamplerLayoutPolicy {

    /** Stable, non-null policy identity. */
    FixedSamplerPolicyFingerprint fingerprint();

    /** Validate one band's projection; called in canonical band order for every
     *  provider-permitted band before any GL work. */
    SamplerLayoutValidation validate(StageId effectiveStage, StageBand effectiveBand,
        List<ProgramSamplerDeclaration> declarations);

    /** Derive the frozen fixed-unit initialization plan for one valid shader layout:
     *  immutable, sorted by exact name's UTF-8 bytes, every distinct declared sampler
     *  exactly once, units 0–15, no extra names; all validated bands must agree. */
    List<SamplerUnitAssignment> initializationAssignments(ProgramSamplerLayout.Shader layout);
}
