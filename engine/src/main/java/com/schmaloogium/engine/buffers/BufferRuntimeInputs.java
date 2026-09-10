// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;

/**
 * The complete runtime planning identity (PHASE_5_DOC §2.2): all three fields participate by value in
 * planning identity and reuse.
 */
public record BufferRuntimeInputs(Extent2i displayExtent, double renderQuality, double shadowQuality) {

    public BufferRuntimeInputs {
        Objects.requireNonNull(displayExtent, "displayExtent");
    }
}
