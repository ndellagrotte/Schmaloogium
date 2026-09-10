// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;

/**
 * Actually realized color allocation (PHASE_5_DOC §2.2); {@code format} is the canonical internal-format
 * name ({@code "RGBA"} for plain RGBA).
 */
public record RealizedColorAllocation(String format, ColorAllocationOrigin origin) {

    public RealizedColorAllocation {
        Objects.requireNonNull(format, "format");
        Objects.requireNonNull(origin, "origin");
    }
}
