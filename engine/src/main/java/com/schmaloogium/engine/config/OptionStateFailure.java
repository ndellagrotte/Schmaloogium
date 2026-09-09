// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Closed option-state operation failures with the §4.3 priority order. */
public enum OptionStateFailure {
    NULL_INPUT, FOREIGN_CATALOG, UNKNOWN_OPTION, MISSING_OPTION, NULL_VALUE,
    KIND_MISMATCH, DISABLED_AMBIGUOUS, UNSAFE_TEXT
}
