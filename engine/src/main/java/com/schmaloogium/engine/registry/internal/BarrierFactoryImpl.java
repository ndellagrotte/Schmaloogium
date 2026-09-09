// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.BarrierConstructionResult;
import com.schmaloogium.engine.registry.BarrierPublicationCandidate;
import com.schmaloogium.engine.registry.CompiledRegistryCandidate;
import com.schmaloogium.engine.registry.ProductionBarrierParticipants;

import java.util.Objects;

/**
 * The package-private factory (PHASE_4_DOC §4.10). Checks both private credentials — the
 * registry product's compiler-origin state and the participant bundle's production
 * credential — and mints the private barrier for that exact product and registry identity.
 * Null inputs, a closed or non-compiler-issued registry product, or an internal missing
 * member return Invalid without GL work or retained participant references.
 */
public final class BarrierFactoryImpl implements com.schmaloogium.engine.registry.ProgramStateBarrierFactory {

    private final ProductionCredential requiredCredential;

    BarrierFactoryImpl(ProductionCredential requiredCredential) {
        this.requiredCredential = Objects.requireNonNull(requiredCredential, "requiredCredential");
    }

    @Override
    public BarrierConstructionResult create(
            CompiledRegistryCandidate registry,
            ProductionBarrierParticipants participants) {
        try {
            if (registry == null || participants == null) {
                return invalid("COMPOSITION_NULL_INPUT");
            }
            if (registry.isClosed()) {
                return invalid("REGISTRY_STATE_CLOSED");
            }
            CompiledProgramRegistryImpl impl = registry.registry();
            if (impl.ownershipTransferred()) {
                return invalid("REGISTRY_STATE_TRANSFERRED");
            }
            if (!requiredCredential.equals(participants.credential())) {
                return invalid("COMPOSITION_PROVENANCE");
            }
            long nextGeneration = 0; // publisher stamps the accepted generation
            BarrierCore core = new BarrierCore(
                nextGeneration,
                impl.fingerprint(),
                impl,
                impl.contexts().identity(),
                new ProgramBindingParticipantRef(participants.samplers(), "samplers"),
                new ProgramBindingParticipantRef(participants.builtIns(), "builtIns"),
                new ProgramBindingParticipantRef(participants.customs(), "customs"),
                () -> 0,
                false);
            BarrierBundle bundle = new BarrierBundle(
                impl.identity(), nextGeneration, impl.fingerprint(), core, false);
            return new BarrierConstructionResult.Ready(new BarrierPublicationCandidate(bundle));
        } catch (RuntimeException failure) {
            return invalid("COMPOSITION_EXCEPTION");
        }
    }

    private static BarrierConstructionResult.Invalid invalid(String diagnosticId) {
        return new BarrierConstructionResult.Invalid(diagnosticId);
    }
}
