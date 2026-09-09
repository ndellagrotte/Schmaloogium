// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.NormalizedPackPath;

/** Adjacent {@code .mcmeta} sidecar reference; never interpreted by Phase 3. */
public record TextureSidecarRef(NormalizedPackPath path) {

    public TextureSidecarRef {
        java.util.Objects.requireNonNull(path, "path");
    }
}
