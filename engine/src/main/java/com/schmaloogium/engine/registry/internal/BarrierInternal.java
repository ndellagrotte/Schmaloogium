// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramBindingSelections;
import com.schmaloogium.engine.registry.ProgramSelectionValidation;

/**
 * Internal facade behind {@link ProgramBindingSelections}; the pure private-credential
 * check with no GL and no new publisher argument.
 */
public final class BarrierInternal {

    private BarrierInternal() {
    }

    public static ProgramSelectionValidation validateSelection(
            ProgramBindingSelection selection,
            BarrierContext context) {
        if (selection == null || context == null) {
            return new ProgramSelectionValidation.Rejected(
                com.schmaloogium.engine.registry.ProgramSelectionRejection.INVALID_ISSUER);
        }
        BarrierCore issuer = selection.credential().inner().issuer();
        return issuer.validate(selection, context);
    }
}
