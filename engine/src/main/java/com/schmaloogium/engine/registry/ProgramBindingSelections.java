// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The pure private-credential selection validator (PHASE_4_DOC §4.10). No GL, no publisher
 * argument; first failure wins: INVALID_ISSUER, STALE_GENERATION, STALE_CONTEXT,
 * WRONG_STAGE_BAND, PROVIDER_LAYOUT_MISMATCH. Public-record equality alone never
 * authenticates — the selection's minted credential and the context's minted identity are
 * checked against the currently published barrier.
 */
public final class ProgramBindingSelections {

    private ProgramBindingSelections() {
    }

    public static ProgramSelectionValidation validateSelection(
            ProgramBindingSelection selection,
            BarrierContext context) {
        return com.schmaloogium.engine.registry.internal.BarrierInternal
            .validateSelection(selection, context);
    }
}
