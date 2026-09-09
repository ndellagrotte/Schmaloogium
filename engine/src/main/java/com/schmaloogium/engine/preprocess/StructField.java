// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

public record StructField(String exactName, DeclaredGlslType type) {

    public StructField {
        java.util.Objects.requireNonNull(exactName, "exactName");
        java.util.Objects.requireNonNull(type, "type");
    }
}
