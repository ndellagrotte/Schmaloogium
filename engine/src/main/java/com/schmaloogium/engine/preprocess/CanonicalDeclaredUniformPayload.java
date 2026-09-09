// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import java.util.List;

/** Hashable declared-uniform payload without the fingerprint back-reference. */
public record CanonicalDeclaredUniformPayload(
        int schemaVersion,
        List<DeclaredUniform> declarations) {

    public CanonicalDeclaredUniformPayload {
        declarations = List.copyOf(declarations);
    }
}
