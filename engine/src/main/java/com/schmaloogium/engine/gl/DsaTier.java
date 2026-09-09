// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Direct-state-access tier (R-P14→P1-2, PHASE_1_DOC §4.7.8; PHASE_14_DOC §2.1):
 * CORE_45 | ARB | BIND_TO_EDIT. BIND_TO_EDIT is the facade's existing path and the
 * reference-faithful shipping default (D-P14-17); the tiers are behaviorally identical
 * (D-P14-7).
 */
public enum DsaTier {
    CORE_45,
    ARB,
    BIND_TO_EDIT
}
