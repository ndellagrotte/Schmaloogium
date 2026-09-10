// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * The shadow estate is unusable after creation failure or runtime neutralization.
 */
public record ShadowEstateUnavailable(BufferFailure reason, long estateGeneration)
        implements ShadowEstateResult {

    public ShadowEstateUnavailable {
        java.util.Objects.requireNonNull(reason, "reason");
    }
}
