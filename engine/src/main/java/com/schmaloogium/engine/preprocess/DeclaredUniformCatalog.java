// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import java.util.List;

public record DeclaredUniformCatalog(
        MaterializationFingerprint materialization,
        List<DeclaredUniform> declarations) {

    public DeclaredUniformCatalog {
        java.util.Objects.requireNonNull(materialization, "materialization");
        declarations = List.copyOf(declarations);
    }
}
