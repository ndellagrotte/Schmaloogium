// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The nine pack-stage identities (PHASE_4_DOC §2.2). Identity is separate from schedule
 * occurrence: {@link #GBUFFERS} legitimately occurs twice per frame schedule, once before
 * and once after deferred. Schedule order is data carried by {@link StageStep}, never
 * enum-ordinal control flow (§2.1.1).
 */
public enum StageId {
    SETUP,
    BEGIN,
    SHADOW,
    SHADOWCOMP,
    PREPARE,
    GBUFFERS,
    DEFERRED,
    COMPOSITE,
    FINAL
}
