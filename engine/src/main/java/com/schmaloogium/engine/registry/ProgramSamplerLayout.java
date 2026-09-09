// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.List;
import java.util.Set;

/**
 * The immutable per-provider sampler layout with its validation evidence (PHASE_4_DOC
 * §2.2/§4.7). {@link Shader} carries every provider-permitted validated band, the ordered
 * declarations and the full {@link SamplerLayoutValidation} evidence; failed layouts retain
 * the attempted bands and payload, never a successful status. Fixed-function descriptors use
 * {@link FixedFunctionEmpty}; virtuals use {@link VirtualNotApplicable} with distinct
 * canonical encodings and no bindings.
 */
public sealed interface ProgramSamplerLayout {

    ProgramSamplerLayoutFingerprint fingerprint();

    FixedSamplerPolicyFingerprint policyFingerprint();

    /** One validated (or evidence-bearing failed) shader sampler projection. */
    record Shader(
            ProgramSamplerLayoutFingerprint fingerprint,
            FixedSamplerPolicyFingerprint policyFingerprint,
            StageId effectiveStage,
            Set<StageBand> validatedBands,
            List<ProgramSamplerDeclaration> declarations,
            SamplerLayoutValidation validation) implements ProgramSamplerLayout {

        public Shader {
            java.util.Objects.requireNonNull(fingerprint, "fingerprint");
            java.util.Objects.requireNonNull(policyFingerprint, "policyFingerprint");
            java.util.Objects.requireNonNull(effectiveStage, "effectiveStage");
            validatedBands = Set.copyOf(validatedBands);
            declarations = List.copyOf(declarations);
            java.util.Objects.requireNonNull(validation, "validation");
        }

        @Override
        public ProgramSamplerLayoutFingerprint fingerprint() {
            return fingerprint;
        }

        @Override
        public FixedSamplerPolicyFingerprint policyFingerprint() {
            return policyFingerprint;
        }
    }

    /** The canonical fixed-function sampler layout: no declarations, no bindings. */
    record FixedFunctionEmpty(
            ProgramSamplerLayoutFingerprint fingerprint,
            FixedSamplerPolicyFingerprint policyFingerprint) implements ProgramSamplerLayout {

        public FixedFunctionEmpty {
            java.util.Objects.requireNonNull(fingerprint, "fingerprint");
            java.util.Objects.requireNonNull(policyFingerprint, "policyFingerprint");
        }

        @Override
        public ProgramSamplerLayoutFingerprint fingerprint() {
            return fingerprint;
        }

        @Override
        public FixedSamplerPolicyFingerprint policyFingerprint() {
            return policyFingerprint;
        }
    }

    /** Virtual prelude planning metadata: typed, binding-free, never a descriptor payload. */
    record VirtualNotApplicable(
            ProgramSamplerLayoutFingerprint fingerprint,
            FixedSamplerPolicyFingerprint policyFingerprint) implements ProgramSamplerLayout {

        public VirtualNotApplicable {
            java.util.Objects.requireNonNull(fingerprint, "fingerprint");
            java.util.Objects.requireNonNull(policyFingerprint, "policyFingerprint");
        }

        @Override
        public ProgramSamplerLayoutFingerprint fingerprint() {
            return fingerprint;
        }

        @Override
        public FixedSamplerPolicyFingerprint policyFingerprint() {
            return policyFingerprint;
        }
    }
}
