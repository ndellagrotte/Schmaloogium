// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.compat;

/**
 * One coexistence check (PHASE_1_DOC §4.10). Phase 1 owns the mechanism; the mod-id
 * list, the detection technique and the message text are Phase 10 / OQ-5.
 */
public interface CompatCheck {

    /** Stable identifier, for logs and for user-facing attribution. */
    String id();

    CompatVerdict check(CompatContext ctx);
}
