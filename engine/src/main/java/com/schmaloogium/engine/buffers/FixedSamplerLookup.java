// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Result of looking up an exact sampler spelling against the fixed-sampler domain. */
public sealed interface FixedSamplerLookup {
    record Known(FixedSamplerName name) implements FixedSamplerLookup {
    }

    record Unknown(String exactName) implements FixedSamplerLookup {
    }
}
