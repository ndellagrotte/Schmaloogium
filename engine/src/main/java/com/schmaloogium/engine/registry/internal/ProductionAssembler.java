// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.BarrierConstructionResult;
import com.schmaloogium.engine.registry.BarrierParticipantResult;
import com.schmaloogium.engine.registry.CompiledRegistryCandidate;
import com.schmaloogium.engine.registry.ProductionBarrierComposer;
import com.schmaloogium.engine.registry.ProductionBarrierParticipants;
import com.schmaloogium.engine.registry.ProgramBindingParticipant;

import java.util.Objects;

/**
 * The production assembler (PHASE_4_DOC §4.10): the only minter of
 * {@code ProductionBarrierParticipants}. Exactly one successful production candidate may be
 * composed per registry product; a repeated call returns Invalid. The facade delegates here
 * and to the package-private factory.
 */
public final class ProductionAssembler implements ProductionBarrierComposer {

    private final BarrierFactoryImpl factory;
    private final ProductionCredential credential;

    private ProductionAssembler(BarrierFactoryImpl factory, ProductionCredential credential) {
        this.factory = factory;
        this.credential = credential;
    }

    public static ProductionAssembler create() {
        ProductionCredential credential = ProductionCredential.MINTED;
        return new ProductionAssembler(new BarrierFactoryImpl(credential), credential);
    }

    @Override
    public BarrierConstructionResult compose(
            CompiledRegistryCandidate registry,
            ProgramBindingParticipant samplers,
            ProgramBindingParticipant builtIns,
            ProgramBindingParticipant customs) {
        try {
            if (registry == null || samplers == null || builtIns == null || customs == null) {
                return new BarrierConstructionResult.Invalid("COMPOSITION_NULL_INPUT");
            }
            CompiledProgramRegistryImpl impl = registry.registry();
            if (!impl.claimComposition()) {
                return new BarrierConstructionResult.Invalid("COMPOSITION_REPEATED");
            }
            ProductionBarrierParticipants bundle = mint(samplers, builtIns, customs, credential);
            BarrierConstructionResult result = factory.create(registry, bundle);
            if (result instanceof BarrierConstructionResult.Ready) {
                impl.sealComposition();
            } else {
                impl.releaseCompositionClaim();
            }
            return result;
        } catch (RuntimeException failure) {
            return new BarrierConstructionResult.Invalid("COMPOSITION_EXCEPTION");
        }
    }

    /** Mint-only entry so no public/protected constructor or factory exists. */
    static ProductionBarrierParticipants mint(
            ProgramBindingParticipant samplers,
            ProgramBindingParticipant builtIns,
            ProgramBindingParticipant customs,
            ProductionCredential credential) {
        Objects.requireNonNull(samplers, "samplers");
        Objects.requireNonNull(builtIns, "builtIns");
        Objects.requireNonNull(customs, "customs");
        return new ProductionBarrierParticipants(samplers, builtIns, customs, credential);
    }

}
