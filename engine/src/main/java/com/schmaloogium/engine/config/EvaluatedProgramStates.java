// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.ProgramKey;

import java.util.List;
import java.util.Map;

/** Evaluated program-state aggregate over the projected executable universe. */
public record EvaluatedProgramStates(
        List<EvaluatedProgramState> programs,
        Map<ProgramKey, Map<FlipBufferKey, FlipOverride>> explicitFlips) {

    public EvaluatedProgramStates {
        programs = List.copyOf(programs);
        java.util.SortedMap<ProgramKey, Map<FlipBufferKey, FlipOverride>> ordered =
            new java.util.TreeMap<>(ProgramStateModel.ProgramKeyOrder.INSTANCE);
        ordered.putAll(java.util.Objects.requireNonNull(explicitFlips, "explicitFlips"));
        explicitFlips = java.util.Collections.unmodifiableSortedMap(ordered);
    }
}
