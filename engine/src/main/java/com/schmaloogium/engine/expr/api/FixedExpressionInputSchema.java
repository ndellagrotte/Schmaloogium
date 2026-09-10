// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Map;
import java.util.Objects;

/** Immutable versioned exact-name/type view of the permitted Phase 6 fixed catalog
 * (§4.4). Samplers and every excluded per-draw name are simply absent. */
public record FixedExpressionInputSchema(String version, Map<String, FixedInputKind> inputs) {

    public FixedExpressionInputSchema {
        Objects.requireNonNull(version, "version");
        if (version.isEmpty()) {
            throw new IllegalArgumentException("version must be non-empty");
        }
        Objects.requireNonNull(inputs, "inputs");
        inputs = Map.copyOf(inputs);
        for (Map.Entry<String, FixedInputKind> entry : inputs.entrySet()) {
            Objects.requireNonNull(entry.getValue(), "input kind");
            if (!entry.getKey().matches("[A-Za-z_][A-Za-z0-9_]*")) {
                throw new IllegalArgumentException("invalid fixed input name: " + entry.getKey());
            }
        }
    }
}
