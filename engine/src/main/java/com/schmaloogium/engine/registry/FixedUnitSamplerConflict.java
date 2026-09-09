// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.List;

/** One fixed-unit conflict: two or more incompatible declarations contesting one unit. */
public record FixedUnitSamplerConflict(int unit, List<ProgramSamplerDeclaration> witnesses) {

    public FixedUnitSamplerConflict {
        if (unit < 0 || unit > 15) {
            throw new IllegalArgumentException("fixed unit outside 0..15: " + unit);
        }
        witnesses = List.copyOf(witnesses);
    }
}
