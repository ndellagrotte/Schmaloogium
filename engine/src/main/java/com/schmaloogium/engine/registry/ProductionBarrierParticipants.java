// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The opaque production participant bundle (PHASE_4_DOC §4.10). Minted alone by the
 * Phase-4-owned production assembler from the three position-named participants plus
 * credential; no public/protected constructor, factory, subclass, or component access.
 */
public final class ProductionBarrierParticipants {

    private final ProgramBindingParticipant samplers;
    private final ProgramBindingParticipant builtIns;
    private final ProgramBindingParticipant customs;
    private final com.schmaloogium.engine.registry.internal.ProductionCredential credential;

    public ProductionBarrierParticipants(
            ProgramBindingParticipant samplers,
            ProgramBindingParticipant builtIns,
            ProgramBindingParticipant customs,
            com.schmaloogium.engine.registry.internal.ProductionCredential credential) {
        this.samplers = java.util.Objects.requireNonNull(samplers, "samplers");
        this.builtIns = java.util.Objects.requireNonNull(builtIns, "builtIns");
        this.customs = java.util.Objects.requireNonNull(customs, "customs");
        this.credential = java.util.Objects.requireNonNull(credential, "credential");
    }

    public ProgramBindingParticipant samplers() {
        return samplers;
    }

    public ProgramBindingParticipant builtIns() {
        return builtIns;
    }

    public ProgramBindingParticipant customs() {
        return customs;
    }

    public com.schmaloogium.engine.registry.internal.ProductionCredential credential() {
        return credential;
    }

    @Override
    public String toString() {
        return "ProductionBarrierParticipants[opaque]";
    }
}
