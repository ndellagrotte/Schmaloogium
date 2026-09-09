// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.compat;

import java.util.List;

/** Aggregated outcome of a {@link BailRegistry} evaluation (PHASE_1_DOC §4.10). */
public record CompatEvaluation(
        List<CompatVerdict.Bail> bails,
        List<CompatVerdict.Degrade> degradations) {

    public CompatEvaluation {
        bails = List.copyOf(bails);
        degradations = List.copyOf(degradations);
    }

    public boolean shouldBail() {
        return !bails.isEmpty();
    }
}
