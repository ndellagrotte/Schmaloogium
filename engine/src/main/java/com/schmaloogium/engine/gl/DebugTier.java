// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Debug label/group tier (R-P14→P1-2, PHASE_1_DOC §4.7.8; PHASE_14_DOC §2.1):
 * KHR | NONE. NONE ships in every configuration without the dev flag
 * {@code schmaloogium.debug.glLabels}; see {@link DebugService#isActive} for the v0.5
 * gate.
 */
public enum DebugTier {
    KHR,
    NONE
}
