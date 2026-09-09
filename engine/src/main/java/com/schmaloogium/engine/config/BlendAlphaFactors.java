// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Optional;

/** Optional separate alpha blend factors. */
public record BlendAlphaFactors(BlendFactor source, BlendFactor destination) {

    public BlendAlphaFactors {
        java.util.Objects.requireNonNull(source, "source");
        java.util.Objects.requireNonNull(destination, "destination");
    }
}
