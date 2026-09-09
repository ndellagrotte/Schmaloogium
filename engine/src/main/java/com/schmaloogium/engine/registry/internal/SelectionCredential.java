// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

/** Typed wrapper so public classes never see raw credential records. */
public record SelectionCredential(Credentials.Selection inner) {

    public SelectionCredential {
        java.util.Objects.requireNonNull(inner, "inner");
    }
}
