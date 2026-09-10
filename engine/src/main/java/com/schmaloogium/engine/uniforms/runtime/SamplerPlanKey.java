// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.runtime;

import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;

import java.util.Objects;

/**
 * The sampler-plan reuse identity inside one {@link ProgramUniformCacheKey} cache
 * (PHASE_6_DOC §4.9): the effective sampler-layout/policy and the Phase-4-issued
 * stage/band combination. Both gbuffers bands may share the identical plan when the
 * layout and context inputs match. Immutable.
 */
public record SamplerPlanKey(
        StageId stage,
        StageBand band,
        ProgramSamplerLayoutFingerprint samplerLayout,
        FixedSamplerPolicyFingerprint policy) {

    public SamplerPlanKey {
        Objects.requireNonNull(stage, "stage");
        Objects.requireNonNull(band, "band");
        Objects.requireNonNull(samplerLayout, "samplerLayout");
        Objects.requireNonNull(policy, "policy");
    }
}
