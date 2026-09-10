// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Plan-local random stream; {@code nextFloat()} must be finite in [0,1) — violation is a
 * provider protocol failure aborting only that refresh (§4.6/§4.10). */
public interface RandomSource {
    float nextFloat();
}
