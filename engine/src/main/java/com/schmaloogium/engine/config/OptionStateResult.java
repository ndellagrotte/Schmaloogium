// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.OptionState;

/** Closed option-state operation outcome. */
public sealed interface OptionStateResult {
    record Valid(OptionState state) implements OptionStateResult {}
    record Invalid(OptionStateFailure failure) implements OptionStateResult {}
}
