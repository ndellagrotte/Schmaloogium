// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/** The selection validation outcome (PHASE_4_DOC §2.2). */
public sealed interface ProgramSelectionValidation {

    record Valid() implements ProgramSelectionValidation {
    }

    record Rejected(ProgramSelectionRejection reason) implements ProgramSelectionValidation {
    }
}
