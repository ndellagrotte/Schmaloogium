// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.pack.NormalizedPackPath;

/** Canonical physical source identity. */
public record SourceId(NormalizedPackPath path) {

    public SourceId {
        java.util.Objects.requireNonNull(path, "path");
    }
}
