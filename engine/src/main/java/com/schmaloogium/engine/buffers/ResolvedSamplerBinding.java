// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.preprocess.DeclaredGlslType;

/** One resolved fixed-sampler binding: exact name, declared shape and texture unit. */
public record ResolvedSamplerBinding(String exactName, DeclaredGlslType.Sampler shape, int unit) {

    public ResolvedSamplerBinding {
        java.util.Objects.requireNonNull(exactName, "exactName");
        java.util.Objects.requireNonNull(shape, "shape");
        if (unit < 0 || unit > 15) {
            throw new IllegalArgumentException("unit must be 0..15: " + unit);
        }
    }
}
