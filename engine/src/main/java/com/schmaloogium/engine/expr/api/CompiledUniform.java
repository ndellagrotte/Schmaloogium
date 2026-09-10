// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** One valid upload-designated uniform of a compiled plan, in declaration order (§4.1). */
public record CompiledUniform(int sourceOrdinal, ExpressionType type, String name) {

    public CompiledUniform {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(name, "name");
        if (sourceOrdinal < 0) {
            throw new IllegalArgumentException("sourceOrdinal must be >= 0");
        }
    }
}
