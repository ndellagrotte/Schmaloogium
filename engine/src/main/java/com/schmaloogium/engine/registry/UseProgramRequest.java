// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The use-program request (PHASE_4_DOC §2.2): the retained selection and the identical
 * originating context. Records are equal-value; only private-origin selections authenticate.
 */
public record UseProgramRequest(
        ProgramBindingSelection selection,
        BarrierContext context) {

    public UseProgramRequest {
        java.util.Objects.requireNonNull(selection, "selection");
        java.util.Objects.requireNonNull(context, "context");
    }
}
