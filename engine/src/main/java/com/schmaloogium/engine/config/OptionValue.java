// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Sealed option value sum. */
public sealed interface OptionValue permits BooleanOptionValue, TextOptionValue {
}
