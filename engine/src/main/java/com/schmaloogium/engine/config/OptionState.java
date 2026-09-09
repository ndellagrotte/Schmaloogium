// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.OptionState;

import java.util.Map;
import java.util.Optional;

/** Sealed catalog-issued option state; consumers cannot mint one. */
public sealed interface OptionState permits OptionStateValue {

    Map<String, OptionValue> values();

    Optional<OptionValue> value(String name);
}
