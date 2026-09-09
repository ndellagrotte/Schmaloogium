// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.compat;

/**
 * A check that can run before the MOD mixin plugin's first evaluation (PHASE_1_DOC
 * §4.10, D-P1-50). Its terminal veto is retained for the session and prevents all
 * dependent vertex hooks from applying.
 */
public interface EarlyCompatCheck extends CompatCheck {

    CompatVerdict checkEarly(EarlyCompatContext ctx);
}
