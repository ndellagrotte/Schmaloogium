// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

/**
 * Per-row hook disposition (PHASE_8_DOC §4.13.1): HEALTHY exactly when the observed count
 * equals the expected count; FEATURE_DISABLED otherwise, including overmatch.
 */
public enum HookDisposition {
    HEALTHY, FEATURE_DISABLED
}
