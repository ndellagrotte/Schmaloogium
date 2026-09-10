// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

import java.util.Objects;
import java.util.Set;

/**
 * The colortex indices whose mipmaps are generated before a fullscreen pass reads them
 * (PHASE_7_DOC §5.1). Immutable snapshot.
 */
public record MipmapSet(Set<Integer> colortexIndices) {

    public MipmapSet {
        Objects.requireNonNull(colortexIndices, "colortexIndices");
        colortexIndices = Set.copyOf(colortexIndices);
        for (int index : colortexIndices) {
            if (index < 0 || index > 15) {
                throw new IllegalArgumentException("colortex index out of range: " + index);
            }
        }
    }

    public static final MipmapSet EMPTY = new MipmapSet(Set.of());
}
