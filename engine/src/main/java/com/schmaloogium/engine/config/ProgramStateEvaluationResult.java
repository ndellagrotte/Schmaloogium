// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Closed program-state evaluation outcome. */
public sealed interface ProgramStateEvaluationResult {
    record Evaluated(EvaluatedProgramStates states) implements ProgramStateEvaluationResult {}
    record InvalidState(OptionStateFailure failure) implements ProgramStateEvaluationResult {}
}
