// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

/**
 * The static, pure preliminary macro producer (PHASE_13_DOC §2.2/§4.1.6, D-P13-10/D-P13-20).
 * Independent of any texture owner, atlas, GL operation, configuration, registry or linked
 * declaration.
 */
public final class CompanionMacroPolicy {

    private CompanionMacroPolicy() {
    }

    /**
     * Each output bit is active pack AND fixed-unit capability AND that independent decoded
     * user preference. Off short-circuits: an inactive pack yields (false, false) before the
     * preferences are consulted.
     */
    public static CompanionMacroState preliminaryMacroState(PreliminaryCompanionDemand demand) {
        java.util.Objects.requireNonNull(demand, "demand");
        if (!demand.packActive()) {
            return CompanionMacroState.OFF;
        }
        boolean capable = demand.fixedUnitCapabilityAvailable();
        return new CompanionMacroState(
                capable && demand.normalMapEnabled(),
                capable && demand.specularMapEnabled());
    }
}
