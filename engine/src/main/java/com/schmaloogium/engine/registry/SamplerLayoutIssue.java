// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * One typed layout issue (PHASE_4_DOC §2.2). Canonical order is StageBand declaration
 * order, then declarationOrder, then issue-code declaration order.
 */
public record SamplerLayoutIssue(
        SamplerLayoutIssueCode code,
        StageBand band,
        ProgramSamplerDeclaration declaration) {

    public SamplerLayoutIssue {
        java.util.Objects.requireNonNull(code, "code");
        java.util.Objects.requireNonNull(band, "band");
        java.util.Objects.requireNonNull(declaration, "declaration");
    }
}
