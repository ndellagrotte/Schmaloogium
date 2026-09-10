// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;
import java.util.Optional;

/**
 * Main render sizing plus optional shadow sizing, with structural value equality over exactly these two
 * fields (PHASE_5_DOC §2.2). {@code shadowExtent} is present exactly when either the P3 shadow-depth or
 * shadow-color minimum is positive, and empty exactly when no sfb is planned (D-P5-27 two-field cutover;
 * no superSamplingLevel accessor).
 */
public record BufferSizing(Extent2i mainExtent, Optional<Extent2i> shadowExtent) {

    public BufferSizing {
        mainExtent = Objects.requireNonNull(mainExtent, "mainExtent");
        shadowExtent = shadowExtent == null ? Optional.empty() : shadowExtent;
    }
}
