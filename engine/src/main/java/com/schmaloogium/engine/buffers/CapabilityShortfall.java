// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;

/** One capability shortfall: required vs available for a single {@link CapabilityLimit} (PHASE_5_DOC §2.2). */
public record CapabilityShortfall(CapabilityLimit limit, int required, int available) {

    public CapabilityShortfall {
        Objects.requireNonNull(limit, "limit");
        if (required < 0) {
            throw new IllegalArgumentException("negative required capability count: " + required);
        }
        if (available < 0) {
            throw new IllegalArgumentException("negative available capability count: " + available);
        }
    }
}
