// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.preprocess.LegacyGeometryConfig;
import java.util.Optional;
import java.util.Set;

/** Per-program resource requirements. */
public record ProgramRequirements(DrawRouting routing,
    Set<ColorAttachmentKey> mipmappedAfterPass, VertexRequirements vertices,
    int instanceCount, Optional<LegacyGeometryConfig> legacyGeometry) {

    public ProgramRequirements {
        mipmappedAfterPass = mipmappedAfterPass == null
            ? Set.of() : java.util.Collections.unmodifiableSet(
                new java.util.TreeSet<>(java.util.Comparator.comparingInt(ColorAttachmentKey::colortexIndex)));
        legacyGeometry = legacyGeometry == null ? Optional.empty() : legacyGeometry;
    }
}
