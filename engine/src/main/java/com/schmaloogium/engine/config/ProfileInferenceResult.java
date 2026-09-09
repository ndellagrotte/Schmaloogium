// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Closed profile inference outcome. */
public sealed interface ProfileInferenceResult {
    record Inferred(ProfileInference inference) implements ProfileInferenceResult {}
    record InvalidState(OptionStateFailure failure) implements ProfileInferenceResult {}
}
