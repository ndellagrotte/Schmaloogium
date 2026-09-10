// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Phase 6's exact-name typed runtime value view consumed at refresh (§5.3/§5.4). */
public interface BuiltInExpressionView {
    BuiltInLookup lookup(String name);
}
