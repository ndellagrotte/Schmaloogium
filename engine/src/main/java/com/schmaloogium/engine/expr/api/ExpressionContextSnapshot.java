// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** One by-copy context snapshot: no entities, worlds, biome objects, registries, or
 * nullable values (§4.10). Temperature/rainfall must be finite. */
public record ExpressionContextSnapshot(int biomeId, float temperature, float rainfall,
                                        ViewEntityFlags viewEntityFlags) {

    public ExpressionContextSnapshot {
        Objects.requireNonNull(viewEntityFlags, "viewEntityFlags");
        if (!Float.isFinite(temperature) || !Float.isFinite(rainfall)) {
            throw new IllegalArgumentException("temperature and rainfall must be finite");
        }
    }
}
