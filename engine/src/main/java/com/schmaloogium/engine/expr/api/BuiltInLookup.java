// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Closed exact-name lookup result (§5.3): present typed value or absent. Absent fixed
 * inputs are runtime errors for the one uniform that reaches them — Phase 11 never
 * invents zero (§3.3). */
public sealed interface BuiltInLookup {

    record Present(BuiltInValue value) implements BuiltInLookup {
        public Present {
            if (value == null) {
                throw new IllegalArgumentException("value");
            }
        }
    }

    record Absent() implements BuiltInLookup {}
}
