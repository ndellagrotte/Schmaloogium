// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Optional;

/** Stateless catalog validation outcome. */
public record OptionStateValidation(Optional<OptionStateFailure> failure) {

    public boolean valid() { return failure.isEmpty(); }
}
