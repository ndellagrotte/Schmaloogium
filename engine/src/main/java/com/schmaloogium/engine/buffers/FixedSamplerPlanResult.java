// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import java.util.List;

/** Closed result of fixed-sampler planning: ready bindings or a layout validation failure. */
public sealed interface FixedSamplerPlanResult {
    record Ready(List<ResolvedSamplerBinding> bindings, FixedSamplerPolicyFingerprint policy)
        implements FixedSamplerPlanResult {

        public Ready {
            bindings = List.copyOf(bindings);
            java.util.Objects.requireNonNull(policy, "policy");
        }
    }

    record Invalid(SamplerLayoutValidation reason) implements FixedSamplerPlanResult {
    }
}
